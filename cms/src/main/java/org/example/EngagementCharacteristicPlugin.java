/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.google.common.collect.ImmutableMap;
import com.onehippo.cms7.targeting.frontend.plugin.CharacteristicPlugin;
import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.example.model.Segments;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.repository.util.JcrUtils;
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
import org.wicketstuff.js.ext.util.ExtClass;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@ExtClass("Hippo.Targeting.EngagementCharacteristicPlugin")
public class EngagementCharacteristicPlugin extends CharacteristicPlugin {

    private static final Logger log = LoggerFactory.getLogger(EngagementCharacteristicPlugin.class);

    private static final JavaScriptResourceReference JS = new JavaScriptResourceReference(EngagementCharacteristicPlugin.class, "EngagementCharacteristicPlugin.js");

    private EngagementClient engagementClient;

    public EngagementCharacteristicPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        engagementClient = new EngagementClient(context, config);
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(JS));
    }

    @Override
    protected ResourceReference getIcon() {
        return new PackageResourceReference(EngagementCharacteristicPlugin.class, "engagement.png");
    }

    @Override
    protected void onRenderProperties(final JSONObject properties) throws JSONException {
        super.onRenderProperties(properties);
        engagementClient.setProperties(properties);
    }
}
