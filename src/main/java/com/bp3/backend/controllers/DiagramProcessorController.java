package com.bp3.backend.controllers;

import com.bp3.backend.common.Messages;
import com.bp3.backend.models.ProcessDiagramDto;
import com.bp3.backend.services.DiagramProcessorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for processing and reducing BPMN process diagrams.
 * 
 * This controller provides endpoints for reducing process diagrams by removing
 * non-human steps (ServiceTask, Gateway) and keeping only human tasks, start,
 * and end nodes while maintaining the logical flow between them.</p>
 */
@RestController
@RequestMapping("/api/diagramprocess")
public class DiagramProcessorController {

    private static final Logger logger = LoggerFactory.getLogger(DiagramProcessorController.class);

    private final DiagramProcessorService processService;

    public DiagramProcessorController(DiagramProcessorService processService) {
        this.processService = processService;
    }

    /**
     * Reduces a process diagram by removing non-human steps and keeping only human tasks.
     * 
     * This endpoint accepts a JSON payload containing a process diagram with nodes and edges,
     * processes it to remove automated steps (ServiceTask, Gateway), and returns a simplified
     * diagram that maintains the logical flow between human-interaction points.</p>
     * 
     * @param diagram The process diagram to reduce
     * @return ResponseEntity containing the reduced diagram or error details
     * 
     */
    @PostMapping("/reduce")
    public ResponseEntity<ProcessDiagramDto> reduceProcessDiagram(@Valid @RequestBody ProcessDiagramDto diagram) {
        String requestId = generateRequestId();

        logger.info("[{}] " + Messages.PROCESSING_DIAGRAM_REDUCTION, 
            requestId, diagram.getNodes().size(), diagram.getEdges().size());
        
        // Process the diagram
        ProcessDiagramDto reducedDiagram = processService.reduceToHumanTasks(diagram);
        
        logger.info("[{}] " + Messages.DIAGRAM_REDUCTION_COMPLETED, 
            requestId, reducedDiagram.getNodes().size(), reducedDiagram.getEdges().size());
        
        return ResponseEntity.ok(reducedDiagram);
    }

    /**
     * Generates a unique request identifier for tracking requests in logs.
     * 
     * @return A unique request identifier
     */
    private String generateRequestId() {
        return String.format(Messages.REQUEST_ID_FORMAT, System.currentTimeMillis(), Thread.currentThread().getId());
    }
}
