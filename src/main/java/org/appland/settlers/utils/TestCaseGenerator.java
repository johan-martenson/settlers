package org.appland.settlers.utils;

import org.appland.settlers.model.*;

import java.util.List;

public class TestCaseGenerator {
    public void recordPlaceBuilding(Building building, Point point) {
        printStartTry();

        System.out.println(
                "    map.placeBuilding(" +
                    "new " + building.getClass().getName() + "(player)" +
                    ", " + printPoint(point) +
                ");"
        );

        System.out.println();

        printEndTry();
    }

    private String printPoint(Point point) {
        return "new Point(" + point.x + ", " + point.y + ")";
    }

    public void recordPlaceFlag(Player player, Point point) {
        printStartTry();

        System.out.println("    map.placeFlag(\"" + player.getName() + "\", " + printPoint(point) + ");");

        System.out.println();

        printEndTry();
    }

    public void recordCallScout(Flag flag) {
        printStartTry();

        System.out.println("    map.getFlagAtPoint(" + printPoint(flag.getPosition()) + ").callScout();");

        System.out.println();

        printEndTry();
    }


    public void recordCallGeologist(Flag flag) {
        printStartTry();

        System.out.println("    map.getFlagAtPoint(" + printPoint(flag.getPosition()) + ").callGeologist();");

        System.out.println();

        printEndTry();
    }

    public void recordPlaceRoadAutomatically(Player player, Point start, Point end) {
        printStartTry();

        System.out.println(
                "    map.placeAutoSelectedRoad(player, " +
                    printPoint(start) + ", " +
                    printPoint(end) +
                ");");

        System.out.println();

        printEndTry();
    }

    public void recordFastForward(int iterations, GameMap map) {
        printStartTry();

        System.out.println("    Utils.fastForward(" + iterations + ", map);");

        System.out.println();

        printEndTry();
    }

    public void recordRemoveFlag(Flag flag) {
        printStartTry();

        Point point = null;

        if (flag != null) {
            point = flag.getPosition();
        }

        System.out.println(
                "    map.removeFlag(" + "map.getFlagAtPoint(" + point + "));"
        );

        System.out.println();

        printEndTry();
    }

    private void printEndTry() {
        System.out.println("} catch (Exception e) {e.printStackTrace();}");
    }

    private void printStartTry() {
        System.out.println("try {");
    }

    public void recordEnablePromotions(Building building) {
        printStartTry();

        System.out.println(
                "    map.getBuildingAtPoint(" +
                        printPoint(building.getPosition()) +
                ").enablePromotions();"
        );

        printEndTry();
    }

    public void recordDisablePromotions(Building building) {
        printStartTry();

        System.out.println(
                "    map.getBuildingAtPoint(" +
                        printPoint(building.getPosition()) +
                        ").disablePromotions();"
        );

        printEndTry();
    }

    public void recordCancelEvacuation(Building building) {
        printStartTry();

        System.out.println(
                "    map.getBuildingAtPoint(" +
                        printPoint(building.getPosition()) +
                        ").cancelEvacuation();"
        );

        printEndTry();
    }

    public void recordEvacuate(Building building) {
        printStartTry();

        System.out.println(
                "    map.getBuildingAtPoint(" +
                        printPoint(building.getPosition()) +
                        ").evacuate();"
        );

        printEndTry();
    }

    public void recordGetAvailableMines(Player player) {
        printStartTry();

        System.out.println(
                "    map.getAvailableMines(" + player.getName() + ");"
        );

        printEndTry();
    }

    public void recordGetAvailableFlags(Player player) {
        printStartTry();

        System.out.println("    map.getAvailableFlags(" + player.getName() + ");");

        printEndTry();
    }

    public void recordGetPossibleAdjacentRoadPoints(Point point, Player player) {
        printStartTry();

        System.out.println("    map.getPossibleAdjacentRoadPoints(" + player.getName() + ", " + printPoint(point) + ");");

        printEndTry();
    }

    public void recordPlaceRoad(Player player, List<Point> points) {
        printStartTry();

        System.out.print("    map.placeRoad(" + player.getName() + ", ");

        boolean first = true;

        for (Point point : points) {
            if (!first) {
                System.out.print(", " + printPoint(point));
            } else {
                System.out.print(printPoint(point));
            }
        }

        System.out.println(");");

        printEndTry();
    }

    public void recordGetAvailableHousePoints(Player player) {
        printStartTry();

        System.out.println("    map.getAvailableHousePoints(" + player.getName() + ");");

        printEndTry();
    }

    public void recordAttack(Player attacker, Building building, int attackers) {
        printStartTry();

        System.out.println(
                "    " + attacker.getName() + ".attack(map.getBuildingAtPoint(" + printPoint(building.getPosition()) + "), " + attackers + ");"
        );

        printEndTry();

    }

    public void recordSetFoodQuota(Player player, Class<? extends Building> buildingClass, int quota) {
        printStartTry();

        System.out.println("    " + player.getName() + ".setFoodQuota(" + buildingClass.getSimpleName() + ", " + quota + ");");

        printEndTry();
    }

    public void recordSetCoalQuota(Player player, Class<? extends Building> buildingClass, int quota) {
        printStartTry();

        System.out.println(
                "    " + player.getName() + ".setCoalQuota(" +
                        buildingClass == null ? "null" : buildingClass.getSimpleName() +
                        ", " + quota + ");"
        );

        printEndTry();
    }

    public void recordSetTransportPriority(Player player, int priority, TransportCategory material) {
        printStartTry();

        System.out.println("    " + player.getName() + ".setTransportPriority(" + priority + ", " + material.name() + ");");

        printEndTry();
    }

    public void recordUpgradeBuilding(Building building) {
        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + printPoint(building.getPosition()) + ").upgrade();");

        printEndTry();
    }

    public void recordRemoveRoad(Road road) {
        printStartTry();

        System.out.println("    map.removeRoadAtPoint(" + road.getWayPoints() + ");");

        printEndTry();
    }

    public void recordTearDownBuilding(Building building) {
        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + building.getPosition() + ".tearDown());");

        printEndTry();
    }

    public void recordStopProduction(Building building) {
        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + building.getPosition() + ".stopProduction());");

        printEndTry();
    }

    public void recordResumeProduction(Building building) {
        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + building.getPosition() + ".resumeProduction());");

        printEndTry();
    }

    public void recordSetVegetationDownRight(Point point, DetailedVegetation vegetation) {
        printStartTry();

        System.out.println("    map.setVegetationDownRight(" + point + ", " + vegetation + ");");

        printEndTry();
    }

    public void recordSetVegetationBelow(Point point, DetailedVegetation vegetation) {
        printStartTry();

        System.out.println("    map.setVegetationBelow(" + point + ", " + vegetation + ");");

        printEndTry();
    }

    public void recordPushOutMaterial(Building building, Material material) {
        Point point = null;

        if (building != null) {
            point = building.getPosition();
        }

        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + point + ").pushOutMaterial(" + material + ");");

        printEndTry();
    }

    public void recordStopStorageOfMaterial(Building building, Material material) {
        Point point = null;

        if (building != null) {
            point = building.getPosition();
        }

        printStartTry();

        System.out.println("    map.getBuildingAtPoint(" + point + ").blockStorageOfMaterial(" + material + ");");

        printEndTry();
    }
}
