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

package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate.Streams.asStream;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.JournalEntry;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentStore;
import org.apache.jackrabbit.oak.tooling.filestore.api.Tar;

/**
 * An implementation of {@link SegmentStore} based on a {@link NodeState}.
 * The node state is expected to expose a child node {@code store}
 * and a child node {@code journal}.
 */
public class NodeStateBackedSegmentStore implements SegmentStore {

    @Nonnull
    private final NodeState tars;
    private final NodeState journal;

    @Nonnull
    public static SegmentStore newSegmentStore(@Nonnull NodeState node) {
        return new NodeStateBackedSegmentStore(node);
    }

    private NodeStateBackedSegmentStore(@Nonnull NodeState node) {
        this.tars = node.getChildNode("store");
        this.journal = node.getChildNode("journal");
    }

    @Nonnull
    @Override
    public Iterable<Tar> tars() {
        return () -> asStream(tars.getChildNodeEntries())
                        .map(ChildNodeEntry::getNodeState)
                        .map(NodeBackedTar::newTar)
                        .iterator();
    }

    @Nonnull
    @Override
    public Optional<Segment> segment(@Nonnull UUID id) {
        return asStream(tars())
                .flatMap(NodeStateBackedSegmentStore::getSegments)
                .filter(segment -> id.equals(segment.id()))
                .findFirst();
    }

    @Nonnull
    private static Stream<Segment> getSegments(@Nonnull Tar tar) {
        return asStream(tar.segments());
    }

    @Nonnull
    @Override
    public Iterable<JournalEntry> journalEntries() {
        return () -> asStream(journal.getChildNodeEntries())
                        .map(ChildNodeEntry::getNodeState)
                        .map(NodeStateBackedJournalEntry::newJournalEntry)
                        .iterator();
    }

}
