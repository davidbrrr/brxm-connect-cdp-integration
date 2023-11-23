/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;
import org.example.model.Segments;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class EngagementClient {

    private static final Logger log = LoggerFactory.getLogger(EngagementClient.class);

    private static final String PROJECT_ID = "project.id";
    private static final String API_BASE_URL = "api.base.url";
    private static final String API_KEY_ID = "api.key.id";
    private static final String API_SECRET = "api.secret";

    private final String projectid;
    private final String apiBaseUrl;
    private final String apiKeyId;
    private final String apiSecret;

    public EngagementClient(IPluginContext context, IPluginConfig config) {
        projectid = config.getString(PROJECT_ID);
        apiBaseUrl = config.getString(API_BASE_URL);
        apiKeyId = config.getString(API_KEY_ID);
        apiSecret = config.getString(API_SECRET);
        if (projectid == null) {
            throw new IllegalArgumentException("Engagement plugins should be configured with property 'project.id'.");
        }
        if (apiBaseUrl == null) {
            throw new IllegalArgumentException("Engagement plugins should be configured with property 'api.base.url'.");
        }
        if (apiKeyId == null) {
            throw new IllegalArgumentException("Engagement plugins should be configured with property 'api.key.id'.");
        }
        if (apiSecret == null) {
            throw new IllegalArgumentException("Engagement plugins should be configured with property 'api.secret'.");
        }
    }

    public void setProperties(final JSONObject properties) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> uriVariables = ImmutableMap.of("projectid", this.projectid);
        List<Segments> segments = restTemplate.exchange(
                apiBaseUrl + "/data/v2/projects/{projectid}/exposed-segmentations",
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(this.apiKeyId, this.apiSecret)),
                new ParameterizedTypeReference<List<Segments>>() {},
                uriVariables)
            .getBody();
        properties.put("segments", get(segments));
        log.debug(properties.get("segments").toString());
    }

    private JSONArray get(List<Segments> segments) {
        JSONArray array = new JSONArray();
        segments.forEach(segmentations -> {
            segmentations.getSegments().forEach(segment -> {
                try {
                    final JSONObject codeAndDescription = new JSONObject();
                    codeAndDescription.put("code", segment.getId());
                    codeAndDescription.put("description", segmentations.getName() + " - " + segment.getName());
                    array.put(codeAndDescription);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });
        return array;
    }

    private HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                auth.getBytes(Charset.forName("UTF-8")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

}
