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

/**
 * An instance of this interface represents a node of a  segment store.
 */
public interface Node {

    /**
     * Singleton instance to represent non existing nodes.
     */
    Node NULL_NODE = new Node() {

        /**
         * @return  always empty.
         */
        @Nonnull
        @Override
        public Iterable<String> childNames() {
            return emptyList();
        }

        /**
         * @return  always empty.
         */
        @Nonnull
        @Override
        public Iterable<Node> children() {
            return emptyList();
        }

        /**
         * @return  {@link #NULL_NODE}
         */
        @Nonnull
        @Override
        public Node node(@Nonnull String name) {
            return NULL_NODE;
        }

        /**
         * @return  always empty.
         */
        @Nonnull
        @Override
        public Iterable<Property> properties() {
            return emptyList();
        }

        /**
         * @return  {@link Property#NULL_PROPERTY}
         */
        @Nonnull
        @Override
        public Property property(@Nonnull String name) {
            return NULL_PROPERTY;
        }
    };

    /**
     * @return  the names of the child nodes of this node. The oder is not specified.
     */
    @Nonnull
    Iterable<String> childNames();

    /**
     * @return  the child nodes of this node. The order is not specified.
     */
    @Nonnull
    Iterable<Node> children();

    /**
     * Look up a child node of the given {@code name}.
     * @param name  name of the child node to look up.
     * @return  a child node or {@link #NULL_NODE} if no
     * such node exists.
     */
    @Nonnull
    Node node(@Nonnull String name);

    /**
     * @return  the properties of this node. The order is not specified.
     */
    @Nonnull
    Iterable<Property> properties();

    /**
     * Look up a property of the given {@code name}.
     * @param name  name of the property to look up.
     * @return  a property or {@link Property#NULL_PROPERTY} if no
     * such property exists.
     */
    @Nonnull
    Property property(@Nonnull String name);
}
