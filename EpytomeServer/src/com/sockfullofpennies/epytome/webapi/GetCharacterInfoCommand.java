package com.sockfullofpennies.epytome.webapi;

import java.util.Date;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.sockfullofpennies.epytome.db.EpytomeDB.CharacterModel;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.world.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.webapi.GetCharacterInfoCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;

public class GetCharacterInfoCommand extends
		CommandProcessor<GetCharacterInfoRequest, GetCharacterInfoReply> {

	private static final Long AP_RESET_INTERVAL = (long) (24*60*60*1000);	//One day in milliseconds

	public GetCharacterInfoCommand() {
		super(GetCharacterInfoRequest.class, GetCharacterInfoReply.class);
	}

	protected GetCharacterInfoReply DoWork(GetCharacterInfoRequest request) throws CommandFailedException {
		GetCharacterInfoReply reply = new GetCharacterInfoReply();
		Player player = new Player();
		
		Character character;
		try {
			character = new Character(request.WorldId, player.getId());
		}
		catch (CommandFailedException e) {
			if (e.Status == CommandStatus.CharacterNotFound) {
				reply.CharacterInWorld = false;
				return reply;
			}
			throw e;
		}
		
		CharacterModel model = character.getModel();

		long currTime = new WorldTime().getTime();

		//*** Special case for Matt *******************************
		if (player.getEmail().equals("matt.yellen@gmail.com")) {
			if (currTime > (model.LastAPResetTime + (10*1000))) {
				character.resetAP(true);
			}
		}
		//*** end special case *************************************
		else {
			if (currTime > (model.LastAPResetTime + AP_RESET_INTERVAL)) {
				character.resetAP();
			}
		}

		reply.CharacterInWorld = true;
		reply.Name = model.Name; 
		reply.Class = model.Class; 
		reply.ActionPoints = model.ActionPoints; 
		reply.MaxActionPoints = model.MaxActionPoints; 
		reply.LocX = model.LocX; 
		reply.LocY = model.LocY;
		reply.Inventory = model.Inventory;
		
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class GetCharacterInfoRequest extends RequestBase {
		@Value public Long WorldId;
	}

	@Entity
	public static class GetCharacterInfoReply extends ReplyBase {
		@Value public boolean CharacterInWorld;
		@Value public String Name;
		@Value public String Class;
		@Value public Integer ActionPoints;
		@Value public Integer MaxActionPoints;
		@Value public Integer LocX;
		@Value public Integer LocY;
		@Value(ordinal = true) 
		public List<Character.InventoryItem> Inventory;
	}
}
