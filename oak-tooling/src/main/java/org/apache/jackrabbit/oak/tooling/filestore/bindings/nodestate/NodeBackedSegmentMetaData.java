package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.api.Type.BOOLEAN;
import static org.apache.jackrabbit.oak.api.Type.LONG;
import static org.apache.jackrabbit.oak.api.Type.STRING;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentMetaData;

/**
 * An implementation of {@link SegmentMetaData} based on a {@link NodeState}.
 * The node state is expected to expose the following properties:
 * <ul>
 *     <li>{@code version} of type {@code LONG}</li>
 *     <li>{@code generation} of type {@code LONG}</li>
 *     <li>{@code fullGeneration} of type {@code LONG}</li>
 *     <li>{@code compacted} of type {@code BOOLEAN}</li>
 *     <li>{@code info} of type {@code STRING}</li>
 * </ul>
 */
public class NodeBackedSegmentMetaData implements SegmentMetaData {
    @Nonnull
    private final NodeState node;

    public NodeBackedSegmentMetaData(@Nonnull NodeState node) {this.node = node;}

    @Override
    public int version() {
        return Optional.ofNullable(node.getProperty("version"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
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
        return Optional.ofNullable(node.getProperty("info"))
                .map(property -> property.getValue(STRING))
                .map(this::toMap)
                .orElseThrow(RuntimeException::new);
    }

    private Map<String, String> toMap(String keyValuePairs) {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        return new Gson().fromJson(keyValuePairs, type);
    }

    @Override
    public String toString() {
        return node.toString();
    }
}
