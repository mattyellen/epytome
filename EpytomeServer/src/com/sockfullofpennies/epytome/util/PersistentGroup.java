package com.sockfullofpennies.epytome.util;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;

abstract public class PersistentGroup <T, TM> {
	private List<Key<TM>> keyList;
	private Class modelClass;
	
	public PersistentGroup(Class mc) {
		keyList = new LinkedList<Key<TM>>();
		modelClass = mc;
	}

	public void add(Long id) {
		add(Key.create(modelClass, id));
	}

	public void add(String id) {
		add(Key.create(modelClass, id));
	}

	public void add(Key key) {
		keyList.add(key);
	}

	public void reset() {
		keyList = new LinkedList<Key<TM>>();
	}
	
	//I might need a transacted version of this in the future, but that would require
	//all these object to be in the same entity group.
	public List<T> createAll() {
		Map<Key<TM>, TM> map = ofy().load().keys(keyList);
		
		List<T> objList = new LinkedList<T>();
		for (TM model : map.values()) {
			objList.add(createInstance(model));
		}
		
		return objList;
	}
	
	abstract protected T createInstance(TM model); 
}
