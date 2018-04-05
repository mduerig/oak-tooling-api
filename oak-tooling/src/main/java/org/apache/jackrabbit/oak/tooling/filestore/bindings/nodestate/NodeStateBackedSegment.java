package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.Record;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentMetaData;

/**
 * michid document
 */
public class NodeStateBackedSegment implements Segment {

    @Nonnull
    private final NodeState node;

    @Nonnull
    public static Segment newSegment(@Nonnull NodeState node) {
        return new NodeStateBackedSegment(node);
    }

    private NodeStateBackedSegment(@Nonnull NodeState node) {
        this.node = node;
    }

    @Nonnull
    @Override
    public UUID id() {
        return null; // michid implement id
    }

    @Override
    public int size() {
        return 0; // michid implement size
    }

    @Nonnull
    @Override
    public Type type() {
        return null; // michid implement type
    }

    @Nonnull
    @Override
    public Iterable<UUID> references() {
        return null; // michid implement references
    }

    @Nonnull
    @Override
    public Iterable<Record> records() {
        return null; // michid implement records
    }

    @Nonnull
    @Override
    public SegmentMetaData metaData() {
        return null; // michid implement metaData
    }

    @Nonnull
    @Override
    public String hexDump(boolean includeHeader) {
        return null; // michid implement hexDump
    }

    @Override
    public String toString() {
        // michid implement toString
        return node.toString();
    }
}
