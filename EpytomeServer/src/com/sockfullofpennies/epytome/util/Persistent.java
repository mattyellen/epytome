package com.sockfullofpennies.epytome.util;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

public class Persistent<T> {
	protected T model;
	protected Key<T> key;
	
	protected Persistent(Class<T> modelClass) {
		try {
			model = modelClass.newInstance();
		}
		catch(Exception e) {
			throw new RuntimeException("Invalid class for model: "+modelClass, e);
		}
	}
	
	protected boolean reload() {
		return load(key);
	}
	
	protected boolean load(final Long id) {
		return load(Key.create(model.getClass(), id));
	}

	protected boolean load(final String id) {
		return load(Key.create(model.getClass(), id));
	}

	protected boolean load(final Key key) {
		this.key = key;
		return ofy().transact(new Work<Boolean>() {
			@SuppressWarnings("unchecked")
			public Boolean run() {
				Object result = ofy().load().key(key).get();
				if (result != null) {
					model = (T)result;
					return true;
				}
				return false;
			}
		});
	}
	
	protected void save() {
		ofy().transact(new VoidWork() {
			public void vrun() {
				ofy().save().entity(model).now();
			}
		});
	}

	protected void delete() {
		ofy().transact(new VoidWork() {
			public void vrun() {
				ofy().delete().key(key).now();
			}
		});
	}
}
