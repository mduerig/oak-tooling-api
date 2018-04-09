package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.api.Type.BOOLEAN;
import static org.apache.jackrabbit.oak.api.Type.LONG;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentMetaData;

/**
 * michid document
 */
public class NodeBackedSegmentMetaData implements SegmentMetaData {
    @Nonnull
    private final NodeState node;

    public NodeBackedSegmentMetaData(@Nonnull NodeState node) {this.node = node;}

    @Override
    public int version() {
        return 0;
        // michid implement version
//        return Optional.ofNullable(node.getProperty("version"))
//                .map(property -> property.getValue(LONG).intValue())
//                .orElseThrow(RuntimeException::new);
    }

    @Override
    public int generation() {
        return Optional.ofNullable(node.getProperty("generation"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public int fullGeneration() {
        return Optional.ofNullable(node.getProperty("fullGeneration"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public boolean compacted() {
        return Optional.ofNullable(node.getProperty("compacted"))
                .map(property -> property.getValue(BOOLEAN))
                .orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public Map<String, String> info() {
        return Collections.emptyMap(); // michid implement info
    }
}
