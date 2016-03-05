using System.Collections.Generic;

namespace EpytomeUI.WebApi
{
    public class GetAllUsersCommand : WebCommandBase<GetAllUsersRequest, GetAllUsersReply>
    {
        public GetAllUsersCommand() : base("GetAllUsersCommand")
        {
        }
    }

    public class GetAllUsersRequest : RequestBase
    {
    }

    public class GetAllUsersReply : ReplyBase
    {
        public List<UserSummary> Users { get; set; }
    }

    public class UserSummary
    {
        public long Id { get; set; }
        public string Nickname { get; set; }
    }
}
