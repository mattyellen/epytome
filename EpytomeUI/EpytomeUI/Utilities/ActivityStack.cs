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
using EpytomeUI.Controls;

namespace EpytomeUI.Utilities
{
    public class ActivityStack<TActivityWindow> where TActivityWindow : ActivityWindow, new()
    {
        private int _activityCount;
        public event EventHandler Changed;
        private TActivityWindow _activityWindow;

        public void InvokeChanged()
        {
            var handler = Changed;
            if (handler != null) handler(this, EventArgs.Empty);
        }

        private bool _state;
        private bool State
        {
            get { return _state; }
            set
            {
                if (_state != value)
                {
                    _state = value;
                    if (_state)
                    {
                        _activityWindow = new TActivityWindow();
                        _activityWindow.Show();
                    }
                    else
                    {
                        _activityWindow.Close();
                    }

                    InvokeChanged();
                }
            }
        }

        public static implicit operator bool(ActivityStack<TActivityWindow> activityStack)
        {
            return activityStack.State;
        }

        public void Push()
        {
            _activityCount++;
            State = (_activityCount > 0);
        }

        public void Pop()
        {
            if (_activityCount > 0)
            {
                _activityCount--;
            }
            State = (_activityCount > 0);
        }
    }
}
