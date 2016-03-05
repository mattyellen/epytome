using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;

namespace EpytomeUI.WebApi.World
{
    public enum TransferDirection
    {
        Invalid,
        PickUp,
        Drop
    }

    public class TransferItemsCommand : WebCommandBase<TransferItemsRequest, TransferItemsReply>
    {
        public TransferItemsCommand() : base("TransferItemsCommand")
        {
        }
    }

    public class TransferItemsRequest : RequestBase
    {
        public long WorldId { get; set; }
        public List<InventoryItem> Items { get; set; }
        public TransferDirection Direction { get; set; }
    }

    public class TransferItemsReply : ReplyBase
    {
    }
}
