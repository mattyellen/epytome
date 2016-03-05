using System;
using System.Linq.Expressions;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.ComponentModel;

namespace EpytomeUI.Utilities
{
    public class Notifier : INotifyPropertyChanged
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public void Notify<T>(Expression<Func<T>> exp)
        {
            //the cast will always succeed
            var memberExpression = (MemberExpression)exp.Body;
            var propertyName = memberExpression.Member.Name;

            if (PropertyChanged != null)
            {
                PropertyChanged(this, new PropertyChangedEventArgs(propertyName));
            }
        }
    }
}
