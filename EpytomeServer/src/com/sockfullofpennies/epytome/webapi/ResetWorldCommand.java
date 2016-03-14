package com.sockfullofpennies.epytome.webapi;

import java.util.LinkedList;
import java.util.List;

import com.twolattes.json.Entity;
import com.googlecode.objectify.*;
import com.sockfullofpennies.epytome.db.EpytomeDB.CharacterModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlotModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.TractModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.WorldModel;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.ResetWorldCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Plot;
import com.sockfullofpennies.epytome.world.PlotLoc;
import com.sockfullofpennies.epytome.world.World;
import static com.googlecode.objectify.ObjectifyService.ofy;


public class ResetWorldCommand extends
		CommandProcessor<ResetWorldRequest, ResetWorldReply> {

	public ResetWorldCommand() {
		super(ResetWorldRequest.class, ResetWorldReply.class);
	}

	protected ResetWorldReply DoWork(ResetWorldRequest request) throws CommandFailedException {
		ResetWorldReply reply = new ResetWorldReply();
		Player player = new Player();
		if (!player.getEmail().equals("matt.yellen@gmail.com")) {
			throw new CommandFailedException(CommandStatus.Failure, "You don't seem to be Matt Yellen.");
		}

		List<String> charIds = new LinkedList<String>();
		for (CharacterModel chardb : ofy().load().type(CharacterModel.class).list()) {
			chardb.ActionPoints = 200;
			chardb.MaxActionPoints = 200;
			chardb.LocX = 0;
			chardb.LocY = 0;
			ofy().save().entity(chardb).now();
			
			charIds.add(chardb.Id);
		}
		
		//Save away the worldId so we can reuse it
		//Need this to preserve characters
		WorldModel oldWorld = ofy().load().type(WorldModel.class).first().get();
		Long worldId = null;
		if (oldWorld != null) {
			worldId = oldWorld.Id;
		}

		//Nuke the world
		ofy().delete().keys(ofy().load().type(TractModel.class).keys());
		ofy().delete().keys(ofy().load().type(WorldModel.class).keys());
		ofy().delete().keys(ofy().load().type(PlotModel.class).keys());

		//Build a new one
		WorldModel newWorld = new WorldModel();
		newWorld.Id = (worldId == null) ? 0 : worldId;
		newWorld.Name = "Epytome";
		ofy().save().entity(newWorld).now();
		
		//Get the world view to generate the initial tract
		//This will trigger off my user / character which has been reset
		//with everyone else to 0,0
		new World(worldId).getWorldView();
		
		//Now reset and re-add all the characters to 0,0
		PlotModel startPlot = ofy().load().key(Plot.getKey(worldId, 0, 0)).get();
		startPlot.CharacterIds = charIds;
		startPlot.Type = Plot.Type.Grass;
		ofy().save().entity(startPlot).now();
		
		//And force neighbors to Grass to improve chances this map has some place to go
		PlotLoc startLoc = new PlotLoc(0,0);
		for(PlotLoc dir : PlotLoc.Directions) {
			PlotLoc adjLoc = startLoc.add(dir);
			PlotModel adjPlot = ofy().load().key(Plot.getKey(worldId, adjLoc.X, adjLoc.Y)).get();
			adjPlot.Type = Plot.Type.Grass;
			ofy().save().entity(adjPlot).now();
		}
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class ResetWorldRequest extends RequestBase {
	}

	@Entity
	public static class ResetWorldReply extends ReplyBase {
	}
}
