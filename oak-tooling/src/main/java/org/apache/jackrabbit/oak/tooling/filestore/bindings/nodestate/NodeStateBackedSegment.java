package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.api.Type.BINARY;
import static org.apache.jackrabbit.oak.api.Type.LONG;
import static org.apache.jackrabbit.oak.api.Type.STRING;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.apache.commons.io.HexDump;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.jackrabbit.oak.api.Blob;
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
        return Optional.ofNullable(node.getProperty("id"))
                .map(property -> property.getValue(STRING))
                .map(UUID::fromString)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public long length() {
        return Optional.ofNullable(node.getProperty("length"))
                .map(property -> property.getValue(LONG))
                .orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public Type type() {
        return Type.DATA; // michid implement type
    }

    @Nonnull
    @Override
    public Iterable<UUID> references() {
        return Collections.emptyList(); // michid implement references
    }

    @Nonnull
    @Override
    public Iterable<Record> records() {
        return Collections.emptyList(); // michid implement records
    }

    @Override
    @Nonnull
    public Blob data() {
        return Optional.ofNullable(node.getProperty("data"))
                .map(property -> property.getValue(BINARY))
                .orElseThrow(RuntimeException::new);
    }

    @Nonnull
    @Override
    public SegmentMetaData metaData() {
        return new NodeBackedSegmentMetaData(node);
    }

    @Nonnull
    @Override
    public String hexDump(boolean includeHeader) {
        StringWriter string = new StringWriter();
        try (PrintWriter writer = new PrintWriter(string)) {
            if (includeHeader) {
                writer.format("Segment %s (%d bytes)%n", id(), length());
                writer.format("Version: %d%n", metaData().version());
                writer.format("GC: (generation=%d, full generation=%d, compacted=%b)%n",
                              metaData().generation(), metaData().fullGeneration(), metaData().compacted());
                writer.format("Info: (%s)%n", Joiner.on(',').withKeyValueSeparator("=").join(metaData().info()));

                if (type() == Type.DATA) {
                    writer.println("--------------------------------------------------------------------------");
                    int i = 1;
                    for (UUID segmentId : references()) {
                        writer.format("reference %02x: %s%n", i++, segmentId);
                    }
                    for (Record record : records()) {
                        int offset = record.offset();
                        writer.format("%10s record %08x: %08x @ %08x%n",
                                      record.type(), record.number(), offset, record.address());
                    }
                }
                writer.println("--------------------------------------------------------------------------");
            }
            try {
                hexDump(data(), writer);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            if (includeHeader) {
                writer.println("--------------------------------------------------------------------------");
            }
        }
        return string.toString();
    }

    private void hexDump(@Nonnull Blob blob, @Nonnull PrintWriter out) throws IOException {
        try (WriterOutputStream writer = new WriterOutputStream(out, Charsets.UTF_8);
             InputStream bytes = blob.getNewStream()) {
                byte[] data = new byte[(int) blob.length()];
                bytes.read(data);
                HexDump.dump(data, 0, writer, 0);
        }
    }

    @Override
    public String toString() {
        return node.toString();
    }
}
