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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.adapter.annotations.testing.adapters.AbstractNoOpAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.LongToIntegerIfFitsAdapterFactory;
import org.apache.sling.adapter.annotations.testing.adapters.TextLengthIfFitsAdapterFactory;
import org.apache.sling.adapter.annotations.util.AppConstants;
import org.apache.sling.adapter.annotations.util.AppSlingClient;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class AdapterStatusIT implements AdapterAnnotationsIT {
    private static String adaptersStatus;

    @BeforeClass
    public static void setUpAdaptersStatus() throws ClientException, InterruptedException, TimeoutException, URISyntaxException, IOException {
        try (final OsgiConsoleClient client = AppSlingClient.newSlingClient().adaptTo(OsgiConsoleClient.class)) {
            adaptersStatus = client.doGet("/system/console/status-adapters.txt").getContent();
            adaptersStatus = StringUtils.replace(adaptersStatus, "\r", StringUtils.EMPTY); // Prevent platform-specific issues
        }
    }

    @Override
    @Test
    public void testLongToIntegerIfFitsAdapterFactory() {
        assertAdapterDescriptor(Long.class, LongToIntegerIfFitsAdapterFactory.CONDITION, Integer.class);
    }

    @Override
    @Test
    public void testShortToIntegerAndLongAdapterFactory() {
        assertAdapterDescriptor(Short.class, null, Integer.class, Long.class);
    }

    @Override
    @Test
    public void testIntegerAndShortToLongAdapterFactory() {
        assertAdapterDescriptor(Integer.class, null, Long.class);
        assertAdapterDescriptor(Short.class, null, Long.class);
    }

    @Override
    @Test
    public void testTextLengthIfFitsAdapterFactory() {
        assertAdapterDescriptor(CharSequence.class, TextLengthIfFitsAdapterFactory.CONDITION, Short.class, Integer.class, Long.class, BigInteger.class);
        assertAdapterDescriptor(String.class, TextLengthIfFitsAdapterFactory.CONDITION, Short.class, Integer.class, Long.class, BigInteger.class);
    }

    @Override
    @Test
    public void testDeprecatedAdapterFactory() {
        // Deprecated status is not dumped on the status page
        assertAdapterDescriptor(SlingHttpServletRequest.class, null, Resource.class);
    }

    @Override
    @Test
    public void testInvalidAdapterFactories() {
        assertThat(adaptersStatus, not(containsString("\nCondition: " + AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE + "\n")));
    }

    private static void assertAdapterDescriptor(final Class<?> adaptable, final String condition, final Class<?>... adapters) {
        final StringBuilder descriptor = new StringBuilder("\nAdaptable: ").append(adaptable.getName()).append("\n");
        if (condition != null) {
            descriptor.append("Condition: ").append(condition).append("\n");
        }
        descriptor.append("Providing Bundle: ").append(AppConstants.BUNDLE_SYMBOLIC_NAME).append("\n");
        descriptor.append("Available Adapters:\n");
        for (final Class<?> adapter : adapters) {
            descriptor.append(" * ").append(adapter.getName()).append("\n");
        }
        descriptor.append("\n");
        assertThat(adaptersStatus, containsString(descriptor.toString()));
    }
}
