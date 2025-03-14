package org.appland.settlers.rest;

import jakarta.websocket.server.ServerEndpointConfig;
import org.appland.settlers.maps.MapFile;
import org.appland.settlers.maps.MapLoader;
import org.appland.settlers.rest.resource.MapsResource;
import org.appland.settlers.rest.resource.WebsocketApi;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ResourceServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.websocket.jakarta.server.config.JakartaWebSocketServletContainerInitializer;
import org.eclipse.jetty.server.Server;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            container.setDefaultMaxSessionIdleTimeout(1800_000); // 30 minutes

            ServerEndpointConfig echoConfig = ServerEndpointConfig.Builder.create(WebsocketApi.class, "/ws/api").build();
            container.addEndpoint(echoConfig);
        });

        servletContextHandler.getSessionHandler().setMaxInactiveInterval(1800); // 30 minutes in seconds

        // Add file server for the assets
        // add special pathspec of "/alt/" content mapped to the altPath
        Path altPath = Paths.get("assets").toRealPath();

        System.out.printf("Serving files from %s%n", altPath);

        ServletHolder holderAlt = new ServletHolder("static-alt", ResourceServlet.class);
        // Use the String representation of the URL
        try {
            URL url = altPath.toUri().toURL();
            holderAlt.setInitParameter("baseResource", url.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        holderAlt.setInitParameter("dirAllowed", "true");
        holderAlt.setInitParameter("pathInfoOnly", "true");
        servletContextHandler.addServlet(holderAlt, "/assets/*");

        // Lastly, the default servlet for root content (always needed, to satisfy servlet spec)
        // It is important that this is last.
        ServletHolder holderDef = new ServletHolder("default", DefaultServlet.class);
        holderDef.setInitParameter("dirAllowed", "true");
        servletContextHandler.addServlet(holderDef, "/");

        server.start();
        server.join();

        System.out.println(" - Server started");
    }
}
