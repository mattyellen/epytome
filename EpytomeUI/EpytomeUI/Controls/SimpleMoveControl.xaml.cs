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
using EpytomeUI.ViewModels;

namespace EpytomeUI.Controls
{
    public partial class SimpleMoveControl : UserControl
    {
        public SimpleMoveControl()
        {
            InitializeComponent();
        }

        private void MoveButtonClick(object sender, RoutedEventArgs e)
        {
            var button = sender as Button;
            if (button == null) return;

            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            var dir = (string)button.Content;
            viewModel.Move(dir);
        }
    }
}
