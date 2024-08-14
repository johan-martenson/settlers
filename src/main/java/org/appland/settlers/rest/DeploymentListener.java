package org.appland.settlers.rest;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.rest.resource.MapsResource;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebListener
class DeploymentListener implements ServletContextListener {

    private final List<MapFile> mapFiles;

    DeploymentListener() {
        mapFiles = new ArrayList<>();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Context initialized event.");

        /* Start the game ticker */
        GameTicker.GAME_TICKER.activate();

        /* Load all maps and put them into the servlet context */
        String largeMapDirectoryPath = "maps/WORLDS/";

        File largeMapDirectory = new File(largeMapDirectoryPath);

        MapLoader mapLoader = new MapLoader();

        if (largeMapDirectory.exists()) {

            File[] mapFilenames = largeMapDirectory.listFiles(
                    (dir, name) -> name.toLowerCase().endsWith(".swd") || name.toLowerCase().endsWith(".wld"));

            Arrays.stream(mapFilenames).parallel().forEach(mapFilename ->
                    {
                        try {
                            MapFile mapFile = mapLoader.loadMapFromFile(mapFilename.toString());

                            synchronized (mapFiles) {
                                mapFiles.add(mapFile);
                                MapsResource.mapsResource.addMap(mapFile);
                            }
                        } catch (Exception e) {
                            System.out.println(mapFilename.toString());
                            System.out.println("Exception while loading maps: " + e);
                        }
                    }
            );
        }

        /* Pick the single reference map */
        File mapFile = Paths.get("src/test/resources/000.SWD").toFile();

        try {
            MapFile loadedMapFile = mapLoader.loadMapFromFile(mapFile.toString());

            mapFiles.addFirst(loadedMapFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FAILED TO LOAD MAP");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("Context destroyed event");

        GameTicker gameTicker = (GameTicker) servletContextEvent.getServletContext().getAttribute("gameTicker");

        gameTicker.deactivate();
    }

    private String getCurrentDirectory() {
        return new File(".").getAbsolutePath();
    }
}
