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
public class Countdown {

    private int count = -1;
    private int startedAt;

    public void countFrom(int i) {
        count = i;
        startedAt = i;
    }

    public void step() {
        count--;
    }

    public boolean hasReachedZero() {
        return count == 0;
    }

    public boolean isActive() {
        return count != -1;
    }

    public boolean isCounting() {
        return count > 0;
    }

    public int getCount() {
        return count;
    }

    int getStartedAt() {
        return startedAt;
    }
}
