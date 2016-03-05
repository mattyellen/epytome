package com.sockfullofpennies.epytome.webapi;

import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.webapi.UpdateUserInfoCommand.*;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

public class UpdateUserInfoCommand extends
		CommandProcessor<UpdateUserInfoRequest, UpdateUserInfoReply> {

	public UpdateUserInfoCommand() {
		super(UpdateUserInfoRequest.class, UpdateUserInfoReply.class);
	}

	protected UpdateUserInfoReply DoWork(UpdateUserInfoRequest request) throws CommandFailedException {
		UpdateUserInfoReply reply = new UpdateUserInfoReply();
		Player player = new Player();
		player.setNickname(request.NewNickname);
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class UpdateUserInfoRequest extends RequestBase {
        @Value public String NewNickname;
	}

	@Entity
	public static class UpdateUserInfoReply extends ReplyBase {
	}
}