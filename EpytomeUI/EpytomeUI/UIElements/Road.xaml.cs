using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.UIElements
{
    public partial class Road : UserControl
    {
        public enum Direction
        {
            East,
            SouthEast,
            SouthWest,
            West,
            NorthWest,
            NorthEast
        }

        public Road()
        {
            InitializeComponent();
            CenterImageFile = "/dirt_road_center.png";
        }

        private string CenterImageFile { get; set; }

        private double _length;
        public double Length
        {
            get { return _length; }
            set
            {
                _length = value;
                var scale = _length / (LayoutRoot.Width - 2);
                _roadCanvasScaleTransform.ScaleX = scale;
                _roadCanvasScaleTransform.ScaleY = scale;
            }
        }

        private ConstructionType _type;
        public ConstructionType Type
        {
            get { return _type; }
            set
            {
                _type = value;
                var centerImage = string.Empty;
                var roadImage = string.Empty;
                var centerMargin = new Thickness();
                switch(_type)
                {
                    case ConstructionType.Road:
                        centerImage = "/dirt_road_center.png";
                        roadImage = "/dirt_road.png";
                        centerMargin = new Thickness(-11,-11,0,0);
                        break;
                    case ConstructionType.StoneRoad:
                        centerImage = "/stone_road_center.png";
                        roadImage = "/stone_road.png";
                        centerMargin = new Thickness(-17,-18,0,0);
                        break;
                }

                _centerRoad.Source = GetBitmap(centerImage);
                _centerRoad.Margin = centerMargin;
                _eastRoad.Source = GetBitmap(roadImage);
                _southEastRoad.Source = GetBitmap(roadImage);
                _southWestRoad.Source = GetBitmap(roadImage);
                _westRoad.Source = GetBitmap(roadImage);
                _northWestRoad.Source = GetBitmap(roadImage);
                _northEastRoad.Source = GetBitmap(roadImage);
            }
        }

        private static ImageSource GetBitmap(string file)
        {
            return new BitmapImage(new Uri(file, UriKind.Relative));
        }

        public void SetRoadDirections(ISet<Direction> dirs)
        {
            _eastRoad.Visibility = dirs.Contains(Direction.East) ? Visibility.Visible : Visibility.Collapsed;
            _southEastRoad.Visibility = dirs.Contains(Direction.SouthEast) ? Visibility.Visible : Visibility.Collapsed;
            _southWestRoad.Visibility = dirs.Contains(Direction.SouthWest) ? Visibility.Visible : Visibility.Collapsed;
            _westRoad.Visibility = dirs.Contains(Direction.West) ? Visibility.Visible : Visibility.Collapsed;
            _northWestRoad.Visibility = dirs.Contains(Direction.NorthWest) ? Visibility.Visible : Visibility.Collapsed;
            _northEastRoad.Visibility = dirs.Contains(Direction.NorthEast) ? Visibility.Visible : Visibility.Collapsed;
        }


    }
}
