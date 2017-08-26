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
import static java.util.Objects.hash;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Property {
    final class Type<T> {
        @Nonnull
        public static Type<Void> VOID =
                new Type<>(Void.class, "VOID");

        @Nonnull
        public static Type<String> STRING =
                new Type<>(String.class, "STRING");

        @Nonnull
        public static final Type<Binary> BINARY =
                new Type<>(Binary.class, "BINARY");

        @Nonnull
        public static final Type<Long> LONG =
                new Type<>(Long.class, "LONG");

        @Nonnull
        public static final Type<Double> DOUBLE =
                new Type<>(Double.class, "DOUBLE");

        @Nonnull
        public static final Type<String> DATE =
                new Type<>(String.class, "DATE");

        @Nonnull
        public static final Type<Boolean> BOOLEAN =
                new Type<>(Boolean.class, "BOOLEAN");

        @Nonnull
        public static final Type<String> NAME =
                new Type<>(String.class, "NAME");

        @Nonnull
        public static final Type<String> PATH =
                new Type<>(String.class, "PATH");

        @Nonnull
        public static final Type<String> REFERENCE =
                new Type<>(String.class, "REFERENCE");

        @Nonnull
        public static final Type<String> WEAKREFERENCE =
                new Type<>(String.class, "WEAKREFERENCE");

        @Nonnull
        public static final Type<String> URI =
                new Type<>(String.class, "URI");

        @Nonnull
        public static final Type<BigDecimal> DECIMAL =
                new Type<>(BigDecimal.class, "DECIMAL");

        @Nonnull
        public static final Map<String, Type<?>> ALL = new HashMap<>();

        @Nonnull
        private final Class<T> type;

        @Nonnull
        private final String name;

        private Type(@Nonnull Class<T> type, @Nonnull String name) {
            this.type = type;
            this.name = name;
            ALL.put(name, this);
        }

        @Nonnull
        public Class<T> getType() {
            return type;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Type<?> that = (Type<?>) other;
            return Objects.equals(type, that.type) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return hash(type, name);
        }

        @Override
        public String toString() {
            return "Property.Type{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    Property NULL_PROPERTY = new Property() {
        @Nonnull
        @Override
        public Type type() {
            return Type.VOID;
        }

        @Override
        public int cardinality() {
            return 0;
        }

        @Nonnull
        @Override
        public <T> T value(Type<T> type, int index) {
            throw new IndexOutOfBoundsException("No element at index " + index);
        }

        @Nonnull
        @Override
        public <T> Iterable<T> values(Type<T> type) {
            return emptyList();
        }
    };

    @Nonnull
    Type type();

    int cardinality();

    @Nonnull
    <T> T value(Type<T> type, int index);

    @Nonnull
    <T> Iterable<T> values(Type<T> type);
}
