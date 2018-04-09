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

package org.apache.jackrabbit.oak.tooling.filestore.api;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * An instance of this class represent some metadata associated
 * with a segment.
 */
public interface SegmentMetaData {

    /**
     * @return  the version of the segment format
     */
    int version();

    /**
     * @return  the garbage collection generation
     */
    int generation();

    /**
     * @return  the full garbage collection generation
     */
    int fullGeneration();

    /**
     * @return  {@code true} iff this segment has been created by compaction
     */
    boolean compacted();

    /**
     * @return  a map representation of the segment info
     */
    @Nonnull
    Map<String, String> info();
}
