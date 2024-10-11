/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkvoyager.web.controllers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.vituchon.linkexplorer.api.UrlExplorer;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;

/**
 *
 * @author Administrador
 */
public class ExplorerAgengy {

    private final Map<UUID, UrlExplorer> map;

    public ExplorerAgengy() {
        this.map = new ConcurrentHashMap<>();
    }

    public String requestExploration(ExplorerParameters parameters) {
        UrlExplorer explorer = new UrlExplorer.Builder().setMaxDeep(parameters.getMaxDeep()).setWorkersDesired(parameters.getWorkers()).setRoot(parameters.getRoot()).build();
        UUID key = UUID.randomUUID();
        map.put(key, explorer);
        explorer.begin();
        return key.toString();
    }
    

    public UrlExplorer queryExploration(String uuid) {
        return map.get(UUID.fromString(uuid));
    }
}
