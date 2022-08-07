package org.appland.settlers.rest;

import org.appland.settlers.rest.resource.WebsocketMonitor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.websocket.server.ServerContainer;

public class Main {

    private static final String APPLICATION_PATH = "/settlers/api/*";
    private static final String CONTEXT_ROOT = "/";

    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.run();
    }

    private void run() throws Exception {

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
        container.addEndpoint(WebsocketMonitor.class);

        // Register the lifecycle listener
        context.addEventListener(new DeploymentListener());

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix", APPLICATION_PATH);
        restEasyServlet.setInitParameter("javax.ws.rs.Application", FatJarApplication.class.getName());
        context.addServlet(restEasyServlet, APPLICATION_PATH);

        server.start();
        server.join();
    }
}
