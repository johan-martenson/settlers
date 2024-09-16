package org.appland.settlers.assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a data chunk that can contain children chunks and a content payload.
 */
public class Chunk {
    private final String typeId;
    private final List<Chunk> children = new ArrayList<>();

    private byte[] content;
    private long size;

    /**
     * Constructs a Chunk with a specified type ID and no content.
     *
     * @param typeId the type ID of the chunk
     */
    public Chunk(String typeId) {
        this.typeId = typeId;
        this.content = null;
    }

    /**
     * Constructs a Chunk with a specified type ID and content.
     *
     * @param typeId  the type ID of the chunk
     * @param content the byte array content of the chunk
     */
    public Chunk(String typeId, byte[] content) {
        this.typeId = typeId;
        this.content = content;
    }

    /**
     * Returns a string representation of the chunk.
     *
     * @return a formatted string describing the chunk
     */
    @Override
    public String toString() {
        return content != null
                ? String.format("Chunk - type id: %s, data size: %d, children: %s.", typeId, content.length, children)
                : String.format("Chunk - type id: %s, children: %s.", typeId, children);
    }

    /**
     * Retrieves the type ID of the chunk.
     *
     * @return the type ID as a String
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * Retrieves the content of the chunk.
     *
     * @return the byte array content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Retrieves the total size of the chunk.
     *
     * @return the total size as a long
     */
    public long getTotalSize() {
        return size;
    }

    /**
     * Sets the content for the chunk.
     *
     * @param content the byte array content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Adds a child chunk to this chunk.
     *
     * @param childChunk the child chunk to add
     */
    public void addChild(Chunk childChunk) {
        children.add(childChunk);
    }

    /**
     * Sets the total size for the chunk.
     *
     * @param size the total size as a long
     */
    public void setTotalSize(long size) {
        this.size = size;
    }

    /**
     * Retrieves the list of child chunks.
     *
     * @return a List of child chunks
     */
    public List<Chunk> getChildren() {
        return children;
    }
}
