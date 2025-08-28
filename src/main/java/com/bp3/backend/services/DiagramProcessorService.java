package com.bp3.backend.services;

import com.bp3.backend.models.ProcessDiagramDto;


public interface DiagramProcessorService {

    /**
     * Service that transforms a BPM Diagram into its reduced version removing all non-human entities
     *
     * @param diagram composed with Nodes and Edges
     * @return ProcessDiagramDto diagram as a reduced version with Nodes and Edges
     */
    ProcessDiagramDto reduceToHumanTasks(ProcessDiagramDto diagram);
}
