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
 *
 */

package org.apache.jackrabbit.oak.tooling.filestore;

import static java.util.Collections.emptyList;
import static org.apache.jackrabbit.oak.tooling.filestore.Property.NULL_PROPERTY;

import javax.annotation.Nonnull;

public interface Node {
    Node NULL_NODE = new Node() {
        @Nonnull
        @Override
        public Iterable<Node> getNodes() {
            return emptyList();
        }

        @Nonnull
        @Override
        public Node node(@Nonnull String name) {
            return NULL_NODE;
        }

        @Nonnull
        @Override
        public Iterable<Property> properties() {
            return emptyList();
        }

        @Nonnull
        @Override
        public Property property(@Nonnull String name) {
            return NULL_PROPERTY;
        }
    };

    @Nonnull
    Iterable<Node> getNodes();

    @Nonnull
    Node node(@Nonnull String name);

    @Nonnull
    Iterable<Property> properties();

    @Nonnull
    Property property(@Nonnull String name);
}
