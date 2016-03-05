package com.sockfullofpennies.epytome.webapi;

import java.util.Arrays;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.DoConstructionWorkCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.ConstructionUpgrade;
import com.sockfullofpennies.epytome.world.Plot;
import com.sockfullofpennies.epytome.world.Character.InventoryItem;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class DoConstructionWorkCommand extends
		CommandProcessor<DoConstructionWorkRequest, DoConstructionWorkReply> {

	public DoConstructionWorkCommand() {
		super(DoConstructionWorkRequest.class, DoConstructionWorkReply.class);
	}

	protected DoConstructionWorkReply DoWork(final DoConstructionWorkRequest request) {
		final DoConstructionWorkReply reply = new DoConstructionWorkReply();
		
		final Player player = new Player();
		
		ofy().transact(new VoidWork(){
			public void vrun() {	
				Character character = new Character(request.WorldId, player.getId());
				
				Plot currPlot = new Plot(request.WorldId, character.getLocation());
				ConstructionUpgrade upgrade = currPlot.getConstructionUpgrade();
				if (upgrade.getUpgradeType() == Construction.Type.None) {
					throw new CommandFailedException(CommandStatus.NoConstruction, "There's no construction to upgrade to here.");
				}
				
				int requiredAP = Math.min(upgrade.getTotalAP(), request.ActionPoints);
				character.verifyAP(requiredAP);
				
				List<InventoryItem> requiredItems = upgrade.getResourcesForAP(requiredAP);
				currPlot.removeFromInventory(requiredItems);
				character.reduceActionPoints(requiredAP);
				boolean completed = upgrade.ContributeAP(requiredAP);

				if (completed) {
					currPlot.setConstruction(upgrade.getUpgradeType());
					upgrade.reset();
					reply.Completed = true;
				}
			}
		});

		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class DoConstructionWorkRequest extends RequestBase {
        @Value public Long WorldId;
        @Value public Integer ActionPoints;
	}

	@Entity
	public static class DoConstructionWorkReply extends ReplyBase {
		@Value public Boolean Completed;
	}
}
