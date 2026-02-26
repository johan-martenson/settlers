package org.appland.settlers.model.utils;

import org.appland.settlers.model.AttackStrength;
import org.appland.settlers.model.GameUtils;
import org.appland.settlers.model.Point;
import org.appland.settlers.model.actors.Soldier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MilitaryUtils {

    ///  Utility types
    /**
     * Represents a soldier and its associated distance.
     * Used for sorting soldiers based on distance and rank.
     */
    public record SoldierAndDistance(Soldier soldier, int distance) { }

    ///  Internal utility functions
    private static int rankToInt(Soldier.Rank rank) {
        return switch (rank) {
            case PRIVATE_RANK -> 0;
            case PRIVATE_FIRST_CLASS_RANK -> 1;
            case SERGEANT_RANK -> 2;
            case OFFICER_RANK -> 3;
            case GENERAL_RANK -> 4;
        };
    }

    // Comparator for sorting soldiers by strength
    public static Comparator<? super Soldier> strengthSorter = Comparator.comparingInt(s -> s.getRank().toInt());

    // Comparator for sorting soldiers by strength and shorter distance
    public static Comparator<SoldierAndDistance> strongerAndShorterDistanceSorter = Comparator
            .comparing((SoldierAndDistance sd) -> sd.soldier.getRank().toInt()).reversed()
            .thenComparingInt(sd -> sd.distance);

    // Comparator for sorting soldiers by weaker and shorter distance
    public static Comparator<SoldierAndDistance> weakerAndShorterDistanceSorter = Comparator
            .comparingInt((SoldierAndDistance sd) -> sd.soldier.getRank().toInt())
            .thenComparingInt(sd -> sd.distance);

    ///  Exposed utility functions

    /**
     * Converts a strength value to a list of preferred Soldier ranks.
     *
     * @param strength The strength value to convert.
     * @return A list of Soldier ranks ordered by preference.
     */
    public static List<Soldier.Rank> strengthToRank(int strength) {
        var populationPreferenceOrder = new ArrayList<Integer>();

        populationPreferenceOrder.add(strength);

        for (int i = 1; i < Math.max(10 - strength, strength); i++) {
            if (strength + i < 11) {
                populationPreferenceOrder.add(strength + i);
            }

            if (strength - i > -1) {
                populationPreferenceOrder.add(strength - i);
            }
        }

        // Go through the list in order of preference and add the rank
        var ranks = new ArrayList<Soldier.Rank>();

        for (int preferred : populationPreferenceOrder) {
            var rank = Soldier.Rank.intToRank(preferred);

            if (ranks.isEmpty() || ranks.getLast() != rank) {
                ranks.add(rank);
            }
        }

        return ranks;
    }

    public static List<Soldier> sortSoldiersByPreferredStrength(List<Soldier> soldiers, AttackStrength attackStrength) {
        var sortedSoldiers = soldiers.stream()
                .sorted(Comparator.comparingInt(s0 -> rankToInt(s0.getRank())))
                .toList();

        if (attackStrength == AttackStrength.STRONG) {
            return sortedSoldiers.reversed();
        }

        return sortedSoldiers;
    }

    /**
     * Sorts a list of soldiers based on the preferred strength, expressed as a value from 0 to 10.
     *
     * @param soldiers List of soldiers to be sorted.
     * @param strength Preferred strength of soldiers
     * @return A new list of soldiers sorted by preferred rank.
     */
    public static List<Soldier> sortSoldiersByPreferredStrength(List<Soldier> soldiers, int strength) {
        var prefRankList = strengthToRank(strength);

        return soldiers.stream()
                .sorted((soldier0, soldier1) -> {
                    int rankDist0 = prefRankList.indexOf(soldier0.getRank());
                    int rankDist1 = prefRankList.indexOf(soldier1.getRank());

                    return Integer.compare(rankDist0, rankDist1);
                })
                .collect(Collectors.toList());
    }

    /**
     * Sorts a list of soldiers by the preferred strength and distance from a given position.
     *
     * @param soldiers List of soldiers to be sorted.
     * @param strength Strength value determining the preference order of ranks.
     * @param position Position from which distance is calculated.
     */
    public static void sortSoldiersByPreferredStrengthAndDistance(List<Soldier> soldiers, int strength, Point position) {
        soldiers.sort((soldier0, soldier1) -> {
            if (soldier0.getRank() == soldier1.getRank()) {
                var dist0 = GameUtils.distanceInGameSteps(soldier0.getHome().getPosition(), position);
                var dist1 = GameUtils.distanceInGameSteps(soldier1.getHome().getPosition(), position);

                return Integer.compare(dist0, dist1);
            } else {
                var prefRankList = strengthToRank(strength);

                var rankDist0 = prefRankList.indexOf(soldier0.getRank());
                var rankDist1 = prefRankList.indexOf(soldier1.getRank());

                return Integer.compare(rankDist0, rankDist1);
            }
        });
    }
}
