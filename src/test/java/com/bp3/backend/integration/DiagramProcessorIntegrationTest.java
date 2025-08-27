package com.bp3.backend.integration;

import com.bp3.backend.models.ProcessDiagramDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DiagramProcessorIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void testSimpleProcessReduction() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                },
                {
                  "id": 1,
                  "name": "A",
                  "type": "ServiceTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                },
                {
                  "id": 3,
                  "name": "C",
                  "type": "ServiceTask"
                },
                {
                  "id": 4,
                  "name": "D",
                  "type": "HumanTask"
                },
                {
                  "id": 5,
                  "name": "End",
                  "type": "End"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                },
                {
                  "from": 1,
                  "to": 2
                },
                {
                  "from": 2,
                  "to": 3
                },
                {
                  "from": 3,
                  "to": 4
                },
                {
                  "from": 4,
                  "to": 5
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.nodes.length()").value(4))
                .andExpect(jsonPath("$.edges").isArray())
                .andExpect(jsonPath("$.edges.length()").value(3))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '2' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '4' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '5' && @.type == 'End')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '2')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '2' && @.to == '4')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '4' && @.to == '5')]").exists());
    }

    @Test
    void testMultipleHumanServicesReduction() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                },
                {
                  "id": 1,
                  "name": "A",
                  "type": "HumanTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                },
                {
                  "id": 3,
                  "name": "C",
                  "type": "ServiceTask"
                },
                {
                  "id": 4,
                  "name": "D",
                  "type": "HumanTask"
                },
                {
                  "id": 5,
                  "name": "End",
                  "type": "End"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                },
                {
                  "from": 1,
                  "to": 2
                },
                {
                  "from": 2,
                  "to": 3
                },
                {
                  "from": 3,
                  "to": 4
                },
                {
                  "from": 4,
                  "to": 5
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.nodes.length()").value(5))
                .andExpect(jsonPath("$.edges").isArray())
                .andExpect(jsonPath("$.edges.length()").value(4))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '1' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '2' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '4' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '5' && @.type == 'End')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '1')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '1' && @.to == '2')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '2' && @.to == '4')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '4' && @.to == '5')]").exists());
    }

    @Test
    void testBranchingProcessReduction() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                },
                {
                  "id": 1,
                  "name": "A",
                  "type": "ServiceTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                },
                {
                  "id": 3,
                  "name": "G1",
                  "type": "Gateway"
                },
                {
                  "id": 4,
                  "name": "C",
                  "type": "HumanTask"
                },
                {
                  "id": 5,
                  "name": "D",
                  "type": "HumanTask"
                },
                {
                  "id": 6,
                  "name": "G2",
                  "type": "Gateway"
                },
                {
                  "id": 7,
                  "name": "#",
                  "type": "ServiceTask"
                },
                {
                  "id": 8,
                  "name": "End",
                  "type": "End"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                },
                {
                  "from": 1,
                  "to": 2
                },
                {
                  "from": 2,
                  "to": 3
                },
                {
                  "from": 3,
                  "to": 4
                },
                {
                  "from": 3,
                  "to": 5
                },
                {
                  "from": 4,
                  "to": 6
                },
                {
                  "from": 5,
                  "to": 6
                },
                {
                  "from": 6,
                  "to": 7
                },
                {
                  "from": 7,
                  "to": 8
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.nodes.length()").value(5))
                .andExpect(jsonPath("$.edges").isArray())
                .andExpect(jsonPath("$.edges.length()").value(3))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '2' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '4' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '5' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '8' && @.type == 'End')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '2')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '2' && @.to == '4')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '4' && @.to == '8')]").exists());
    }

    @Test
    void testRecursiveBranchingProcessReduction() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                },
                {
                  "id": 1,
                  "name": "A",
                  "type": "ServiceTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                },
                {
                  "id": 3,
                  "name": "G1",
                  "type": "Gateway"
                },
                {
                  "id": 4,
                  "name": "C",
                  "type": "HumanTask"
                },
                {
                  "id": 5,
                  "name": "D",
                  "type": "HumanTask"
                },
                {
                  "id": 6,
                  "name": "G2",
                  "type": "Gateway"
                },
                {
                  "id": 7,
                  "name": "#",
                  "type": "ServiceTask"
                },
                {
                  "id": 8,
                  "name": "End",
                  "type": "End"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                },
                {
                  "from": 1,
                  "to": 2
                },
                {
                  "from": 2,
                  "to": 3
                },
                {
                  "from": 3,
                  "to": 4
                },
                {
                  "from": 3,
                  "to": 5
                },
                {
                  "from": 4,
                  "to": 6
                },
                {
                  "from": 5,
                  "to": 6
                },
                {
                  "from": 6,
                  "to": 2
                },
                {
                  "from": 6,
                  "to": 7
                },
                {
                  "from": 7,
                  "to": 8
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes").isArray())
                .andExpect(jsonPath("$.nodes.length()").value(5))
                .andExpect(jsonPath("$.edges").isArray())
                .andExpect(jsonPath("$.edges.length()").value(4))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '2' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '4' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '5' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '8' && @.type == 'End')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '2')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '2' && @.to == '4')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '4' && @.to == '5')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '5' && @.to == '8')]").exists());
    }

    @Test
    void testInvalidInput_MissingStartNode() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 1,
                  "name": "A",
                  "type": "ServiceTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                },
                {
                  "id": 3,
                  "name": "End",
                  "type": "End"
                }
              ],
              "edges": [
                {
                  "from": 1,
                  "to": 2
                },
                {
                  "from": 2,
                  "to": 3
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidInput_MissingEndNode() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                },
                {
                  "id": 1,
                  "name": "A",
                  "type": "ServiceTask"
                },
                {
                  "id": 2,
                  "name": "B",
                  "type": "HumanTask"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                },
                {
                  "from": 1,
                  "to": 2
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidInput_MalformedJson() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {
                  "id": 0,
                  "name": "Start",
                  "type": "Start"
                }
              ],
              "edges": [
                {
                  "from": 0,
                  "to": 1
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEdgeCase_NoHumanTasks_ReduceStartToEnd() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {"id": 0, "name": "Start", "type": "Start"},
                {"id": 1, "name": "A", "type": "ServiceTask"},
                {"id": 2, "name": "B", "type": "ServiceTask"},
                {"id": 3, "name": "End", "type": "End"}
              ],
              "edges": [
                {"from": 0, "to": 1},
                {"from": 1, "to": 2},
                {"from": 2, "to": 3}
              ]
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes.length()").value(2))
                .andExpect(jsonPath("$.edges.length()").value(1))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '3' && @.type == 'End')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '3')]").exists());
    }

    @Test
    void testEdgeCase_NoPathFromStartToEnd_ReturnsNoEdges() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Graph has Start and End but no connecting path
        String inputJson = """
            {
              "nodes": [
                {"id": 0, "name": "Start", "type": "Start"},
                {"id": 1, "name": "A", "type": "ServiceTask"},
                {"id": 2, "name": "End", "type": "End"}
              ],
              "edges": [
                {"from": 1, "to": 0}
              ]
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes.length()").value(2))
                .andExpect(jsonPath("$.edges.length()").value(0))
                .andExpect(jsonPath("$.nodes[?(@.id == '0' && @.type == 'Start')]").exists())
                .andExpect(jsonPath("$.nodes[?(@.id == '2' && @.type == 'End')]").exists());
    }

    @Test
    void testEdgeCase_DisconnectedHumanTask_NotConnectedInEdges() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Human task 9 is disconnected from main path
        String inputJson = """
            {
              "nodes": [
                {"id": 0, "name": "Start", "type": "Start"},
                {"id": 1, "name": "A", "type": "ServiceTask"},
                {"id": 2, "name": "B", "type": "HumanTask"},
                {"id": 3, "name": "C", "type": "ServiceTask"},
                {"id": 4, "name": "D", "type": "HumanTask"},
                {"id": 5, "name": "End", "type": "End"},
                {"id": 9, "name": "X", "type": "HumanTask"}
              ],
              "edges": [
                {"from": 0, "to": 1},
                {"from": 1, "to": 2},
                {"from": 2, "to": 3},
                {"from": 3, "to": 4},
                {"from": 4, "to": 5}
              ]
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nodes.length()").value(5))
                .andExpect(jsonPath("$.edges.length()").value(3))
                .andExpect(jsonPath("$.nodes[?(@.id == '9' && @.type == 'HumanTask')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '0' && @.to == '2')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '2' && @.to == '4')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '4' && @.to == '5')]").exists())
                .andExpect(jsonPath("$.edges[?(@.from == '9')]").doesNotExist());
    }

    @Test
    void testError_NullBody_ReturnsBadRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testError_NullNodesField_ReturnsBadRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": null,
              "edges": [ {"from": 0, "to": 1} ]
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testError_NullEdgesField_ReturnsBadRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [ {"id": 0, "name": "Start", "type": "Start"}, {"id": 1, "name": "End", "type": "End"} ],
              "edges": null
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testError_EmptyNodes_ReturnsBadRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [],
              "edges": []
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testError_InvalidEnumType_ReturnsBadRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                {"id": 0, "name": "Start", "type": "Start"},
                {"id": 1, "name": "Weird", "type": "StrangeType"},
                {"id": 2, "name": "End", "type": "End"}
              ],
              "edges": [
                {"from": 0, "to": 1},
                {"from": 1, "to": 2}
              ]
            }
        """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }
}
