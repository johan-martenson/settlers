/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

/**
 *
 * @author johan
 */
public enum Animal {
    NO_ANIMAL(0),
    RABBIT(1),
    FOX(2),
    STAG(3),
    DEER(4),
    DUCK(5),
    SHEEP(6),
    DEER_2(7),
    DUCK_2(8),
    PACK_DONKEY(9);

    private final int id;

    Animal(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }

    public boolean isWildAnimal() {
        return id == RABBIT.getValue() ||
               id == FOX.getValue()    ||
               id == STAG.getValue()   ||
               id == DEER.getValue()   ||
               id == DUCK.getValue()   ||
               id == SHEEP.getValue()  ||
               id == DEER_2.getValue() ||
               id == DUCK_2.getValue();
    }

    static Animal animalFromInt(short i) {
        return switch (i) {
            case 0 -> NO_ANIMAL;
            case 1 -> RABBIT;
            case 2 -> FOX;
            case 3 -> STAG;
            case 4 -> DEER;
            case 5 -> DUCK;
            case 6 -> SHEEP;
            case 7 -> DEER_2;
            case 8 -> DUCK_2;
            case 9 -> PACK_DONKEY;
            default -> NO_ANIMAL;
        };
    }
}
