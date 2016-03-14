using System;
using System.Collections;
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
using EpytomeUI.Controls;
using EpytomeUI.Utilities;
using EpytomeUI.WebApi;
using EpytomeUI.WebApi.World;

namespace EpytomeUI.ViewModels
{
    public class CharacterControlViewModel : Notifier
    {
        public CharacterControlViewModel()
        {
            Initialized = false;
            CharacterInventory = new InventoryViewModel();
            PlotInventory = new InventoryViewModel();
            ConstructionWork = new ConstructionWorkViewModel();
        }

        private bool _initialized;
        public bool Initialized
        {
            get { return _initialized; }
            set { _initialized = value; Notify(() => Initialized);}
        }

        private bool _characterInWorld;
        public bool CharacterInWorld
        {
            get { return _characterInWorld; }
            set { _characterInWorld = value; Notify(() => CharacterInWorld); }
        }

        private string _name;
        public string Name
        {
            get { return _name; }
            set { _name = value; Notify(() => Name); }
        }

        private string _class;
        public string Class
        {
            get { return _class; }
            set { _class = value; Notify(() => Class); }
        }

        public string ActionPointsString
        {
            get { return ActionPoints + " / " + MaxActionPoints; }
        }

        private int _actionPoints;
        public int ActionPoints
        {
            get { return _actionPoints; }
            set
            {
                _actionPoints = value;
                Notify(() => ActionPoints);
                Notify(() => ActionPointsString);
            }
        }

        private float _maxActionPoints;
        public float MaxActionPoints
        {
            get { return _maxActionPoints; }
            set { _maxActionPoints = value; Notify(() => ActionPointsString);}
        }

        public string LocationString
        {
            get { return LocX + "," + LocY; }
        }

        private int _locX;
        public int LocX
        {
            get { return _locX; }
            set { _locX = value; Notify(() => LocationString); }
        }

        private int _locY;
        public int LocY
        {
            get { return _locY; }
            set { _locY = value; Notify(() => LocationString); }
        }

        private string _newCharacterName;
        public string NewCharacterName
        {
            get { return _newCharacterName; }
            set { _newCharacterName = value; Notify(() => NewCharacterName);}
        }

        private bool _moveInterfaceEnabled;
        public bool MoveInterfaceEnabled
        {
            get { return _moveInterfaceEnabled; }
            set 
            { 
                _moveInterfaceEnabled = value; 
                Notify(()=>MoveInterfaceEnabled);
            }
        }

        //TODO: refactor this into it's own plot info view model?
        private int? _resourceAmount;
        public int? ResourceAmount
        {
            get { return _resourceAmount; }
            set
            {
                _resourceAmount = value;
                Notify(() => ResourceAmount);
                Notify(() => HasResources);
            }
        }

        public bool HasResources {get { return (ResourceAmount != null); }}
        public bool HasUpgrade { get { return ConstructionWork.TargetConstruction != ConstructionType.None; } }

        public InventoryViewModel CharacterInventory { get; private set; }

        public InventoryViewModel PlotInventory { get; private set; }

        public ConstructionWorkViewModel ConstructionWork { get; private set; }

        private long _worldId;
        public void Initialize(long worldId)
        {
            Initialized = true;
            _worldId = worldId;
            GlobalEvents.CharacterMoveCompleted.Register(() =>
                                                             {
                                                                 MoveInterfaceEnabled = false;
                                                                 Refresh();
                                                             });
            Refresh();
        }

        public void Refresh()
        {
            var charReq = new GetCharacterInfoRequest {WorldId = _worldId};
            new GetCharacterInfoCommand 
            {
                ReplyCallback = OnGetCharacterInfoReply,
                ErrorCallback = SuppressErrors,
            }.Send(charReq);

            var plotReq = new GetCurrentPlotInfoRequest() { WorldId = _worldId };
            new GetCurrentPlotInfoCommand()
            {
                ReplyCallback = OnGetCurrentPlotInfoReply,
                ErrorCallback = SuppressErrors
            }.Send(plotReq);
        }

        private void SuppressErrors(ReplyBase reply)
        {
            if (reply.Status == CommandStatus.UserNotLoggedIn ||
                reply.Status == CommandStatus.CharacterNotFound)
            {
                //This one is okay.  Suppress the error popup
                reply.ShowErrorWindow = false;
            }
        }

        private void OnGetCharacterInfoReply(GetCharacterInfoReply reply)
        {
            CharacterInWorld = reply.CharacterInWorld;
            Name = reply.Name;
            Class = reply.Class;
            ActionPoints = reply.ActionPoints;
            MaxActionPoints = reply.MaxActionPoints;
            LocX = reply.LocX;
            LocY = reply.LocY;
            if (reply.CharacterInWorld)
            {
                CharacterInventory.SetItems(reply.Inventory);
            }
        }

