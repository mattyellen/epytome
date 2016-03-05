package com.sockfullofpennies.epytome.webapi;

import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.TransferItemsCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.Plot;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class TransferItemsCommand extends
		CommandProcessor<TransferItemsRequest, TransferItemsReply> {

	public TransferItemsCommand() {
		super(TransferItemsRequest.class, TransferItemsReply.class);
	}

	protected TransferItemsReply DoWork(final TransferItemsRequest request) {
		TransferItemsReply reply = new TransferItemsReply();
		final Player player = new Player();

		ofy().transact(new VoidWork() {
			public void vrun() {
				Character character = new Character(request.WorldId, player.getId());
				Plot currPlot = new Plot(request.WorldId, character.getLocation());

				if (request.Direction == TransferDirection.PickUp) {
					character.addToInventory(request.Items);
					currPlot.removeFromInventory(request.Items);
				}
				else if (request.Direction == TransferDirection.Drop) {
					character.removeFromInventory(request.Items);
					currPlot.addToInventory(request.Items);
				}
				else {
					throw new CommandFailedException(CommandStatus.Failure, "Internal error: Invalid TransferDirection");
				}
			}
		});
		
		return reply;
	}

	//------------ BEGIN MESSAGES ------------
	
    public static enum TransferDirection
    {
        Invalid,
        PickUp,
        Drop
    }

	@Entity
	public static class TransferItemsRequest extends RequestBase {
		@Value Long WorldId;
        @Value(ordinal=true) List<Character.InventoryItem> Items;
        @Value(ordinal=true) TransferDirection Direction;
	}

	@Entity
	public static class TransferItemsReply extends ReplyBase {
	}
}
