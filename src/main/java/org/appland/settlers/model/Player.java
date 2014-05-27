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
	
	/* Buildings */
	List<Storage> storages;
	
	public Player() {
		storages = new ArrayList<>();
		
		storages.add(Storage.createStorage());
	}
	
	public List<Storage> getStorages() {
		return storages;
	}

	public static Player createInitialPlayer() {

		return new Player();
	}
}
