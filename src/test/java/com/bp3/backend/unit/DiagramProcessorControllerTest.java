package com.bp3.backend.unit;

import com.bp3.backend.controllers.DiagramProcessorController;
import com.bp3.backend.models.*;
import com.bp3.backend.services.DiagramProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagramProcessorControllerTest {

    @Mock
    private DiagramProcessorService processService;

    @InjectMocks
    private DiagramProcessorController controller;

    private ProcessDiagramDto validInput;
    private ProcessDiagramDto expectedOutput;

    @BeforeEach
    void setUp() {
        // Setup valid input
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "A", NodeType.ServiceTask),
            new NodeDto("2", "B", NodeType.HumanTask),
            new NodeDto("3", "C", NodeType.ServiceTask),
            new NodeDto("4", "D", NodeType.HumanTask),
            new NodeDto("5", "End", NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto("0", "1"),
            new EdgeDto("1", "2"),
            new EdgeDto("2", "3"),
            new EdgeDto("3", "4"),
            new EdgeDto("4", "5")
        );

        validInput = new ProcessDiagramDto(inputNodes, inputEdges);

        // Setup expected output
        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("2", "B", NodeType.HumanTask),
            new NodeDto("4", "D", NodeType.HumanTask),
            new NodeDto("5", "End", NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto("0", "2"),
            new EdgeDto("2", "4"),
            new EdgeDto("4", "5")
        );

        expectedOutput = new ProcessDiagramDto(outputNodes, outputEdges);
    }

    @Test
    void testReduceProcessDiagram_Success() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenReturn(expectedOutput);

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(validInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ProcessDiagramDto result = response.getBody();
        assertEquals(4, result.getNodes().size());
        assertEquals(3, result.getEdges().size());
        
        // Verify nodes
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "0".equals(node.getId()) && NodeType.Start.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "2".equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "4".equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "5".equals(node.getId()) && NodeType.End.equals(node.getType())));
        
        // Verify edges
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            "0".equals(edge.getFrom()) && "2".equals(edge.getTo())));
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            "2".equals(edge.getFrom()) && "4".equals(edge.getTo())));
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            "4".equals(edge.getFrom()) && "5".equals(edge.getTo())));
    }

    @Test
    void testReduceProcessDiagram_NoHumanTasks() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "A", NodeType.ServiceTask),
            new NodeDto("2", "B", NodeType.ServiceTask),
            new NodeDto("3", "End", NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto("0", "1"),
            new EdgeDto("1", "2"),
            new EdgeDto("2", "3")
        );

        ProcessDiagramDto input = new ProcessDiagramDto(inputNodes, inputEdges);

        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("3", "End", NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto("0", "3")
        );

        ProcessDiagramDto expected = new ProcessDiagramDto(outputNodes, outputEdges);

        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenReturn(expected);

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ProcessDiagramDto result = response.getBody();
        assertEquals(2, result.getNodes().size());
        assertEquals(1, result.getEdges().size());
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            "0".equals(edge.getFrom()) && "3".equals(edge.getTo())));
    }

    @Test
    void testReduceProcessDiagram_ServiceThrowsIllegalArgumentException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new IllegalArgumentException("Start node not found"));

        // When
        assertThrows(IllegalArgumentException.class, () -> controller.reduceProcessDiagram(validInput));
    }

    @Test
    void testReduceProcessDiagram_ServiceThrowsRuntimeException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When
        assertThrows(RuntimeException.class, () -> controller.reduceProcessDiagram(validInput));
    }

    @Test
    void testReduceProcessDiagram_ComplexDiagram() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "A", NodeType.ServiceTask),
            new NodeDto("2", "B", NodeType.HumanTask),
            new NodeDto("3", "C", NodeType.ServiceTask),
            new NodeDto("4", "D", NodeType.HumanTask),
            new NodeDto("5", "E", NodeType.ServiceTask),
            new NodeDto("6", "F", NodeType.HumanTask),
            new NodeDto("7", "End", NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto("0", "1"),
            new EdgeDto("1", "2"),
            new EdgeDto("2", "3"),
            new EdgeDto("3", "4"),
            new EdgeDto("4", "5"),
            new EdgeDto("5", "6"),
            new EdgeDto("6", "7")
        );

        ProcessDiagramDto input = new ProcessDiagramDto(inputNodes, inputEdges);

        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("2", "B", NodeType.HumanTask),
            new NodeDto("4", "D", NodeType.HumanTask),
            new NodeDto("6", "F", NodeType.HumanTask),
            new NodeDto("7", "End", NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto("0", "2"),
            new EdgeDto("2", "4"),
            new EdgeDto("4", "6"),
            new EdgeDto("6", "7")
        );

        ProcessDiagramDto expected = new ProcessDiagramDto(outputNodes, outputEdges);

        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenReturn(expected);

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ProcessDiagramDto result = response.getBody();
        assertEquals(5, result.getNodes().size());
        assertEquals(4, result.getEdges().size());
        
        // Verify all human tasks are present
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "2".equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "4".equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            "6".equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
    }

    @Test
    void testReduceProcessDiagram_EmptyDiagram() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "End", NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto("0", "1")
        );

        ProcessDiagramDto input = new ProcessDiagramDto(inputNodes, inputEdges);

        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenReturn(input);

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(input);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        ProcessDiagramDto result = response.getBody();
        assertEquals(2, result.getNodes().size());
        assertEquals(1, result.getEdges().size());
    }
}
