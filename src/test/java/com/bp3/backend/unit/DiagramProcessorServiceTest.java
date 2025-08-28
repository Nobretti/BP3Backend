package com.bp3.backend.unit;

import com.bp3.backend.common.MessagesTest;
import com.bp3.backend.models.*;
import com.bp3.backend.services.DiagramProcessorService;
import com.bp3.backend.services.DiagramProcessorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiagramProcessorServiceTest {

    private DiagramProcessorService service;

    @BeforeEach
    void setUp() {
        service = new DiagramProcessorServiceImpl();
    }

    @Test
    void testReduceToHumanTasks_SimpleCase() {
        // Create test diagram: Start -> A (Service) -> B (Human) -> C (Service) -> D (Human) -> End
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.TEST_NODE_C, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_4, MessagesTest.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_5, MessagesTest.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.TEST_NODE_ID_1), // Start -> A
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2), // A -> B
            new EdgeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_ID_3), // B -> C
            new EdgeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.TEST_NODE_ID_4), // C -> D
            new EdgeDto(MessagesTest.TEST_NODE_ID_4, MessagesTest.TEST_NODE_ID_5)  // D -> End
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expected: Start -> B -> D -> End
        assertEquals(4, result.getNodes().size());
        assertEquals(3, result.getEdges().size());

        // Check nodes
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_0) && n.getType() == NodeType.Start));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_2) && n.getType() == NodeType.HumanTask));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_4) && n.getType() == NodeType.HumanTask));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_5) && n.getType() == NodeType.End));

        // Check edges
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_0) && e.getTo().equals(MessagesTest.TEST_NODE_ID_2)));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_2) && e.getTo().equals(MessagesTest.TEST_NODE_ID_4)));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_4) && e.getTo().equals(MessagesTest.TEST_NODE_ID_5)));
    }

    @Test
    void testReduceToHumanTasks_NoHumanTasks() {
        // Create test diagram: Start -> A (Service) -> B (Service) -> End
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.TEST_NODE_ID_1), // Start -> A
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2), // A -> B
            new EdgeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_ID_3)  // B -> End
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expected: Start -> End
        assertEquals(2, result.getNodes().size());
        assertEquals(1, result.getEdges().size());

        // Check nodes
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_0) && n.getType() == NodeType.Start));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_3) && n.getType() == NodeType.End));

        // Check edges
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_0) && e.getTo().equals(MessagesTest.TEST_NODE_ID_3)));
    }

    @Test
    void testReduceToHumanTasks_ComplexPath() {
        // Create test diagram with multiple paths
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.TEST_NODE_C, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_4, MessagesTest.TEST_NODE_D, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_5, MessagesTest.TEST_NODE_E, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_6, MessagesTest.TEST_NODE_F, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_7, MessagesTest.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.TEST_NODE_ID_1), // Start -> A
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2), // A -> B
            new EdgeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_ID_3), // B -> C
            new EdgeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.TEST_NODE_ID_4), // C -> D
            new EdgeDto(MessagesTest.TEST_NODE_ID_4, MessagesTest.TEST_NODE_ID_5), // D -> E
            new EdgeDto(MessagesTest.TEST_NODE_ID_5, MessagesTest.TEST_NODE_ID_6), // E -> F
            new EdgeDto(MessagesTest.TEST_NODE_ID_6, MessagesTest.TEST_NODE_ID_7)  // F -> End
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expected: Start -> B -> D -> F -> End
        assertEquals(5, result.getNodes().size());
        assertEquals(4, result.getEdges().size());

        // Check nodes
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_0) && n.getType() == NodeType.Start));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_2) && n.getType() == NodeType.HumanTask));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_4) && n.getType() == NodeType.HumanTask));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_6) && n.getType() == NodeType.HumanTask));
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_7) && n.getType() == NodeType.End));

        // Check edges
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_0) && e.getTo().equals(MessagesTest.TEST_NODE_ID_2)));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_2) && e.getTo().equals(MessagesTest.TEST_NODE_ID_4)));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_4) && e.getTo().equals(MessagesTest.TEST_NODE_ID_6)));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_6) && e.getTo().equals(MessagesTest.TEST_NODE_ID_7)));
    }

    @Test
    void testReduceToHumanTasks_MissingStartNode() {
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.END_NODE_NAME, NodeType.End)
        );

        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2),
            new EdgeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_ID_3)
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.reduceToHumanTasks(input);
        });
    }

    @Test
    void testReduceToHumanTasks_MissingEndNode() {
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask)
        );

        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.TEST_NODE_ID_1),
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2)
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.reduceToHumanTasks(input);
        });
    }

    @Test
    void testReduceToHumanTasks_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.reduceToHumanTasks(null);
        });
    }

    @Test
    void testReduceToHumanTasks_EmptyNodes() {
        ProcessDiagramDto input = new ProcessDiagramDto(Arrays.asList(), Arrays.asList());
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.reduceToHumanTasks(input);
        });
    }

    @Test
    void testReduceToHumanTasks_DisconnectedHumanTask_RemainsButNoEdges() {
        // Start -> A -> B(human) -> End, plus disconnected human H
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.END_NODE_NAME, NodeType.End),
            new NodeDto(MessagesTest.TEST_NODE_ID_9, MessagesTest.TEST_NODE_H, NodeType.HumanTask)
        );
        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.TEST_NODE_ID_1),
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2),
            new EdgeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_ID_3)
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Nodes include disconnected human
        assertTrue(result.getNodes().stream().anyMatch(n -> n.getId().equals(MessagesTest.TEST_NODE_ID_9) && n.getType() == NodeType.HumanTask));
        // Edges do not include disconnected human
        assertFalse(result.getEdges().stream().anyMatch(e -> e.getFrom().equals(MessagesTest.TEST_NODE_ID_9) || e.getTo().equals(MessagesTest.TEST_NODE_ID_9)));
    }

    @Test
    void testReduceToHumanTasks_NoPathFromStartToEnd() {
        // No path from start to end; one human exists but unreachable
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto(MessagesTest.TEST_NODE_ID_0, MessagesTest.START_NODE_NAME, NodeType.Start),
            new NodeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_A, NodeType.ServiceTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_2, MessagesTest.TEST_NODE_B, NodeType.HumanTask),
            new NodeDto(MessagesTest.TEST_NODE_ID_3, MessagesTest.END_NODE_NAME, NodeType.End)
        );
        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto(MessagesTest.TEST_NODE_ID_1, MessagesTest.TEST_NODE_ID_2) // disconnected from start and end
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expect only Start and End with no edges
        assertEquals(3, result.getNodes().size()); // includes disconnected human
        assertEquals(0, result.getEdges().size());
    }

    @Test
    void testReduceToHumanTasks_LastHumanNotConnectedToEnd() {
        // Start -> Svc -> H1(human) ; End is disconnected from H1
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "S", NodeType.ServiceTask),
            new NodeDto("2", "H1", NodeType.HumanTask),
            new NodeDto("9", "Mid", NodeType.ServiceTask),
            new NodeDto("3", "End", NodeType.End)
        );
        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto("0", "1"),
            new EdgeDto("1", "2"),
            new EdgeDto("9", "3") // end only reachable from 9, not from 2
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expect Start -> H1, but no connection to End
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals("0") && e.getTo().equals("2")));
        assertFalse(result.getEdges().stream().anyMatch(e -> e.getFrom().equals("2") && e.getTo().equals("3")));
    }

    @Test
    void testReduceToHumanTasks_CycleInGraph() {
        // Graph with cycle among service nodes
        // Start -> A -> B(human) -> C -> A (cycle), D(service) -> End
        List<NodeDto> nodes = Arrays.asList(
            new NodeDto("0", "Start", NodeType.Start),
            new NodeDto("1", "A", NodeType.ServiceTask),
            new NodeDto("2", "B", NodeType.HumanTask),
            new NodeDto("3", "C", NodeType.ServiceTask),
            new NodeDto("4", "D", NodeType.ServiceTask),
            new NodeDto("5", "End", NodeType.End)
        );
        List<EdgeDto> edges = Arrays.asList(
            new EdgeDto("0", "1"),
            new EdgeDto("1", "2"),
            new EdgeDto("2", "3"),
            new EdgeDto("3", "1"), // cycle back to A
            new EdgeDto("3", "4"),
            new EdgeDto("4", "5")
        );

        ProcessDiagramDto input = new ProcessDiagramDto(nodes, edges);
        ProcessDiagramDto result = service.reduceToHumanTasks(input);

        // Expected reduced edges should not loop indefinitely; should include Start->B and B->End path via services
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals("0") && e.getTo().equals("2")));
        assertTrue(result.getEdges().stream().anyMatch(e -> e.getFrom().equals("2") && e.getTo().equals("5")));
    }
}
