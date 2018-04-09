package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.api.Type.LONG;
import static org.apache.jackrabbit.oak.api.Type.STRING;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.Record;

/**
 * michid document
 */
public class NodeStateBackedRecord implements Record {

    @Nonnull
    private final NodeState node;

    @Nonnull
    public static Record newRecord(@Nonnull NodeState node) {
        return new NodeStateBackedRecord(node);
    }

    private NodeStateBackedRecord(@Nonnull NodeState node) {
        this.node = node;
    }

    @Nonnull
    @Override
    public UUID segmentId() {
        return Optional.ofNullable(node.getProperty("segmentId"))
                .map(property -> property.getValue(STRING))
                .map(UUID::fromString)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public int offset() {
        return Optional.ofNullable(node.getProperty("offset"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public int number() {
        return Optional.ofNullable(node.getProperty("number"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public int address() {
        return Optional.ofNullable(node.getProperty("address"))
                .map(property -> property.getValue(LONG).intValue())
                .orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public Type type() {
        return Optional.ofNullable(node.getProperty("type"))
                .map(property -> property.getValue(STRING))
                .map(Type::valueOf)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public String toString() {
        return node.toString();
    }
}
