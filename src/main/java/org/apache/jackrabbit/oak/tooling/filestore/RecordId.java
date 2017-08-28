/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.jackrabbit.oak.tooling.filestore;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An instance of this class represent an identifier of a record
 * in a segment store. A record if consists of an identifier of the
 * segment containing the record and an offset for the record inside
 * that segment.
 */
public class RecordId {
    @Nonnull
    private final UUID segmentId;

    private final int offset;

    /**
     * Create a new record id instance.
     * @param segmentId  the segment identifier
     * @param offset     the offset within the segment
     */
    public RecordId(@Nonnull UUID segmentId, int offset) {
        this.segmentId = requireNonNull(segmentId);
        this.offset = offset;
    }

    /**
     * @return  the segment identifier
     */
    @Nonnull
    public UUID getSegmentId() {
        return segmentId;
    }

    /**
     * @return  the offset with the segment
     */
    public int getOffset() {
        return offset;
    }

    /**
     * @return  {@code true} iff {@code other} is an instance of
     * this class and has the same segment id and offset.
     */
    @Override
    public boolean equals(@Nullable  Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        RecordId that = (RecordId) other;
        return offset == that.offset &&
                Objects.equals(segmentId, that.segmentId);
    }

    @Override
    public int hashCode() {
        return hash(segmentId, offset);
    }

    @Override
    public String toString() {
        return "RecordId{" +
                "segmentId=" + segmentId +
                ", offset=" + offset +
                '}';
    }
}
