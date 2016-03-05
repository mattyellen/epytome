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
    public class GlobalEvents
    {
        public static GlobalEvent WorldViewChanged = new GlobalEvent();
        public static GlobalEvent CharacterMoveCompleted = new GlobalEvent();
        public static GlobalEvent<bool, int> SetMoveInterface = new GlobalEvent<bool, int>();
    }

    //Global event with no args
    public class GlobalEvent
    {
        private event Action Event;

        public void Invoke()
        {
            if (Event != null) Event();
        }

        public void Register(Action handler)
        {
            Event += handler;
        }
    }

    //Global event with two args
    public class GlobalEvent<T1, T2>
    {
        private event Action<T1, T2> Event;
        
        public void Invoke(T1 eventArg1, T2 eventArg2)
        {
            if (Event != null) Event(eventArg1, eventArg2);
        }

        public void Register(Action<T1, T2> handler)
        {
            Event += handler;
        }
    }
}


