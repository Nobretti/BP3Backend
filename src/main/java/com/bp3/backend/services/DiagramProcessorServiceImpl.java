package com.bp3.backend.services;

import com.bp3.backend.models.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.bp3.backend.common.Messages;

@Service
public class DiagramProcessorServiceImpl implements DiagramProcessorService{

    /**
     * Service that transforms a BPM Diagram into its reduced version removing all non-human entities
     *
     * @param diagram composed with Nodes and Edges
     * @return ProcessDiagramDto diagram as a reduced version with Nodes and Edges
     */
    public ProcessDiagramDto reduceToHumanTasks(ProcessDiagramDto diagram) {
        if (diagram == null || diagram.getNodes() == null || diagram.getEdges() == null) {
            throw new IllegalArgumentException(Messages.INVALID_DIAGRAM);
        }

        // Single pass to find start, end, and human tasks
        NodeDto startNode = null;
        NodeDto endNode = null;
        final List<NodeDto> humanTaskNodes = new ArrayList<>();

        for (NodeDto node : diagram.getNodes()) {
            switch (node.getType()) {
                case Start:
                    startNode = node;
                    break;
                case End:
                    endNode = node;
                    break;
                case HumanTask:
                    humanTaskNodes.add(node);
                    break;
            }
        }

        if (startNode == null) {
            throw new IllegalArgumentException(Messages.START_NODE_NOT_FOUND);
        }
        if (endNode == null) {
            throw new IllegalArgumentException(Messages.END_NODE_NOT_FOUND);
        }

        // Create edge lookup map
        Map<String, List<EdgeDto>> edgesFromMap = diagram.getEdges()
                .stream()
                .collect(Collectors.groupingBy(EdgeDto::getFrom));

        return new ProcessDiagramDto(generateReducedNodes(startNode, endNode, humanTaskNodes), 
            generateReducedEdges(startNode, endNode, humanTaskNodes, edgesFromMap));
    }

    /**
     * Generate Reduced Nodes based on start, end and human nodes
     *
     * @param startNode
     * @param endNode
     * @param humanTaskNodes
     * @return
     */

    private List<NodeDto> generateReducedNodes(NodeDto startNode, NodeDto endNode, List<NodeDto> humanTaskNodes){

        final List<NodeDto> reducedNodes = new ArrayList<>();
        // Add start node
        reducedNodes.add(startNode);

        // Add human task nodes
        reducedNodes.addAll(humanTaskNodes);

        // Add end node
        reducedNodes.add(endNode);

        return reducedNodes;
    }

    /**
     * Generate Reduced Edges based on start, end and human nodes
     * Creates direct connections between human tasks in ID order
     *
     * @param startNode
     * @param endNode
     * @param humanTaskNodes
     * @param edgesFromMap
     * @return
     */
    private List<EdgeDto> generateReducedEdges(NodeDto startNode, NodeDto endNode,
                                               List<NodeDto> humanTaskNodes,
                                               Map<String, List<EdgeDto>> edgesFromMap) {
        List<EdgeDto> result = new ArrayList<>();
        
        // If no human tasks, connect start directly to end
        if (humanTaskNodes.isEmpty()) {
            if (hasPath(startNode.getId(), endNode.getId(), edgesFromMap)) {
                result.add(new EdgeDto(startNode.getId(), endNode.getId()));
            }
            return result;
        }

        // Find all human tasks that are reachable from start
        List<NodeDto> reachableHumanTasks = humanTaskNodes.stream()
                .filter(humanNode -> hasPath(startNode.getId(), humanNode.getId(), edgesFromMap))
                .collect(Collectors.toList());

        if (reachableHumanTasks.isEmpty()) {
            // No human tasks reachable from start, connect start to end if possible
            if (hasPath(startNode.getId(), endNode.getId(), edgesFromMap)) {
                result.add(new EdgeDto(startNode.getId(), endNode.getId()));
            }
            return result;
        }

        // Connect start to first human task
        result.add(new EdgeDto(startNode.getId(), reachableHumanTasks.get(0).getId()));

        // Connect human tasks in sequence
        for (int i = 0; i < reachableHumanTasks.size() - 1; i++) {
            NodeDto current = reachableHumanTasks.get(i);
            NodeDto next = reachableHumanTasks.get(i + 1);
            result.add(new EdgeDto(current.getId(), next.getId()));
        }

        // Connect last human task to end
        NodeDto lastHumanTask = reachableHumanTasks.get(reachableHumanTasks.size() - 1);
        if (hasPath(lastHumanTask.getId(), endNode.getId(), edgesFromMap)) {
            result.add(new EdgeDto(lastHumanTask.getId(), endNode.getId()));
        }

        return result;
    }



    /**
     * Check if there's a path from fromId to toId using BFS
     * 
     * @param fromId
     * @param toId
     * @param edgesFromMap
     * @return
     */
    private boolean hasPath(String fromId, String toId, Map<String, List<EdgeDto>> edgesFromMap) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(fromId);
        visited.add(fromId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            
            if (current.equals(toId)) {
                return true;
            }

            List<EdgeDto> edges = edgesFromMap.get(current);
            if (edges != null) {
                for (EdgeDto edge : edges) {
                    String next = edge.getTo();
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.add(next);
                    }
                }
            }
        }
        return false;
    }
}