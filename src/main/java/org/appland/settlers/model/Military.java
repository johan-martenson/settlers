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

    public void enterBuilding(Building b) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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
    public void stepTime() {
        super.stepTime();
    }
    
    @Override
    public String toString() {
        return rank.name();
    }
}
