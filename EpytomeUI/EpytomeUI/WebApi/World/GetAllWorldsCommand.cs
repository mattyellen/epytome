using System.Collections.Generic;

namespace EpytomeUI.WebApi.World
{
    public class GetAllWorldsCommand : WebCommandBase<GetAllWorldsRequest, GetAllWorldsReply>
    {
        public GetAllWorldsCommand() : base("GetAllWorldsCommand")
        {
        }
    }

    public class GetAllWorldsRequest : RequestBase
    {
    }

    public class GetAllWorldsReply : ReplyBase
    {
        public List<WorldInfo> Worlds;
    }

    public class WorldInfo
    {
        public long Id { get; set; }
        public string Name { get; set; }
    }
}
