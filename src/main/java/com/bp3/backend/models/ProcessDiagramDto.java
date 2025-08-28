package com.bp3.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data Transfer Object representing a BPMN process diagram.
 * 
 * <p>This class encapsulates the structure of a process diagram containing nodes
 * and edges that define the workflow. It is used for both input (original diagram)
 * and output (reduced diagram) of the diagram reduction process.</p>
 * 
 * <p>The diagram structure consists of:</p>
 *   List of process nodes (Start, End, HumanTask, ServiceTask, Gateway)
 *   List of connections between nodes defining the flow
 */
public class ProcessDiagramDto {
    
    /**
     * List of nodes in the process diagram.
     * 
     * <p>Each node represents a step in the process workflow and must have a unique ID,
     * a descriptive name, and a specific type that determines its role in the process.</p>
     */
    @NotNull(message = "Nodes list cannot be null")
    @Valid
    @JsonProperty("nodes")
    private List<NodeDto> nodes;
    
    /**
     * List of edges connecting nodes in the process diagram.
     * 
     * <p>Each edge represents a transition between two nodes and defines the flow
     * of the process. Edges must reference valid node IDs that exist in the nodes list.</p>
     */
    @NotNull(message = "Edges list cannot be null")
    @Valid
    @JsonProperty("edges")
    private List<EdgeDto> edges;

    /**
     * Default constructor for JSON deserialization.
     */
    public ProcessDiagramDto() {}

    /**
     * Constructor with nodes and edges.
     * 
     * @param nodes List of nodes in the diagram
     * @param edges List of edges connecting the nodes
     */
    public ProcessDiagramDto(List<NodeDto> nodes, List<EdgeDto> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    /**
     * Gets the list of nodes in the diagram.
     * 
     * @return List of NodeDto objects
     */
    public List<NodeDto> getNodes() {
        return nodes;
    }

    /**
     * Sets the list of nodes in the diagram.
     * 
     * @param nodes List of NodeDto objects
     */
    public void setNodes(List<NodeDto> nodes) {
        this.nodes = nodes;
    }

    /**
     * Gets the list of edges in the diagram.
     * 
     * @return List of EdgeDto objects
     */
    public List<EdgeDto> getEdges() {
        return edges;
    }

    /**
     * Sets the list of edges in the diagram.
     * 
     * @param edges List of EdgeDto objects
     */
    public void setEdges(List<EdgeDto> edges) {
        this.edges = edges;
    }

    /**
     * Returns a string representation of the diagram.
     * 
     * @return String containing node and edge counts
     */
    @Override
    public String toString() {
        return "ProcessDiagramDto{" +
                "nodes=" + (nodes != null ? nodes.size() : "null") +
                ", edges=" + (edges != null ? edges.size() : "null") +
                '}';
    }
}