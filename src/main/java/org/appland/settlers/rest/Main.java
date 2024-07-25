package org.appland.settlers.rest;

import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.rest.resource.MapsResource;
import org.appland.settlers.rest.resource.WebsocketApi;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerContainer;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    private final List<MapFile> mapFiles = new ArrayList<>();
    private static final String CONTEXT_ROOT = "/";

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.run();
    }

    private void run() throws Exception {

        // Start the game ticker
        GameTicker.GAME_TICKER.activate();

        // Load the maps
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

        final int port = 8080;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(CONTEXT_ROOT);
        server.setHandler(context);

        // Add javax.websocket support
        ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);

        // Add echo endpoint to server container
        container.addEndpoint(WebsocketApi.class);

        server.start();
        server.join();
    }
}
