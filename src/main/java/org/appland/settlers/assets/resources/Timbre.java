package org.appland.settlers.assets.resources;

/**
 * Represents a timbre with a patch and bank.
 */
public record Timbre(short patch, short bank) {

    /**
     * Constructs a Timbre object with the specified patch and bank values.
     *
     * @param patch The patch value (uint 8).
     * @param bank  The bank value (uint 8).
     */
    public Timbre {
        // Ensure patch and bank values are within the valid range for uint8
        if (patch < 0 || patch > 255) {
            throw new IllegalArgumentException(String.format("Invalid patch value: %d. Must be between 0 and 255.", patch));
        }

        if (bank < 0 || bank > 255) {
            throw new IllegalArgumentException(String.format("Invalid bank value: %d. Must be between 0 and 255.", bank));
        }
    }
}