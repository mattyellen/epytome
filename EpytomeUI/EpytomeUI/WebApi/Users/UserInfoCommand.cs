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

namespace EpytomeUI.WebApi
{
    public class UserInfoCommand : WebCommandBase<UserInfoRequest, UserInfoReply>
    {
        public UserInfoCommand() : base("UserInfoCommand")
        {
        }
    }

    public class UserInfoReply : ReplyBase
    {
        public bool IsUserLoggedIn { get; set; }
        public bool IsUserAdmin { get; set; }
        public string LoginUrl { get; set; }
        public string LogoutUrl { get; set; }
        public string UserAuthDomain { get; set; }
        public string UserEmail { get; set; }
        public string UserFederatedIdentity { get; set; }
        public string UserNickname { get; set; }
        public long UserId { get; set; }
    }

    public class UserInfoRequest : RequestBase
    {
        public string DestinationUrl { get; set; }
    }
}
