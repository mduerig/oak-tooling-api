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

package org.apache.jackrabbit.oak.tooling.filestore.api;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;

/**
 * An instances of this interface serves as main entry point to the
 * Oak Tooling API. It provides means to access and examine the
 * segment store's underlying storage entities.
 */
public interface SegmentStore {

    /**
     * @return The tar files of the segment store in
     * reverse chronological order.
     */
    @Nonnull
    Iterable<Tar> tars();

    /**
     * Read a segment from the store.
     *
     * @param id the uuid of the segment to read.
     * @return an optional segment with the given uuid
     */
    @Nonnull
    Optional<Segment> segment(@Nonnull UUID id);

    /**
     * Retrieve the root node of the head state of this segment store.
     * @return  a node state if the store is none empty
     */
    @Nonnull
    Optional<NodeState> head();

    /**
     * Retrieve a node state
     * @param segmentId  segment id of the node state
     * @param recordNumber     record recordNumber of the node state
     * @return  the node state identified by {@code segmentId} and {@code recordNumber} if it exists.
     */
    @Nonnull
    Optional<NodeState> node(@Nonnull UUID segmentId, int recordNumber);

    /**
     * @return The entries in the {@code journal.log} in
     * reverse chronological order.
     */
    @Nonnull
    Iterable<JournalEntry> journalEntries();
}