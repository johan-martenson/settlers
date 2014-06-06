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

    private Rank rank;

    public Military(Rank r) {
        rank = r;
    }
    
    @Override
    public String toString() {
        return rank.name();
    }
}
