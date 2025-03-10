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
package org.apache.sling.adapter.annotations.testing.adapters;

import org.apache.sling.adapter.annotations.AdapterCondition;
import org.apache.sling.adapter.annotations.SlingAdapter;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;

@Component(service = AdapterFactory.class)
@SlingAdapter(
        adaptables = {},
        adapters = Void.class)
@AdapterCondition(AbstractNoOpAdapterFactory.INVALID_CONFIGURATION_MESSAGE)
public class InvalidNoAdaptablesAdapterFactory extends AbstractNoOpAdapterFactory {}
