/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

/**
 *
 * @author johan
 */
class Countdown {

    int count = -1;
    
    public Countdown() {
    }

    void countFrom(int i) {
        count = i;
    }

    void step() {
        count--;
    }

    boolean reachedZero() {
        return count == 0;
    }

    boolean isActive() {
        return count != -1;
    }

    boolean isInactive() {
        return !isActive();
    }

    void reset() {
        count = -1;
    }

    boolean isCounting() {
        return count > 0;
    }

    int getCount() {
        return count;
    }
}
