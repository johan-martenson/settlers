package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.AnimalImageCollection;
import org.appland.settlers.assets.collectors.PigImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.Map0ZLst;
import org.appland.settlers.assets.gamefiles.MapBobs0Lst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.actors.Pig;

import java.io.IOException;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.*;
import static org.appland.settlers.model.Material.*;

public class AnimalsExtractor {
    private static void log(String log) {
        System.out.println(log);
    }

    public static void extractAnimals(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        var mapBobs0Lst = LstDecoder.loadLstFile(fromDir + "/" + MapBobs0Lst.FILENAME, defaultPalette);
        var map0ZLst = LstDecoder.loadLstFile(fromDir + "/" + Map0ZLst.FILENAME, defaultPalette);

        log("Extracting animals:");

        // Extract animal animations
        var iceBear = new AnimalImageCollection("ice-bear");
        var fox = new AnimalImageCollection("fox");
        var rabbit = new AnimalImageCollection("rabbit");
        var stag = new AnimalImageCollection("stag");
        var deer = new AnimalImageCollection("deer");
        var sheep = new AnimalImageCollection("sheep");
        var deer2 = new AnimalImageCollection("deer2");
        var duck = new AnimalImageCollection("duck");

        iceBear.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_EAST_ANIMATION));
        iceBear.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_EAST_ANIMATION));
        iceBear.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_EAST_ANIMATION));
        iceBear.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_SOUTH_WEST_ANIMATION));
        iceBear.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_WEST_ANIMATION));
        iceBear.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.ICE_BEAR_WALKING_NORTH_WEST_ANIMATION));

        fox.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_EAST_ANIMATION));
        fox.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_EAST_ANIMATION));
        fox.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_EAST_ANIMATION));
        fox.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_SOUTH_WEST_ANIMATION));
        fox.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_WEST_ANIMATION));
        fox.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.FOX_WALKING_NORTH_WEST_ANIMATION));

        fox.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_EAST));
        fox.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_EAST));
        fox.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_SOUTH_WEST));
        fox.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_WEST));
        fox.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_WEST));
        fox.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.FOX_SHADOW_NORTH_EAST));

        rabbit.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_EAST_ANIMATION));
        rabbit.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_EAST_ANIMATION));
        rabbit.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_EAST_ANIMATION));
        rabbit.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_SOUTH_WEST_ANIMATION));
        rabbit.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_WEST_ANIMATION));
        rabbit.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.RABBIT_WALKING_NORTH_WEST_ANIMATION));

        stag.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_EAST_ANIMATION));
        stag.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_EAST_ANIMATION));
        stag.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_EAST_ANIMATION));
        stag.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_SOUTH_WEST_ANIMATION));
        stag.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_WEST_ANIMATION));
        stag.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.STAG_WALKING_NORTH_WEST_ANIMATION));

        stag.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_EAST));
        stag.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_EAST));
        stag.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_SOUTH_WEST));
        stag.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_WEST));
        stag.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_WEST));
        stag.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.STAG_SHADOW_NORTH_EAST));

        deer.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_EAST_ANIMATION));
        deer.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_EAST_ANIMATION));
        deer.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_EAST_ANIMATION));
        deer.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_SOUTH_WEST_ANIMATION));
        deer.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_WEST_ANIMATION));
        deer.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_WALKING_NORTH_WEST_ANIMATION));

        deer.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_EAST));
        deer.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_EAST));
        deer.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_SOUTH_WEST));
        deer.addShadowImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_WEST));
        deer.addShadowImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_WEST));
        deer.addShadowImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_SHADOW_NORTH_EAST));

        sheep.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_EAST_ANIMATION));
        sheep.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_EAST_ANIMATION));
        sheep.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_EAST_ANIMATION));
        sheep.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_SOUTH_WEST_ANIMATION));
        sheep.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_WEST_ANIMATION));
        sheep.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.SHEEP_WALKING_NORTH_WEST_ANIMATION));

        deer2.addImages(NORTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_EAST_ANIMATION));
        deer2.addImages(EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_EAST_ANIMATION));
        deer2.addImages(SOUTH_EAST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_EAST_ANIMATION));
        deer2.addImages(SOUTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_SOUTH_WEST_ANIMATION));
        deer2.addImages(WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_WEST_ANIMATION));
        deer2.addImages(NORTH_WEST, getImagesAt(mapBobsLst, MapBobsLst.DEER_2_WALKING_NORTH_WEST_ANIMATION));

        deer2.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_EAST));
        deer2.addShadowImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_EAST));
        deer2.addShadowImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DEER_2_SHADOW_SOUTH_WEST));

        duck.addImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST));
        duck.addImage(SOUTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 1));
        duck.addImage(SOUTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 2));
        duck.addImage(WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 3));
        duck.addImage(NORTH_WEST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 4));
        duck.addImage(NORTH_EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_EAST + 5));

        duck.addShadowImage(EAST, getImageAt(mapBobsLst, MapBobsLst.DUCK_SHADOW));

        iceBear.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Ice bear");

        fox.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Fox");

        rabbit.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Rabbit");

        stag.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Stag");

        deer.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Deer");

        sheep.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Sheep");

        deer2.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Deer 2");

        duck.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Duck");

        var donkey = new AnimalImageCollection("donkey");

        donkey.addImages(EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_ANIMATION, 8));
        donkey.addImages(SOUTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_ANIMATION, 8));
        donkey.addImages(SOUTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_ANIMATION, 8));
        donkey.addImages(WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_WEST_ANIMATION, 8));
        donkey.addImages(NORTH_WEST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_WEST_ANIMATION, 8));
        donkey.addImages(NORTH_EAST, getImagesAt(mapBobs0Lst, MapBobs0Lst.DONKEY_NORTH_EAST_ANIMATION, 8));

        donkey.addShadowImage(EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_EAST_SHADOW));
        donkey.addShadowImage(SOUTH_EAST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_EAST_SHADOW));
        donkey.addShadowImage(SOUTH_WEST, getImageAt(mapBobs0Lst, MapBobs0Lst.DONKEY_SOUTH_WEST_SHADOW));

        donkey.addCargoImage(BEER, getImageAt(map0ZLst, Map0ZLst.DONKEY_BEER));
        donkey.addCargoImage(TONGS, getImageAt(map0ZLst, Map0ZLst.DONKEY_TONGS));
        donkey.addCargoImage(HAMMER, getImageAt(map0ZLst, Map0ZLst.DONKEY_HAMMER));
        donkey.addCargoImage(AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_AXE));
        donkey.addCargoImage(SAW, getImageAt(map0ZLst, Map0ZLst.DONKEY_SAW));
        donkey.addCargoImage(PICK_AXE, getImageAt(map0ZLst, Map0ZLst.DONKEY_PICK_AXE));
        donkey.addCargoImage(SHOVEL, getImageAt(map0ZLst, Map0ZLst.DONKEY_SHOVEL));
        donkey.addCargoImage(CRUCIBLE, getImageAt(map0ZLst, Map0ZLst.DONKEY_CRUCIBLE));
        donkey.addCargoImage(FISHING_ROD, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISHING_ROD));
        donkey.addCargoImage(SCYTHE, getImageAt(map0ZLst, Map0ZLst.DONKEY_SCYTHE));
        //donkeyImageCollection.addCargoImage(, getImageFromResourceLocation(map0ZLst, Map0ZLst.DONKEY_EMPTY_BARREL));
        donkey.addCargoImage(WATER, getImageAt(map0ZLst, Map0ZLst.DONKEY_WATER));
        donkey.addCargoImage(CLEAVER, getImageAt(map0ZLst, Map0ZLst.DONKEY_CLEAVER));
        donkey.addCargoImage(ROLLING_PIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROLLING_PIN));
        donkey.addCargoImage(BOW, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOW));
        donkey.addCargoImage(BOAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_BOAT));
        donkey.addCargoImage(SWORD, getImageAt(map0ZLst, Map0ZLst.DONKEY_SWORD));
        donkey.addCargoImage(IRON_BAR, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON_BAR));
        donkey.addCargoImage(FLOUR, getImageAt(map0ZLst, Map0ZLst.DONKEY_FLOUR));
        donkey.addCargoImage(FISH, getImageAt(map0ZLst, Map0ZLst.DONKEY_FISH));
        donkey.addCargoImage(BREAD, getImageAt(map0ZLst, Map0ZLst.DONKEY_BREAD));
        donkey.addNationSpecificCargoImage(ROMANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_ROMAN_SHIELD));
        donkey.addCargoImage(WOOD, getImageAt(map0ZLst, Map0ZLst.DONKEY_WOOD));
        donkey.addCargoImage(PLANK, getImageAt(map0ZLst, Map0ZLst.DONKEY_PLANK));
        donkey.addCargoImage(STONE, getImageAt(map0ZLst, Map0ZLst.DONKEY_STONE));
        donkey.addNationSpecificCargoImage(VIKINGS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_VIKING_SHIELD));
        donkey.addNationSpecificCargoImage(AFRICANS, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_AFRICAN_SHIELD));
        donkey.addCargoImage(WHEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_WHEAT));
        donkey.addCargoImage(COIN, getImageAt(map0ZLst, Map0ZLst.DONKEY_COIN));
        donkey.addCargoImage(GOLD, getImageAt(map0ZLst, Map0ZLst.DONKEY_GOLD));
        donkey.addCargoImage(IRON, getImageAt(map0ZLst, Map0ZLst.DONKEY_IRON));
        donkey.addCargoImage(COAL, getImageAt(map0ZLst, Map0ZLst.DONKEY_COAL));
        donkey.addCargoImage(MEAT, getImageAt(map0ZLst, Map0ZLst.DONKEY_MEAT));
        donkey.addCargoImage(PIG, getImageAt(map0ZLst, Map0ZLst.DONKEY_PIG));
        donkey.addNationSpecificCargoImage(JAPANESE, SHIELD, getImageAt(map0ZLst, Map0ZLst.DONKEY_JAPANESE_SHIELD));

        donkey.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Donkey");

        // Extract the pig
        var pig = new PigImageCollection();

        pig.addAnimation(Pig.PigAction.PIG_ACTION_1, getImagesAt(mapBobsLst, MapBobsLst.PIG_ACTION_1));
        pig.addAnimation(Pig.PigAction.PIG_ACTION_2, getImagesAt(mapBobsLst, MapBobsLst.PIG_ACTION_2));
        pig.addAnimation(Pig.PigAction.PIG_ACTION_3, getImagesAt(mapBobsLst, MapBobsLst.PIG_ACTION_3));
        pig.addAnimation(Pig.PigAction.PIG_ACTION_4, getImagesAt(mapBobsLst, MapBobsLst.PIG_ACTION_4));
        pig.addAnimation(Pig.PigAction.PIG_ACTION_5, getImagesAt(mapBobsLst, MapBobsLst.PIG_ACTION_5));

        pig.addShadows(getImageAt(mapBobsLst, MapBobsLst.ADULT_PIG_SHADOW), getImageAt(mapBobsLst, MapBobsLst.PIGLET_SHADOW));

        pig.writeImageAtlas(toDir + "/animals/", defaultPalette);
        log(" - Pig");
    }
}
