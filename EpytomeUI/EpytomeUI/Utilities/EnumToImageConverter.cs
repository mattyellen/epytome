using System;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.Utilities
{
    public class EnumToImageConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (!(value is Enum)) return string.Empty;

            var valueEnum = (Enum) value;
            var image = valueEnum.GetAttributeValue<DisplayAttribute, string>(a => a.Image) ??
                        valueEnum.ToString().ToLower() + ".png";

            return "/" + image;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    public class EnumToNameConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (!(value is Enum)) return string.Empty;

            var valueEnum = (Enum)value;
            var image = valueEnum.GetAttributeValue<DisplayAttribute, string>(a => a.Name) ??
                        valueEnum.ToString();

            return image;
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }

    internal static class AttributeHelper
    {
        public static TExpected GetAttributeValue<TAttribute, TExpected>(this Enum enumeration, Func<TAttribute, TExpected> expression)
            where TAttribute : Attribute
        {
            TAttribute attribute = enumeration.GetType().GetMember(enumeration.ToString())[0].GetCustomAttributes(typeof(TAttribute), false).Cast<TAttribute>().SingleOrDefault();

            if (attribute == null)
                return default(TExpected);

            return expression(attribute);
        }
    }
}
