package com.sockfullofpennies.epytome.webapi;

import com.google.appengine.api.users.*;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.webapi.UserInfoCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

public class UserInfoCommand extends CommandProcessor<UserInfoRequest, UserInfoReply>{

	public UserInfoCommand() {
		super(UserInfoRequest.class, UserInfoReply.class);
	}

	@Override
	protected UserInfoReply DoWork(UserInfoRequest request) throws CommandFailedException {
		UserInfoReply reply = new UserInfoReply();
		
		Player player = new Player(false);
		
		reply.IsUserLoggedIn = player.isPlayerLoggedIn();
		if (request.DestinationUrl != null) {
			reply.LoginUrl = player.createLoginURL(request.DestinationUrl);
			reply.LogoutUrl = player.createLogoutURL(request.DestinationUrl);
		}
		
		
		if (reply.IsUserLoggedIn) {
			reply.IsUserAdmin = player.isPlayerAdmin();

			//Use the internal ID, not the GAE one.
			reply.UserId = player.getId();
			reply.UserEmail = player.getEmail();
			reply.UserNickname = player.getNickname();

			//Don't bother with this for now.
			reply.UserAuthDomain = "";
			reply.UserFederatedIdentity = "";
		}
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class UserInfoRequest extends RequestBase{
		@Value public String DestinationUrl;
	}

	@Entity
	public static class UserInfoReply extends ReplyBase{
		@Value public boolean IsUserLoggedIn;
		@Value public boolean IsUserAdmin;
		@Value public String LoginUrl;
		@Value public String LogoutUrl;
		@Value public String UserAuthDomain;
		@Value public String UserEmail;
		@Value public String UserFederatedIdentity;
		@Value public String UserNickname;
		@Value public long UserId;
	}
}
