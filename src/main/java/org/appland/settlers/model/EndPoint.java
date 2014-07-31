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
public interface EndPoint {

    public Iterable<Cargo> getStackedCargo();
    
    public void putCargo(Cargo c) throws Exception;
    
    public boolean hasCargoWaitingForRoad(Road r);

    public Cargo retrieveCargo(Cargo c);

    public Cargo getCargoWaitingForRoad(Road r);

    public Point getPosition();
}
