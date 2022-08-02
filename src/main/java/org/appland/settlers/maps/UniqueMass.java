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
class UniqueMass {

    final MassType type;
    final java.awt.Point position;
    final long totalMass;

    public UniqueMass(MassType type, java.awt.Point position, long totalMass) {
        this.type = type;
        this.position = position;
        this.totalMass = totalMass;
    }
}
