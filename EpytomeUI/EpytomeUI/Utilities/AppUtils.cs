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

namespace EpytomeUI.Utilities
{
    public class AppUtils
    {
        public static string BaseUri
        {
            get
            {
                var localHost = Application.Current.Host.Source;
                if (localHost == null)
                    throw new Exception("Application.Current.Host.Source == null???");

                return "http://" + localHost.Host + ":" + localHost.Port;
            }
        }
    }
}
