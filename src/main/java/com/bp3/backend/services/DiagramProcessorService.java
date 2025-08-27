package com.bp3.backend.services;

import com.bp3.backend.models.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.bp3.backend.common.Messages;

@Service
public class DiagramProcessorService {

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

        // Create lookup maps for efficient access
        Map<String, NodeDto> nodeMap = diagram.getNodes().stream()
                .collect(Collectors.toMap(NodeDto::getId, node -> node));
        
        Map<String, List<EdgeDto>> edgesFromMap = diagram.getEdges().stream()
                .collect(Collectors.groupingBy(EdgeDto::getFrom));

        // Find start and end nodes
        NodeDto startNode = diagram.getNodes().stream()
                .filter(node -> node.getType() == NodeType.Start)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Messages.START_NODE_NOT_FOUND));

        NodeDto endNode = diagram.getNodes().stream()
                .filter(node -> node.getType() == NodeType.End)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(Messages.END_NODE_NOT_FOUND));

        // Find all human task nodes
        List<NodeDto> humanTaskNodes = diagram.getNodes().stream()
                .filter(node -> node.getType() == NodeType.HumanTask)
                .collect(Collectors.toList());


        return new ProcessDiagramDto(generateReducedNodes(startNode, endNode, humanTaskNodes), 
            generateReducedEdges(startNode, endNode, humanTaskNodes, nodeMap, edgesFromMap));
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
     * 
     * @param startNode
     * @param endNode
     * @param humanTaskNodes
     * @param nodeMap
     * @param edgesFromMap
     * @return
     */
    private List<EdgeDto> generateReducedEdges(NodeDto startNode, NodeDto endNode,
                                               List<NodeDto> humanTaskNodes,
                                               Map<String, NodeDto> nodeMap,
                                               Map<String, List<EdgeDto>> edgesFromMap) {
        List<EdgeDto> result = new ArrayList<>();
        
        // If no human tasks, connect start directly to end
        if (humanTaskNodes.isEmpty()) {
            if (hasPath(startNode.getId(), endNode.getId(), nodeMap, edgesFromMap)) {
                result.add(new EdgeDto(startNode.getId(), endNode.getId()));
            }
            return result;
        }

        // Find the first reachable human task from start
        NodeDto currentHumanTask = findFirstReachableHumanTask(startNode.getId(), humanTaskNodes, nodeMap, edgesFromMap);
        if (currentHumanTask != null) {
            result.add(new EdgeDto(startNode.getId(), currentHumanTask.getId()));
        } else {
            // No human tasks reachable from start, connect start to end if possible
            if (hasPath(startNode.getId(), endNode.getId(), nodeMap, edgesFromMap)) {
                result.add(new EdgeDto(startNode.getId(), endNode.getId()));
            }
            return result;
        }

        // Find paths between human tasks
        NodeDto previousHumanTask = currentHumanTask;
        Set<String> processedHumanTasks = new HashSet<>();
        processedHumanTasks.add(currentHumanTask.getId());

        while (processedHumanTasks.size() < humanTaskNodes.size()) {
            NodeDto nextHumanTask = findNextReachableHumanTask(previousHumanTask.getId(), humanTaskNodes, 
                                                              processedHumanTasks, nodeMap, edgesFromMap);
            if (nextHumanTask != null) {
                result.add(new EdgeDto(previousHumanTask.getId(), nextHumanTask.getId()));
                previousHumanTask = nextHumanTask;
                processedHumanTasks.add(nextHumanTask.getId());
            } else {
                break;
            }
        }

        // Connect the last human task to end if there's a path
        if (hasPath(previousHumanTask.getId(), endNode.getId(), nodeMap, edgesFromMap)) {
            result.add(new EdgeDto(previousHumanTask.getId(), endNode.getId()));
        }

        return result;
    }

    private NodeDto findFirstReachableHumanTask(String startId, List<NodeDto> humanTaskNodes,
                                               Map<String, NodeDto> nodeMap,
                                               Map<String, List<EdgeDto>> edgesFromMap) {
        for (NodeDto humanTask : humanTaskNodes) {
            if (hasPath(startId, humanTask.getId(), nodeMap, edgesFromMap)) {
                return humanTask;
            }
        }
        return null;
    }

    private NodeDto findNextReachableHumanTask(String fromId, List<NodeDto> humanTaskNodes,
                                              Set<String> processedTasks,
                                              Map<String, NodeDto> nodeMap,
                                              Map<String, List<EdgeDto>> edgesFromMap) {
        for (NodeDto humanTask : humanTaskNodes) {
            if (!processedTasks.contains(humanTask.getId()) && 
                hasPath(fromId, humanTask.getId(), nodeMap, edgesFromMap)) {
                return humanTask;
            }
        }
        return null;
    }

    private boolean hasPath(String fromId, String toId, Map<String, NodeDto> nodeMap,
                           Map<String, List<EdgeDto>> edgesFromMap) {
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