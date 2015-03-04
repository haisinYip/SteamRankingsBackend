/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author Michael
 */
public class VersionHandler extends AbstractHandler {

    private final static Logger LOGGER = Logger.getLogger(VersionHandler.class.getName());

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        
        Properties properties = new Properties();
        String version = null;
        try {
            properties.load(Initialization.class.getResourceAsStream("/buildNumber.properties"));
            version = properties.getProperty("git-sha-1");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, e, null);
        }

        if (version == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            
            if (version.equals("${buildNumber}")) {
                out.print("DEV");
            } else {
                out.print(version);
            }
            baseRequest.setHandled(true);
        }

    }

}
