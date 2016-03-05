namespace EpytomeUI.WebApi
{
    public class UpdateUserInfoCommand : WebCommandBase<UpdateUserInfoRequest, UpdateUserInfoReply>
    {
        public UpdateUserInfoCommand() : base("UpdateUserInfoCommand")
        {
        }
    }

    public class UpdateUserInfoRequest : RequestBase
    {
        public string NewNickname { get; set; }
    }

    public class UpdateUserInfoReply : ReplyBase
    {
    }
}
