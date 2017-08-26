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

import static java.util.Objects.hash;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An instance of this class represents a record in segment
 * of the segment store.
 */
public interface Record {

    /**
     * The type of a record in a segment.
     */
    final class Type<T> {

        /**
         * A leaf of a map (which is a HAMT tree). This contains
         * <ul>
         * <li>the size (int)</li>
         * <li>for each entry, the hash code of the key (4 bytes), then the record id of
         * the key and the record id of the value</li>
         * </ul>
         */
        @Nonnull
        public static final Type<LeafRecord> LEAF =
                new Type<>(LeafRecord.class, "TEMPLATE");


        /**
         * A branch of a map (which is a HAMT tree). This contains
         * <ul>
         * <li>level within the HAMT structure (4 most significant bits), plus size
         * of the that branch of the map</li>
         * <li>bitmap (4 bytes)</li>
         * <li>record ids of the buckets of the next level of the map</li>
         * </ul>
         * There is a special case: if the first int (level/size) is -1, then it's a
         * diff record, to handle the common case of when exactly one existing child
         * node was modified. This is common because whenever one node was changed,
         * we need to propagate that up to the root.
         * <ul>
         * <li>-1 (int)</li>
         * <li>hash code of the key that was changed (4 bytes)</li>
         * <li>the record id of the key</li>
         * <li>the record id of the value</li>
         * <li>the record id of the (base version of the) modified map</li>
         * </ul>
         * There is only ever one single diff record for a map.
         */
        @Nonnull
        public static final Type<BranchRecord> BRANCH =
                new Type<>(BranchRecord.class, "TEMPLATE");

        /**
         * A bucket (a list of references). It always includes at least 2 elements,
         * up to 255 entries (because each entry could in theory point to a
         * different segment, in which case this couldn't be stored in a segment).
         * This contains just the record ids. The size of the list is not stored, as
         * it is stored along with the reference to this record.
         */
        @Nonnull
        public static final Type<BucketRecord> BUCKET =
                new Type<>(BucketRecord.class, "TEMPLATE");

        /**
         * A list including the size (an int). This could be 0, in which case there
         * is no reference. If the size is 1, then reference points to the value of
         * the list. If the size is larger, then a record id follows, which points
         * to a bucket with the actual record ids. If there are more than 255
         * entries in the list, then the list is partitioned into sub-lists of 255
         * entries each, which are stored kind of recursively.
         */
        @Nonnull
        public static final Type<ListRecord> LIST =
                new Type<>(ListRecord.class, "TEMPLATE");

        /**
         * A value (for example a string, or a long, or a blob). The format is:
         * length (variable length encoding, one byte if shorter than 128, else more
         * bytes), then the data as a byte array, or, for large values, a record id
         * of the top level bucket that contains the list of block record ids of the
         * actual binary data.
         * <p>
         * Therefore, a value can reference other records.
         */
        @Nonnull
        public static final Type<ValueRecord> VALUE =
                new Type<>(ValueRecord.class, "TEMPLATE");

        /**
         * A block of bytes (a binary value, or a part of a binary value, or part of
         * large strings). It only contains the raw data.
         */
        @Nonnull
        public static final Type<BlockRecord> BLOCK =
                new Type<>(BlockRecord.class, "TEMPLATE");

        /**
         * A template (the "hidden class" of a node; inspired by the Chrome V8
         * Javascript engine). This includes a list of property templates. Format:
         * <ul>
         * <li>head (int), which is: 1 bit (most significant one) whether the node
         * has a single valued jcr:primaryType property. 1 bit whether it has
         * mixins, in which case 10 bits (27 to 18) are used for the number of
         * mixins. 1 bit whether the node has no child nodes. 1 bit whether the node
         * has more than one child nodes. 18 bits (0 to 17) the number of properties
         * (0 to 262143).</li>
         * <li>The record ids of: if needed, record id of the primary type (a
         * value), record ids of the mixin names (value records), for single child
         * node: the name of the child node</li>
         * <li>The list of record ids of property names (which are stored before the
         * template in separate value records), and the property type (negative
         * values for multi-value properties).</li>
         * </ul>
         */
        @Nonnull
        public static final Type<TemplateRecord> TEMPLATE =
                new Type<>(TemplateRecord.class, "TEMPLATE");

        /**
         * A JCR node, which contains a list of record ids:
         * <ul>
         * <li>the record id of the template</li>
         * <li>depending on the template, the record id of the map of the ids of the
         * child node name(s) and child node record id(s), or if there is just one
         * child node, the child node record id</li>
         * <li>the record ids of the property values (for multi-valued property a
         * pointer to the list record)</li>
         * </ul>
         */
        @Nonnull
        public static final Type<NodeRecord> NODE =
                new Type<>(NodeRecord.class, "NODE");

        /**
         * A reference to an external binary object.
         */
        @Nonnull
        public static final Type<BlobIdRecord> BLOB_ID =
                new Type<>(BlobIdRecord.class, "TEMPLATE");

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
            return "Record.Type{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * @return  the identifier of this record
     */
    @Nonnull
    RecordId id();

    /**
     * @return  the type of this record
     */
    @Nonnull
    Type type();

    /**
     * Read a number of bytes from this record. This method does not
     * protect callers from reading beyond this record's boundary.
     * @param offset  offset from the beginning of this record
     * @param count   number of bytes to read from this record
     * @return  a buffer containing {@code count} bytes from this segment
     * starting at {@code offset}.
     * @throws IllegalArgumentException if {@code count} or {@code offset} is negative.
     * @throws IllegalStateException if either {@code offset + count >= segment.size()}
     * where segment represents this record's underlying segment.
     */
    @Nonnull
    ByteBuffer read(int offset, int count);
}
