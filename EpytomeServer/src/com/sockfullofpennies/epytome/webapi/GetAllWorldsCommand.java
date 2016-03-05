package com.sockfullofpennies.epytome.webapi;

import java.util.LinkedList;
import java.util.List;
import com.twolattes.json.Entity;
import com.twolattes.json.Value;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.sockfullofpennies.epytome.db.EpytomeDB.WorldModel;
import com.sockfullofpennies.epytome.webapi.GetAllWorldsCommand.*;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.*;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class GetAllWorldsCommand extends
		CommandProcessor<GetAllWorldsRequest, GetAllWorldsReply> {

	public GetAllWorldsCommand() {
		super(GetAllWorldsRequest.class, GetAllWorldsReply.class);
	}

	protected GetAllWorldsReply DoWork(GetAllWorldsRequest request) {
		GetAllWorldsReply reply = new GetAllWorldsReply();
		reply.Worlds = new LinkedList<WorldInfo>();

		List<WorldModel> worlds = ofy().load().type(WorldModel.class).list();
		for(WorldModel w : worlds) {
			WorldInfo worldInfo = new WorldInfo();
			worldInfo.Id = w.Id;
			worldInfo.Name = w.Name;
			reply.Worlds.add(worldInfo);
		}
		
		reply.Status = CommandStatus.Success;
		return reply;
	}

	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class GetAllWorldsRequest extends RequestBase {
	}

	@Entity
	public static class GetAllWorldsReply extends ReplyBase {
		@Value public List<WorldInfo> Worlds;
	}

	@Entity
	public static class WorldInfo {
        @Value public long Id;
        @Value public String Name;
	}
}
