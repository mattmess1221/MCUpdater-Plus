package mcupdater;

import java.util.ArrayList;

import mcupdater.update.IUpdatable;

public class UpdatableList<T extends IUpdatable> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns an object that refers to the same updatable thing.
	 */
	public T get(IUpdatable updatable){
		for(T update : this){
			if(updatable.equals(update))
				return update;
		}
		return null;
	}
}
