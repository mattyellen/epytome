package com.sockfullofpennies.epytome.world;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.sockfullofpennies.epytome.db.EpytomeDB.PlotModel;
import com.sockfullofpennies.epytome.db.EpytomeDB.ResourceConstructionModel;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.Persistent;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class ResourceConstruction 
	extends Persistent<ResourceConstructionModel> 
	implements IConstruction {
	
	//Creates a new ResourceConstruction
	public ResourceConstruction(String plotId, int amount) {
		super(ResourceConstructionModel.class);
		model.Id = plotId;
		model.PlotKey = Key.create(PlotModel.class, plotId);
		model.Amount = amount;
		save();
	}
	
	//Loads existing ResourceConstruction
	public ResourceConstruction(String plotId) {
		super(ResourceConstructionModel.class);
		load(Construction.getKey(ResourceConstructionModel.class, plotId));
	}
	
	public Integer getAmount() {
		return model.Amount;
	}

	public void removeResource() {
		ofy().transact(new VoidWork() {
			public void vrun() {
				if (model.Amount <= 0) {
					throw new CommandFailedException(CommandStatus.NoResources, "This location is depleted of resources.");
				}
				model.Amount--;
				save();
			}
		});
		
	}

}
