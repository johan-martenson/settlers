package org.appland.settlers.assets;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Chunk {
    private final String typeId;
    private final List<Chunk> children;

    private byte[] content;
    private long size;

    public Chunk(String typeId) {
        this.typeId = typeId;
        this.content = null;
        children = new ArrayList<>();
    }

    public Chunk(String typeId, byte[] content) {
        this.typeId = typeId;
        this.content = content;
        children = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (content != null) {
            return format("Chunk - type id: %s, data size: %d, children: %s.", typeId, content.length, children);
        } else {
            return format("Chunk - type id: %s, children: %s", typeId, children);
        }
    }

    public String getTypeId() {
        return typeId;
    }

    public byte[] getContent() {
        return content;
    }

    public long getTotalSize() {
        return size;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void addChild(Chunk childChunk) {
        children.add(childChunk);
    }

    public void setTotalSize(long size) {
        this.size = size;
    }

    public List<Chunk> getChildren() {
        return children;
    }
}
