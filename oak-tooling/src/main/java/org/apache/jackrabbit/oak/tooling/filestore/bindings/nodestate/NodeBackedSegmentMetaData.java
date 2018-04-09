package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentMetaData;

/**
 * michid document
 */
public class NodeBackedSegmentMetaData implements SegmentMetaData {
    public NodeBackedSegmentMetaData(NodeState node) {}

    @Override
    public int version() {
        return 0; // michid implement version
    }

    @Override
    public int generation() {
        return 0; // michid implement generation
    }

    @Override
    public int fullGeneration() {
        return 0; // michid implement fullGeneration
    }

    @Override
    public boolean compacted() {
        return false; // michid implement compacted
    }

    @Nonnull
    @Override
    public Map<String, String> info() {
        return Collections.emptyMap(); // michid implement info
    }
}
