package com.sockfullofpennies.epytome.webapi;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.GetWorldViewCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import com.sockfullofpennies.epytome.world.Construction;
import com.sockfullofpennies.epytome.world.Construction.Type;
import com.sockfullofpennies.epytome.world.PlotLoc;
import com.sockfullofpennies.epytome.world.Plot;
import com.sockfullofpennies.epytome.world.World;
import com.sockfullofpennies.epytome.world.Character;

public class GetWorldViewCommand extends
		CommandProcessor<GetWorldViewRequest, GetWorldViewReply> {

	public GetWorldViewCommand() {
		super(GetWorldViewRequest.class, GetWorldViewReply.class);
	}

	protected GetWorldViewReply DoWork(GetWorldViewRequest request) throws CommandFailedException {
		GetWorldViewReply reply = new GetWorldViewReply();
		reply.WorldView = new LinkedList<PlotInfo>();
		
		List<Plot> plotList = new World(request.WorldId).getWorldView();
		
		for (Plot plot : plotList) {
			PlotInfo pi = new PlotInfo();
			pi.Type = plot.getType();
			
			PlotLoc loc = plot.getLocation();
			pi.LocX = loc.X;
			pi.LocY = loc.Y;
			pi.Characters = new LinkedList<CharacterInfo>();
			
			List<Character> charList = plot.getCharacters();
			for (Character character : charList) {
				CharacterInfo charInfo = new CharacterInfo();
				charInfo.Name = character.getModel().Name;
				pi.Characters.add(charInfo);
			}

			pi.ConstructionType = plot.getConstructionType();
			pi.MoveCost = plot.getMoveCost();
			
			reply.WorldView.add(pi);
		}
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class GetWorldViewRequest extends RequestBase {
		@Value public Long WorldId;
	}

	@Entity
	public static class GetWorldViewReply extends ReplyBase {
		@Value public List<PlotInfo> WorldView;
	}
	
	@Entity
	public static class PlotInfo {
		@Value(ordinal=true) public Plot.Type Type;
		@Value public Integer LocX;
		@Value public Integer LocY;
		@Value public List<CharacterInfo> Characters;
		@Value(ordinal=true) public Construction.Type ConstructionType;
		@Value public Integer MoveCost;
	}

	@Entity
	public static class CharacterInfo {
		@Value public String Name;
	}

}
