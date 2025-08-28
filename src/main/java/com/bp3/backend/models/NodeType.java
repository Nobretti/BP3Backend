package com.bp3.backend.models;

/**
 * The different types of Nodes within a BPM process diagram.
 */
public enum NodeType {
    
    /**
     * End node - represents the termination point of a process.
     *
     * <p>The end node marks the completion of the process workflow. It is always
     * preserved in the reduced diagram and serves as the final destination for
     * all process paths.</p>
     */
    End,
    
    /**
     * Human task node - represents a step that requires human interaction.

     * <p>Human task nodes are the primary focus of the diagram reduction process.
     * They represent steps where human intervention is required, such as approvals,
     * data entry, or decision making. These nodes are always preserved and form
     * the backbone of the reduced diagram.</p>
     */
    HumanTask,
    
    /**
     * Service task node - represents an automated step or system process.
     *
     * <p>Service task nodes represent automated processes that do not require
     * human intervention. Examples include data processing, system integrations,
     * automated calculations, or background tasks. These nodes are removed during
     * reduction to focus on human-interaction points.</p>
     */
    ServiceTask,
    
    /**
     * Start node - represents the initiation point of a process.
     *
     * <p>The start node marks the beginning of the process workflow. It is always
     * preserved in the reduced diagram and serves as the entry point for all
     * process paths.</p>
     */
    Start,
    
    /**
     * Gateway node - represents a decision point or flow control element.

     * <p>Gateway nodes represent decision points, parallel splits, or flow control
     * elements in the process. They determine the routing logic between different
     * paths. Examples include exclusive gateways (XOR), parallel gateways (AND),
     * and inclusive gateways (OR). These nodes are removed during reduction as
     * they represent process logic rather than human interaction points.</p>
     */
    Gateway
}
