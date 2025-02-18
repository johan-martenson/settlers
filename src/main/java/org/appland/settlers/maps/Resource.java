/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.appland.settlers.maps;

/**
 * @author johan
 */
record Resource(ResourceType type, int amount) {
    private static final int COAL_OFFSET = 64; // 0x40
    private static final int IRON_OFFSET = 72; // 0x48
    private static final int GOLD_OFFSET = 80; // 0x50
    private static final int GRANITE_OFFSET = 88; // 0x58

    public static Resource resourceFromInt(int i) {
        ResourceType type = ResourceType.resourceTypeFromInt(i);

        int amount = 0;

        if (null != type) {
            switch (type) {
                case COAL:
                    amount = i - COAL_OFFSET;
                    break;
                case IRON_ORE:
                    amount = i - IRON_OFFSET;
                    break;
                case GOLD:
                    amount = i - GOLD_OFFSET;
                    break;
                case GRANITE:
                    amount = i - GRANITE_OFFSET;
                    break;
                default:
                    break;
            }
        }

        if (type == null) {
            return null;
        }

        return new Resource(type, amount);
    }
}
