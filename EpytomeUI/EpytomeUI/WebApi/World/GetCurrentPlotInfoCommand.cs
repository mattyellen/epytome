using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;

namespace EpytomeUI.WebApi.World
{
    public class GetCurrentPlotInfoCommand : WebCommandBase<GetCurrentPlotInfoRequest, GetCurrentPlotInfoReply>
    {
        public GetCurrentPlotInfoCommand() : base("GetCurrentPlotInfoCommand")
        {
        }
    }

    public class GetCurrentPlotInfoRequest : RequestBase
    {
		public long WorldId;
    }

    [DataContract]
    public class GetCurrentPlotInfoReply : ReplyBase
    {
        // Just return the correct one.
        public IConstructionInfo ConstructionInfo
        {
            get
            {
                if (_Type == null) return null;
                var prop = GetType().GetField(_Type);
                if (prop != null)
                {
                    return (IConstructionInfo) prop.GetValue(this);
                }
                return null;
            }
        }

        [DataMember] public string _Type;
        [DataMember] public ResourceConstructionInfo _ResourceConstructionInfo;

        [DataMember] public List<InventoryItem> Inventory { get; set; }
        [DataMember] public ConstructionUpgradeInfo UpgradeInfo { get; set; }
    }

  	public class ResourceConstructionInfo : IConstructionInfo {
		public int Amount;
	}

    public class ConstructionUpgradeInfo
    {
        public bool FirstIsCurrent { get; set; }
        public ConstructionType UpgradeType { get; set; }
        public List<UpgradeResource> UpgradeResources { get; set; }
    }

    public class UpgradeResource
    {
        public InventoryItem Resource { get; set; }
        public int CostToAdd { get; set; }
    }

    public interface IConstructionInfo
    {
    }
}
