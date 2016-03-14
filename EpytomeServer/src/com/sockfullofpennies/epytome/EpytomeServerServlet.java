package com.sockfullofpennies.epytome;

import java.io.IOException;

import javax.servlet.http.*;

import com.sockfullofpennies.epytome.db.EpytomeDB;
import com.sockfullofpennies.epytome.webapi.ICommandProcessor;
import com.sockfullofpennies.epytome.util.StackTraceUtil;
import com.twolattes.json.Json;

@SuppressWarnings("serial")
public class EpytomeServerServlet extends HttpServlet {
	static {
		EpytomeDB.Initialize();
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Json.Object jsonObj = (Json.Object)Json.read(req.getReader());
		String commandName = jsonObj.get(Json.string("Command")).toString();
		
		String commandClassName = "com.sockfullofpennies.epytome.webapi."+commandName.substring(1, commandName.length()-1);
		String replyStr;
		try
		{
			Class<?> commandClass = Class.forName(commandClassName);
			ICommandProcessor command = (ICommandProcessor)commandClass.newInstance();
			replyStr = command.Process(jsonObj);
		}
		catch(Exception e)
		{
			//TODO: Send a generic error back in a command
			replyStr = StackTraceUtil.getStackTrace(e);
		}
		resp.setContentType("text/plain");
		resp.getWriter().println(replyStr);
	}
	
}
