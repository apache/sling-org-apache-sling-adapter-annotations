/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.adapter.annotations.util;

import org.codehaus.jackson.JsonNode;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Util {
    private static final Set<String> DYNAMIC_PROPERTIES = new HashSet<>(Arrays.asList(
            ComponentConstants.COMPONENT_ID,
            Constants.SERVICE_BUNDLEID
    ));

    public static Map<String, Object> getNonDynamicPropertiesForService(final JsonNode json) {
        final JsonNode props = json.get("data").get(0).get("props");
        final Map<String, Object> properties = new LinkedHashMap<>();
        for (final JsonNode prop : props) {
            final String name = prop.get("key").getTextValue();
            if (!DYNAMIC_PROPERTIES.contains(name)) {
                properties.put(name, getPropertyValue(prop.get("value")));
            }
        }
        return properties;
    }

    private static Object getPropertyValue(final JsonNode value) {
        if (value.isBoolean()) {
            return value.getBooleanValue();
        }
        if (value.isNumber()) {
            return value.getNumberValue();
        }
        if (value.isTextual()) {
            return value.getTextValue();
        }
        if (value.isArray()) {
            final List<String> items = new ArrayList<>();
            for (final JsonNode item : value) {
                items.add(item.getTextValue());
            }
            return items;
        }
        return null;
    }
}
