package com.sockfullofpennies.epytome.webapi;

import com.twolattes.json.Json;

public interface ICommandProcessor {
	String Process(Json.Object command);
}
