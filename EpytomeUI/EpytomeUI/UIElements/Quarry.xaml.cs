using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;

namespace EpytomeUI.UIElements
{
    public partial class Quarry : UserControl
    {
        private readonly double HalfSqrt3 = .5 * Math.Sqrt(3);

        public Quarry()
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
            return new Point(x * Size, y * Size);
        }

    }
}
