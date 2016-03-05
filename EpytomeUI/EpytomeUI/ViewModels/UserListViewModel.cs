using System.Collections.Generic;
using System.Linq;
using EpytomeUI.Utilities;
using EpytomeUI.WebApi;

namespace EpytomeUI.ViewModels
{
    public class UserListViewModel : Notifier
    {
        private List<string> _userNicknames;
        public List<string> UserNicknames
        {
            get { return _userNicknames; }
            set { _userNicknames = value; Notify(() => UserNicknames);}
        }

        public void Initialize()
        {
            Refresh();
        }

        public void Refresh()
        {
            new GetAllUsersCommand(){ReplyCallback = OnGetAllUsersReply}.Send(new GetAllUsersRequest());
        }

        private void OnGetAllUsersReply(GetAllUsersReply reply)
        {
            UserNicknames = (from u in reply.Users
                             select u.Nickname).ToList();
        }
    }
}
