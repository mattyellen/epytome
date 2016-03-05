using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
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
    public class InventoryItemObject
    {
        public InventoryItem Type { get; set; }
    }

    public class InventoryViewModel : Notifier
    {
        public InventoryViewModel()
        {
            Items = new ObservableCollection<InventoryItemObject>();
            SelectedItems = new List<InventoryItemObject>();
        }

        public void SetItems(IEnumerable<InventoryItem> items)
        {
            Items = new ObservableCollection<InventoryItemObject>(from i in items select new InventoryItemObject() {Type = i});
        }

        private ObservableCollection<InventoryItemObject> _items;
        public ObservableCollection<InventoryItemObject> Items
        {
            get { return _items; }
            set { _items = value; Notify(() => Items); }
        }

        public List<InventoryItemObject> SelectedItems { get; set; }

        public void Add(IEnumerable<InventoryItemObject> addItems)
        {
            foreach(var item in addItems)
            {
                Items.Add(item);
            }
        }

        public void Remove(IEnumerable<InventoryItemObject> removeItems)
        {
            foreach(var item in removeItems)
            {
                Items.Remove(item);
            }
        }
    }
}
