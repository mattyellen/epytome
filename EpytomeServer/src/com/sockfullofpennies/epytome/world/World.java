package com.sockfullofpennies.epytome.world;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.events.Characters;

import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.db.EpytomeDB.*;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;
import static com.googlecode.objectify.ObjectifyService.ofy;


public class World  extends Persistent<WorldModel>{
	public static final PlotLoc VIEW_DIST = new PlotLoc(6, 6);
	
	public World(Long id){
		super(WorldModel.class);
		load(id);
	}
	
	public void join(final String characterName) throws CommandFailedException {
		final Player player = new Player();
		if (model.PlayerIds.contains(player.getId())) {
			throw new CommandFailedException(CommandStatus.UserInWorld, "You are already in this world.");
		}
		
		ofy().transact(new VoidWork() {
			public void vrun() {
				reload();
				Player playerReload = new Player(player.getId());
				
				Plot startPlot = new Plot(model.Id, 0, 0);
				Character newChar = new Character(characterName, model.Id, player.getId());
				startPlot.addCharacter(newChar);
				
				playerReload.addToWorld(model.Id);
				
				save();
			}
		});
	}

	public List<Plot> getWorldView() throws CommandFailedException {
		PlotLoc fromLoc;
		Player player = new Player();
		if ((player != null) && (player.isInWorld(model.Id))) {
			Character character = new Character(model.Id, player.getId());
			fromLoc = character.getLocation();
		}
		else {
			fromLoc = new PlotLoc(0,0);
		}
		
		// Ensure everything exists at our view extents 
		new Tract(model.Id, fromLoc.X - VIEW_DIST.X*2, fromLoc.Y - VIEW_DIST.Y).create();
		new Tract(model.Id, fromLoc.X - VIEW_DIST.X*2, fromLoc.Y + VIEW_DIST.Y).create();
		new Tract(model.Id, fromLoc.X + VIEW_DIST.X*2, fromLoc.Y - VIEW_DIST.Y).create();
		new Tract(model.Id, fromLoc.X + VIEW_DIST.X*2, fromLoc.Y + VIEW_DIST.Y).create();
		
		Plot.Group plotGroup = new Plot.Group();
		for(int y = (fromLoc.Y-VIEW_DIST.Y); y <= (fromLoc.Y+VIEW_DIST.Y); y++) {
			int xoff = Math.abs(y-fromLoc.Y)%2;
			for(int x = (fromLoc.X-VIEW_DIST.X*2+xoff); x <= (fromLoc.X+VIEW_DIST.X*2); x+=2) {
				plotGroup.add(model.Id, x, y);
			}			
		}
		
		List<Plot> plots = plotGroup.createAll();
		Map<String, Plot> plotMap = new HashMap<String, Plot>();
		for(Plot p : plots) {
			plotMap.put(p.getLocation().toString(), p);
		}
		plotMap.get(fromLoc.toString()).setMoveCost(0);

		calculateMoveCosts(plotMap, fromLoc);
		return plots;
	}

	private void calculateMoveCosts(Map<String, Plot> plotMap, PlotLoc fromLoc) {
		Plot currPlot = plotMap.get(fromLoc.toString());
		if (currPlot == null) return;
		
		List<PlotLoc> searchList = new LinkedList<PlotLoc>();
		for(PlotLoc dir : PlotLoc.Directions) {
			PlotLoc toLoc = fromLoc.add(dir);
			Plot targetPlot = plotMap.get(toLoc.toString());
			if (targetPlot == null) continue;	//This direction is off the map
			
			Integer adjMoveCost = currPlot.CostToAdjacentPlot(targetPlot);
			if (adjMoveCost == null) continue;	//Illegal move to this plot
			
			int resultCost = currPlot.getMoveCost() + adjMoveCost;
			Integer targetCost = targetPlot.getMoveCost();
			if ((targetCost == null) || (resultCost < targetCost)) {
				//Found a cheaper way to get here -- keep searching
				targetPlot.setMoveCost(resultCost);
				searchList.add(toLoc);
			}
		}
		
		for(PlotLoc loc : searchList) {
			calculateMoveCosts(plotMap, loc);
		}
	}

}
