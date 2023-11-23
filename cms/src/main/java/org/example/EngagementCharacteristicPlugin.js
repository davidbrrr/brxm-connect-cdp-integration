/**
 * Copyright 2011-2013 Hippo B.V. (http://www.onehippo.com)
 */
"use strict";

Ext.namespace('Hippo.Targeting');

Hippo.Targeting.EngagementCharacteristicPlugin = Ext.extend(Hippo.Targeting.CharacteristicPlugin, {

    constructor: function(config) {
        this.segmentsMap = new Hippo.Targeting.Map(config.segments, 'code', 'description')
        Hippo.Targeting.EngagementCharacteristicPlugin.superclass.constructor.call(this, Ext.apply(config, {
            visitorCharacteristic: {
                segmentsMap: this.segmentsMap,
                xtype: 'Hippo.Targeting.EngagementCharacteristic'
            },
            editor: {
                segmentsMap: this.segmentsMap,
                resources: config.resources,
                xtype: 'Hippo.Targeting.EngagementTargetGroupEditor'
            },
            renderer: this.renderEngagementSegments,
            scope: this
        }));
    },

    renderEngagementSegments: function(properties) {
        var result = [];
        Ext.each(properties, function(property) {
            var segmentDescription = this.segmentsMap.getValue(property.name);
            if (!Ext.isEmpty(segmentDescription)) {
                result.push(segmentDescription);
            }
        }, this);
        return result.join(', ');
    }

});

Hippo.Targeting.EngagementTargetGroupEditor = Ext.extend(Hippo.Targeting.TargetGroupCheckboxGroup, {

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
            vertical: true
        }));
    }

});
Ext.reg('Hippo.Targeting.EngagementTargetGroupEditor', Hippo.Targeting.EngagementTargetGroupEditor);

Hippo.Targeting.EngagementCharacteristic = Ext.extend(Hippo.Targeting.VisitorCharacteristic, {

    constructor: function(config) {
        Hippo.Targeting.EngagementCharacteristic.superclass.constructor.call(this, config);
        this.segmentsMap = config.segmentsMap;
    },

    isCollected: function(targetingData) {
        //return true if at least 1 segment is valid
        var collected = false;
        if (!Ext.isEmpty(targetingData.segments)) {
            Ext.each(targetingData.segments, function (segment) {
                collected |= !Ext.isEmpty(this.segmentsMap.getValue(String(segment)));
            }, this);
        }
        return collected;
    },

    getTargetGroupName: function(targetingData) {
        var names = [];

        Ext.each(targetingData.segments, function (segment) {
            var name = this.segmentsMap.getValue(String(segment));
            if (Ext.isDefined(name)) {
                names.push(name);
            }
        }, this);

        return names.join(', ');
    },

    getTargetGroupProperties: function(targetingData) {
        var props = [];

        Ext.each(targetingData.segments, function (segment) {
            var name = this.segmentsMap.getValue(String(segment));
            if (Ext.isDefined(name)) {
                props.push({ name: String(name), value: '' });
            }
        }, this);

        return props;
    }

});
Ext.reg('Hippo.Targeting.EngagementCharacteristic', Hippo.Targeting.EngagementCharacteristic);
