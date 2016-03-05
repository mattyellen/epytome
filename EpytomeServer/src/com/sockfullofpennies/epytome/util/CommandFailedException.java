package com.sockfullofpennies.epytome.util;

import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;

@SuppressWarnings("serial")
public class CommandFailedException extends RuntimeException {
	public CommandStatus Status;
	public String StatusDetail;
	
	public CommandFailedException(CommandStatus status, String statusDetail) {
		Status = status;
		StatusDetail = statusDetail;
	}
} 
