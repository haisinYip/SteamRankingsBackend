/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steamrankings.service.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * Responds with the key/value map sent for debugging.
 * @author Michael
 */
public class EchoHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        out.println("<html>");

        Map<String, String[]> attNames = request.getParameterMap();
        attNames.keySet().stream().forEach((String key) -> {
            out.println("Key: " + key + " | Value: " + Arrays.toString(attNames.get(key)));
        });
        out.println("</html>");

        baseRequest.setHandled(true);
    }
}
