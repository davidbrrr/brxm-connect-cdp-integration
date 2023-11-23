/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.onehippo.cms7.targeting.Scorer;
import com.onehippo.cms7.targeting.model.TargetGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class EngagementScorer implements Scorer<EngagementData> {

    private static final Logger log = LoggerFactory.getLogger(EngagementScorer.class);

    private final Map<String, Set<String>> targetGroups = new HashMap<>();

    @Override
    public void init(final Map<String, TargetGroup> targetGroups) {
        for (Map.Entry<String, TargetGroup> entry : targetGroups.entrySet()) {
            final String targetGroupId = entry.getKey();
            this.targetGroups.put(targetGroupId, entry.getValue().getProperties().keySet());
        }
    }

    @Override
    public double evaluate(final String targetGroupId, final EngagementData targetingData) {
        if (targetingData == null) {
            return 0.0;
        }

        if (!targetGroups.containsKey(targetGroupId)) {
            return 0.0;
        }

        Set<String> segmentIds = targetGroups.get(targetGroupId);
        return CollectionUtils.containsAny(targetingData.getSegments(), segmentIds) ? 1.0 : 0.0;
    }
}
