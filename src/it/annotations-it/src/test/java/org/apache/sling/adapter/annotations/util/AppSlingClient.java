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

import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.SlingClient;
import org.apache.sling.testing.clients.osgi.OsgiConsoleClient;
import org.apache.sling.testing.clients.util.poller.Polling;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

public class AppSlingClient {
    private static boolean bundleInstalledAndStarted;

    @SuppressWarnings("squid:S2095") // Caller will close the client
    public static SlingClient newSlingClient() throws URISyntaxException, ClientException, TimeoutException, InterruptedException {
        final SlingClient client = new SlingClient(new URI(System.getProperty("baseUrl")), "admin", "admin");

        // client.waitExists() adds ".json" to the path, which is not desired, since that requests the Sling Default GET Servlet instead of Sling Starter HTML
        new Polling(() -> client.doGet("/starter/index.html").getStatusLine().getStatusCode() == 200)
                .poll(60_000, 500);

        if (!bundleInstalledAndStarted) {
            final OsgiConsoleClient osgiConsoleClient = client.adaptTo(OsgiConsoleClient.class);
            osgiConsoleClient.waitInstallBundle(new File(AppConstants.BUNDLE_FILE), true, -1, 10_000, 500);
            osgiConsoleClient.waitBundleStarted(AppConstants.BUNDLE_SYMBOLIC_NAME, 10_000, 500);
            bundleInstalledAndStarted = true;
        }
        return client;
    }
}
