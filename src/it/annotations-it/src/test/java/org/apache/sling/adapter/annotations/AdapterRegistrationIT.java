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
import org.apache.sling.adapter.Adaption;
import org.apache.sling.adapter.annotations.util.AppSlingClient;
import org.apache.sling.adapter.annotations.util.Util;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.osgi.ServiceInfo;
import org.apache.sling.testing.clients.osgi.ServicesInfo;
import org.apache.sling.testing.clients.util.JsonUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Constants;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.function.UnaryOperator;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class AdapterRegistrationIT implements AdapterAnnotationsIT {
    private static Set<Map<String, Object>> registeredAdaptions;

    @BeforeClass
    public static void setUpAdaptions() throws ClientException, InterruptedException, TimeoutException, URISyntaxException, IOException {
        try (final OsgiConsoleClient client = AppSlingClient.newSlingClient().adaptTo(OsgiConsoleClient.class)) {
            registeredAdaptions = new HashSet<>();
            final String servicesJsonString = client.doGet("/system/console/services.json").getContent();
            final ServicesInfo services = new ServicesInfo(JsonUtils.getJsonNodeFromString(servicesJsonString));
            for (final ServiceInfo serviceInfo : services.forType(Adaption.class.getName())) {
                final String serviceJsonString = client.doGet("/system/console/services/" + serviceInfo.getId() + ".json").getContent();
                try {
                    final JsonNode serviceJson = JsonUtils.getJsonNodeFromString(serviceJsonString);
                    registeredAdaptions.add(Util.getNonDynamicPropertiesForService(serviceJson));
                } catch (final ClientException e) {
                    System.err.println("Unable to find proper JSON content for " + serviceJsonString + " - skipping.");
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    @Override
    @Test
    public void testLongToIntegerIfFitsAdapterFactory() {
        assertAdaption(properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Long.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Integer.class.getName())));
    }

    @Override
    @Test
    public void testTextLengthIfFitsAdapterFactory() {
        assertAdaption(properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Arrays.asList(CharSequence.class.getName(), String.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Arrays.asList(
                        Short.class.getName(),
                        Integer.class.getName(),
                        Long.class.getName(),
                        BigInteger.class.getName())));
    }

    @Override
    @Test
    public void testShortToIntegerAndLongAdapterFactory() {
        assertAdaption(properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(Short.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Arrays.asList(Integer.class.getName(), Long.class.getName())));
    }

    @Override
    @Test
    public void testIntegerAndShortToLongAdapterFactory() {
        assertAdaption(properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Arrays.asList(Integer.class.getName(), Short.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Long.class.getName())));
    }

    @Override
    @Test
    public void testDeprecatedAdapterFactory() {
        assertAdaption(properties -> properties
                .put(AdapterFactory.ADAPTABLE_CLASSES, Collections.singletonList(SlingHttpServletRequest.class.getName()))
                .put(AdapterFactory.ADAPTER_CLASSES, Collections.singletonList(Resource.class.getName())));
    }

    @Override
    @Test
    public void testInvalidAdapterFactories() {
        assertFalse(registeredAdaptions.stream().anyMatch(properties -> properties.containsValue(Collections.singletonList(Void.class.getName()))));
    }

    private static void assertAdaption(final UnaryOperator<ImmutableMap.Builder<String, Object>> properties) {
        assertThat(registeredAdaptions, hasItem(properties.apply(ImmutableMap.<String, Object>builder()
                .put(Constants.SERVICE_SCOPE, Constants.SCOPE_SINGLETON)
        ).build()));
    }
}
