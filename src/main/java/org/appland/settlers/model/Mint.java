/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.appland.settlers.model;

import static org.appland.settlers.model.Material.COAL;
import static org.appland.settlers.model.Material.COIN;
import static org.appland.settlers.model.Material.GOLD;
import static org.appland.settlers.model.Material.MINTER;
import static org.appland.settlers.model.Size.MEDIUM;

/**
 *
 * @author johan
 */
@HouseSize(size = MEDIUM)
@Production(requiredGoods = {GOLD, COAL}, output = COIN)
@RequiresWorker(workerType = MINTER)
public class Mint extends Building {}