package com.sockfullofpennies.epytome.db;

import java.util.ConcurrentModificationException;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.sockfullofpennies.epytome.util.CommandFailedException;
import com.sockfullofpennies.epytome.webapi.CommandProcessor.CommandStatus;
import com.sockfullofpennies.epytome.db.EpytomeDB.Lock;
import com.sockfullofpennies.epytome.db.EpytomeDB.UniqueSet;
import static com.googlecode.objectify.ObjectifyService.ofy;


public class LockManager {
	private static final int DEFAULT_LOCK_RETRIES = 3;

	private static String MakeObjectLockId(Class<?> clazz, String id) {
		return "c:"+clazz.getName()+"|"+id;
	}
	
	// Object locks
	public static void GetObjectLock(Class<?> clazz, String id) throws CommandFailedException {
		GetObjectLock(clazz, id, DEFAULT_LOCK_RETRIES);
	}
	public static void GetObjectLock(Class<?> clazz, String id, int retries) throws CommandFailedException {
		GetLock(MakeObjectLockId(clazz, id), retries);
	}

	public static void ReleaseObjectLock(Class<?> clazz, String id) {
		ReleaseLock(MakeObjectLockId(clazz, id));
	}

	// Global Locks
	public static void GetGlobalLock(LockType lockType) throws CommandFailedException {
		GetGlobalLock(lockType, DEFAULT_LOCK_RETRIES);
	}
	public static void GetGlobalLock(LockType lockType, int retries) throws CommandFailedException {
		GetLock(lockType.name(), retries);
	}

	public static void ReleaseGlobalLock(LockType lockType) {
		ReleaseLock(lockType.name());
	}
	
	// Lock Implementation
	private static void GetLock(final String lockName, int retries) throws CommandFailedException {
		
		boolean gotLock = false;
		do {			
			gotLock = ofy().transact(new Work<Boolean>() {
		        public Boolean run() {
					Lock lock = ofy().load().type(Lock.class).id(lockName).get();
					if (lock == null) {
						lock = new Lock();
						lock.Id = lockName;
					}
					
					if (!lock.Held) {
						lock.Held = true;
						ofy().save().entity(lock).now();
						return true;
					}
					
					return false;
		        }
		    });
		    
		    if (gotLock) return;

			try {Thread.sleep(1000);} catch (InterruptedException e) {}		
			retries--;
		}
		while (retries > 0);
		
		throw new CommandFailedException(CommandStatus.LockFailure, "Failed to get lock for "+lockName);
	}

	private static void ReleaseLock(final String lockName) {
		ofy().transact(new VoidWork() {
			public void vrun() {
				Lock lock = ofy().load().type(Lock.class).id(lockName).get();
				if (!lock.Held) {
					throw new RuntimeException("Trying to release a lock not held!");
				}
				lock.Held = false;
				ofy().save().entity(lock).now();
			}
		});
	}

	public enum LockType {
		Invalid,
		UserLock,
		WorldLock
	}

	public static boolean ReleaseUniqueString(UniqueSetType type, String value) throws CommandFailedException {
		return ReserveUniqueString(type, null, value);
	}

	public static boolean ReserveUniqueString(UniqueSetType type, String value) throws CommandFailedException {
		return ReserveUniqueString(type, value, null);
	}
	
	public static boolean ReserveUniqueString(UniqueSetType type, String value, String relValue) throws CommandFailedException {
		int retries = 10;
		while(retries-- > 0) {
			try {
				return ReserveUniqueStringImpl(type, value, relValue);
			}
			catch(ConcurrentModificationException e) {
				//retry
			}
		}

		throw new CommandFailedException(CommandStatus.Failure, 
				"Unable to reserve a unique string of type "+type+": "+value);
	}
	
	private static boolean ReserveUniqueStringImpl(final UniqueSetType type, final String value, final String relValue) throws CommandFailedException {
		return ofy().transact(new Work<Boolean>() {
			public Boolean run() {
				UniqueSet set = ofy().load().type(UniqueSet.class).id(type.name()).get();
				if (set == null) {
					set = new UniqueSet();
					set.Id = type.name();
				}
				
				if (relValue != null) {
					if (!set.StringSet.contains(relValue)) {
						throw new CommandFailedException(CommandStatus.Failure, 
								"Tried to release a unique string of type "+type+" that was not aquired: "+relValue);
					}
					else {
						set.StringSet.remove(relValue);
					}
				}
				
				if (value != null) {
					if (set.StringSet.contains(value)) {
						return false;
					}
					else {
						set.StringSet.add(value);
					}
				}
				
				ofy().save().entity(set).now();
				return true;
			}
		});
	}

	public enum UniqueSetType {
		Invalid,
		Username,
		CharacterName
	}
}
