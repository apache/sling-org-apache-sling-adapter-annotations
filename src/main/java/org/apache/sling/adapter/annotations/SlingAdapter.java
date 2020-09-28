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

import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.adapter.AdapterManager;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.component.propertytypes.ServiceRanking;

/**
 * Component Property Type (as defined by OSGi DS 1.4) for Sling Adapters.
 * Takes care of writing the service properties, that can be found as constants in {@link AdapterFactory}.
 * The annotated component will be registered as a Sling adapter, using its minimum required configuration.
 * <p>
 * Due to technical limitations, it is possible to set either of the values to an empty array, in which
 * case the adapter registration will not be picked up by the framework. Extra care must be taken by the
 * developer to ensure that both values will are entered correctly.
 * <p>
 * If multiple implementations for the same combination of adapter and adaptable are registered,
 * the implementation with the lowest <code>service.ranking</code>-property wins. This property can be set
 * through the use of the {@link ServiceRanking} Component Property Type annotation.
 * <p>
 * The services defined through this annotation will be picked up by the {@link AdapterManager}, which is
 * responsible for honoring the <code>service.ranking</code> when multiple matching factories are found.
 * <p>
 * Warning: It is up to the implementation of the annotated class to handle the adaption properly:
 * <ul>
 *     <li>It is up to the implementation to always return the same object for the adaptable, or not;</li>
 *     <li>It is up to the implementation to handle all combinations of the specified adaptables and adapters;</li>
 *     <li>The adaptTo()-method's <code>type</code>-parameter can be used to check the adapter-type requested;</li>
 *     <li>If the adaptTo()-method can return null, <code>adapter.condition</code> should be set to indicate this.</li>
 * </ul>
 * <p>
 * For the registration of the other related service properties, see also {@link AdapterCondition} and the
 * lesser used {@link AdapterDeprecated}.
 *
 * @see <a href="https://sling.apache.org/documentation/the-sling-engine/adapters.html">Sling Adapters</a>
 */
@ComponentPropertyType
public @interface SlingAdapter {
    /**
     * Specifies the adaptables for which this adapter will be called. For each adaptable specified, this
     * adapter will be called to request a result of any of the specified adapters.
     * @return The classes that can be used as an adaptable
     */
    Class<?>[] adaptables();

    /**
     * Specifies the adapters for which this adapter will be called, but only if the source adaptable matches
     * any of the specified adaptables.
     * @return The classes that this adapter can adapt to
     */
    Class<?>[] adapters();
}
