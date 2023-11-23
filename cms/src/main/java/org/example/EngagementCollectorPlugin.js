/**
 * Copyright 2012-2013 Hippo B.V. (http://www.onehippo.com)
 */
"use strict";

Ext.namespace('Hippo.Targeting');

Hippo.Targeting.EngagementCollectorPlugin = Ext.extend(Hippo.Targeting.CollectorPlugin, {

    constructor: function(config) {
        this.resources = config.resources;

        this.segmentsMap = new Hippo.Targeting.Map(config.segments, 'code', 'description')

        Hippo.Targeting.EngagementCollectorPlugin.superclass.constructor.call(this, Ext.apply(config, {
            editor: {
                segmentsMap: this.segmentsMap,
                resources: config.resources,
                xtype: 'Hippo.Targeting.EngagementTargetingDataEditor'
            },
            renderer: this.renderEngagementSegments
        }));
    },

    renderEngagementSegments: function(properties) {
        var result = [];
        Ext.each(properties.segments, function(segment) {
            var segmentDescription = this.segmentsMap.getValue(segment);
            if (!Ext.isEmpty(segmentDescription)) {
                result.push(segmentDescription);
            }
        }, this);
        return result.join(', ');
    }

});

Hippo.Targeting.EngagementTargetingDataEditor = Ext.extend(Hippo.Targeting.TargetingDataCheckboxGroup, {

    constructor: function(config) {
        var checkboxes = [];

        config.segmentsMap.each(function(segmentCode, segmentDescription) {
            checkboxes.push({
                boxLabel: segmentDescription,
                name: segmentCode,
                listeners: Hippo.Targeting.formElementQtipListeners(segmentDescription)
            });
        });

        Hippo.Targeting.EngagementTargetGroupEditor.superclass.constructor.call(this, Ext.apply(config, {
            allowBlank: config.allowBlank || false,
            blankText: config.resources['error-select-at-least-one'],
            columns: config.columns || 2,
            items: checkboxes,
            vertical: true,
            targetingDataProperty: 'segments'
        }));
    }

});
Ext.reg('Hippo.Targeting.EngagementTargetingDataEditor', Hippo.Targeting.EngagementTargetingDataEditor);