        private void OnGetCurrentPlotInfoReply(GetCurrentPlotInfoReply reply)
        {
            if (reply.ConstructionInfo == null) ResourceAmount = null;
            if (reply.ConstructionInfo is ResourceConstructionInfo)
            {
                ResourceAmount = ((ResourceConstructionInfo) reply.ConstructionInfo).Amount;
            }

            PlotInventory.SetItems(reply.Inventory);
            ConstructionWork.SetTargetAndResourceList(reply.UpgradeInfo);
            Notify(() => HasUpgrade);
        }

        public void JoinWorld()
        {
            var confirm = new JoinWorldConfirmationWindow();
            confirm.CharacterName.Text = NewCharacterName;
            confirm.JoinButton.Click += JoinWorldConfirmed;
            confirm.Show();
        }

        private void JoinWorldConfirmed(object sender, RoutedEventArgs e)
        {
            var request = new JoinWorldRequest
            {
                NewCharacterName = NewCharacterName,
                WorldId = _worldId
            };
            new JoinWorldCommand { ReplyCallback = OnJoinWorldReply }.Send(request);
        }

        private void OnJoinWorldReply(JoinWorldReply obj)
        {
            Refresh();
            GlobalEvents.WorldViewChanged.Invoke();
        }

        public void Move(string dir)
        {
            var request = new MoveCharacterRequest {WorldId = _worldId};
            switch(dir)
            {
                case "NW":
                    request.TargetX = LocX - 1;
                    request.TargetY = LocY + 1;
                    break;
                case "NE":
                    request.TargetX = LocX + 1;
                    request.TargetY = LocY + 1;
                    break;
                case "W":
                    request.TargetX = LocX - 2;
                    request.TargetY = LocY;
                    break;
                case "E":
                    request.TargetX = LocX + 2;
                    request.TargetY = LocY;
                    break;
                case "SW":
                    request.TargetX = LocX - 1;
                    request.TargetY = LocY - 1;
                    break;
                case "SE":
                    request.TargetX = LocX + 1;
                    request.TargetY = LocY - 1;
                    break;
            }

            new MoveCharacterCommand() {ReplyCallback = OnMoveCharacterReply}.Send(request);
        }

        private void OnMoveCharacterReply(MoveCharacterReply reply)
        {
            Refresh();
            GlobalEvents.WorldViewChanged.Invoke();
        }

        public void StartConstruction(ConstructionType constructionType)
        {
            var request = new StartConstructionRequest
                              {
                                  WorldId = _worldId,
                                  Type = constructionType
                              };
            new StartConstructionCommand {ReplyCallback = OnStartConstructionReply}.Send(request);
        }

        private void OnStartConstructionReply(StartConstructionReply obj)
        {
            Refresh();
            GlobalEvents.WorldViewChanged.Invoke();
        }

        public void GatherResources()
        {
            var request = new GatherResourcesRequest() {WorldId = _worldId};
            new GatherResourcesCommand() { ReplyCallback = OnGatherResourcesReply }.Send(request);
        }

        private void OnGatherResourcesReply(GatherResourcesReply obj)
        {
            Refresh();
        }

        public void PickUpItems(IEnumerable<InventoryItemObject> items)
        {
            new TransferItemsCommand()
                {
                    ReplyCallback = r =>
                                        {
                                            CharacterInventory.Add(items);
                                            PlotInventory.Remove(items);
                                        }
                }.Send(new TransferItemsRequest()
                           {
                               WorldId = _worldId,
                               Items = (from i in items select i.Type).ToList(),
                               Direction = TransferDirection.PickUp
                           });
        }

        public void DropItems(IEnumerable<InventoryItemObject> items)
        {
            new TransferItemsCommand()
            {
                ReplyCallback = r =>
                {
                    PlotInventory.Add(items);
                    CharacterInventory.Remove(items);
                }
            }.Send(new TransferItemsRequest()
            {
                WorldId = _worldId,
                Items = (from i in items select i.Type).ToList(),
                Direction = TransferDirection.Drop
            });
        }

        public void DoConstructionWork(int ap)
        {
            new DoConstructionWorkCommand()
            {
                ReplyCallback = OnDoContructionWorkReply
            }.Send(new DoConstructionWorkRequest()
            {
                ActionPoints = ap,
                WorldId = _worldId
            });
        }

        private void OnDoContructionWorkReply(DoConstructionWorkReply reply)
        {
            Refresh();
            if (reply.Completed)
            {
                GlobalEvents.WorldViewChanged.Invoke();
            }
        }
    }
}
