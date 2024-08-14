package org.appland.settlers.rest;

import jakarta.websocket.server.ServerEndpointConfig;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.rest.resource.MapsResource;
import org.appland.settlers.rest.resource.WebsocketApi;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.run();
    }

    private void run() throws Exception {

        // Start the game ticker
        GameTicker.GAME_TICKER.activate();
        System.out.println(" - Started game ticker");

        // Load the maps
        System.out.println(" - Loading maps");
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

                            synchronized (MapsResource.mapsResource) {
                                MapsResource.mapsResource.addMap(mapFile);
                            }
                        } catch (Exception e) {
                            System.out.println(mapFilename.toString());
                            System.out.println("Exception while loading maps: " + e);
                        }
                    }
            );
        }
        System.out.println(" - Loaded maps");

        final int port = 8080;

        System.out.printf(" - Starting server on part %s%n", port);

        Server server = new Server(port);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");
        server.setHandler(servletContextHandler);

        // Add jakarta.websocket support
        JakartaWebSocketServletContainerInitializer.configure(servletContextHandler, (context, container) ->
        {
            // Add echo endpoint to server container
            ServerEndpointConfig echoConfig = ServerEndpointConfig.Builder.create(WebsocketApi.class, "/ws/monitor/games").build();
            container.addEndpoint(echoConfig);
        });

        // Add default servlet (to serve the html/css/js)
        // Figure out where the static files are stored.
        /*URL urlStatics = Thread.currentThread().getContextClassLoader().getResource("assets/");
        Objects.requireNonNull(urlStatics, "Unable to find assets to serve");
        String urlBase = urlStatics.toExternalForm().replaceFirst("/[^/]*$", "/");
        ServletHolder defHolder = new ServletHolder("default", new DefaultServlet());
        defHolder.setInitParameter("resourceBase", urlBase);
        defHolder.setInitParameter("dirAllowed", "true");
        servletContextHandler.addServlet(defHolder, "/");*/

        server.start();
        server.join();

        System.out.println(" - Server started");
    }
}
