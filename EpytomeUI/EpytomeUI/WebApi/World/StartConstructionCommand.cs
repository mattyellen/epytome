namespace EpytomeUI.WebApi.World
{
    public class StartConstructionCommand : WebCommandBase<StartConstructionRequest, StartConstructionReply>
    {
        public StartConstructionCommand() : base("StartConstructionCommand")
        {
        }
    }

    public class StartConstructionRequest : RequestBase
    {
        public long WorldId;
        public ConstructionType Type;
    }

    public class StartConstructionReply : ReplyBase
    {
    }
}
