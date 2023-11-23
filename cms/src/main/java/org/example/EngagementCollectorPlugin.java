/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
package org.example;

import com.onehippo.cms7.targeting.frontend.plugin.CollectorPlugin;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.js.ext.util.ExtClass;

@ExtClass("Hippo.Targeting.EngagementCollectorPlugin")
public class EngagementCollectorPlugin extends CollectorPlugin {

    private static final Logger log = LoggerFactory.getLogger(EngagementCharacteristicPlugin.class);

    private static final JavaScriptResourceReference JS = new JavaScriptResourceReference(EngagementCollectorPlugin.class, "EngagementCollectorPlugin.js");

    private EngagementClient engagementClient;

    public EngagementCollectorPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
        engagementClient = new EngagementClient(context, config);
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(JavaScriptHeaderItem.forReference(JS));
    }

    @Override
    protected void onRenderProperties(final JSONObject properties) throws JSONException {
        super.onRenderProperties(properties);
        engagementClient.setProperties(properties);
    }
}
