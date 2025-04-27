package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.ShipConstructionProgress;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.ShipImageCollection;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.BootBobsLst;
import org.appland.settlers.assets.resources.Palette;

import java.io.IOException;

import static org.appland.settlers.assets.CompassDirection.*;
import static org.appland.settlers.assets.Utils.getImageAt;

public class ShipExtractor {
    public static void extractShips(String fromDir, String toDir, Palette defaultPalette) throws IOException, UnknownResourceTypeException, InvalidFormatException {
        var bootBobsLst = LstDecoder.loadLstFile(fromDir + "/" + BootBobsLst.FILENAME, defaultPalette);
        var shipImageCollection = new ShipImageCollection();

        shipImageCollection.addShipImageWithShadow(
                EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_EAST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                SOUTH_WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_SOUTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_WEST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_WEST_SHADOW)
        );
        shipImageCollection.addShipImageWithShadow(
                NORTH_EAST,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_NORTH_EAST_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.JUST_STARTED,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_JUST_STARTED_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.HALF_WAY,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_HALF_WAY_SHADOW)
        );

        shipImageCollection.addShipUnderConstructionImageWithShadow(
                ShipConstructionProgress.ALMOST_DONE,
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE),
                getImageAt(bootBobsLst, BootBobsLst.SHIP_CONSTRUCTION_ALMOST_DONE_SHADOW)
        );

        shipImageCollection.writeImageAtlas(toDir, defaultPalette);
    }
}
