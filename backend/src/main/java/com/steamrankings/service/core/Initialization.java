package com.steamrankings.service.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class Initialization {

    final public static Properties CONFIG = new Properties();
    private static final Logger logger = Logger.getLogger(Initialization.class.getName());

    private static final String REST_API_INTERFACE_PROFILES = "/profile";
    private static final String REST_API_INTERFACE_LEADERBOARDS = "/leaderboards";
    private static final String REST_API_INTERFACE_GAMES = "/games";
    private static final String REST_API_INTERFACE_ACHIEVEMENTS = "/achievements";
    private static final String REST_API_INTERFACE_BLACKLIST = "/blacklist";
    private static final String REST_API_INTERFACE_VERSION = "/version";
    private static final String REST_API_INTERFACE_UPDATE_PROFILE = "/update";
    private static final String REST_API_INTERFACE_TOP_PLAYER = "/topplayer";
    private static final String REST_API_INTERFACE_ECHO = "/echo";
    private static final String REST_API_INTERFACE_NEWS = "/news";

    public static void main(String[] args) throws IOException {

        try ( // Load configuration file
                InputStream inputStream = new FileInputStream("config.properties")) {
            CONFIG.load(inputStream);
        }

        String portAsString = System.getenv("BACKEND_PORT");
        int portAsInt = 6789;
        if (portAsString != null) {
            System.out.println(portAsString);
            portAsInt = Integer.decode(portAsString);
        }
        
        // Create HTTP server
        Server server = new Server(portAsInt);

        // Initialize updater
        Updater update = new Updater();

        // Initialize various mappings from URL -> handler
        ContextHandler context1 = new ContextHandler(REST_API_INTERFACE_ECHO);
        context1.setHandler(new EchoHandler());

        ContextHandler context2 = new ContextHandler(REST_API_INTERFACE_VERSION);
        context2.setHandler(new VersionHandler());

        ContextHandler context3 = new ContextHandler(REST_API_INTERFACE_PROFILES);
        context3.setHandler(new ProfileHandler());

        ContextHandler context4 = new ContextHandler(REST_API_INTERFACE_GAMES);
        context4.setHandler(new GamesHandler());

        ContextHandler context5 = new ContextHandler(REST_API_INTERFACE_ACHIEVEMENTS);
        context5.setHandler(new AchievementsHandler());

        ContextHandler context6 = new ContextHandler(REST_API_INTERFACE_BLACKLIST);
        context6.setHandler(new BlacklistHandler());

        ContextHandler context7 = new ContextHandler(REST_API_INTERFACE_LEADERBOARDS);
        context7.setHandler(new LeaderboardHandler());

        ContextHandler context8 = new ContextHandler(REST_API_INTERFACE_TOP_PLAYER);
        context8.setHandler(new TopPlayerHandler());

        ContextHandler context9 = new ContextHandler(REST_API_INTERFACE_UPDATE_PROFILE);
        context9.setHandler(new UpdateHandler());
        
        ContextHandler context10 = new ContextHandler(REST_API_INTERFACE_NEWS);
        context10.setHandler(new UpdateHandler());

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context1, context2, context3, context4, context5, context6, context7, context8, context9, context10});

        server.setHandler(contexts);

        try {
            // Start things up!
            server.start();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        try {
            // The use of server.join() the will make the current thread join and
            // wait until the server is done executing.
            // See
            // http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
            server.join();

        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

    }

}
