/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

import org.appland.settlers.model.DecorationType;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.Size;
import org.appland.settlers.model.StoneType;
import org.appland.settlers.model.Tree.TreeType;
import org.appland.settlers.model.TreeSize;

import static org.appland.settlers.maps.Translator.DEFAULT_OBJECT_PROPERTY_TO_DECORATION_MAP;

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
    private static final int DECORATIVE_OBJECT_TYPE = 0x02;
    private static final int GRANITE_OBJECT_TYPE = 0x03;
    private static final int NONE_OBJECT_TYPE = 0x00;

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
    private TreeType treeType;
    private TreeSize treeSize;
    private DecorationType decorativeObject;
    private StoneType stoneType;
    private short stoneAmount;

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

    void setObjectProperties(short unsignedByteInArray) {
        objectProperties = unsignedByteInArray;
    }

    void setObjectType(short objectType) {

        // Store the object type
        this.objectType = objectType;

        // Interpret the object type
        int variant = objectType & 0x03;
        int type = (objectType >> 2) & 0x03;
        int info = objectType >> 4;

        // Read the info block
        boolean isTimeLimited = false;
        boolean isBurntBuilding = false;
        boolean isTerrainObject = false;
        boolean objectExists = false;

        if ((info & TIME_LIMITED_MASK) > 0) {
            isTimeLimited = true;
        }

        if ((info & BURNT_BUILDING_MASK) > 0) {
            isBurntBuilding = true;
        }

        if ((info & TERRAIN_OBJECT_MASK) > 0) {
            isTerrainObject = true;
        }

        if ((info & OBJECT_EXISTS_MASK) > 0) {
            objectExists = true;
        }

        // Manage objects that are not burnt or time limited
        if (!isTimeLimited && !isBurntBuilding && objectExists) {

            switch (type) {
                case TREE_OBJECT_TYPE:
                    int id = (variant << 2) & (objectProperties >> 6);
                    int isCut = (objectProperties >> 3) & 0x01;
                    int size = (objectProperties >> 4) & 0x03;
                    int step = objectProperties & 0x07;

                    treeType = TreeTranslator.DEFAULT_ID_TO_TREE_TYPE_MAP.get(id);
                    treeSize = TreeTranslator.TREE_SIZE_MAP.get(size);

                    // TODO: consider the terrain type and pick tree types accordingly (only greenland for now)

                    if (treeType == null) {
                        throw new RuntimeException("Can't handle this tree type yet: " + id);
                    }

                    if (treeSize == null) {
                        throw new RuntimeException("Can't handle tree size: " + size);
                    }

                    // TODO: consider isCut

                    break;
                case DECORATIVE_OBJECT_TYPE:

                    // TODO: pick the decoration type based on the MAPBOBS.LST / MIS#BOBS.LST file used

                    this.decorativeObject = DEFAULT_OBJECT_PROPERTY_TO_DECORATION_MAP.get((int)objectProperties);

                    break;
                case GRANITE_OBJECT_TYPE:
                    stoneType = StoneType.STONE_2;

                    if (variant == 0) {
                        stoneType = StoneType.STONE_1;
                    }

                    stoneAmount = objectProperties;

                    break;
                case NONE_OBJECT_TYPE:
                    break;
                default:
                    throw new RuntimeException("Unknown object type value: " + type);
            }
        }
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
        return stoneType != null;
    }

    StoneType getStoneType() {
        return stoneType;
    }

    short getStoneAmount() {
        return stoneAmount;
    }

    boolean hasTree() {
        return treeType != null;
    }

    boolean isNatureDecoration() {
        return this.decorativeObject != null;
    }

    DecorationType getNatureDecorationType() {
        return decorativeObject;
    }

    TreeType getTreeType() {
        return treeType;
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

    public TreeSize getTreeSize() {
        return treeSize;
    }
}
