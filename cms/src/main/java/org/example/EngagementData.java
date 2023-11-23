/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.onehippo.cms7.targeting.data.AbstractTargetingData;

import java.util.List;


public class EngagementData extends AbstractTargetingData {
    List<String> segments;

    @JsonCreator
    public EngagementData(@JsonProperty("collectorId") String collectorId, @JsonProperty("segments") List<String> segments) {
        super(collectorId);
        this.segments = segments;
    }

    public List<String> getSegments() {
        return segments;
    }

    public void setSegments(List<String> segments) {
        this.segments = segments;
    }
}
