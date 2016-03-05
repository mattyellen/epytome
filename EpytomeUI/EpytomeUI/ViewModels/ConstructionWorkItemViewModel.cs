using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using EpytomeUI.Utilities;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.ViewModels
{
    public class ConstructionWorkItemViewModel : Notifier
    {
        private InventoryItem _resource;
        public InventoryItem Resource
        {
            get { return _resource; }
            set { _resource = value; Notify(() => Resource);
            }
        }

        private int _cost;
        public int Cost
        {
            get { return _cost; }
            set { _cost = value; Notify(() => Cost);}
        }
    }
}
