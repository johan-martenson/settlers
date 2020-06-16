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

    void putCargo(Cargo cargo) throws InvalidMaterialException, InvalidRouteException;

    Point getPosition();
}
