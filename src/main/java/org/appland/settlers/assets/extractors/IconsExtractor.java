package org.appland.settlers.assets.extractors;

import org.appland.settlers.assets.InvalidFormatException;
import org.appland.settlers.assets.UiIcon;
import org.appland.settlers.assets.UnknownResourceTypeException;
import org.appland.settlers.assets.collectors.UIElementsImageCollection;
import org.appland.settlers.assets.decoders.DatDecoder;
import org.appland.settlers.assets.decoders.LstDecoder;
import org.appland.settlers.assets.gamefiles.IoDat;
import org.appland.settlers.assets.gamefiles.IoLst;
import org.appland.settlers.assets.gamefiles.MapBobsLst;
import org.appland.settlers.assets.resources.Palette;
import org.appland.settlers.assets.utils.ImageUtils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.appland.settlers.assets.Utils.getImageAt;
import static org.appland.settlers.model.Size.*;

public class IconsExtractor {
    static final Set<IoDat> ICONS_TO_EXTRACT = Set.of(
            IoDat.BROKEN_FLAG,
            IoDat.PULL_DOWN_FLAG,
            IoDat.COIN_CROSSED_OUT,
            IoDat.COIN,
            IoDat.MAGNIFYING_GLASS,
            IoDat.FILM_CAMERA,
            IoDat.SHRINK_SCREEN_AND_MAGNIFYING_GLASS,
            IoDat.ENLARGE_SCREEN_AND_MAGNIFYING_GLASS,
            IoDat.ONE_YELLOW_SHIELD,
            IoDat.FIVE_YELLOW_SHIELDS,
            IoDat.LIGHT_ROAD_IN_NATURE,
            IoDat.ROMAN_GENERAL,
            IoDat.ROMAN_PRIVATE,
            IoDat.GO_TO_POINT,
            IoDat.SEND_OUT_ARROWS,
            IoDat.ROAD_AND_FLAGS,
            IoDat.PLUS_AVAILABLE_SMALL_BUILDING_WITH_TITLES,
            IoDat.PLUS_AVAILABLE_BUILDINGS,
            IoDat.RED_SMALL_AVAILABLE_BUILDING,
            IoDat.RED_MEDIUM_AVAILABLE_BUILDING,
            IoDat.RED_LARGE_AVAILABLE_BUILDING,
            IoDat.SHOVEL,
            IoDat.GEARS,
            IoDat.GEARS_CROSSED_OVER,
            IoDat.SMALLEST_FORTRESS_WITH_MINUS,
            IoDat.SMALLEST_FORTRESS_WITH_PLUS,
            IoDat.SMALLER_FORTRESS_WITH_MINUS,
            IoDat.SMALLER_FORTRESS_WITH_PLUS,
            IoDat.FORTRESS_WITH_MINUS,
            IoDat.FORTRESS_WITH_PLUS,
            IoDat.WEAK_SOLDIER_WITH_MINUS,
            IoDat.STRONG_SOLDIER_WITH_PLUS,
            IoDat.ONE_SHIELD_WITH_MINUS,
            IoDat.TWO_SHIELDS_WITH_PLUS,
            IoDat.MILITARY_BUILDING_WITH_YELLOW_SHIELD_AND_MINUS,
            IoDat.MILITARY_BUILDING_WITH_YELLOW_SHIELD_AND_PLUS,
            IoDat.MILITARY_BUILDING_WITH_SWORDS_AND_MINUS,
            IoDat.MILITARY_BUILDING_WITH_SWORDS_AND_PLUS,
            IoDat.MAP_WITH_QUESTION_MARK,
            IoDat.BUILDINGS_WITH_QUESTION_MARK,
            IoDat.WORKERS_WITH_QUESTION_MARK,
            IoDat.GOODS_WITH_QUESTION_MARK,
            IoDat.GENERAL_WITH_QUESTION_MARK,
            IoDat.COINS_WITH_QUESTION_MARK,
            IoDat.GEARS_WITH_QUESTION_MARK,
            IoDat.ANGEL_WITH_QUESTION_MARK,
            IoDat.WREATH_ON_MAP,
            IoDat.GOODS_ON_MAP,
            IoDat.WORKERS_GOODS_AND_QUESTION_MARK,
            IoDat.TRANSPORT_PRIORITY,
            IoDat.TOOLS_WITH_QUESTION_MARK,
            IoDat.HOUSE_WITH_GEARS_AND_PROGRESS_BAR,
            IoDat.WEAPONS_AND_SOLDIER,
            IoDat.SHIP_AND_ANCHOR,
            IoDat.HOUSE_ON_MAP,
            IoDat.WEAPONS_MOVING,
            IoDat.FOOD,
            IoDat.SAW_AND_MINUS,
            IoDat.SAW_AND_PLUS,
            IoDat.AXE_AND_MINUS,
            IoDat.AXE_AND_PLUS,
            IoDat.SHOVEL_AND_MINUS,
            IoDat.SHOVEL_AND_PLUS,
            IoDat.PICK_AXE_AND_MINUS,
            IoDat.PICK_AXE_AND_PLUS,
            IoDat.LINE_AND_HOOK_AND_MINUS,
            IoDat.LINE_AND_HOOK_AND_PLUS,
            IoDat.BOW_AND_MINUS,
            IoDat.BOW_AND_PLUS,
            IoDat.CLEAVER_AND_MINUS,
            IoDat.CLEAVER_AND_PLUS,
            IoDat.ROLLING_PIN_AND_MINUS,
            IoDat.ROLLING_PIN_AND_PLUS,
            IoDat.CRUCIBLE_AND_MINUS,
            IoDat.CRUCIBLE_AND_PLUS,
            IoDat.TONGS_AND_MINUS,
            IoDat.TONGS_AND_PLUS,
            IoDat.SCYTHE_AND_MINUS,
            IoDat.SCYTHE_AND_PLUS,
            IoDat.PLUS,
            IoDat.MINUS,
            IoDat.TWO_SWORDS,
            IoDat.UP_ARROW,
            IoDat.DOWN_ARROW,
            IoDat.ARROW_TO_TOP,
            IoDat.ARROW_TO_BOTTOM,
            IoDat.TRASHCAN,
            IoDat.SPRAY_CAN,
            IoDat.RIGHT_ARROW,
            IoDat.GLOBE_WITH_MAGNIFYING_GLASS,
            IoDat.OWNED_AREA_ON_MAP,
            IoDat.OWNED_BUILDINGS_ON_MAP,
            IoDat.OWNED_ROADS_ON_MAP,
            IoDat.GRAPH_OF_OWNED_AREA_ON_MAP,
            IoDat.FORWARD,
            IoDat.REVERSE,
            IoDat.PLUS_RETURN_TO_HEADQUARTERS
    );

