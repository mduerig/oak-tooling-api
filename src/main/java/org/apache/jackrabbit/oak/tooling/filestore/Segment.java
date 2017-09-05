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

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * An instance of this interface represents a segment of the
 * segment store.
 */
public interface Segment {

    /**
     * Type of the segment.
     */
    enum Type {

        /** A data segment */
        DATA,

        /** A bulk segment */
        BULK
    }

    /**
     * @return  the id of this segment
     */
    @Nonnull
    UUID id();

    /**
     * @return  the size of this segment in bytes.
     */
    int size();

    /**
     * @return  the type of this segment
     */
    @Nonnull
    Type type();

    /**
     * @return  the ids of the segments referenced by this segment
     */
    @Nonnull
    Iterable<UUID> references();

    /**
     * @return  the records contained in this segment
     */
    @Nonnull
    Iterable<Record> records();

    /**
     * @return  a human readable map with string keys and values representing
     * this segment's header or an empty map for bulk segments.
     * TODO: should we return a proper interface instead of a map here?
     */
    @Nonnull
    Map<String, String> header();

    /**
     * Create an human readable hex dump of this segment.
     * @param includeHeader  Include the header in the hex dump if {@code true}. Otherwise
     *                       exclude the header.
     * @return  a hex dump
     */
    @Nonnull
    String hexDump(boolean includeHeader);
}
