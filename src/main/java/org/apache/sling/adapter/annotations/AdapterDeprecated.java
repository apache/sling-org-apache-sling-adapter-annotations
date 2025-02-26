/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.adapter.annotations;

import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * Component Property Type (as defined by OSGi DS 1.4) for the condition for Sling Adapters.
 * Takes care of writing the service property <code>adapter.deprecated</code>.
 * <p>
 * The use of this annotation will only have an effect if used in conjunction with the {@link SlingAdapter}
 * annotation, which defines properties that are required for an {@link AdapterFactory} to be picked up.
 * <p>
 * Use this annotation to mark the adapter as deprecated. The only consequence of setting this property
 * is an indication in the Web Console Plugin, where it is made apparent to a developer that the use
 * of this adapter is deprecated and may not work anymore in the future.
 */
@ComponentPropertyType
public @interface AdapterDeprecated {}
