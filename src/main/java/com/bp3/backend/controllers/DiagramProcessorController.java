package com.bp3.backend.controllers;

import com.bp3.backend.models.ProcessDiagramDto;
import com.bp3.backend.services.DiagramProcessorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diagramprocess")
public class DiagramProcessorController {

    private final DiagramProcessorService processService;

    public DiagramProcessorController(DiagramProcessorService processService) {
        this.processService = processService;
    }

    @GetMapping("/reduce")
    public ResponseEntity<ProcessDiagramDto> reduceProcessDiagram(@Valid @RequestBody ProcessDiagramDto diagram) {
        try {
            ProcessDiagramDto reducedDiagram = processService.reduceToHumanTasks(diagram);
            return ResponseEntity.ok(reducedDiagram);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}