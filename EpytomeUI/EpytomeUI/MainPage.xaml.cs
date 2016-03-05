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
using EpytomeUI.Controls;
using EpytomeUI.Utilities;
using EpytomeUI.WebApi;
using EpytomeUI.WebApi.World;

namespace EpytomeUI
{
    public partial class MainPage : UserControl
    {
        private MainPageViewModel ViewModel { get; set; }

        public MainPage()
        {
            InitializeComponent();

            //This is pretty obnoxious, but it was the best I could figure out.
            //This canvas is displayed on the CharacterControl but needs to be
            //updated by the WorldControl.
            WorldControl.CurrentPlotCanvas = CharacterControl.CurrentPlotCanvas;

            ViewModel = new MainPageViewModel();
            LayoutUpdated += OnMainPageLayoutUpdated;
        }

        private void OnMainPageLayoutUpdated(object sender, EventArgs e)
        {
            //var leftPadWidth = (ActualWidth - (_characterControlColumn.Width.Value + WorldControl.ActualWidth)) / 2;
            //if (leftPadWidth < 0)
            //    leftPadWidth = 0;

            //_leftColumnPadding.Width = new GridLength(leftPadWidth);
        }

        private void OnLoaded(object sender, RoutedEventArgs e)
        {
            ViewModel.Initialize();
            DataContext = ViewModel;

            WorldControl.Initialize();
        }

        private void ResetWorld(object sender, RoutedEventArgs e)
        {
            var confirmWin = new GenericConfirmationWindow();
            confirmWin.Closed += OnResetWorldConfirmationClosed;
            confirmWin.Show();
        }

        private void OnResetWorldConfirmationClosed(object sender, EventArgs e)
        {
            var confirm = sender as GenericConfirmationWindow;
            if ((confirm != null) &&
                (confirm.DialogResult.GetValueOrDefault(false)))
            {
                new ResetWorldCommand().Send(new ResetWorldRequest());
            }
        }
    }
}
