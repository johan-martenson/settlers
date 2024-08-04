package org.appland.settlers.rest.resource;

import org.appland.settlers.maps.MapFile;

import java.util.ArrayList;
import java.util.Collection;

public class MapsResource {
    private final Collection<MapFile> mapFiles = new ArrayList<>();

    public static final MapsResource mapsResource = new MapsResource();

    public void addMap(MapFile mapFile) {
        mapFiles.add(mapFile);
    }

    Collection<MapFile> getMaps() {
        return mapFiles;
    }
}
