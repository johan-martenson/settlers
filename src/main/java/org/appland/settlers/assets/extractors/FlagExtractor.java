package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.FlagImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.AfrZLst;
import org.appland.settlers.assets.gamefiles.JapZLst;
import org.appland.settlers.assets.gamefiles.RomZLst;
import org.appland.settlers.assets.gamefiles.VikZLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.model.Flag;

import java.io.IOException;

import static org.appland.settlers.assets.Nation.*;
import static org.appland.settlers.assets.Utils.getImagesAt;
import static org.appland.settlers.assets.Utils.getPlayerImagesAt;

public class FlagExtractor {
    public static void extractFlags(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {

        var afrZLst = LstDecoder.loadLstFile(fromDir + "/" + AfrZLst.FILENAME, defaultPalette);
        var japZLst = LstDecoder.loadLstFile(fromDir + "/" + JapZLst.FILENAME, defaultPalette);
        var romZLst = LstDecoder.loadLstFile(fromDir + "/" + RomZLst.FILENAME, defaultPalette);
        var vikZLst = LstDecoder.loadLstFile(fromDir + "/" + VikZLst.FILENAME, defaultPalette);

        var flagImageCollection = new FlagImageCollection();

        // Africans
        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.NORMAL, getPlayerImagesAt(afrZLst, AfrZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.NORMAL, getImagesAt(afrZLst, AfrZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.MAIN, getPlayerImagesAt(afrZLst, AfrZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.MAIN, getImagesAt(afrZLst, AfrZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(AFRICANS, Flag.FlagType.MARINE, getPlayerImagesAt(afrZLst, AfrZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(AFRICANS, Flag.FlagType.MARINE, getImagesAt(afrZLst, AfrZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Japanese
        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.NORMAL, getPlayerImagesAt(japZLst, JapZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.NORMAL, getImagesAt(japZLst, JapZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.MAIN, getPlayerImagesAt(japZLst, JapZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.MAIN, getImagesAt(japZLst, JapZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(JAPANESE, Flag.FlagType.MARINE, getPlayerImagesAt(japZLst, JapZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(JAPANESE, Flag.FlagType.MARINE, getImagesAt(japZLst, JapZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Romans
        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.NORMAL, getPlayerImagesAt(romZLst, RomZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.NORMAL, getImagesAt(romZLst, RomZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.MAIN, getPlayerImagesAt(romZLst, RomZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.MAIN, getImagesAt(romZLst, RomZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(ROMANS, Flag.FlagType.MARINE, getPlayerImagesAt(romZLst, RomZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(ROMANS, Flag.FlagType.MARINE, getImagesAt(romZLst, RomZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Vikings
        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.NORMAL, getPlayerImagesAt(vikZLst, VikZLst.NORMAL_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.NORMAL, getImagesAt(vikZLst, VikZLst.NORMAL_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.MAIN, getPlayerImagesAt(vikZLst, VikZLst.MAIN_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.MAIN, getImagesAt(vikZLst, VikZLst.MAIN_FLAG_SHADOW_ANIMATION, 8));

        flagImageCollection.addImagesForFlag(VIKINGS, Flag.FlagType.MARINE, getPlayerImagesAt(vikZLst, VikZLst.MARINE_FLAG_ANIMATION, 8));
        flagImageCollection.addImagesForFlagShadow(VIKINGS, Flag.FlagType.MARINE, getImagesAt(vikZLst, VikZLst.MARINE_FLAG_SHADOW_ANIMATION, 8));

        // Write the image atlas to file
        flagImageCollection.writeImageAtlas(toDir + "/", defaultPalette);
    }
}
