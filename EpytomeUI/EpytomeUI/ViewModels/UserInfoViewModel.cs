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
using EpytomeUI.Utilities;
using EpytomeUI.WebApi;

namespace EpytomeUI.ViewModels
{
    public class UserInfoViewModel : Notifier
    {
        public event EventHandler UserInfoUpdated;

        private bool _initialized;
        public bool Initialized
        {
            get { return _initialized; }
            set { _initialized = value; Notify(() => Initialized); }
        }

        private UserInfoReply _userInfo;
        public UserInfoReply UserInfo
        {
            get { return _userInfo; }
            set
            {
                _userInfo = value; 
                Notify(() => UserInfo);
                Notify(() => IsMattYellen);
            }
        }

        private string _displayUser;
        public string DisplayUser
        {
            get { return _displayUser; }
            set { _displayUser = value; Notify(() => DisplayUser); }
        }

        public bool IsMattYellen
        {
            get
            {
                return ((UserInfo != null) && (UserInfo.UserEmail == "matt.yellen@gmail.com"));
            }
        }

        public void Initialize()
        {
            Refresh();
        }

        private void Refresh()
        {
            if (Application.Current.Host.Source == null)
                return;

            var request = new UserInfoRequest()
                              {
                                  DestinationUrl = AppUtils.BaseUri
                              };
            new UserInfoCommand() { ReplyCallback = OnUserInfoReply }.Send(request);
        }

        private void OnUserInfoReply(UserInfoReply reply)
        {
            UserInfo = reply;
            DisplayUser = UserInfo.UserNickname != null ?
                String.Format("({0})", UserInfo.UserNickname) :
                "";

            Initialized = true;
            InvokeUserInfoUpdated();
        }

        private string _newNickname;
        public string NewNickname
        {
            get { return _newNickname; }
            set { _newNickname = value; Notify(() => NewNickname); }
        }

        public void SetNickname()
        {
            new UpdateUserInfoCommand(){ReplyCallback = OnSetNicknameReply}.Send(new UpdateUserInfoRequest() { NewNickname = NewNickname });
            NewNickname = "";
        }

        private void OnSetNicknameReply(UpdateUserInfoReply obj)
        {
            Refresh();
        }

        public void InvokeUserInfoUpdated()
        {
            var handler = UserInfoUpdated;
            if (handler != null) handler(this, EventArgs.Empty);
        }
    }
}