    public static void extractIcons(String fromDir, String toDir, Palette defaultPalette) throws UnknownResourceTypeException, IOException, InvalidFormatException {
        var mapBobsLst = LstDecoder.loadLstFile(fromDir + "/" + MapBobsLst.FILENAME, defaultPalette);
        var ioLst = LstDecoder.loadLstFile(fromDir + "/" + IoLst.FILENAME, defaultPalette);
        var ioDat = DatDecoder.loadDatFile(fromDir + "/" + IoDat.FILENAME, defaultPalette);

        var collector = new UIElementsImageCollection();

        collector.addSelectedPointImage(getImageAt(mapBobsLst, MapBobsLst.SELECTED_POINT));
        collector.addHoverPoint(getImageAt(mapBobsLst, MapBobsLst.HOVER_POINT));
        collector.addHoverAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_FLAG));
        collector.addHoverAvailableMine(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MINE));
        collector.addHoverAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_SMALL_BUILDING));
        collector.addHoverAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_MEDIUM_BUILDING));
        collector.addHoverAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_LARGE_BUILDING));
        collector.addHoverAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.HOVER_AVAILABLE_HARBOR));
        collector.addAvailableFlag(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_FLAG));
        collector.addAvailableMine(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MINE));
        collector.addAvailableBuilding(SMALL, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_SMALL_BUILDING));
        collector.addAvailableBuilding(MEDIUM, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_MEDIUM_BUILDING));
        collector.addAvailableBuilding(LARGE, getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_LARGE_BUILDING));
        collector.addAvailableHarbor(getImageAt(mapBobsLst, MapBobsLst.AVAILABLE_HARBOR));

        collector.addUiElement(UiIcon.DESTROY_BUILDING, getImageAt(ioLst, IoLst.BURNING_HOUSE_ICON));
        collector.addUiElement(UiIcon.ATTACK, getImageAt(ioLst, IoLst.ATTACK_ICON));
        collector.addUiElement(UiIcon.SCISSORS, getImageAt(ioLst, IoLst.SCISSORS));
        collector.addUiElement(UiIcon.INFORMATION, getImageAt(ioLst, IoLst.INFORMATION));
        collector.addUiElement(UiIcon.GEOLOGIST, getImageAt(ioLst, IoLst.GEOLOGIST_ICON));

        for (var icon : ICONS_TO_EXTRACT) {
            collector.addIcon(icon, ImageUtils.getBitmapFromResource(ioDat.get(icon.index)));
        }

        try (InputStream in = IconsExtractor.class.getResourceAsStream("/pause.png")) {
            if (in == null) {
                throw new IOException("Resource not found: /pause.png");
            }
            var img = ImageIO.read(in);

            collector.addUiElement(UiIcon.PAUSE, ImageUtils.toBitmap(img));
        }

        try (InputStream in = IconsExtractor.class.getResourceAsStream("/play.png")) {
            if (in == null) {
                throw new IOException("Resource not found: /play.png");
            }

            var img = ImageIO.read(in);

            collector.addUiElement(UiIcon.PLAY, ImageUtils.toBitmap(img));
        }

        collector.writeImageAtlas(toDir, defaultPalette);
    }
}
