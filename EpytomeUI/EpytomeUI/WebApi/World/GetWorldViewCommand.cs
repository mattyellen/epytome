using System;
using System.Collections.Generic;
using System.Runtime.Serialization;
using EpytomeUI.Utilities;

namespace EpytomeUI.WebApi.World
{
    public class GetWorldViewCommand : WebCommandBase<GetWorldViewRequest, GetWorldViewReply>
    {
        public GetWorldViewCommand() : base("GetWorldViewCommand")
        {
        }
    }

    public class GetWorldViewRequest : RequestBase
    {
		public long WorldId;
    }

    public class GetWorldViewReply : ReplyBase
    {
        public List<PlotInfo> WorldView { get; set; }
    }

    public class PlotInfo {
        public PlotType Type { get; set; }

        public int LocX { get; set; }
        public int LocY { get; set; }
        public List<CharacterInfo> Characters{ get; set; }
        public ConstructionType ConstructionType { get; set; }

        public int? MoveCost { get; set; }
    }

    public class CharacterInfo
    {
        public string Name { get; set; }
    }

    public enum PlotType
    {
        Mountain,
        Grass,
        Water,
        MoveInterface
    }

    public enum ConstructionType
    {
        None,
        [Display(Image = "dirt_road_icon.png")]
        Road,
        [Display(Name="Stone Road", Image="stone_road_icon.png")]
        StoneRoad,
        House,
        Quarry,
    }
}
