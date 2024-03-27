package org.appland.settlers.model.buildings;

import org.appland.settlers.model.InvalidUserActionException;
import org.appland.settlers.model.Player;

import static org.appland.settlers.model.Material.PLANK;
import static org.appland.settlers.model.Material.SCOUT;
import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL, material = {PLANK, PLANK, PLANK, PLANK})
@RequiresWorker(workerType = SCOUT)
public class LookoutTower extends Building {
    public LookoutTower(Player player) {
        super(player);
    }

    @Override
    public void stopProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot stop production in barracks.");
    }

    @Override
    public void resumeProduction() throws InvalidUserActionException {
        throw new InvalidUserActionException("Cannot resume production in barracks.");
    }
}
