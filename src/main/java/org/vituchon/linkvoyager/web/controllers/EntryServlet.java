package org.vituchon.linkvoyager.web.controllers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import java.util.logging.Logger;

import javax.servlet.http.*;
import javax.servlet.*;

import org.giordans.graphs.WeightedGraph;
import org.vituchon.linkexplorer.api.UrlExplorer;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.HtmlMap;

public class EntryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final ExplorerAgengy explorerAgengy = new ExplorerAgengy();
	private static final Logger logger = Logger.getLogger(HttpServlet.class.getName());

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
            String baseUrl = request.getParameter("rootURL");
            String uuid = request.getParameter("uuid");
            String uuidJson = request.getParameter("uuidJson");
            if (baseUrl != null && !baseUrl.isEmpty()) {
                int deep = parseOrDefault(request.getParameter("deep"), 0, "deep");
                int workers = parseOrDefault(request.getParameter("workers"), 5, "workers");

                String requestExploration = explorerAgengy.requestExploration(new ExplorerParameters(deep, workers, baseUrl));
                writer.append("exploration queued : " + requestExploration);
            } else if (uuid != null) {
               	UrlExplorer explorer = explorerAgengy.queryExploration(uuid);
                ProcedureStatus lastStatus = explorer.getLastStatus();
                String jsonStatus = lastStatus.toString();
                if (lastStatus.isDone()) {
                  WeightedGraph<String>  map = explorer.getOutput().getMap();
                  int nodesCount = map.getNodes().size();
                  int edgesCount = map.getEdges().size();
                  StringBuilder updatedStatus = new StringBuilder(jsonStatus);
                  updatedStatus.insert(updatedStatus.length() - 1, ", \"nodesCount\": " + nodesCount + ", \"edgesCount\": " + edgesCount);
                  jsonStatus = updatedStatus.toString();
                }
                response.setContentType("application/json");
                writer.append(jsonStatus.toString());
            } else if (uuidJson != null) {
                UrlExplorer explorer = explorerAgengy.queryExploration(uuidJson);
                HtmlMap htmlMap = explorer.getOutput();
                response.setContentType("application/json");
                writer.append(htmlMap.toJsonString());
            } else {
                writer.append("do get");
            }
        } catch (Exception e) {
        e.printStackTrace();
      }
    }

    private int parseOrDefault(String paramValue, int defaultValue, String paramName) {
        try {
            return paramValue != null ? Integer.parseInt(paramValue) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warning("Failed to parse parameter '" + paramName + "' as integer. Using default value. Error: " + e.getMessage());
            return defaultValue;
        }
    }
}
