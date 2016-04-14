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
public class InvalidUserActionException extends Exception {

    public InvalidUserActionException(String msg) {
        super(msg);
    }
}
