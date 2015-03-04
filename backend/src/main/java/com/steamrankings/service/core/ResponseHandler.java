/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;

/**
 *
 * @author Michael
 */
public class ResponseHandler {

    private final static Logger LOGGER = Logger.getLogger(ResponseHandler.class.getName());

    /**
     * Sends an error message.
     *
     * @param message
     * @param response
     * @param baseRequest
     */
    public static void sendError(String message, HttpServletResponse response, Request baseRequest) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain");

            out = response.getWriter();
            if (message != null) {
                out.print(message);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        baseRequest.setHandled(true);
    }

    public static void sendData(String data, HttpServletResponse response, Request baseRequest) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.print(data);
            baseRequest.setHandled(true);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
