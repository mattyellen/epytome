package com.sockfullofpennies.epytome.webapi;

import java.lang.reflect.Field;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.sockfullofpennies.epytome.user.Player;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.GetCurrentPlotInfoCommand.*;
import com.sockfullofpennies.epytome.webapi.GetWorldViewCommand.PlotInfo;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Character;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.ConstructionUpgrade;
import com.sockfullofpennies.epytome.world.IConstruction;
import com.sockfullofpennies.epytome.world.Plot;
import com.sockfullofpennies.epytome.world.PlotLoc;
import com.sockfullofpennies.epytome.world.ResourceConstruction;
import com.sockfullofpennies.epytome.world.ConstructionUpgrade.UpgradeResource;

public class GetCurrentPlotInfoCommand extends
		CommandProcessor<GetCurrentPlotInfoRequest, GetCurrentPlotInfoReply> {

	public GetCurrentPlotInfoCommand() {
		super(GetCurrentPlotInfoRequest.class, GetCurrentPlotInfoReply.class);
	}

	protected GetCurrentPlotInfoReply DoWork(GetCurrentPlotInfoRequest request) {
		GetCurrentPlotInfoReply reply = new GetCurrentPlotInfoReply();
		
		PlotLoc currentPlot;
		Player player = new Player();
		if (player.isInWorld(request.WorldId)) {
			Character character = new Character(request.WorldId, player.getId());
			currentPlot = character.getLocation();
		}
		else {
			throw new CommandFailedException(CommandStatus.CharacterNotFound, "Player does not have a character in this world.");
		}
		
		Plot plot = new Plot(request.WorldId, currentPlot);
		IConstruction construction = plot.getConstruction();
		
		//TODO: If this gets big I should replace it with some sort of fancy reflection based automapper
		if (construction != null) {
			if(construction.getClass() == ResourceConstruction.class) {
				ResourceConstruction resCon = (ResourceConstruction)construction;
				reply._Type = "_ResourceConstructionInfo";
				reply._ResourceConstructionInfo = new ResourceConstructionInfo();
				reply._ResourceConstructionInfo.Amount = resCon.getAmount();
			}
		}
		
		ConstructionUpgrade upgrade = plot.getConstructionUpgrade();
		reply.UpgradeInfo = new ConstructionUpgradeInfo();
		reply.UpgradeInfo.FirstIsCurrent = upgrade.isFirstCurrent();
		reply.UpgradeInfo.UpgradeType = upgrade.getUpgradeType();
		reply.UpgradeInfo.UpgradeResources = upgrade.getUpgradeResources();
		
		reply.Inventory = plot.getInventory();

		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class GetCurrentPlotInfoRequest extends RequestBase {
		@Value public Long WorldId;
	}

	@Entity
	public static class GetCurrentPlotInfoReply extends ReplyBase {
		@Value public String _Type;
		@Value public ResourceConstructionInfo _ResourceConstructionInfo;
		@Value(ordinal=true) public List<Character.InventoryItem> Inventory;
		@Value public ConstructionUpgradeInfo UpgradeInfo;
	}

	@Entity
	public static class ResourceConstructionInfo {
		@Value public Integer Amount;
	}

	@Entity
	public static class ConstructionUpgradeInfo {
		@Value public boolean FirstIsCurrent;
		@Value(ordinal=true) public Construction.Type UpgradeType;
		@Value public List<UpgradeResource> UpgradeResources;
	}
}
