package org.appland.settlers.model;

import static org.appland.settlers.model.Building.ConstructionState.BURNING;
import static org.appland.settlers.model.Building.ConstructionState.DESTROYED;
import static org.appland.settlers.model.Building.ConstructionState.DONE;
import static org.appland.settlers.model.Building.ConstructionState.UNDER_CONSTRUCTION;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Building implements Actor {

	public enum ConstructionState {
		UNDER_CONSTRUCTION,
		DONE,
		BURNING,
		DESTROYED
	}

	protected ConstructionState constructionState;
	protected int constructionCountdown;
	protected Map<Material, Integer> queue;
	
	private int destructionCountdown;
	private int productionCountdown;
	private Flag flag;
	private Cargo outputCargo; 
	
	private Logger log = Logger.getLogger(Building.class.getName());
	
	public Building() {
		constructionState = ConstructionState.UNDER_CONSTRUCTION;
		queue = createEmptyQueue();
		constructionCountdown = getConstructionCountdown(this);
		outputCargo = null;
		flag = Flag.createFlag(null);
		productionCountdown = -1;
	}

	public Flag getFlag() {
		return flag;
	}

    private Map<Material, Integer> createEmptyQueue() {
	Map<Material, Integer> result = new HashMap<Material, Integer>();

	for (Material m : Material.values()) {
	    result.put(m, 0);
	}

	return result;
    }
	
	private int getConstructionCountdown(Building building) {
		HouseSize sizeAnnotation = building.getClass().getAnnotation(HouseSize.class);
		int constructionTime = 100;
		
		switch (sizeAnnotation.size()) {
		case SMALL:
			constructionTime = 100;
			break;
		case MEDIUM:
			constructionTime = 150;
			break;
		case LARGE:
			constructionTime = 200;
			break;
		}
		
		return constructionTime;
	}

	public Object getConstructionState() {
		return constructionState;
	}

	public void stepTime() {
		if (constructionState == UNDER_CONSTRUCTION) {
			constructionCountdown--;
	
			if (constructionCountdown == 0) {
				constructionState = DONE;
			}
		} else if (constructionState == BURNING) {
			destructionCountdown--;
	
			if (destructionCountdown == 0) {
				constructionState = DESTROYED;
			}
		} else if (constructionState == DONE && outputCargo == null) {
			System.out.println("Calling produce");
			outputCargo = produce();
			System.out.println("Result from produce is " + outputCargo);
		}
	}

	public void tearDown() {
		constructionState = ConstructionState.BURNING;
		destructionCountdown = 50;
	}
	
	private Cargo produce() {
		Cargo result = null;

		System.out.println("Production countdown is " + productionCountdown);
		
		/* Construction hasn't started */
		if (productionCountdown == -1) {
		    if (productionCanStart(this)) {
			productionCountdown = getProductionTime(this) - 2;
		    }

		/* Construction ongoing and not finished */
		} else if (productionCountdown > 0) {
		    productionCountdown--;

		/* Construction just finished */
		} else if (productionCountdown == 0) {
		    System.out.println("Creating cargo");
		    
		    result = Cargo.createCargo(getProductionMaterial(this));
		    
		    productionCountdown = -1;
		    consumeResources(this);
		}
		
		return result;
	}

	private void consumeResources(Building building) {
		Map<Material, Integer> requiredGoods = getRequiredGoods(building);
		
		for (Material m : requiredGoods.keySet()) {
			int before = queue.get(m);
			int cost = requiredGoods.get(m);
			queue.put(m, before - cost);
		}
	}

	private boolean productionCanStart(Building building) {
		Map<Material, Integer> requiredGoods = getRequiredGoods(building);
		
		if (requiredGoods.keySet().isEmpty()) {
			return true;
		}
		
		boolean resourcesPresent = true;
		
		for (Material m : requiredGoods.keySet()) {
			int amount = requiredGoods.get(m);
			
			if (building.queue.get(m) < amount) {
				resourcesPresent = false;
			}
		}
		
		return resourcesPresent;
	}

	public int getProductionTime(Building building) {
		Production p = building.getClass().getAnnotation(Production.class);
		
		return p.productionTime();
	}
	
	public Material getProductionMaterial(Building building) {
		Production p = building.getClass().getAnnotation(Production.class);
		
		return p.output();
	}
	
	public Map<Material, Integer> getRequiredGoods(Building building) {
		log.log(Level.INFO, "Getting the required goods for this building");
		
		Production p = building.getClass().getAnnotation(Production.class);
		Map<Material, Integer> requiredGoods = new HashMap<Material, Integer>();

		log.log(Level.FINE, "Found annotations for {0} in class", requiredGoods);
		
		Material[] goods = p.requiredGoods();
		
		for (Material m : goods) {
			if (!requiredGoods.containsKey(m)) {
				requiredGoods.put(m, 0);
			}
			
			requiredGoods.put(m, 1);
		}
		
		return requiredGoods;
	}

	public int getQueue(Material material) throws InvalidMaterialException {
		if (!isAccepted(material, this)) {
			throw new InvalidMaterialException(material);
		}
		
		if (!queue.containsKey(material)) {
			return 0;
		}
		
		return queue.get(material);
	}

	private boolean isAccepted(Material material, Building building) {
		Map<Material, Integer> requiredGoods = getRequiredGoods(building);
		
		if (requiredGoods.containsKey(material)) {
			return true;
		} else {
			return false;
		}

	}

	public void deliver(Cargo c)
	    throws InvalidMaterialException, DeliveryNotPossibleException, InvalidStateForProduction {
	    log.log(Level.INFO, "Adding cargo {0} to queue ({1})", new Object[] {c, queue});

	    Material material = c.getMaterial();
	    if (constructionState != DONE) {
		/* Can't accept delivery when building is unfinished or destroyed */
		throw new InvalidStateForProduction(this);
	    } else if (!canAcceptGoods(this)) {
		throw new DeliveryNotPossibleException();
	    } else if (!isAccepted(material, this)) {
		throw new InvalidMaterialException(material);
	    }
	    
	    int existingQuantity = 0;
	    
	    if (queue.containsKey(material)) {
		existingQuantity = queue.get(material);
	    }
	    
	    queue.put(material, existingQuantity + 1);
	}

	private boolean canAcceptGoods(Building building) {
		Map<Material, Integer> requiredGoods = building.getRequiredGoods(building);
		
		if (requiredGoods.keySet().isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public boolean outputAvailable() {
		System.out.println("Output cargo is " + outputCargo);
		
		if (outputCargo == null) {
			return false;
		} else {
			return true;
		}
	}

	public Cargo retrieveCargo() {
	    Cargo result = outputCargo;
	    outputCargo = null;

	    if (result == null) {
		return null;
	    }
		
	    System.out.println("Cargo created of type " + result.getMaterial());
		
	    result.setPosition(this.getFlag().getPosition());
		
	    return result;
	}

	public boolean needsMaterial(Material wood) {
		log.log(Level.INFO, "Does this building require {0}", wood);
		
		Map<Material, Integer> requiredGoods = getRequiredGoods(this);
		
		if (!requiredGoods.containsKey(wood)) {
			/* Building does not accept the material */
			log.log(Level.FINE, "This building does not accept {0}", wood);
			return false;
		}
		
		if (!queue.containsKey(wood)) {
			/* Building does not have any cargos of the material in its queue */
			log.log(Level.FINE, "This building requires {0} and has no {0} in its queue", wood);
			return true;
		}
		
		int neededAmount = requiredGoods.get(wood);
		
		if (queue.get(wood) >= neededAmount) {
			/* Building has all the cargos it needs of the material */
			log.log(Level.FINE, "This building has all the {0} it needs", wood);
			return false;
		}
		
		log.log(Level.FINE, "This building requires {0}", wood);
		return true;
	}
	
	public String buildingToString() {
		return " at " + flag + " with " + queue + " in queue and " + outputCargo + " waiting to be picked up";
	}

	public boolean cargoIsReady() {
		// TODO Auto-generated method stub
		return false;
	}
}
