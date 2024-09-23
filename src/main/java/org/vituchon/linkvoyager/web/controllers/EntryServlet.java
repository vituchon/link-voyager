package org.vituchon.linkvoyager.web.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.vituchon.linkexplorer.api.UrlExplorer;

public class EntryServlet extends HttpServlet {
    
    private final ExplorerAgengy explorerAgengy = new ExplorerAgengy();
    
    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        try (PrintWriter writer = response.getWriter()) {
            writer.append("do post");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (PrintWriter writer = response.getWriter()) {
            String baseUrl = request.getParameter("baseUrl");
            String uuid = request.getParameter("uuid");
            if (baseUrl != null && !baseUrl.isEmpty()) {
                String requestExploration = explorerAgengy.requestExploration(new ExplorerParameters(2, 2, baseUrl));
                writer.append("exploration queued : " + requestExploration);
            } else if (uuid != null) {
                UrlExplorer explorer = explorerAgengy.queryExploration(uuid);
                writer.append(explorer.getLastStatus().toString());
            } else {
                writer.append("do get");
            }
        }
    }

}
