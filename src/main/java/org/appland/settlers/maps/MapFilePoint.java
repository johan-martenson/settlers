/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.Stone;
import org.appland.settlers.model.Tree;
import org.appland.settlers.model.Tree.TreeType;

/**
 *
 * @author johan
 */
class MapFilePoint {
    private static final short DEAD_TREE = 0x1F;
    private static final short NATURE_DECORATION_1 = 0xC8;
    private static final short NATURE_DECORATION_2 = 0xC9;
    private static final int TIME_LIMITED_MASK = 0x01;
    private static final int BURNT_BUILDING_MASK = 0x02;
    private static final int TERRAIN_OBJECT_MASK = 0x04;
    private static final int OBJECT_EXISTS_MASK = 0x08;
    private static final int TREE_OBJECT_TYPE = 0x01;
    private static final int GRANITE_OBJECT_TYPE = 0x03;
    private static final short DECORATION_MASK = 64;

    private int height;
    private Texture textureDown;
    private Texture textureDownRight;
    private short objectProperties;
    private short objectType;
    private Animal animal;
    private BuildableSite buildableSite;
    private Resource resource;
    private Point gamePointPosition;
    private boolean isPossibleHarbor;
    private java.awt.Point mapFilePosition;
    private int variant;
    private int type;
    private int info;
    private boolean isTimeLimited;
    private boolean isBurntBuilding;
    private boolean isTerrainObject;
    private boolean objectExists;

    public MapFilePoint() {
        isPossibleHarbor = false;
    }

    void setHeight(int heightAtPoint) {
        height = heightAtPoint;
    }

    void setVegetationBelow(Texture texture) {
        textureDown = texture;
    }

    Texture getVegetationBelow() {
        return textureDown;
    }

    void setVegetationDownRight(Texture texture) {
        textureDownRight = texture;
    }

    Texture getVegetationDownRight() {
        return textureDownRight;
    }

    void setObjectProperties(short objectProperties) {
        this.objectProperties = objectProperties;
    }

    void setObjectType(short objectType) {
        this.objectType = objectType;

        // Interpret the object type
        this.variant = objectType & 0x03;
        this.type = (objectType >> 2) & 0x03;
        this.info = objectType >> 4;

        // Read the info block
        this.isTimeLimited = (info & TIME_LIMITED_MASK) > 0;
        this.isBurntBuilding = (info & BURNT_BUILDING_MASK) > 0;
        this.isTerrainObject = (info & TERRAIN_OBJECT_MASK) > 0;
        this.objectExists = (info & OBJECT_EXISTS_MASK) > 0;
    }

    void setAnimal(Animal animal) {
        this.animal = animal;
    }

    void setBuildableSite(BuildableSite site) {
        buildableSite = site;
    }

    void setResource(Resource resource) {
        this.resource = resource;
    }

    boolean hasMineral() {
        return resource != null && resource.type != null &&
               (resource.type == ResourceType.COAL     ||
                resource.type == ResourceType.GOLD     ||
                resource.type == ResourceType.IRON_ORE ||
                resource.type == ResourceType.GRANITE) &&
                resource.amount > 0;
    }

    ResourceType getMineralType() {
        return resource.type;
    }

    Size getMineralQuantity() {

        /* Amount goes from 0 to 7. 0 means there is no more minerals
        *
        * Map 1-2 to SMALL, 3-4 to MEDIUM, 5-7 to LARGE
        *
        * */

        if (resource.amount > 4) {
            return Size.LARGE;
        } else if (resource.amount > 2) {
            return Size.MEDIUM;
        } else {
            return Size.SMALL;
        }
    }

    boolean hasStone() {
        return type == GRANITE_OBJECT_TYPE;
    }

    Stone.StoneType getStoneType() {
        return variant == 0 ? Stone.StoneType.STONE_1 : Stone.StoneType.STONE_2;
    }

    short getStoneAmount() {
        return objectProperties;
    }

    boolean hasTree() {
        return type == TREE_OBJECT_TYPE;
    }

    boolean hasDecoration() {
        return (objectType & DECORATION_MASK) != 0;
    }

    DecorationType getNatureDecorationType() {
        return Translator.DEFAULT_OBJECT_PROPERTY_TO_DECORATION_MAP.get((int)objectProperties);
    }

    TreeType getTreeType() {
        int id = (variant << 2) | ((objectProperties >> 6) & 0x03);

        return TreeTranslator.DEFAULT_ID_TO_TREE_TYPE_MAP.get(id);
    }

    public boolean hasWildAnimal() {
        return animal != null;
    }

    public void setPositionAsGamePoint(Point gamePoint) {
        gamePointPosition = gamePoint;
    }

    public Point getGamePointPosition() {
        return gamePointPosition;
    }

    public BuildableSite getBuildableSite() {
        return buildableSite;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasDeadTree() {
        return (objectType == NATURE_DECORATION_1 || objectType == NATURE_DECORATION_2) && objectProperties == DEAD_TREE;
    }

    public boolean isPossiblePlaceForHarbor() {
        return isPossibleHarbor;
    }

    public void setPossibleHarbor() {
        isPossibleHarbor = true;
    }

    public Tree.TreeSize getTreeSize() {
        var sizeIndex = (objectProperties >> 4) & 0x03;

        return TreeTranslator.TREE_SIZE_MAP.get(sizeIndex);
    }

    public java.awt.Point getPosition() {
        return mapFilePosition;
    }

    public void setPosition(java.awt.Point point) {
        mapFilePosition = point;
    }
}
