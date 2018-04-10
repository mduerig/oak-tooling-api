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

package org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate;

import static org.apache.jackrabbit.oak.segment.file.FileStoreBuilder.fileStoreBuilder;
import static org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate.NodeStateBackedSegmentStore.newSegmentStore;
import static org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate.Streams.asStream;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.oak.segment.file.ReadOnlyFileStore;
import org.apache.jackrabbit.oak.segment.file.proc.Proc;
import org.apache.jackrabbit.oak.tooling.filestore.api.JournalEntry;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentStore;
import org.apache.jackrabbit.oak.tooling.filestore.api.Tar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ExampleQueries {
    private final static String SEGMENT_DIR = System.getProperty("segmentstore", null);

    private ReadOnlyFileStore fileStore;

    private SegmentStore segmentStore;

    @Before
    public void setup() throws IOException, InvalidFileStoreVersionException {
        assumeTrue("No segment store directory specified. " +
                           "Use -Dsegmentstore=/path/to/segmentstore", SEGMENT_DIR != null);

        FileStoreBuilder builder = fileStoreBuilder(new File(SEGMENT_DIR));
        fileStore = builder.buildReadOnly();
        segmentStore = newSegmentStore(Proc.of(builder.buildProcBackend(fileStore)));
    }

    @After
    public void tearDown() {
        segmentStore = null;
        if (fileStore != null) {
            fileStore.close();
        }
    }

    @Test
    public void listTars() {
        segmentStore.tars()
            .forEach(tar -> System.out.println(tar.name() + " " + tar.size()));
    }

    @Test
    public void tarSize() {
        long tarSizeSum = asStream(segmentStore.tars())
            .filter(tar -> tar.name().endsWith("tar"))
            .mapToLong(Tar::size)
            .sum();

        System.out.println(tarSizeSum);
    }

    @Test
    public void segmentSize() {
        long segmentSizeSum = asStream(segmentStore.tars())
                .filter(tar -> tar.name().endsWith("tar"))
                .flatMap(tar -> asStream(tar.segments()))
                .mapToLong(Segment::length)
                .sum();

        System.out.println(segmentSizeSum);
    }

    @Test
    public void referenceCount() {
        long referenceCount = asStream(segmentStore.tars())
                .filter(tar -> tar.name().endsWith("tar"))
                .flatMap(tar -> asStream(tar.segments()))
                .flatMap(segment -> asStream(segment.references()))
                .count();

        System.out.println(referenceCount);
    }

    @Test
    public void checkpointCountPerRevision() {
        Stream<Long> checkpointCountPerRevision = asStream(segmentStore.journalEntries())
                .map(JournalEntry::getRoot)
                .map(n -> n.getChildNode("checkpoints").getChildNodeCount(Integer.MAX_VALUE));

        List<Long> latest100Checkpoints = checkpointCountPerRevision
                .limit(100)
                .collect(Collectors.toList());
        System.out.println(latest100Checkpoints);
    }

}
