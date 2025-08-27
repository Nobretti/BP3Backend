package com.bp3.backend.services;

import com.bp3.backend.models.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiagramProcessorService {

    public ProcessDiagramDto reduceToHumanTasks(ProcessDiagramDto diagram) {
        if (diagram == null || diagram.getNodes() == null || diagram.getEdges() == null) {
            throw new IllegalArgumentException("Invalid diagram: nodes and edges cannot be null");
        }

        //Nodes Lookup only Human and End
        Map<String, NodeDto> nodeMap = diagram.getNodes().stream()
                .filter(nodeDto -> nodeDto.getType() == NodeType.HumanTask)
                .collect(Collectors.toMap(NodeDto::getId, node -> node));

        //Edge From Lookup
        Map<String, List<EdgeDto>> edgesMapped =
                diagram.getEdges()
                        .stream()
                        .collect(Collectors.groupingBy(EdgeDto::getFrom));

        //Fetch Start Node
        Optional<NodeDto> startNode = diagram.getNodes().stream()
                .filter(node -> node.getType() == NodeType.Start)
                .findFirst();

        if (startNode.isEmpty()){
            throw new RuntimeException("No start Node Provided");
        }

        //Fetch End Node
        Optional<NodeDto> endNode = diagram.getNodes().stream()
                .filter(node -> node.getType() == NodeType.End)
                .findFirst();

        if (endNode.isEmpty()){
            throw new RuntimeException("No End Node Provided");
        }

        List<EdgeDto> reducedEdges = findReachableHumanNodes(startNode.get(), endNode.get(), nodeMap, edgesMapped);

        return new ProcessDiagramDto(List.of(), reducedEdges);
    }

    private List<EdgeDto> findReachableHumanNodes(NodeDto startNode,
                                                  NodeDto endNode,
                                                  Map<String, NodeDto> nodesToLookup,
                                                  Map<String, List<EdgeDto>> edgesFromLookup) {

        List<EdgeDto> reducedEdges = new ArrayList<>();
        Map<String, String> edgesVisited = new HashMap<>();

        Map<String, String> edges = edgesFromLookup.get(startNode.getId())
                .stream()
                .collect(Collectors.toMap(EdgeDto::getFrom, EdgeDto::getTo));

        String newFromEdge = startNode.getId();

        while(!edges.isEmpty()) {

            Map<String, String> nextEdges = new HashMap<>();

            for (Map.Entry<String, String> currentEdge : edges.entrySet()) {
                Optional<NodeDto> fromNode = Optional.ofNullable(nodesToLookup.get(currentEdge.getKey()));
                Optional<String> edgeVisited = Optional.ofNullable(edgesVisited.get(currentEdge.getKey()));

                if (edgeVisited.isEmpty() || !currentEdge.getValue().equals(edgeVisited.get())) {
                    //If not human and is not end add next edges
                    if (fromNode.isEmpty()) {
                        Optional<List<EdgeDto>> nextEdgesLookup = Optional.ofNullable(edgesFromLookup.get(currentEdge.getValue()));
                        nextEdgesLookup.ifPresent(nextEdgesLookupValue ->
                                nextEdgesLookupValue.forEach(edgeDto -> nextEdges.put(edgeDto.getFrom(), edgeDto.getTo())));
                    }
                    //If human add to the final List
                    else if (fromNode.get().getType() == NodeType.HumanTask) {
                        reducedEdges.add(new EdgeDto(newFromEdge, currentEdge.getKey()));
                        newFromEdge = currentEdge.getKey();

                        Optional<List<EdgeDto>> nextEdgesLookup = Optional.ofNullable(edgesFromLookup.get(currentEdge.getValue()));
                        nextEdgesLookup.ifPresent(nextEdgesLookupValue -> nextEdgesLookupValue.forEach(edgeDto -> nextEdges.put(edgeDto.getFrom(), edgeDto.getTo())));
                    }
                }
                //Keep in Memory Edges Visited
                edgesVisited.put(currentEdge.getKey(), currentEdge.getValue());

                edges = nextEdges;
            }
        }

        reducedEdges.add(new EdgeDto(newFromEdge, endNode.getId()));

        return reducedEdges;
    }
}