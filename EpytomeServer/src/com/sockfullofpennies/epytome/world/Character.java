package com.sockfullofpennies.epytome.world;

import java.util.List;

import com.googlecode.objectify.VoidWork;
import com.sockfullofpennies.epytome.db.EpytomeDB.CharacterModel;
import com.sockfullofpennies.epytome.db.LockManager;
import com.sockfullofpennies.epytome.db.LockManager.UniqueSetType;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.util.PersistentGroup;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;
import com.sockfullofpennies.epytome.world.Character.InventoryItem;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class Character extends Persistent<CharacterModel>{
	
	public static enum InventoryItem {
		Invalid,
		Wood,
		Stone
	}
	
	private static final int INVENTORY_MAX_COUNT = 8;

	public static class Group extends PersistentGroup<Character, CharacterModel> {
		public Group() {super(CharacterModel.class);}
		protected Character createInstance(CharacterModel model) {return new Character(model);}
	};

	private Character(CharacterModel cm) {
		super(CharacterModel.class);
		model = cm;
	}

	public static final int MAX_ACTION_POINTS = 200;
	private static String makeId(Long worldId, Long userId) {
		return "w:"+worldId+"|u:"+userId;
	}
	
	//Creates a new character
	public Character(
			String characterName, 
			Long worldId,
			Long userId) throws CommandFailedException {
		super(CharacterModel.class);
		
		LockManager.ReserveUniqueString(UniqueSetType.CharacterName, characterName);
		
		model.Id = makeId(worldId, userId);
		model.WorldId = worldId;
		model.PlayerId = userId;
		model.Name = characterName;
		model.Class = "Person";
		model.ActionPoints = MAX_ACTION_POINTS;
		model.MaxActionPoints = MAX_ACTION_POINTS;
		model.LocX = 0;
		model.LocY = 0;
		save();
	}

	//Loads existing character by id
	public Character(Long worldId, Long userId) throws CommandFailedException {
		this(makeId(worldId, userId));
	}

	public Character(String id) throws CommandFailedException {
		super(CharacterModel.class);
		if (!load(id)) {
			throw new CommandFailedException(CommandStatus.CharacterNotFound, "No such character in this world");
		}
	}

	public String getId() {
		return model.Id;
	}

	public PlotLoc getLocation() {
		return new PlotLoc(model.LocX, model.LocY);
	}

	//TODO: reconsider getters for each property
	public CharacterModel getModel() {
		return model;
	}
	
	public void moveTo(final PlotLoc target, int expectedCost) throws CommandFailedException {
		final PlotLoc currLoc = getLocation();

		//Get a new world view to ensure we have up to date costs
		World world = new World(model.WorldId);
		List<Plot> plotList = world.getWorldView();
		Integer requiredAP = null;
		for(Plot p : plotList) {
			if (p.getLocation().equals(target)) {
				requiredAP = p.getMoveCost();
				break;
			}
		}
		
		if ((requiredAP == null) || (requiredAP > expectedCost)) {
			throw new CommandFailedException(CommandStatus.Failure, "Unexpected AP cost for this move.  The world may have changed.  You should try again.");
		}

		//Validate AP for this move
		verifyAP(requiredAP);

		final Character thisChar = this;
		final Integer reduceAP = requiredAP;
		ofy().transact(new VoidWork() {
			public void vrun() {
				Plot currPlot = new Plot(model.WorldId, currLoc);
				Plot targetPlot = new Plot(model.WorldId, target);
				
				currPlot.removeCharacter(thisChar);
				targetPlot.addCharacter(thisChar);
				
				model.LocX = target.X;
				model.LocY = target.Y;
				
				//Add reduce action points... 
				model.ActionPoints -= reduceAP;
	
				save();
			}
		});
	}

	public void reduceActionPoints(int requiredAP) {
		model.ActionPoints -= requiredAP;
		save();
	}

	public void verifyAP(int requiredAP) throws CommandFailedException {
		if (model.ActionPoints < requiredAP) {
			throw new CommandFailedException(CommandStatus.InsufficientActionPoints, "You don't have enough action points for this action.");
		}
	}

	public void resetAP() {
		resetAP(false);
	}

	public void resetAP(boolean exact) {
		WorldTime wt = new WorldTime();
		
		model.ActionPoints = model.MaxActionPoints;
		model.LastAPResetTime = exact ? wt.getTime() : wt.getPriorMidnight();

		save();
	}

	public void addToInventory(InventoryItem item) {
		if (model.Inventory.size() >= INVENTORY_MAX_COUNT) {
			throw new CommandFailedException(CommandStatus.CharacterInventoryFull, "Your inventory is full.  Cannot add anything else.");
		}
		model.Inventory.add(item);
		save();
	}

	public void addToInventory(List<InventoryItem> items) {
		if (model.Inventory.size() + items.size() > INVENTORY_MAX_COUNT) {
			throw new CommandFailedException(CommandStatus.CharacterInventoryFull, "Not enough room in your inventory for these items.");
		}
		model.Inventory.addAll(items);
		save();
	}

	public void removeFromInventory(List<InventoryItem> items) {
		for(InventoryItem item : items) {
			if (!model.Inventory.contains(item)) {
				throw new CommandFailedException(CommandStatus.ItemNotInInventory, "The item \""+item+"\" does not seem to be in your inventory.");
			}
			model.Inventory.remove(item);
		}
		save();
	}
}
