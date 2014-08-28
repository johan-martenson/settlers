/**
 *
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Military.Rank.GENERAL_RANK;
import static org.appland.settlers.model.Military.Rank.SERGEANT_RANK;

/**
 * @author johan
 *
 */
@Walker(speed = 10)
public class Military extends Worker {

    public enum Rank {

        PRIVATE_RANK,
        SERGEANT_RANK,
        GENERAL_RANK
    }

    private Rank rank;

    public Military(Rank r) {
        this(r, null);
    }
    
    public Military(Rank r, GameMap map) {
        super(map);

        rank = r;
    }

    public Rank getRank() {
        return rank;
    }

    void promote() {
        switch (rank) {
        case PRIVATE_RANK:
            rank = SERGEANT_RANK;
            break;
        case SERGEANT_RANK:
            rank = GENERAL_RANK;
            break;
        default:
        }
    }

    @Override
    public String toString() {
        return rank.name();
    }
}
