using System;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.UIElements
{
    public partial class Plot : UserControl
    {
        private double HalfSqrt3 = .5*Math.Sqrt(3);
        public Plot()
        {
            InitializeComponent();
        }

        private double _size;
        public double Size
        {
            get { return _size; }
            set
            {
                _size = value;
                SetHexPoints();
            }
        }

        public double MinDiameter
        {
            get { return Size*HalfSqrt3*2; }
        }

        private PlotType _type;
        public PlotType Type
        {
            get { return _type; }
            set 
            { 
                _type = value;
                SetHexTexture();
            }
        }

        private bool _centerPlot;
        public bool CenterPlot
        {
            get { return _centerPlot; }
            set
            {
                _centerPlot = value;
                SetCenterHexIndicator();
            }
        }

        private int? _moveCost;
        public int? MoveCost
        {
            get { return _moveCost; }
            set 
            { 
                _moveCost = value; 
                SetHexTexture();
            }
        }

        private void SetCenterHexIndicator()
        {
            Hexagon.Stroke = new SolidColorBrush(CenterPlot ? Colors.Red : Colors.Black);
            Hexagon.StrokeThickness = CenterPlot ? 2 : 1;
        }

        private void OnHexMouseEnter(object sender, MouseEventArgs e)
        {
            Hexagon.Fill = new SolidColorBrush(new Color() { A = 255, B = 255 });
        }

        private void OnHexMouseLeave(object sender, MouseEventArgs e)
        {
            Hexagon.Fill = new SolidColorBrush(new Color() { A = 128, B = 255 });
        }

        private void SetHexTexture()
        {
            Hexagon.MouseEnter -= OnHexMouseEnter;
            Hexagon.MouseLeave -= OnHexMouseLeave;

            if (Type == PlotType.MoveInterface)
            {
                if (MoveCost.GetValueOrDefault(0) != 0)
                {
                    Hexagon.Fill = new SolidColorBrush(new Color() {A = 128, B = 255});
                    Hexagon.Stroke = new SolidColorBrush(Colors.Blue);
                    Hexagon.MouseEnter += OnHexMouseEnter;
                    Hexagon.MouseLeave += OnHexMouseLeave;
                }
                else
                {
                    Hexagon.Fill = new SolidColorBrush(new Color() { A = 128 });
                    Hexagon.Stroke = new SolidColorBrush(new Color() { A = 128 });
                }
            }
            else
            {
                string imageFile = "";
                switch (Type)
                {
                    case PlotType.Water:
                        imageFile = "/water.png";
                        break;
                    case PlotType.Mountain:
                        imageFile = "/mountain.png";
                        break;
                    case PlotType.Grass:
                        imageFile = "/grass.png";
                        break;
                }

                Hexagon.Fill = new ImageBrush()
                {
                    ImageSource = new BitmapImage(new Uri(imageFile, UriKind.Relative))
                };
            }
        }

        private void SetHexPoints()
        {
            Hexagon.Points = new PointCollection()
                                 {
                                     ScalePoint(-HalfSqrt3,0.5),
                                     ScalePoint(0,1),
                                     ScalePoint(HalfSqrt3,0.5),
                                     ScalePoint(HalfSqrt3,-0.5),
                                     ScalePoint(0,-1),
                                     ScalePoint(-HalfSqrt3,-0.5),
                                 };
        }

        private Point ScalePoint(double x, double y)
        {
            return new Point(x*Size, y*Size);
        }
    }
}
