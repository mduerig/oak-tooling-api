package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.Type;
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
        return Optional.ofNullable(node.getString("name"))
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public long size() {
        return Optional.ofNullable(node.getProperty("size"))
                .map(property -> property.getValue(Type.LONG))
                .orElseThrow(RuntimeException::new);
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
