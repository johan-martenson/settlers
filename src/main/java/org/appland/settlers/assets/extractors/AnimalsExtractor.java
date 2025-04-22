package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.AnimalImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.gamefiles.MapBobs0Lst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.*;
import static org.appland.settlers.model.Material.*;

public class AnimalsExtractor {
    public static void extractAnimals(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        var mapBobs0Lst = LstDecoder.loadLstFile(fromDir + "/" + MapBobs0Lst.FILENAME, defaultPalette);
        var map0ZLst = LstDecoder.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);

        /* Extract animal animations */
        AnimalImageCollection iceBearImageCollection = new AnimalImageCollection("ice-bear");
        AnimalImageCollection foxImageCollection = new AnimalImageCollection("fox");
        AnimalImageCollection rabbitImageCollection = new AnimalImageCollection("rabbit");
        AnimalImageCollection stagImageCollection = new AnimalImageCollection("stag");
        AnimalImageCollection deerImageCollection = new AnimalImageCollection("deer");
        AnimalImageCollection sheepImageCollection = new AnimalImageCollection("sheep");
        AnimalImageCollection deer2ImageCollection = new AnimalImageCollection("deer2");
        AnimalImageCollection duckImageCollection = new AnimalImageCollection("duck");

        /* Ice bear */
        iceBearImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_EAST_ANIMATION, 6));
        iceBearImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_WEST_ANIMATION, 6));
        iceBearImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Fox */
        foxImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_EAST_ANIMATION, 6));
        foxImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_WEST_ANIMATION, 6));
        foxImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_WEST_ANIMATION, 6));
        foxImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_WEST_ANIMATION, 6));

        foxImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_EAST));
        foxImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_EAST));
        foxImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_WEST));
        foxImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_WEST));
        foxImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_WEST));
        foxImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_EAST));

        /* Rabbit */
        rabbitImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_EAST_ANIMATION, 6));
        rabbitImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_WEST_ANIMATION, 6));
        rabbitImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_WEST_ANIMATION, 6));

        /* Stag */
        stagImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_EAST_ANIMATION, 8));
        stagImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_WEST_ANIMATION, 8));
        stagImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_WEST_ANIMATION, 8));
        stagImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_WEST_ANIMATION, 8));

        stagImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_EAST));
        stagImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_EAST));
        stagImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_WEST));
        stagImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_WEST));
        stagImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_WEST));
        stagImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_EAST));

        /* Deer */
        deerImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_EAST_ANIMATION, 8));
        deerImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_WEST_ANIMATION, 8));
        deerImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_WEST_ANIMATION, 8));
        deerImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_WEST_ANIMATION, 8));

        deerImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_EAST));
        deerImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_EAST));
        deerImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_WEST));
        deerImageCollection.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_WEST));
        deerImageCollection.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_WEST));
        deerImageCollection.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_EAST));

        /* Sheep */
        sheepImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_EAST_ANIMATION, 2));
        sheepImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_WEST_ANIMATION, 2));
        sheepImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_WEST_ANIMATION, 2));

        /* Deer 2 (horse?) */
        deer2ImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_EAST_ANIMATION, 8));
        deer2ImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_WEST_ANIMATION, 8));
        deer2ImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_WEST_ANIMATION, 8));

        deer2ImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_EAST));
        deer2ImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_WEST));

        /* Extract duck */
        duckImageCollection.addImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST));
        duckImageCollection.addImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 1));
        duckImageCollection.addImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 2));
        duckImageCollection.addImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 3));
        duckImageCollection.addImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 4));
        duckImageCollection.addImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 5));

        duckImageCollection.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_SHADOW));

        iceBearImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        foxImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        rabbitImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        stagImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        deerImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        sheepImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        deer2ImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
        duckImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);

        /* Extract the donkey */
        AnimalImageCollection donkeyImageCollection = new AnimalImageCollection("donkey");

        donkeyImageCollection.addImages(EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_ANIMATION, 8));
        donkeyImageCollection.addImages(SOUTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_WEST_ANIMATION, 8));
        donkeyImageCollection.addImages(NORTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_EAST_ANIMATION, 8));

        donkeyImageCollection.addShadowImage(EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_SHADOW));
        donkeyImageCollection.addShadowImage(SOUTH_WEST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_SHADOW));

        donkeyImageCollection.addCargoImage(BEER, getImageAt(map0ZLst, Map0ZLst.DONKEY_BEER));
        donkeyImageCollection.addCargoImage(TONGS, getImageAt(map0ZLst, Map0ZLst.DONKEY_TONGS));
        donkeyImageCollection.addCargoImage(HAMMER, getImageAt(map0ZLst, Map0ZLst.DONKEY_HAMMER));
        donkeyImageCollection.addCargoImage(AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_AXE));
        donkeyImageCollection.addCargoImage(SAW, getImageAt(map0ZLst, Map0ZLst.DONKEY_SAW));
        donkeyImageCollection.addCargoImage(PICK_AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_PICK_AXE));
        donkeyImageCollection.addCargoImage(SHOVEL, getImageAt(map0ZLst, Map0ZLst.DONKEY_SHOVEL));
        donkeyImageCollection.addCargoImage(CRUCIBLE, getImageAt(map0ZLst, Map0ZLst.DONKEY_CRUCIBLE));
        donkeyImageCollection.addCargoImage(FISHING_ROD, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISHING_ROD));
        donkeyImageCollection.addCargoImage(SCYTHE, getImageAt(map0ZLst, Map0ZLst.DONKEY_SCYTHE));
        //donkeyImageCollection.addCargoImage(, getImageFromResourceLocation(map0ZLst, Map0ZLst.DONKEY_EMPTY_BARREL));
        donkeyImageCollection.addCargoImage(WATER, getImageAt(map0ZLst, Map0ZLst.DONKEY_WATER));
        donkeyImageCollection.addCargoImage(CLEAVER, getImageAt(map0ZLst, Map0ZLst.DONKEY_CLEAVER));
        donkeyImageCollection.addCargoImage(ROLLING_PIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROLLING_PIN));
        donkeyImageCollection.addCargoImage(BOW, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOW));
        donkeyImageCollection.addCargoImage(BOAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOAT));
        donkeyImageCollection.addCargoImage(SWORD, getImageAt(map0ZLst, Map0ZLst.DONKEY_SWORD));
        donkeyImageCollection.addCargoImage(IRON_BAR, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON_BAR));
        donkeyImageCollection.addCargoImage(FLOUR, getImageAt(map0ZLst, Map0ZLst.DONKEY_FLOUR));
        donkeyImageCollection.addCargoImage(FISH, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISH));
        donkeyImageCollection.addCargoImage(BREAD, getImageAt(map0ZLst, Map0ZLst.DONKEY_BREAD));
        donkeyImageCollection.addNationSpecificCargoImage(ROMANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROMAN_SHIELD));
        donkeyImageCollection.addCargoImage(WOOD, getImageAt(map0ZLst, Map0ZLst.DONKEY_WOOD));
        donkeyImageCollection.addCargoImage(PLANK, getImageAt(map0ZLst, Map0ZLst.DONKEY_PLANK));
        donkeyImageCollection.addCargoImage(STONE, getImageAt(map0ZLst, Map0ZLst.DONKEY_STONE));
        donkeyImageCollection.addNationSpecificCargoImage(VIKINGS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_VIKING_SHIELD));
        donkeyImageCollection.addNationSpecificCargoImage(AFRICANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_AFRICAN_SHIELD));
        donkeyImageCollection.addCargoImage(WHEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_WHEAT));
        donkeyImageCollection.addCargoImage(COIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_COIN));
        donkeyImageCollection.addCargoImage(GOLD, getImageAt(map0ZLst, Map0ZLst.DONKEY_GOLD));
        donkeyImageCollection.addCargoImage(IRON, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON));
        donkeyImageCollection.addCargoImage(COAL, getImageAt(map0ZLst, Map0ZLst.DONKEY_COAL));
        donkeyImageCollection.addCargoImage(MEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_MEAT));
        donkeyImageCollection.addCargoImage(PIG, getImageAt(map0ZLst, Map0ZLst.DONKEY_PIG));
        donkeyImageCollection.addNationSpecificCargoImage(JAPANESE, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_JAPANESE_SHIELD));

        donkeyImageCollection.writeImageAtlas(toDir + "/animals/", defaultPalette);
    }
}
