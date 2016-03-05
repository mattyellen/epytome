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
using EpytomeUI.Utilities;
using EpytomeUI.ViewModels;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.Controls
{
    public partial class CharacterControl : UserControl
    {
        public Canvas CurrentPlotCanvas
        {
            get { return _currentPlotCanvas; }
        }

        public CharacterControl()
        {
            InitializeComponent();
            _constructionWorkControl.DoWork = DoConstructionWork;
        }

        private void DoConstructionWork(int ap)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel != null)
            {
                viewModel.DoConstructionWork(ap);
            }
        }

        private void JoinWorld(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel != null)
            {
                viewModel.JoinWorld();
            }
        }

        private void StartConstruction(object sender, RoutedEventArgs e)
        {
            var selectedItem = (StackPanel) _startConstructionChooser.SelectedItem;
            var constructionType = (ConstructionType) Enum.Parse(typeof(ConstructionType), (string)selectedItem.Tag, false);
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.StartConstruction(constructionType);
        }

        private void EnableMoveInterface(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.MoveInterfaceEnabled = true;
            GlobalEvents.SetMoveInterface.Invoke(true, viewModel.ActionPoints);
        }

        private void DisableMoveInterface(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.MoveInterfaceEnabled = false;
            GlobalEvents.SetMoveInterface.Invoke(false, viewModel.ActionPoints);
        }

        private void GatherResources(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.GatherResources();
        }

        private void PickUpItems(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.PickUpItems(from InventoryItemObject i in _plotInventoryListBox.SelectedItems
                                  select i);
        }

        private void DropItems(object sender, RoutedEventArgs e)
        {
            var viewModel = DataContext as CharacterControlViewModel;
            if (viewModel == null) return;

            viewModel.DropItems(from InventoryItemObject i in _characterInventoryListBox.SelectedItems 
                                select i);
        }
    }
}
