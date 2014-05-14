/**
 * 
 */
package org.appland.settlers.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author johan
 *
 */
public class Player {
	
	List<Military> army;
	List<Road> roads;
	List<Flag> flags;
	
	/* Buildings */
	List<Storage> storages;
	
	public Player() {
		storages = new ArrayList<Storage>();
		
		storages.add(Storage.createStorage());
	}
	
	public List<Storage> getStorages() {
		return storages;
	}

	public static Player createInitialPlayer() {

		return new Player();
	}
}
