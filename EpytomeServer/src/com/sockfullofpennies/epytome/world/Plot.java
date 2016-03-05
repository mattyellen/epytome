package com.sockfullofpennies.epytome.world;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlotModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.TractModel;
import com.sockfullofpennies.epytome.game.GameData;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.util.PersistentGroup;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;
import com.sockfullofpennies.epytome.world.Character.InventoryItem;
import com.sockfullofpennies.epytome.world.Construction.Type;

public class Plot extends Persistent<PlotModel>{
	public static class Group extends PersistentGroup<Plot, PlotModel> {
		public Group() {super(PlotModel.class);}
		protected Plot createInstance(PlotModel model) {return new Plot(model);}

		public void add(Long id, int x, int y) {
			add(getKey(id, x, y));
		}
	};
	
	private Plot(PlotModel pm) {
		super(PlotModel.class);
		model = pm;
		if (model.Construction == null)
			model.Construction = Construction.Type.None;
	}
	
	public static enum Type {
		Mountain,
		Grass,
		Water
	}

	private Integer moveCost;

	public static String makeId(Long worldId, int x, int y) {
		return "w:"+worldId+"|l:"+x+","+y;
	}
	
	public static Key<PlotModel> getKey(Long worldId, int x, int y) {
		return Key.create(Tract.getKey(worldId, x, y), PlotModel.class, Plot.makeId(worldId, x, y));
	}

	//Create a new plot
	public Plot(Long worldId, Key<TractModel> tractKey, int x, int y, Type type) {
		super(PlotModel.class);
		model.Id = makeId(worldId, x, y);
		model.TractKey = tractKey;
		model.WorldId = worldId;
		model.LocX = x;
		model.LocY = y;
		model.Type = type;
		model.Construction = Construction.Type.None;

		save();
	}
	
	//Load existing plot
	public Plot(Long worldId, PlotLoc loc) {
		this(worldId, loc.X, loc.Y);
	}

	public Plot(Long worldId, int x, int y) {
		super(PlotModel.class);
		load(getKey(worldId, x, y));
		if (model.Construction == null)
			model.Construction = Construction.Type.None;
	}

	public void addCharacter(Character character) throws CommandFailedException {
		model.CharacterIds.add(character.getId());
		save();
	}

	public void removeCharacter(Character character) throws CommandFailedException {
		model.CharacterIds.remove(character.getId());
		save();
	}

	public Plot.Type getType() {
		return model.Type;
	}
	
	public PlotLoc getLocation() {
		return new PlotLoc(model.LocX, model.LocY);
	}

	public List<Character> getCharacters() {
		Character.Group group = new Character.Group();
		for(String id : model.CharacterIds) {
			group.add(id);
		}
		return group.createAll();
	}
	
	public void startConstruction(final Construction.Type type) throws CommandFailedException {
		
		ofy().transact(new VoidWork() {
			public void vrun() {
				reload();
				
				if (model.Construction != Construction.Type.None)
					throw new CommandFailedException(CommandStatus.Failure, "A plot may only have one construction.");

				//TODO: When we can create non-roads this will need to persist a new construction
				//object to track the work and attributes of the final result. 
				model.Construction = type;
				save();
			}
		});
	}

	public Construction.Type getConstructionType() {
		return model.Construction;
	}

	public IConstruction getConstruction() {
		return Construction.getConstruction(model.Id, model.Construction);
	}

	public ConstructionUpgrade getConstructionUpgrade() {
		return new ConstructionUpgrade(model.Id, model.Construction);
	}

	public void setMoveCost(int cost) {
		moveCost = cost;
	}

	public Integer getMoveCost() {
		return moveCost;
	}

	public Integer CostToAdjacentPlot(Plot targetPlot) {
		if (targetPlot.getType() != Plot.Type.Grass) {
			return null;
		}
		
		return Math.max(
				GameData.Construction.get(model.Construction).MoveCost,
				GameData.Construction.get(targetPlot.model.Construction).MoveCost);
	}

	public void setConstruction(Construction.Type contructionType) {
		model.Construction = contructionType;
		save();
	}
	
	public String getId() {
		return model.Id;
	}

	public Character.InventoryItem removeResource() {
		return ofy().transact(new Work<Character.InventoryItem>() {
			public Character.InventoryItem run() {
				IConstruction construction = getConstruction();
				if (construction == null) {
					throw new CommandFailedException(CommandStatus.NoConstruction, "Cannot gather resources, there is no construction here.");
				}
				
				if (!construction.getClass().isAssignableFrom(ResourceConstruction.class)) {
					throw new CommandFailedException(CommandStatus.InvalidConstructionType, "Can't get resources from this construction.");
				}
				
				ResourceConstruction resourceConstruction = (ResourceConstruction)construction;
				resourceConstruction.removeResource();
				
				return Construction.getResourceTypeForConstruction(model.Construction);
			}
		});
	}
	
	public void addToInventory(InventoryItem item) {
		model.Inventory.add(item);
		save();
	}

	public void addToInventory(List<InventoryItem> items) {
		model.Inventory.addAll(items);
		save();
	}

	public void removeFromInventory(List<InventoryItem> items) {
		for(InventoryItem item : items) {
			if (!model.Inventory.contains(item)) {
				throw new CommandFailedException(CommandStatus.ItemNotInInventory, "The item \""+item+"\" does not seem to be on the ground here.");
			}
			model.Inventory.remove(item);
		}
		save();
	}

	public List<InventoryItem> getInventory() {
		return model.Inventory;
	}
}
