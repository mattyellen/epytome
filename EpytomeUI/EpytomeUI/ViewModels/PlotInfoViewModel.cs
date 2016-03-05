using System;
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
    public class PlotInfoViewModel : Notifier
    {
        private PlotInfo _plotInfo;
        public PlotInfo PlotInfo
        {
            get { return _plotInfo; }
            set
            {
                _plotInfo = value; 
                Notify(() => PlotInfo);
                Notify(() => HasCharacters);
            }
        }

        public bool HasCharacters
        {
            get 
            { 
                return (PlotInfo != null) && 
                       (PlotInfo.Characters != null) && 
                       (PlotInfo.Characters.Any()); 
            }
        }


    }
}
