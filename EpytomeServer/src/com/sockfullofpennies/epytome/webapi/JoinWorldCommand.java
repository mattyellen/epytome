package com.sockfullofpennies.epytome.webapi;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.JoinWorldCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.World;

public class JoinWorldCommand extends
		CommandProcessor<JoinWorldRequest, JoinWorldReply> {

	public JoinWorldCommand() {
		super(JoinWorldRequest.class, JoinWorldReply.class);
	}

	protected JoinWorldReply DoWork(JoinWorldRequest request) throws CommandFailedException {
		JoinWorldReply reply = new JoinWorldReply();
		new World(request.WorldId).join(request.NewCharacterName);
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class JoinWorldRequest extends RequestBase {
		@Value public Long WorldId;
		@Value public String NewCharacterName;
	}

	@Entity
	public static class JoinWorldReply extends ReplyBase {
	}
}
