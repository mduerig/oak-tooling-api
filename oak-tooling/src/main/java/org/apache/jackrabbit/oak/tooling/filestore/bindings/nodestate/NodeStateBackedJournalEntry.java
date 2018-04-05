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

import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return node.getLong("timestamp");
    }

    @Nonnull
    @Override
    public UUID segmentId() {
        return Optional.ofNullable(node.getString("revision"))
                .flatMap(RecordId::fromString)
                .orElseThrow(RuntimeException::new)
                .uuid;
    }

    @Override
    public int offset() {
        return Optional.ofNullable(node.getString("revision"))
                .flatMap(RecordId::fromString)
                .orElseThrow(RuntimeException::new)
                .offset;
    }

    @Nonnull
    @Override
    public NodeState getRoot() {
        return node.getChildNode("root");
    }

    @Override
    public String toString() {
        return node.toString();
    }

    private static class RecordId {
        static final Pattern PATTERN = compile(
                "([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})"
                        + "(:(0|[1-9][0-9]*)|\\.([0-9a-f]{8}))");

        @Nonnull
        final UUID uuid;
        final int offset;

        @Nonnull
        static Optional<RecordId> fromString(@Nonnull String revision) {
            Matcher matcher = PATTERN.matcher(revision);
            if (matcher.matches()) {
                UUID uuid = UUID.fromString(matcher.group(1));

                int offset;
                if (matcher.group(3) != null) {
                    offset = parseInt(matcher.group(3));
                } else {
                    offset = parseInt(matcher.group(4), 16);
                }
                return Optional.of(new RecordId(uuid, offset));
            } else {
                return Optional.empty();
            }
        }

        public RecordId(@Nonnull UUID uuid, int offset) {
            this.uuid = uuid;
            this.offset = offset;
        }
    }

}
