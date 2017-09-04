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

import java.io.Closeable;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

/**
 * An instances of this interface serves as main entry point to the
 * Oak Tooling API. It provides means to access and examine the
 * segment store's underlying storage entities.
 */
public interface Store {

    /**
     * @return  The tar files of the segment store in
     * reverse chronological order.
     */
    @Nonnull
    Iterable<Tar> tars();

    /**
     * Read a segment from the store.
     * @param id  the uuid of the segment to read.
     * @return    an optional segment with the given uuid
     */
    @Nonnull
    Optional<Segment> segment(@Nonnull UUID id);

    /**
     * @return  The entries in the {@code journal.log} in
     * reverse chronological order.
     */
    @Nonnull
    Iterable<JournalEntry> journalEntries();

    /**
     * Read a node from the store.
     * @param id  the record id of the node to read.
     * @return    the node with the given record id or
     * {@link Node#NULL_NODE} if the store does not contain such a node.
     */
    @Nonnull
    Node node(@Nonnull RecordId id);

    /**
     * Connect an {@link IOMonitor} to the underlying store.
     * @param ioMonitor  the {@code IOMonitor} instance to connect.
     * @return  a {@code Closeable} instance for detaching {@code ioMonitor} again.
     */
    @Nonnull
    Closeable addIOMonitor(@Nonnull IOMonitor ioMonitor);

    /**
     * Dynamic cast to an underlying implementation type.
     * This method allows to cast an instance of a type  of the
     * Oak Tooling API to and underlying implementation type.
     * @param value      source value to cast
     * @param classType  target type to cast to
     * @param <T>        target type to cast to
     * @return  an optional result from the cast
     */
    @Nonnull
    <T> Optional<T> cast(Object value, Class<T> classType);
}
