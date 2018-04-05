package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.ChildNodeEntry;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.Tar;

/**
 * michid document
 */
public class NodeBackedTar implements Tar {

    @Nonnull
    private final NodeState node;

    @Nonnull
    public static Tar newTar(@Nonnull NodeState node) {
        return new NodeBackedTar(node);
    }

    private NodeBackedTar(@Nonnull NodeState node) {
        this.node = node;
    }

    @Nonnull
    @Override
    public String name() {
        return null; // michid implement name
    }

    @Override
    public long size() {
        return 0; // michid implement size
    }

    @Override
    public long timestamp() {
        return 0; // michid implement timestamp
    }

    @Nonnull
    @Override
    public Iterable<Segment> segments() {
        return () -> Streams.asStream(node.getChildNodeEntries())
                        .map(ChildNodeEntry::getNodeState)
                        .map(NodeStateBackedSegment::newSegment)
                        .iterator();
    }

    @Override
    public String toString() {
        // michid implement toString
        return node.toString();
    }
}
