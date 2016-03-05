package com.sockfullofpennies.epytome.user;

import java.util.Random;
import static com.googlecode.objectify.ObjectifyService.ofy;


import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlayerModel;
import com.sockfullofpennies.epytome.db.LockManager;
import com.sockfullofpennies.epytome.db.LockManager.UniqueSetType;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;

public class Player extends Persistent<PlayerModel>{
	private UserService userService;
	
	public Player() {
		this(true);
	}

	public Player(boolean requireLogin) {
		super(PlayerModel.class);
		userService = UserServiceFactory.getUserService();
		getLocalUserInfo(requireLogin);
	}

	public Player(Long id) {
		super(PlayerModel.class);
		userService = UserServiceFactory.getUserService();
		if (!load(id)) {
			throw new CommandFailedException(CommandStatus.Failure, "No player with ID "+id+" in the database.");
		}
	}
	
	public boolean isPlayerLoggedIn() {
		return userService.isUserLoggedIn();
	}
	
	public Long getId() {
		return model.Id;
	}
	
	private void getLocalUserInfo(boolean requireLogin) throws CommandFailedException {
		if (isPlayerLoggedIn()) {
			User user = UserServiceFactory.getUserService().getCurrentUser();
			String gaeUserId = user.getUserId();
			PlayerModel pm = ofy().load().type(PlayerModel.class).filter("GaeUserId", gaeUserId).first().get();
			
			//TODO: Should probably have some mechanism to ensure the user data is up to date.
			if (pm == null) {
				// Populate the model 
				model.Email = user.getEmail();
				model.GaeUserId = gaeUserId; 
				
				String nickname = user.getNickname();
				if (!LockManager.ReserveUniqueString(UniqueSetType.Username, nickname)) {
					//Already taken...  try with a random number
					nickname += new Random().nextInt(1000);
					if (!LockManager.ReserveUniqueString(UniqueSetType.Username, nickname)) {
						//Still no?  just fail
						throw new CommandFailedException(CommandStatus.Failure, "Failed to generate a unique nickname.  Try again?");
					}
				}
				
				model.Nickname = nickname;
				
				save();
				System.out.println("Put new user to DB: "+model.Id.toString());
			}
			else {
				model = pm;
			}
		}
		else {
			if (requireLogin) {
				throw new CommandFailedException(CommandStatus.UserNotLoggedIn, "You must be logged in to perform this command.");
			}
		}
	}

	public void setNickname(String newNickname) throws CommandFailedException {
		if (!LockManager.ReserveUniqueString(UniqueSetType.Username, newNickname, model.Nickname)) {
			throw new CommandFailedException(CommandStatus.DuplicateNickname, 
					"This nickname is already taken, choose another.");
		}
		model.Nickname = newNickname;
		save();
	}
	
	public void addToWorld(Long worldId) {
		model.InWorlds.add(worldId);
		save();
	}

	public boolean isInWorld(Long worldId) {
		return model.InWorlds.contains(worldId);
	}

	public String getEmail() {
		return model.Email;
	}

	public String getNickname() {
		return model.Nickname;
	}

	public String createLoginURL(String destinationUrl) {
		return userService.createLoginURL(destinationUrl);
	}

	public String createLogoutURL(String destinationUrl) {
		return userService.createLogoutURL(destinationUrl);
	}

	public boolean isPlayerAdmin() {
		return userService.isUserAdmin();
	}

}
