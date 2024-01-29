package org.appland.settlers.assets.resources;

public class Timbre {
    final short patch; // uint 8
    final short bank; // uint 8

    public Timbre(short patch, short bank) {
        this.patch = patch;
        this.bank = bank;
    }
}
