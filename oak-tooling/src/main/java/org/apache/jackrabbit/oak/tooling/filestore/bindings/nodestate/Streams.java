package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterators.spliteratorUnknownSize;
import static java.util.stream.StreamSupport.stream;

import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

public class Streams {

    /**
     * Convert an iterable to a stream. The iterable must be {@link Spliterator#IMMUTABLE}
     * and {@link Spliterator#NONNULL}.
     */
    @Nonnull
    public static <T> Stream<T> asStream(@Nonnull Iterable<T> iterable) {
        return stream(spliteratorUnknownSize(
                iterable.iterator(), IMMUTABLE | NONNULL), false);
    }

    /**
     * Convert a function to an {@code Iterable<B>} to a function to {@code Stream<B>}.
     * The iterable must be {@link Spliterator#IMMUTABLE} and {@link Spliterator#NONNULL}.
     * @param f    a function from {@code A} to {@code Iterable<B>}
     * @return     a function from {@code A} to {@code Stream<B>}
     */
    @Nonnull
    public static <A, B> Function<A, Stream<B>> asStream(
            @Nonnull Function<A, Iterable<B>> f) {
        return f.andThen(Streams::asStream);
    }

}
