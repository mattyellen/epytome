namespace EpytomeUI.WebApi.World
{
    public class GatherResourcesCommand : WebCommandBase<GatherResourcesRequest, GatherResourcesReply>
    {
        public GatherResourcesCommand() : base("GatherResourcesCommand")
        {
        }
    }

    public class GatherResourcesRequest : RequestBase
    {
        public long WorldId;
    }

    public class GatherResourcesReply : ReplyBase
    {
    }
}
