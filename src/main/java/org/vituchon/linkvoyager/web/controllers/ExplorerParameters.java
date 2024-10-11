/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkvoyager.web.controllers;

/**
 *
 * @author Administrador
 */
public class ExplorerParameters {
    private final int maxDeep;
    private final int workers;
    private final String root;

    public ExplorerParameters(int maxDeep, int workers, String root) {
        this.maxDeep = maxDeep;
        this.workers = workers;
        this.root = root;
    }

    public int getMaxDeep() {
        return maxDeep;
    }

    public int getWorkers() {
        return workers;
    }

    public String getRoot() {
        return root;
    }
    
    
}
