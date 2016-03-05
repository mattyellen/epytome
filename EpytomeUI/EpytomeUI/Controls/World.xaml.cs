using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Numerics;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using EpytomeUI.UIElements;
using EpytomeUI.ViewModels;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.Controls
{
    public partial class World : UserControl
    {
        private WorldViewModel _viewModel;
        private readonly Dictionary<string, Action> _onPropChange;

        private const int ViewDist = 6;
        private double _hexMinDiam;
        private double _hexSideLen;
        private double _canvasWidth;
        private double _canvasHeight;

        private Point _bottomLeftHex;
        private double _borderThicknessX;
        private double _borderThicknessY;
        private Random _rand = new Random();

        private static double HalfSqrt3 = .5 * Math.Sqrt(3);

        private readonly Dictionary<string, Tuple<double, double>> _characterOffset;

        private enum WorldZIndex
        {
            Plot = 1,
            Road,
            IsoObjectStart,
            MoveInterface = Int16.MaxValue-1
        }

        struct RoadDirEntry
        {
            public RoadDirEntry(int x, int y, Road.Direction rd)
            {
                LocX = x;
                LocY = y;
                RoadDir = rd;
            }
            public int LocX;
            public int LocY;
            public Road.Direction RoadDir;
        }
        private readonly List<RoadDirEntry> _roadDirections =
            new List<RoadDirEntry>
                {
                    new RoadDirEntry(-1,-1, Road.Direction.SouthWest),
                    new RoadDirEntry(-2,0, Road.Direction.West),
                    new RoadDirEntry(-1,1, Road.Direction.NorthWest),
                    new RoadDirEntry(1,1, Road.Direction.NorthEast),
                    new RoadDirEntry(2,0, Road.Direction.East),
                    new RoadDirEntry(1,-1, Road.Direction.SouthEast),
                };

        public Canvas CurrentPlotCanvas { get; set; }

        public World()
        {
            InitializeComponent();
            _onPropChange = new Dictionary<string, Action>
            {
                {"PlotList", () =>
                                 {
                                     _characterOffset.Clear();
                                     OnPlotListChanged();
                                 }}
            };

            var border = (LayoutRoot as Border);
            if (border == null) return;

            _borderThicknessX = border.BorderThickness.Left +
                border.BorderThickness.Right;
            _borderThicknessY = border.BorderThickness.Top +
                border.BorderThickness.Bottom;

            _characterOffset = new Dictionary<string, Tuple<double, double>>();
        }

        protected override Size MeasureOverride(Size availableSize)
        {
            var effectiveSize = new Size(availableSize.Width - _borderThicknessX, 
                                         availableSize.Height - _borderThicknessY);

            //Assume height based on width
            _canvasHeight = effectiveSize.Width * 1.5 / Math.Sqrt(3);
            if (_canvasHeight <= availableSize.Height)
            {
                // All good...  complete the calc
                _canvasWidth = effectiveSize.Width;
            }
            else
            {
                //No good...  invert the calc
                _canvasHeight = effectiveSize.Height;
                _canvasWidth = effectiveSize.Height * Math.Sqrt(3) / 1.5;
            }

            _hexMinDiam = _canvasWidth / (ViewDist * 2);
            _hexSideLen = _canvasHeight / (ViewDist * 3);

            OnPlotListChanged();
            return new Size(_canvasWidth + _borderThicknessX, _canvasHeight + _borderThicknessY);
        }

        public void Initialize()
        {
            _viewModel = (DataContext as WorldViewModel);
            if (_viewModel == null) return;
            _viewModel.PropertyChanged += InvokePropertyChangedCallback;
        }

        private void InvokePropertyChangedCallback(object sender, PropertyChangedEventArgs propertyChangedEventArgs)
        {
            if (_onPropChange.ContainsKey(propertyChangedEventArgs.PropertyName))
            {
                _onPropChange[propertyChangedEventArgs.PropertyName]();
            }
        }

        private void OnPlotListChanged()
        {
            if (_viewModel == null || _viewModel.PlotList == null) return;

            var canvas = new Canvas
                             {
                                 Width = _canvasWidth,
                                 Height = _canvasHeight,
                                 Clip = new RectangleGeometry {Rect = new Rect(0, 0, _canvasWidth, _canvasHeight)}
                             };

            if (CurrentPlotCanvas != null)
            {
                CurrentPlotCanvas.Children.Clear();
            }

            var firstHex = _viewModel.PlotList[0];
            var totalPlots = _viewModel.PlotList.Count;
            _bottomLeftHex = new Point(firstHex.LocX, firstHex.LocY);

            var plotDict = _viewModel.PlotList.ToDictionary(p => p.LocX + "," + p.LocY);
            foreach (var plotInfo in _viewModel.PlotList)
            {
                //Debug.WriteLine("Plot ({0},{1}): {2}", plotInfo.LocX, plotInfo.LocY, plotInfo.Type);

                var newPlot = new Plot
                {
                    Size = _hexSideLen, 
                    Type = plotInfo.Type,
                    CenterPlot = (plotInfo == _viewModel.PlotList[totalPlots/2]),
                };

                ToolTipService.SetToolTip(newPlot, new PlotInfoControl()
                                                       {
                                                           DataContext = new PlotInfoViewModel()
                                                                             {
                                                                                 PlotInfo = plotInfo
                                                                             }
                                                       });

                canvas.Children.Add(newPlot);

                var canvasPoint = GetCanvasPointForPlot(plotInfo);
                Canvas.SetLeft(newPlot, canvasPoint.X);
                Canvas.SetTop(newPlot, canvasPoint.Y);

                var currentPlotCanvasPoint = new Point();
                var plotForCurrent = new Plot();
                if (newPlot.CenterPlot)
                {
                    Canvas.SetZIndex(newPlot, (int)WorldZIndex.Plot);
                    
                    //Update the CurrentPlotCanvas displayed on the CharacterControl
                    if (CurrentPlotCanvas != null)
                    {
                        var size = CurrentPlotCanvas.Height/2 - 5;
                        plotForCurrent = new Plot()
                                             {
                                                 Size = size,
                                                 Type = plotInfo.Type,
                                             };
                        CurrentPlotCanvas.Children.Add(plotForCurrent);

                        currentPlotCanvasPoint.X = CurrentPlotCanvas.Width/2;
                        currentPlotCanvasPoint.Y = CurrentPlotCanvas.Height/2;
                        Canvas.SetLeft(plotForCurrent, currentPlotCanvasPoint.X);
                        Canvas.SetTop(plotForCurrent, currentPlotCanvasPoint.Y);
                    }
                }

                foreach (var charInfo in plotInfo.Characters)
                {
                    DrawCharacter(canvas, charInfo, canvasPoint, plotInfo.ConstructionType);
                }

                if (IsRoad(plotInfo.ConstructionType))
                {
                    DrawRoads(canvas, plotDict, canvasPoint, plotInfo, newPlot.MinDiameter);
                    if (newPlot.CenterPlot && (CurrentPlotCanvas != null))
                    {
                        DrawRoads(CurrentPlotCanvas, plotDict, currentPlotCanvasPoint, plotInfo, plotForCurrent.MinDiameter);
                    }
                }

                DrawConstruction(canvas, canvasPoint, plotInfo.ConstructionType, newPlot.Size);
                if (newPlot.CenterPlot && (CurrentPlotCanvas != null))
                {
                    DrawConstruction(CurrentPlotCanvas, currentPlotCanvasPoint, plotInfo.ConstructionType, plotForCurrent.Size);
                }
            }
            
            if (_viewModel.MoveInterfaceEnabled)
            {
                var moveCanvas = ShowMoveInterface();
                canvas.Children.Add(moveCanvas);
                Canvas.SetZIndex(moveCanvas, (Int32)WorldZIndex.MoveInterface);
            }

            LayoutRoot.Child = canvas;
        }

        private void DrawConstruction(Canvas canvas, Point canvasPoint, ConstructionType construction, double size)
        {
            switch(construction)
            {
                case ConstructionType.Quarry:
                    var quarry = new Quarry() { Size = size };
                    canvas.Children.Add(quarry);
                    Canvas.SetLeft(quarry, canvasPoint.X);
                    Canvas.SetTop(quarry, canvasPoint.Y);
                    Canvas.SetZIndex(quarry, (int) WorldZIndex.IsoObjectStart + (int)(canvasPoint.Y));
                    break;
            }
        }

        private Canvas ShowMoveInterface()
        {
            var moveCanvas = new Canvas()
            {
                Width = _canvasWidth,
                Height = _canvasHeight,
                Background = new SolidColorBrush(Colors.Transparent)
            };

            foreach (var plotInfo in _viewModel.PlotList)
            {
                var actualMoveCost = (plotInfo.MoveCost <= _viewModel.ActionPoints) ? plotInfo.MoveCost : null;
                var newPlot = new Plot
                                  {
                                      Size = _hexSideLen,
                                      Type = PlotType.MoveInterface,
                                      MoveCost = actualMoveCost
                                  };

                if (actualMoveCost.GetValueOrDefault(0) != 0)
                {
                    var info = plotInfo;
                    newPlot.MouseLeftButtonUp += (s, e) => _viewModel.MoveCharacter(info);
                }

                moveCanvas.Children.Add(newPlot);

                var canvasPoint = GetCanvasPointForPlot(plotInfo);
                Canvas.SetLeft(newPlot, canvasPoint.X);
                Canvas.SetTop(newPlot, canvasPoint.Y);

                if (plotInfo.MoveCost.GetValueOrDefault(0) != 0)
                {
                    var costText = new TextBlock
                                       {
                                           Text = plotInfo.MoveCost.ToString(),
                                           FontSize = 20,
                                           Foreground = new SolidColorBrush(Colors.White),
                                           IsHitTestVisible = false
                                       };
                    moveCanvas.Children.Add(costText);
                    Canvas.SetLeft(costText, canvasPoint.X - 15);
                    Canvas.SetTop(costText, canvasPoint.Y - 15);
                }
            }

            return moveCanvas;
        }

        private void DrawRoads(Canvas canvas, Dictionary<string, PlotInfo> plotDict, Point canvasPoint, PlotInfo plotInfo, double length)
        {
            var road = new Road { Length = length, Type = plotInfo.ConstructionType };

            var dirs = new HashSet<Road.Direction>();
            foreach (var roadDirEntry in _roadDirections)
            {
                var adjPlot = (plotInfo.LocX + roadDirEntry.LocX) + "," + (plotInfo.LocY + roadDirEntry.LocY);
                if (plotDict.ContainsKey(adjPlot) && IsRoad(plotDict[adjPlot].ConstructionType))
                {
                    dirs.Add(roadDirEntry.RoadDir);
                }
            }

            road.SetRoadDirections(dirs);

            canvas.Children.Add(road);
            Canvas.SetLeft(road, canvasPoint.X);
            Canvas.SetTop(road, canvasPoint.Y);
            Canvas.SetZIndex(road, (int)WorldZIndex.Road);
        }

        private bool IsRoad(ConstructionType constructionType)
        {
            return ((constructionType == ConstructionType.Road) ||
                    (constructionType == ConstructionType.StoneRoad));
        }


        private void DrawCharacter(Canvas canvas, CharacterInfo charInfo, Point canvasPoint, ConstructionType consType)
        {
            Tuple<double, double> randVals;
            if (_characterOffset.ContainsKey(charInfo.Name))
            {
                randVals = _characterOffset[charInfo.Name];
            }
            else
            {
                randVals = Tuple.Create(_rand.NextDouble(), _rand.NextDouble());
                _characterOffset[charInfo.Name] = randVals;
            }

            var angle = (consType == ConstructionType.Quarry) ? (Math.PI) : (Math.PI*2);
            var offset = Complex.FromPolarCoordinates(randVals.Item1 * _hexMinDiam / 2 * .9,
                                                      randVals.Item2 * angle);
            
            canvasPoint.X += offset.Real;
            canvasPoint.Y += offset.Imaginary;

            var character = new Character();
            character.SetHeight(_hexSideLen);
            canvas.Children.Add(character);
            Canvas.SetLeft(character, canvasPoint.X);
            Canvas.SetTop(character, canvasPoint.Y);
            Canvas.SetZIndex(character, (int)canvasPoint.Y+(int)WorldZIndex.IsoObjectStart);
        }

        private Point GetCanvasPointForPlot(PlotInfo plotInfo)
        {
            var x = (plotInfo.LocX - _bottomLeftHex.X)/2 * _hexMinDiam;
            var y = _canvasHeight - (plotInfo.LocY - _bottomLeftHex.Y)*_hexSideLen*1.5;

            return new Point(x,y);
        }
    }
}
