package com.bp3.backend.unit;

import com.bp3.backend.common.Messages;
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
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_3, Messages.TEST_NODE_C, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_5, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_1),
            new EdgeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_ID_2),
            new EdgeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_ID_3),
            new EdgeDto(Messages.TEST_NODE_ID_3, Messages.TEST_NODE_ID_4),
            new EdgeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_ID_5)
        );

        validInput = new ProcessDiagramDto(inputNodes, inputEdges);

        // Setup expected output
        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_5, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_2),
            new EdgeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_ID_4),
            new EdgeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_ID_5)
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
            Messages.TEST_NODE_ID_0.equals(node.getId()) && NodeType.Start.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            Messages.TEST_NODE_ID_2.equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            Messages.TEST_NODE_ID_4.equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            Messages.TEST_NODE_ID_5.equals(node.getId()) && NodeType.End.equals(node.getType())));
        
        // Verify edges
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            Messages.TEST_NODE_ID_0.equals(edge.getFrom()) && Messages.TEST_NODE_ID_2.equals(edge.getTo())));
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            Messages.TEST_NODE_ID_2.equals(edge.getFrom()) && Messages.TEST_NODE_ID_4.equals(edge.getTo())));
        assertTrue(result.getEdges().stream().anyMatch(edge -> 
            Messages.TEST_NODE_ID_4.equals(edge.getFrom()) && Messages.TEST_NODE_ID_5.equals(edge.getTo())));
    }

    @Test
    void testReduceProcessDiagram_NoHumanTasks() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_B, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_3, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_1),
            new EdgeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_ID_2),
            new EdgeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_ID_3)
        );

        ProcessDiagramDto input = new ProcessDiagramDto(inputNodes, inputEdges);

        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_3, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_3)
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
            Messages.TEST_NODE_ID_0.equals(edge.getFrom()) && Messages.TEST_NODE_ID_3.equals(edge.getTo())));
    }

    @Test
    void testReduceProcessDiagram_ServiceThrowsIllegalArgumentException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new IllegalArgumentException(Messages.NO_START_NODE_FOUND));

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(validInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testReduceProcessDiagram_ServiceThrowsRuntimeException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new RuntimeException(Messages.UNEXPECTED_ERROR));

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(validInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testReduceProcessDiagram_ComplexDiagram() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_3, Messages.TEST_NODE_C, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_5, Messages.TEST_NODE_E, NodeType.ServiceTask),
            new NodeDto(Messages.TEST_NODE_ID_6, Messages.TEST_NODE_F, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_7, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_1),
            new EdgeDto(Messages.TEST_NODE_ID_1, Messages.TEST_NODE_ID_2),
            new EdgeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_ID_3),
            new EdgeDto(Messages.TEST_NODE_ID_3, Messages.TEST_NODE_ID_4),
            new EdgeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_ID_5),
            new EdgeDto(Messages.TEST_NODE_ID_5, Messages.TEST_NODE_ID_6),
            new EdgeDto(Messages.TEST_NODE_ID_6, Messages.TEST_NODE_ID_7)
        );

        ProcessDiagramDto input = new ProcessDiagramDto(inputNodes, inputEdges);

        List<NodeDto> outputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_6, Messages.TEST_NODE_F, NodeType.HumanTask),
            new NodeDto(Messages.TEST_NODE_ID_7, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> outputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_2),
            new EdgeDto(Messages.TEST_NODE_ID_2, Messages.TEST_NODE_ID_4),
            new EdgeDto(Messages.TEST_NODE_ID_4, Messages.TEST_NODE_ID_6),
            new EdgeDto(Messages.TEST_NODE_ID_6, Messages.TEST_NODE_ID_7)
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
            Messages.TEST_NODE_ID_2.equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            Messages.TEST_NODE_ID_4.equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
        assertTrue(result.getNodes().stream().anyMatch(node -> 
            Messages.TEST_NODE_ID_6.equals(node.getId()) && NodeType.HumanTask.equals(node.getType())));
    }

    @Test
    void testReduceProcessDiagram_EmptyDiagram() {
        // Given
        List<NodeDto> inputNodes = Arrays.asList(
            new NodeDto(Messages.TEST_NODE_ID_0, Messages.START_NODE_NAME, NodeType.Start),
            new NodeDto(Messages.TEST_NODE_ID_1, Messages.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> inputEdges = Arrays.asList(
            new EdgeDto(Messages.TEST_NODE_ID_0, Messages.TEST_NODE_ID_1)
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

    @Test
    void testReduceProcessDiagram_ServiceThrowsNullPointerException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new NullPointerException(Messages.NULL_REFERENCE));

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(validInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testReduceProcessDiagram_ServiceThrowsIllegalStateException() {
        // Given
        when(processService.reduceToHumanTasks(any(ProcessDiagramDto.class)))
            .thenThrow(new IllegalStateException(Messages.INVALID_STATE));

        // When
        ResponseEntity<ProcessDiagramDto> response = controller.reduceProcessDiagram(validInput);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
