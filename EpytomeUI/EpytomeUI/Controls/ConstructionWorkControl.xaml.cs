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

namespace EpytomeUI.Controls
{
    public partial class ConstructionWorkControl : UserControl
    {
        public delegate void DoWorkDelegate(int ap);
        public DoWorkDelegate DoWork { get; set; }

        public ConstructionWorkControl()
        {
            InitializeComponent();
        }

        private void ContributeActionPoints(object sender, RoutedEventArgs e)
        {
            if (DoWork != null)
            {
                int actionPoints;
                if (int.TryParse(_actionPoints.Text, out actionPoints))
                {
                    DoWork(actionPoints);
                }
            }

            _actionPoints.Text = string.Empty;
        }
    }
}
