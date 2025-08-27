package com.bp3.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class ProcessDiagramDto {
    @NotNull
    @Valid
    private List<NodeDto> nodes;
    
    @NotNull
    @Valid
    private List<EdgeDto> edges;

    public ProcessDiagramDto() {}

    public ProcessDiagramDto(List<NodeDto> nodes, List<EdgeDto> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    @JsonProperty("nodes")
    public List<NodeDto> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }

    @JsonProperty("edges")
    public List<EdgeDto> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDto> edges) {
        this.edges = edges;
    }
}