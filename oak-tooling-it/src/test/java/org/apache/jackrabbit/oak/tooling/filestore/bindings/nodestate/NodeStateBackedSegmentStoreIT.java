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

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.limit;
import static org.apache.jackrabbit.oak.segment.file.FileStoreBuilder.fileStoreBuilder;

import java.io.File;
import java.io.IOException;

import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.oak.segment.file.ReadOnlyFileStore;
import org.apache.jackrabbit.oak.segment.file.proc.Proc;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.JournalEntry;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentStore;
import org.apache.jackrabbit.oak.tooling.filestore.api.Tar;
import org.junit.Test;

/**
 * michid document
 * michid implement real tests
 */
public class NodeStateBackedSegmentStoreIT {

    @Test
    public void journalTest() throws IOException, InvalidFileStoreVersionException {
        FileStoreBuilder fsb = fileStoreBuilder(new File("/Users/mduerig/Repositories/adobe.com/publish/segmentstore"));
        ReadOnlyFileStore fs = fsb.buildReadOnly();
        NodeState proc = Proc.of(fsb.buildProcBackend(fs));

        SegmentStore ss = NodeStateBackedSegmentStore.newSegmentStore(proc);
        limit(ss.journalEntries(), 20)
                .forEach(e -> System.out.println(e.getRoot()));

        JournalEntry e = get(ss.journalEntries(), 0);
        System.out.println(e.getRoot());
        System.out.println(e.segmentId());
        System.out.println(e.offset());

        fs.close();
    }

    @Test
    public void tarsTest() throws IOException, InvalidFileStoreVersionException {
        FileStoreBuilder fsb = fileStoreBuilder(new File("/Users/mduerig/Repositories/adobe.com/publish/segmentstore"));
        ReadOnlyFileStore fs = fsb.buildReadOnly();
        NodeState proc = Proc.of(fsb.buildProcBackend(fs));

        SegmentStore ss = NodeStateBackedSegmentStore.newSegmentStore(proc);
        limit(ss.tars(), 20).forEach(System.out::println);
        Tar t = get(ss.tars(), 0);
        System.out.println(t.name());
        System.out.println(t.timestamp());
        System.out.println(t.size());

        fs.close();
    }

    @Test
    public void segmentsTest() throws IOException, InvalidFileStoreVersionException {
        FileStoreBuilder fsb = fileStoreBuilder(new File("/Users/mduerig/Repositories/adobe.com/publish/segmentstore"));
        ReadOnlyFileStore fs = fsb.buildReadOnly();
        NodeState proc = Proc.of(fsb.buildProcBackend(fs));

        SegmentStore ss = NodeStateBackedSegmentStore.newSegmentStore(proc);
        limit(getFirst(ss.tars(), null).segments(), 20).forEach(System.out::println);

        fs.close();
    }

    @Test
    public void getSegmentTest() throws IOException, InvalidFileStoreVersionException {
        FileStoreBuilder fsb = fileStoreBuilder(new File("/Users/mduerig/Repositories/adobe.com/publish/segmentstore"));
        ReadOnlyFileStore fs = fsb.buildReadOnly();
        NodeState proc = Proc.of(fsb.buildProcBackend(fs));

        SegmentStore ss = NodeStateBackedSegmentStore.newSegmentStore(proc);

        Tar t = get(ss.tars(), 2);
        Segment s = get(t.segments(), 3);

        System.out.println(s.hexDump(true).substring(0, 1000));
        System.out.println(s);
        System.out.println(s.id());
        System.out.println(s.length());
        System.out.println(s.type());
        System.out.println(s.references());
        System.out.println(s.records());
        System.out.println(s.data().length());

        System.out.println(s.metaData().version());
        System.out.println(s.metaData().generation());
        System.out.println(s.metaData().fullGeneration());
        System.out.println(s.metaData().compacted());
        System.out.println(s.metaData().info());

        fs.close();
    }

}
