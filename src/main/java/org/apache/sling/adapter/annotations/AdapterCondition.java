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
import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * Component Property Type (as defined by OSGi DS 1.4) for the condition for Sling Adapters.
 * Takes care of writing the service property <code>adapter.condition</code>.
 * <p>
 * The use of this annotation will only have an effect if used in conjunction with the {@link SlingAdapter}
 * annotation, which defines properties that are required for an {@link AdapterFactory} to be picked up.
 * <p>
 * Use this annotation to specify the condition under which the adaption will take place. The
 * main use-case for annotating a class with this annotation, is when the result of the adaption
 * could be <code>null</code>.
 */
@ComponentPropertyType
public @interface AdapterCondition {
    /**
     * Specifies the condition under which this adaption takes place. The most common way to do this
     * is to use a language such as "If the ... is a ...", "If the adaptable ...", etc.
     * @return The condition under which this adaption takes place
     */
    String value();
}
