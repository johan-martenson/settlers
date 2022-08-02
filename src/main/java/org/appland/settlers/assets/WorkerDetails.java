package org.appland.settlers.assets;

import static org.appland.settlers.assets.BodyType.FAT;
import static org.appland.settlers.assets.BodyType.THIN;

public class WorkerDetails {
    public final static int NUMBER_NATION_SPECIFIC_JOBS = 6;

    private final int id;
    private final BodyType bodyType;

    public WorkerDetails(BodyType bodyType, int id) {
        this.bodyType = bodyType;
        this.id = id;
    }

    public WorkerDetails(boolean fat, int id) {

        if (fat) {
            bodyType = FAT;
        } else {
            bodyType = THIN;
        }

        this.id = id;
    }

    public int getBobId(Nation nation) {
        if(id >= 0) {
            return id;
        }

        /*
         * Translate ids:
         *  - 0 (Africans) -> 3
         *  - 1 (Japanese) -> 2
         *  - 2 (Romans)   -> 0
         *  - 3 (Vikings)  -> 1
         * */
        if (nation.ordinal() >= Nation.values().length) {
            throw new RuntimeException("Unknown nation! Was: " + nation);
        }

        int multiplier;

        if (nation == Nation.AFRICANS) {
            multiplier = 3;
        } else if (nation == Nation.JAPANESE) {
            multiplier = 2;
        } else if (nation == Nation.ROMANS) {
            multiplier = 0;
        } else {
            multiplier = 1;
        }

        int calcId = multiplier * NUMBER_NATION_SPECIFIC_JOBS - id;

        return calcId;
    }

    public BodyType getBodyType() {
        return bodyType;
    }
}
