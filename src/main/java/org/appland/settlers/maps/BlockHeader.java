package org.appland.settlers.maps;

import java.util.Objects;

public class BlockHeader {
    private final int id;
    private final long mustBeZero;
    private final int width;
    private final int height;
    private final int multiplier;
    private final long blockLength;

    public BlockHeader(int id, long unknown, int width, int height, int multiplier, long blockLength) {
        this.id = id;
        this.mustBeZero = unknown;
        this.width = width;
        this.height = height;
        this.multiplier = multiplier;
        this.blockLength = blockLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BlockHeader that = (BlockHeader) o;

        return id == that.id &&
                mustBeZero == that.mustBeZero &&
                width == that.width &&
                height == that.height &&
                multiplier == that.multiplier &&
                blockLength == that.blockLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mustBeZero, width, height, multiplier, blockLength);
    }

    public boolean isValid() {
        return id == 10000 && mustBeZero == 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getMultiplier() {
        return multiplier;
    }

    @Override
    public String toString() {
        return "BlockHeader{" +
                "id=" + id +
                ", mustBeZero=" + mustBeZero +
                ", width=" + width +
                ", height=" + height +
                ", multiplier=" + multiplier +
                ", blockLength=" + blockLength +
                '}';
    }

    public long getBlockLength() {
        return blockLength;
    }
}
