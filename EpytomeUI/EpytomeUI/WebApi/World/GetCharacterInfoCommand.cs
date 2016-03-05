using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;

namespace EpytomeUI.WebApi.World
{
    public class GetCharacterInfoCommand : WebCommandBase<GetCharacterInfoRequest, GetCharacterInfoReply>
    {
        public GetCharacterInfoCommand() : base("GetCharacterInfoCommand")
        {
        }
    }

    public class GetCharacterInfoRequest : RequestBase
    {
        public long WorldId { get; set; }
    }

    public class GetCharacterInfoReply : ReplyBase
    {
        public bool CharacterInWorld { get; set; }
        public string Name { get; set; }
        public string Class { get; set; }
        public int ActionPoints { get; set; }
        public int MaxActionPoints { get; set; }
        public int LocX { get; set; }
        public int LocY { get; set; }
        public List<InventoryItem> Inventory { get; set; }
    }

    public enum InventoryItem
    {
        Invalid,
        Wood,
        Stone
    }

}
