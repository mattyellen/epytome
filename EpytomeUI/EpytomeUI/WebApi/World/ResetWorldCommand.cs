namespace EpytomeUI.WebApi.World
{
    public class ResetWorldCommand : WebCommandBase<ResetWorldRequest, ResetWorldReply>
    {
        public ResetWorldCommand() : base("ResetWorldCommand")
        {
        }
    }

    public class ResetWorldRequest : RequestBase
    {
    }

    public class ResetWorldReply : ReplyBase
    {
    }
}
