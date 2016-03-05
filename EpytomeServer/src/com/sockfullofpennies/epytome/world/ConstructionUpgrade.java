package com.sockfullofpennies.epytome.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.sockfullofpennies.epytome.db.EpytomeDB.ConstructionUpgradeModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlotModel;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.world.Character.InventoryItem;
import com.sockfullofpennies.epytome.world.Construction.Type;
import com.sockfullofpennies.epytome.world.ConstructionUpgrade.UpgradeResource;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;

public class ConstructionUpgrade extends Persistent<ConstructionUpgradeModel> {

	private boolean firstIsCurrent;
	private Construction.Type upgradeType;
	private LinkedList<UpgradeResource> upgradeResources;
	
	public ConstructionUpgrade(String plotId, Construction.Type construction) {
		super(ConstructionUpgradeModel.class);
		upgradeType = Construction.Type.None;
		firstIsCurrent = false;
		
		Initialize(construction);
		if (!load(Construction.getKey(ConstructionUpgradeModel.class, plotId)))
		{
			model.Id = plotId;
			model.PlotKey = Key.create(PlotModel.class, plotId);
			model.ActionPointsContributed = 0;
		}
		
		UpdateResourceListForAP(model.ActionPointsContributed);
	}
	
	public boolean ContributeAP(int points) {
		model.ActionPointsContributed += points;
		UpdateResourceListForAP(points);
		
		save();
		
		return getTotalAP() == 0;
	}

	private void UpdateResourceListForAP(int points) {
		int remaining = points;
		while (remaining > 0) {
			UpgradeResource next = upgradeResources.getFirst();
			
			if (remaining >= next.CostToAdd) {
				remaining -= next.CostToAdd;
				upgradeResources.removeFirst();
				firstIsCurrent = false;
			}
			else {
				next.CostToAdd -= remaining;
				firstIsCurrent = true;
				remaining = 0;
			}
		}
	}

	private void Initialize(Type construction) {
		switch(construction) {
			case Road:
				upgradeType = Construction.Type.StoneRoad;
				upgradeResources = new LinkedList<UpgradeResource>(Arrays.asList(
							new UpgradeResource(InventoryItem.Stone, 10),
							new UpgradeResource(InventoryItem.Stone, 10),
							new UpgradeResource(InventoryItem.Stone, 10)
						));
				break;
		}
		
	}

	public int getTotalAP() {
		int totalAP = 0;
		for(UpgradeResource resource : upgradeResources) {
			totalAP += resource.CostToAdd;
		}
		
		return totalAP;
	}
	
	public List<InventoryItem> getResourcesForAP(int ap) {
		LinkedList<InventoryItem> resourceList = new LinkedList<InventoryItem>();
		int remaining = ap;
		for(UpgradeResource resource : upgradeResources) {
			if (remaining > 0) {
				resourceList.add(resource.Resource);
				remaining -= resource.CostToAdd;
			}
		}
		
		if (firstIsCurrent) {
			//First resource was already added
			resourceList.removeFirst();
		}
		
		return resourceList;
	}

	public void reset() {
		delete();
	}
	
	public boolean isFirstCurrent() {
		return firstIsCurrent;
	}
	
	public Type getUpgradeType() {
		return upgradeType; 
	}

	public List<UpgradeResource> getUpgradeResources() {
		return upgradeResources;
	}
	
	@Entity
	public static class UpgradeResource {
		public UpgradeResource() {
		}
		
		public UpgradeResource(InventoryItem resource, Integer costToAdd) {
			Resource = resource;
			CostToAdd = costToAdd;
		}

		@Value(ordinal=true) public Character.InventoryItem Resource;
		@Value public Integer CostToAdd;
	}
}
