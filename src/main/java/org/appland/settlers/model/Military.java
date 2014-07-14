/**
 *
 */
package org.appland.settlers.model;

/**
 * @author johan
 *
 */
@Walker(speed = 10)
public class Military extends Worker {

    Rank getRank() {
        return rank;
    }

    public enum Rank {

        PRIVATE_RANK,
        SERGEANT_RANK,
        GENERAL_RANK
    }

    private final Rank rank;

    public Military(Rank r) {
        this(r, null);
    }
    
    public Military(Rank r, GameMap map) {
        super(map);

        rank = r;
    }

    @Override
    public String toString() {
        return rank.name();
    }
}
