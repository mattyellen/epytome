package com.sockfullofpennies.epytome.webapi;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.StartConstructionCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.Construction.Type;
import com.sockfullofpennies.epytome.world.Plot;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class StartConstructionCommand extends
		CommandProcessor<StartConstructionRequest, StartConstructionReply> {

	public StartConstructionCommand() {
		super(StartConstructionRequest.class, StartConstructionReply.class);
	}

	protected StartConstructionReply DoWork(final StartConstructionRequest request) throws CommandFailedException {
		StartConstructionReply reply = new StartConstructionReply();
		
		final Player player = new Player();
		
		ofy().transact(new VoidWork(){
			public void vrun() {	
				Character character = new Character(request.WorldId, player.getId());
				
				int requiredAP = 20;
				character.verifyAP(requiredAP);
				
				Plot currPlot = new Plot(request.WorldId, character.getLocation());
				currPlot.startConstruction(request.Type);
				character.reduceActionPoints(requiredAP);
			}
		});
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class StartConstructionRequest extends RequestBase {
		@Value public Long WorldId;
		@Value(ordinal=true) public Construction.Type Type;
	}

	@Entity
	public static class StartConstructionReply extends ReplyBase {
	}
}
