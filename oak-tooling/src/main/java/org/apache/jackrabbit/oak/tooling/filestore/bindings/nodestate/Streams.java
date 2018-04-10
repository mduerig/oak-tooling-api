package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.util.Spliterator;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class Streams {

    /**
     * Convert an iterable to a stream. The iterable must be {@link Spliterator#IMMUTABLE}
     * and {@link Spliterator#NONNULL}.
     */
    public static <T> Stream<T> asStream(@Nonnull Iterable<T> iterable) {
        return stream(spliteratorUnknownSize(
                iterable.iterator(), IMMUTABLE | NONNULL), false);
    }
}
