package com.sockfullofpennies.epytome.world;

import com.googlecode.objectify.Key;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlotModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.ResourceConstructionModel;

public class Construction {

	public static enum Type {
		None,
		Road,
		StoneRoad,
		House,
		Quarry,
		Forest
	}

	public static <T> Key<T> getKey(Class<T> modelClass, String plotId) {
		return Key.create(Key.create(PlotModel.class, plotId), modelClass, plotId);
	}
	
	public static IConstruction getConstruction(String plotId, Construction.Type type) {
		switch(type) {
		case Quarry:
			return new ResourceConstruction(plotId);
		default:
			return null;
		}
	}

	public Long getId() {
		return (long) 0;
	}

	public static Character.InventoryItem getResourceTypeForConstruction(Construction.Type type) {
		switch(type) {
			case Quarry:
				return Character.InventoryItem.Stone;
			case Forest:
				return Character.InventoryItem.Wood;
			default:
				return Character.InventoryItem.Invalid;
		}
	}

}
