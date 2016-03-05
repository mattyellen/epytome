package com.sockfullofpennies.epytome.util;

import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;

public final class Utilities {

	public static void Assert(boolean cond, String errorStr) throws CommandFailedException {
		if (!cond) {
			throw new CommandFailedException(CommandStatus.AssertionFailed, "ASSERT FAILED: "+errorStr);
		}
	}
}
