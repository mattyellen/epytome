namespace EpytomeUI.WebApi.World
{
    public class JoinWorldCommand : WebCommandBase<JoinWorldRequest, JoinWorldReply>
    {
        public JoinWorldCommand() : base("JoinWorldCommand")
        {
        }
    }

    public class JoinWorldRequest : RequestBase
    {
		public long WorldId;
		public string NewCharacterName;
    }

    public class JoinWorldReply : ReplyBase
    {
    }
}
