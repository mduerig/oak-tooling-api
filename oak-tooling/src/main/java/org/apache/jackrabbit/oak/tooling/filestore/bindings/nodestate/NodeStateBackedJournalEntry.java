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

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.JournalEntry;

/**
 * michid document
 */
public class NodeStateBackedJournalEntry implements JournalEntry {
    @Nonnull
    private final NodeState node;

    public static JournalEntry newJournalEntry(@Nonnull NodeState node) {
        return new NodeStateBackedJournalEntry(node);
    }

    private NodeStateBackedJournalEntry(@Nonnull NodeState node) {
        this.node = node;
    }

    @Override
    public long timestamp() {
        return 0; // michid implement timestamp
    }

    @Nonnull
    @Override
    public UUID segmentId() {
        return null; // michid implement segmentId
    }

    @Override
    public int offset() {
        return 0; // michid implement offset
    }

    @Nonnull
    @Override
    public NodeState getRoot() {
        return node.getChildNode("root");
    }

    @Override
    public String toString() {
        // michid implement toString
        return node.toString();
    }
}
