/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts;

import org.vituchon.linkexplorer.domain.model.procedure.AbstractProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.EnumerableProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.ProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.composite.artifacts.MultiPageLinkInspectorStatus.Phase;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Administrador
 */
public class MultiPageLinkInspectorStatus extends AbstractProcedureStatus implements EnumerableProcedureStatus<Enum<Phase>> {
    private volatile Phase phase = Phase.NONE;
    private volatile Map<String, ProcedureStatus> inspections = new HashMap<>();

    synchronized void start() {
        this.start = System.currentTimeMillis();
        phase = Phase.INSPECT;
    }

    synchronized void end() {
        this.end = System.currentTimeMillis();
        phase = Phase.DONE;
    }

    synchronized void setInspectionStatus(String url, ProcedureStatus status) {
        this.inspections.put(url, status);
    }

    synchronized Map<String, ProcedureStatus> getInspections() {
        return Collections.unmodifiableMap(inspections);
    }

    @Override
    public synchronized Enum<Phase> getCurrentPhase() {
        return phase;
    }

    @Override
    public boolean isDone() {
        return Phase.DONE.equals(this.phase);
    }

    public static enum Phase {

        NONE, INSPECT, DONE;
    }

    @Override
    public String toString() {
      StringBuilder json = new StringBuilder();
      Set<Entry<String, ProcedureStatus>> inspectionSet = this.inspections.entrySet();

      json.append("{");
      json.append("\"phase\": \"").append(this.phase).append("\",");
      json.append("\"pagesDiscovered\": ").append(inspectionSet.size()).append(",");

      json.append("\"pages\": [");
      for (Entry<String, ProcedureStatus> inspection : inspectionSet) {
          json.append("  {");
          json.append("    \"url\": \"").append(inspection.getKey()).append("\",");
          json.append("    \"status\": ").append(inspection.getValue().toString());
          json.append("  },");
      }
      if (!inspectionSet.isEmpty()) {
          json.setLength(json.length() - 1); // saca última coma
      }

      json.append("]");
      json.append("}");

      return json.toString();
    }

}
