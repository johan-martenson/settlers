package org.appland.settlers.rest;

import org.appland.settlers.model.Point;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map<String, Integer> pointToMap(Point point) {
        Map<String, Integer> pointMap = new HashMap<>();

        pointMap.put("x", point.x);
        pointMap.put("y", point.y);

        return pointMap;
    }

    public static Point getPositionForBuildingMap(Map<String, Object> building) {
        return new Point((int)building.get("x"), (int)building.get("y"));
    }

    public static Point mapToPoint(Map pointMap) {
        return new Point((int)pointMap.get("x"), (int)pointMap.get("y"));
    }
}
