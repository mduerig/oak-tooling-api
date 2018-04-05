package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.util.stream.Stream;

import javax.annotation.Nonnull;

/**
 * michid document
 */
public class Streams {
    public static <T> Stream<T> asStream(@Nonnull Iterable<T> childNodeEntries) {
        return stream(spliteratorUnknownSize(
                childNodeEntries.iterator(), IMMUTABLE | NONNULL), false);
    }
}
