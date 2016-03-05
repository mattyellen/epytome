package com.sockfullofpennies.epytome.webapi;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.webapi.GatherResourcesCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.Plot;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class GatherResourcesCommand extends
		CommandProcessor<GatherResourcesRequest, GatherResourcesReply> {

	public GatherResourcesCommand() {
		super(GatherResourcesRequest.class, GatherResourcesReply.class);
	}

	protected GatherResourcesReply DoWork(final GatherResourcesRequest request) {
		GatherResourcesReply reply = new GatherResourcesReply();
		
		final Player player = new Player();
		
		ofy().transact(new VoidWork(){
			public void vrun() {	
				Character character = new Character(request.WorldId, player.getId());
				
				int requiredAP = 20;
				character.verifyAP(requiredAP);
				
				Plot currPlot = new Plot(request.WorldId, character.getLocation());
				Character.InventoryItem resource = currPlot.removeResource();
				character.reduceActionPoints(requiredAP);
				
				character.addToInventory(resource);
			}
		});

		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class GatherResourcesRequest extends RequestBase {
		@Value public Long WorldId;
	}

	@Entity
	public static class GatherResourcesReply extends ReplyBase {
	}
}
