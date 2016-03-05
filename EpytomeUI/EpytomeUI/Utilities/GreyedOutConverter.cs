using System;
using System.Globalization;
using System.Windows.Data;

namespace EpytomeUI.Utilities
{
    public class GreyedOutConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var boolean = (value is bool ? (bool) value : false);
            return boolean ? .4 : 1;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            return value;
        }
    }
}