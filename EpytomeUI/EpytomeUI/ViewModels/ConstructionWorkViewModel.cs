using System;
using System.Collections.Generic;
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
    public class ConstructionWorkViewModel : Notifier
    {
        private ConstructionType _targetConstruction;
        public ConstructionType TargetConstruction
        {
            get { return _targetConstruction; }
            set { _targetConstruction = value; Notify(() => TargetConstruction);}
        }

        private ConstructionWorkItemViewModel _currentResource;
        public ConstructionWorkItemViewModel CurrentResource
        {
            get { return _currentResource; }
            set { _currentResource = value; Notify(() => CurrentResource);}
        }

        private List<ConstructionWorkItemViewModel> _nextResources;
        public List<ConstructionWorkItemViewModel> NextResources
        {
            get { return _nextResources; }
            set { _nextResources = value; Notify(() => NextResources);}
        }


        public ConstructionWorkViewModel()
        {
            NextResources = new List<ConstructionWorkItemViewModel>();
        }

        public void SetTargetAndResourceList(ConstructionUpgradeInfo upgradeInfo)
        {
            TargetConstruction = upgradeInfo.UpgradeType;
            if (TargetConstruction == ConstructionType.None)
            {
                CurrentResource = null;
                NextResources = null;
                return;
            }

            var resourceList = (from r in upgradeInfo.UpgradeResources
                                select new ConstructionWorkItemViewModel()
                                           {
                                               Resource = r.Resource,
                                               Cost = r.CostToAdd
                                           }).ToList();

            if (upgradeInfo.FirstIsCurrent)
            {
                CurrentResource = resourceList.First();
                resourceList.RemoveAt(0);
            }
            else
            {
                CurrentResource = null;
            }

            NextResources = resourceList;
        }
    }
}
