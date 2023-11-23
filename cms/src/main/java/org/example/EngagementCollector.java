/*
 * Copyright 2012-2018 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.onehippo.cms7.targeting.collectors.AbstractCollector;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class EngagementCollector extends AbstractCollector<EngagementData, EngagementRequestData> {

    private static final Logger log = LoggerFactory.getLogger(EngagementCollector.class);

    private static final String PROJECT_ID = "project.id";
    private static final String API_BASE_URL = "api.base.url";
    private static final String ENGAGEMENT_COOKIE = "__exponea_etc__";

    private final String projectid;
    private final String apiBaseUrl;
    private final boolean enabled;

    @SuppressWarnings("unused")
    public EngagementCollector(String id, Node node) throws IllegalArgumentException, RepositoryException {
        super(id);
        projectid = JcrUtils.getStringProperty(node, PROJECT_ID, null);
        apiBaseUrl = JcrUtils.getStringProperty(node, API_BASE_URL, null);
        enabled = JcrUtils.getBooleanProperty(node, "enabled", true);
        if (projectid == null) {
            final String nodePath = JcrUtils.getNodePathQuietly(node);
            throw new IllegalArgumentException("Engagement collector should be configured with property 'project.id'. "
                    + ((nodePath == null) ? "" : "Set the value of this property at '"
                    + nodePath + "/@project.id'"));
        }
        if (apiBaseUrl == null) {
            final String nodePath = JcrUtils.getNodePathQuietly(node);
            throw new IllegalArgumentException("Engagement collector should be configured with property 'api.base.url'. "
                + ((nodePath == null) ? "" : "Set the value of this property at '"
                + nodePath + "/@api.base.url'"));
        }
    }

    private final LoadingCache<String, List<String>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(final String id) {
                    RestTemplate restTemplate = new RestTemplate();
                    Map<String, String> uriVariables = ImmutableMap.of("projectid", projectid, "customerid", id);
                    ResponseEntity<String[]> forEntity = restTemplate.getForEntity(
                        apiBaseUrl + "/data/v2/projects/{projectid}/customers/{customerid}/exposed-segmentations",
                        String[].class,
                        uriVariables);
                    return Arrays.asList(forEntity.getBody());
                }
            });

    @Override
    public EngagementRequestData getTargetingRequestData(final HttpServletRequest request,
                                                         final boolean newVisitor,
                                                         final boolean newVisit,
                                                         final EngagementData targetingData) {
        if (request.getCookies() != null) {
            Optional<Cookie> engagementId = Arrays.stream(request.getCookies())
                    .filter(cookie -> cookie.getName().equals(ENGAGEMENT_COOKIE))
                    .findFirst();
            if (enabled && engagementId.isPresent()) {
                EngagementRequestData engagementRequestData = new EngagementRequestData(cache.getUnchecked(engagementId.get().getValue()));
                log.debug("Visitor in segments {}", engagementRequestData.getSegments());
                return engagementRequestData;
            }
        }
        return new EngagementRequestData(new ArrayList<>());
    }

    @Override
    public EngagementData updateTargetingData(EngagementData data, final EngagementRequestData segments) throws IllegalArgumentException {
        if (data == null) {
            data = new EngagementData(getId(), segments.getSegments());
        } else {
            data.setSegments(segments.getSegments());
        }
        return data;
    }
}
