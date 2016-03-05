package com.sockfullofpennies.epytome.webapi;

import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.util.StackTraceUtil;
import com.twolattes.json.Entity;
import com.twolattes.json.Json;
import com.twolattes.json.Marshaller;
import com.twolattes.json.TwoLattes;
import com.twolattes.json.Value;

public abstract class CommandProcessor<TREQ, TREP> implements ICommandProcessor {
	
	private Class<TREQ> _requestClass;
	private Class<TREP> _replyClass;
	
	public CommandProcessor(Class<TREQ> commandClass, Class<TREP> replyClass) {
		_requestClass = commandClass;
		_replyClass = replyClass;
	}

	public String Process(Json.Object command) {

		Marshaller<TREQ> mashaller = TwoLattes.createEntityMarshaller(_requestClass);
		TREQ commandObj = mashaller.unmarshall(command);
		
		TREP reply;
		try {
			reply = DoWork(commandObj);
			((ReplyBase)reply).Status = CommandStatus.Success;
		}
		catch(CommandFailedException e) {
			try {
				reply = _replyClass.newInstance();
				ReplyBase replyBase = (ReplyBase)reply;
				replyBase.Status = e.Status;
				replyBase.StatusDetail = e.StatusDetail;
			}
			catch(Exception e2) {
				throw new RuntimeException(e2);
			}
		}
		catch(Exception e) {
			try {
				reply = _replyClass.newInstance();
				ReplyBase replyBase = (ReplyBase)reply;
				replyBase.Status = CommandStatus.Failure;
				replyBase.StatusDetail = StackTraceUtil.getStackTrace(e);
			}
			catch(Exception e2) {
				throw new RuntimeException(e2);
			}
		}
		
		Marshaller<TREP> jsonResp = TwoLattes.createEntityMarshaller(_replyClass);
		return jsonResp.marshall(reply).toString();
	}
	
	protected abstract TREP DoWork(TREQ command) throws CommandFailedException;
	
	//------------ BEGIN MESSAGES ------------

	@Entity
	public static class RequestBase {
		@Value protected String Command;
	}

	@Entity
	public static class ReplyBase {
		@Value(ordinal=true) public CommandStatus Status;
		@Value public String StatusDetail;
	}
	
	public enum CommandStatus {
		Invalid,
		Success,
		Failure, 
		UserNotLoggedIn, 
		DuplicateGame, 
		LockFailure, 
		DuplicateNickname, 
		ConcurrentAction, 
		UserInWorld, 
		AssertionFailed, 
		InsufficientActionPoints, 
		CharacterNotFound, 
		NoConstruction, 
		InvalidConstructionType, 
		NoResources, 
		CharacterInventoryFull, 
		ItemNotInInventory
	}
}
