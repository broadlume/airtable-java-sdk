package com.sybit.airtable.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * Wrapper class for an {@link Error} to match the format returned by Airtable
 */
@Value
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorWrapper {

    private final Error error;

    @JsonCreator
    public ErrorWrapper(@JsonProperty("error") Error error) {
        this.error = error;
    }
}
