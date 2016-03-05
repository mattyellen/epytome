using System;
using System.Collections.Generic;
using System.Diagnostics;
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
using EpytomeUI.WebApi;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.ViewModels
{
    public class WorldViewModel : Notifier
    {
        private long _worldId;

        private List<PlotInfo> _plotList;
        public List<PlotInfo> PlotList
        {
            get { return _plotList; }
            set { _plotList = value; Notify(() => PlotList); }
        }

        public bool MoveInterfaceEnabled { get; set; }
        public int ActionPoints { get; set; }

        public void Initialize(long worldId)
        {
            _worldId = worldId;
            GlobalEvents.WorldViewChanged.Register(Refresh);
            GlobalEvents.SetMoveInterface.Register((b, ap) =>
                                             {
                                                 MoveInterfaceEnabled = b;
                                                 ActionPoints = ap;
                                                 Refresh();
                                             });
            Refresh();
        }

        public void Refresh()
        {
            var request = new GetWorldViewRequest {WorldId = _worldId};
            new GetWorldViewCommand {ReplyCallback = OnGetWorldViewReply}.Send(request);
        }

        private void OnGetWorldViewReply(GetWorldViewReply reply)
        {
            PlotList = reply.WorldView;
        }

        public void MoveCharacter(PlotInfo info)
        {
            MoveInterfaceEnabled = false;
            var request = new MoveCharacterRequest()
                              {
                                  WorldId = _worldId,
                                  TargetX = info.LocX,
                                  TargetY = info.LocY,
                                  ExpectedCost = info.MoveCost.GetValueOrDefault(0)
                              };
            new MoveCharacterCommand() { ReplyCallback = OnMoveCharacterReply, ErrorCallback = OnMoveCharacterReply }.Send(request);
        }

        private void OnMoveCharacterReply(ReplyBase replyBase)
        {
            GlobalEvents.CharacterMoveCompleted.Invoke();
            Refresh();
        }
    }
}
