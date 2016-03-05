namespace EpytomeUI.WebApi.World
{
    public class MoveCharacterCommand : WebCommandBase<MoveCharacterRequest, MoveCharacterReply>
    {
        public MoveCharacterCommand() : base("MoveCharacterCommand")
        {
        }
    }

    public class MoveCharacterRequest : RequestBase
    {
   		public long WorldId;
		public int TargetX;
		public int TargetY;
        public int ExpectedCost;
    }

    public class MoveCharacterReply : ReplyBase
    {
    }
}
