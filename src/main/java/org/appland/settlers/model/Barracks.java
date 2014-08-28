/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.model;

import static org.appland.settlers.model.Size.SMALL;

@HouseSize(size = SMALL)
@MilitaryBuilding(maxHostedMilitary = 2, defenceRadius = 6, maxCoins = 2)
public class Barracks extends Building {
}
