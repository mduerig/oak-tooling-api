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

import java.util.function.BiPredicate;

import javax.annotation.Nonnull;

/**
 * An instance of this interface represents a node of a segment store.
 * Two instances of the same subtype of {@code Node} are equal (according
 * to {@link #equals(Object)}) iff they are structurally equal. That is iff
 * they have equal child nodes and properties. Two instances of {@code Node}
 * obtained from different instances of {@code Store} can't be compared and
 * attempting to do so will throw an {@link IllegalArgumentException}.
 * <p>
 * <em>Implementation note:</em> the {@link #EQ} predicate can be used to
 * determine structural equality of {@code Node} instances if those cannot
 * come up with a more efficient implementation.
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

        /**
         * @param other
         * @return  {@code true} iff {@code other == this == NULL_NODE}
         */
        @Override
        public boolean equals(@Nonnull Object other) {
            return other instanceof Node && EQ.test(this, (Node) other);
        }

        /**
         * @return {@code "NULL_NODE"}
         */
        @Override
        public String toString() {
            return "NULL_NODE";
        }

        /**
         * @return 0
         */
        @Override
        public int hashCode() {
            return 0;
        }
    };

    /**
     * Predicate representing structural equality of {@code Node}
     * instances. Two {@code Node} instances are structural equal iff
     * they have equal child nodes and equal properties, except for
     * the {@link #NULL_NODE}, which is only equal to itself.
     */
    BiPredicate<Node, Node> EQ = new BiPredicate<Node, Node>() {
        @Override
        public boolean test(Node node1, Node node2) {
            if (node1 == node2) {
                return true;
            }
            if (node1 == NULL_NODE || node2 == NULL_NODE) {
                return false;
            }

            for (Property p1 : node1.properties()) {
                Property p2 = node2.property(p1.getName());
                if (!Property.EQ.test(p1, p2)) {
                    return false;
                }
            }

            for (String name : node1.childNames()) {
                Node c1 = node1.node(name);
                Node c2 = node2.node(name);
                if (!test(c1, c2)) {
                    return false;
                }
            }
            return true;
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
