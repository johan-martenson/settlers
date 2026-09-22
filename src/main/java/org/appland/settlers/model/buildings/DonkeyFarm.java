package org.appland.settlers.model.buildings;

import org.appland.settlers.model.InvalidGameLogicException;
import org.appland.settlers.model.Player;

import java.util.ArrayList;
import java.util.List;

import static org.appland.settlers.model.Material.*;
import static org.appland.settlers.model.Size.LARGE;

/**
 *
 * @author johan
 */
@HouseSize(size = LARGE, material = {PLANK, PLANK, PLANK, STONE, STONE, STONE})
@Production(output = DONKEY, requiredGoods = {WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WHEAT, WATER, WATER, WATER, WATER, WATER, WATER})
@RequiresWorker(workerType = HELPER)
public class DonkeyFarm extends Building {
    private List<BreedingDonkey> donkeys = new ArrayList<>();

    public DonkeyFarm(Player player0) {
        super(player0);
    }

    public List<BreedingDonkey> getDonkeys() {
        return donkeys;
    }

    public void setNumberOfDonkeys(int numberOfDonkeys) {
        var currentNumberOfDonkeys = donkeys.size();
        var diff = numberOfDonkeys - currentNumberOfDonkeys;

        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                donkeys.add(new BreedingDonkey(indexToDonkeySlot(i + currentNumberOfDonkeys)));
            }
        } else if (diff < 0) {
            for (int i = 0; i < Math.abs(diff); i++) {
                donkeys.removeLast();
            }
        }
    }

    private DonkeySlot indexToDonkeySlot(int index) {
        return switch (index) {
            case 0 -> DonkeySlot.SLOT_1;
            case 1 -> DonkeySlot.SLOT_2;
            case 2 -> DonkeySlot.SLOT_3;
            default -> throw new InvalidGameLogicException("Invalid index: " + index);
        };
    }

    public enum DonkeySlot {SLOT_2, SLOT_3, SLOT_1}

    public record BreedingDonkey(DonkeySlot slot) { }
}
