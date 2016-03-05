package com.sockfullofpennies.epytome.webapi;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.PlotLoc;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.MoveCharacterCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;

public class MoveCharacterCommand extends
		CommandProcessor<MoveCharacterRequest, MoveCharacterReply> {

	public MoveCharacterCommand() {
		super(MoveCharacterRequest.class, MoveCharacterReply.class);
	}

	protected MoveCharacterReply DoWork(MoveCharacterRequest request) throws CommandFailedException {
		MoveCharacterReply reply = new MoveCharacterReply();
		
		Player player = new Player();
		Character character = new Character(request.WorldId, player.getId());
		
		character.moveTo(new PlotLoc(request.TargetX, request.TargetY), request.ExpectedCost);
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class MoveCharacterRequest extends RequestBase {
		@Value public long WorldId;
		@Value public int TargetX;
		@Value public int TargetY;
		@Value public int ExpectedCost;
	}

	@Entity
	public static class MoveCharacterReply extends ReplyBase {
	}
}
