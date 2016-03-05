using System;
using System.ComponentModel;
using EpytomeUI.Controls;
using EpytomeUI.ViewModels;
using EpytomeUI.WebApi;
using EpytomeUI.Utilities;
using EpytomeUI.WebApi.World;

namespace EpytomeUI
{
    public class MainPageViewModel : Notifier
    {
        public static ActivityStack<ActivityWindow> GlobalActivityStack = new ActivityStack<ActivityWindow>();

        private UserInfoViewModel _userInfoVM;
        public UserInfoViewModel UserInfoVM
        {
            get { return _userInfoVM; }
            set { _userInfoVM = value; Notify(() => UserInfoVM); }
        }

        private CharacterControlViewModel _characterControlVM;
        public CharacterControlViewModel CharacterControlVM
        {
            get { return _characterControlVM; }
            set { _characterControlVM = value; Notify(() => CharacterControlVM);}
        }

        private WorldViewModel _worldVM;
        public WorldViewModel WorldVM
        {
            get { return _worldVM; }
            set { _worldVM = value; Notify(() => WorldVM);}
        }

        public void Initialize()
        {
            UserInfoVM = new UserInfoViewModel();
            UserInfoVM.Initialize();

            CharacterControlVM = new CharacterControlViewModel();
            WorldVM = new WorldViewModel();
            new GetAllWorldsCommand {ReplyCallback = OnGetAllWorldsReply}.Send(new GetAllWorldsRequest());
        }

        private void OnGetAllWorldsReply(GetAllWorldsReply reply)
        {
            if (reply.Worlds.Count == 0) return;

            var worldId = reply.Worlds[0].Id;
            CharacterControlVM.Initialize(worldId);
            WorldVM.Initialize(worldId);
        }
    }

}
