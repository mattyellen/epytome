namespace EpytomeUI.WebApi.World
{
    public class DoConstructionWorkCommand : WebCommandBase<DoConstructionWorkRequest, DoConstructionWorkReply>
    {
        public DoConstructionWorkCommand() : base("DoConstructionWorkCommand")
        {
        }
    }

    public class DoConstructionWorkRequest : RequestBase
    {
        public long WorldId;
        public int ActionPoints;
    }

    public class DoConstructionWorkReply : ReplyBase
    {
        public bool Completed;
    }
}
