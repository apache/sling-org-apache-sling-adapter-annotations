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
package org.apache.sling.adapter.annotations;

import com.google.common.collect.ImmutableMap;
import org.apache.sling.adapter.annotations.testing.adapters.AbstractNoOpAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.DeprecatedAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.InvalidNoAdaptablesAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.InvalidEmptyAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.InvalidNoAdaptersAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.ShortToIntegerAndLongAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.LongToIntegerIfFitsAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.IntegerAndShortToLongAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.TextLengthIfFitsAdapterFactory;
import org.apache.sling.adapter.annotations.util.AppSlingClient;
import org.apache.sling.adapter.annotations.util.Util;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.util.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;

public class ServicePropertiesIT implements AdapterAnnotationsIT {
    private static final String ADAPTER_CONDITION = "adapter.condition";
    private static final String ADAPTER_DEPRECATED = "adapter.deprecated";

    private static OsgiConsoleClient client;

    @BeforeClass
    public static void setUpOnce() throws InterruptedException, TimeoutException, ClientException, URISyntaxException {
        client = AppSlingClient.newSlingClient().adaptTo(OsgiConsoleClient.class);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    @Override
    @Test
    public void testLongToIntegerIfFitsAdapterFactory() throws ClientException {
        assertProperties(LongToIntegerIfFitsAdapterFactory.class.getName(), properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Long.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Integer.class.getName()))
                .put(ADAPTER_CONDITION, LongToIntegerIfFitsAdapterFactory.CONDITION));
    }

    @Override
    @Test
    public void testTextLengthIfFitsAdapterFactory() throws ClientException {
        assertProperties(TextLengthIfFitsAdapterFactory.class.getName(), properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Arrays.asList(CharSequence.class.getName(), String.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Arrays.asList(
                        Short.class.getName(),
                        Integer.class.getName(),
                        Long.class.getName(),
                        BigInteger.class.getName()
                ))
                .put(ADAPTER_CONDITION, TextLengthIfFitsAdapterFactory.CONDITION));
    }

    @Override
    @Test
    public void testShortToIntegerAndLongAdapterFactory() throws ClientException {
        assertProperties(ShortToIntegerAndLongAdapterFactory.class.getName(), properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Short.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Arrays.asList(Integer.class.getName(), Long.class.getName())));
    }

    @Override
    @Test
    public void testIntegerAndShortToLongAdapterFactory() throws ClientException {
        assertProperties(IntegerAndShortToLongAdapterFactory.class.getName(), properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Arrays.asList(Integer.class.getName(), Short.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Long.class.getName())));
    }

    @Test
    public void testInvalidMissingAdaptablesAndAdaptersAdapter() throws ClientException {
        assertProperties(InvalidEmptyAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE));
    }

    @Test
    public void testInvalidMissingAdaptablesAdapter() throws ClientException {
        assertProperties(InvalidNoAdaptablesAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE)
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Void.class.getName())));
    }

    @Test
    public void testInvalidMissingAdaptersAdapter() throws ClientException {
        assertProperties(InvalidNoAdaptersAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE)
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Void.class.getName())));
    }

    @Override
    @Test
    public void testDeprecatedAdapterFactory() throws ClientException {
        assertProperties(DeprecatedAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_DEPRECATED, true)
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(SlingHttpServletRequest.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Resource.class.getName())));
    }

    @Override
    @Test
    public void testInvalidAdapterFactories() throws ClientException {
        assertProperties(InvalidEmptyAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE));
        assertProperties(InvalidNoAdaptablesAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE)
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Void.class.getName())));
        assertProperties(InvalidNoAdaptersAdapterFactory.class.getName(), properties -> properties
                .put(ADAPTER_CONDITION, AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE)
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Void.class.getName())));
    }

    private static void assertProperties(final String componentName,
                                         final UnaryOperator<ImmutableMap.Builder<String, Object>> properties) throws ClientException {
        final Map<String, Object> expected = properties.apply(ImmutableMap.<String, Object>builder()
                .put(ComponentConstants.COMPONENT_NAME, componentName)
                .put(Constants.SERVICE_SCOPE, Constants.SCOPE_BUNDLE))
                .build();
        assertEquals(expected, getNonDynamicPropertiesOfComponentService(componentName));
    }

    private static Map<String, Object> getNonDynamicPropertiesOfComponentService(final String nameOrId) throws ClientException {
        final JsonNode componentJson = JsonUtils.getJsonNodeFromString(
                client.doGet("/system/console/components/" + nameOrId + ".json").getContent());
        final JsonNode serviceJson = JsonUtils.getJsonNodeFromString(
                client.doGet("/system/console/services/" + getServiceIdFromComponentJson(componentJson) + ".json").getContent());
        return Util.getNonDynamicPropertiesForService(serviceJson);
    }

    private static int getServiceIdFromComponentJson(final JsonNode componentJson) {
        final JsonNode props = componentJson.get("data").get(0).get("props");
        for (final JsonNode prop : props) {
            if ("serviceId".equals(prop.get("key").getValueAsText())) {
                return Integer.parseInt(prop.get("value").getValueAsText());
            }
        }
        throw new AssertionError("No service ID found");
    }
}
