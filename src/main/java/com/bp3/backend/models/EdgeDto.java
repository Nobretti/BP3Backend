package com.bp3.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Edge contract Implementation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdgeDto implements Edge {
    @NotNull
    @JsonProperty("from")
    private String from;

    @NotNull
    @JsonProperty("to")
    private String to;
}
