package com.sockfullofpennies.epytome.db;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.Plot;
import com.sockfullofpennies.epytome.world.Character;

public class EpytomeDB {
	public static void Initialize() {
		for(Class<?> clazz : EpytomeDB.class.getClasses()) {
			System.out.println("Registering: "+clazz);
			ObjectifyService.register(clazz);
		}
	}
	
	@Entity
	public static class UniqueSet {
		@com.googlecode.objectify.annotation.Id public String Id;
		Set<String> StringSet = new HashSet<String>();
	}
	
	@Entity
	public static class Lock {
		@Id public String Id;
		public boolean Held;
	}
	
	@Entity
	public static class PlayerModel {
		@Id public Long Id;
		@Index public String GaeUserId;
		public String Email;
		@Index public String Nickname;
		public List<Long> InWorlds = new LinkedList<Long>();
	}

	@Entity
	public static class WorldModel {
		@Id public Long Id;
		public String Name;
		public List<Long> PlayerIds = new LinkedList<Long>();
	}

	@Entity
	public static class TractModel {
		@Id public String Id;	//Use WorldId/TractLoc
	}

	@Entity
	public static class CharacterModel {
		@Id public String Id;	//Use WorldId/UserId
		public Long WorldId;
		public Long PlayerId;
		public String Name;
		public String Class;
		public Integer ActionPoints;
		public Integer MaxActionPoints;
		public Long LastAPResetTime = (long) 0;
		public Integer LocX;
		public Integer LocY;
		public List<Character.InventoryItem> Inventory = new LinkedList<Character.InventoryItem>();
	}
	
	@Cache(expirationSeconds=600)
	@Entity
	public static class PlotModel {
		@Id public String Id;	//Use WorldId/PlotLoc
		@Parent public Key<TractModel> TractKey;
		public Long WorldId;
		public Integer LocX;
		public Integer LocY;
		public Plot.Type Type;
		public List<String> CharacterIds = new LinkedList<String>();
		public List<Long> MonsterIds = new LinkedList<Long>();
		public Construction.Type Construction;
		public List<Character.InventoryItem> Inventory = new LinkedList<Character.InventoryItem>();
	}
	
	@Entity
	public static class ResourceConstructionModel {
		@Id public String Id;	//Use PlotId -- plots can only have one construction
		@Parent public Key<PlotModel> PlotKey;
		public Integer Amount;
	}

	@Entity
	public static class ConstructionUpgradeModel {
		@Id public String Id;	//Use PlotId -- plots can only have one construction
		@Parent public Key<PlotModel> PlotKey;
		public Integer ActionPointsContributed;
	}
}
