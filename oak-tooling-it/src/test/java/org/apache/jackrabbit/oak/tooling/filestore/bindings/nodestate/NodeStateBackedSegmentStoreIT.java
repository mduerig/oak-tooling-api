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

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.limit;
import static java.util.Arrays.asList;
import static org.apache.jackrabbit.oak.tooling.filestore.api.Record.Type.NODE;
import static org.apache.jackrabbit.oak.tooling.filestore.api.Segment.Type.BULK;
import static org.apache.jackrabbit.oak.tooling.filestore.api.Segment.Type.DATA;
import static org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate.NodeStateBackedSegmentStore.newSegmentStore;
import static org.apache.jackrabbit.oak.tooling.filestore.bindings.nodestate.Streams.asStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Optional;

import com.microsoft.azure.storage.StorageException;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.oak.segment.file.ReadOnlyFileStore;
import org.apache.jackrabbit.oak.segment.file.proc.Proc;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.JournalEntry;
import org.apache.jackrabbit.oak.tooling.filestore.api.Record;
import org.apache.jackrabbit.oak.tooling.filestore.api.Record.Type;
import org.apache.jackrabbit.oak.tooling.filestore.api.Segment;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentMetaData;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentStore;
import org.apache.jackrabbit.oak.tooling.filestore.api.Tar;
import org.junit.AfterClass;
import org.junit.AssumptionViolatedException;
import org.junit.BeforeClass;
import org.junit.Test;

public class NodeStateBackedSegmentStoreIT {

    private static ReadOnlyFileStore fileStore;

    private static SegmentStore segmentStore;

    @BeforeClass
    public static void setup() throws IOException, InvalidFileStoreVersionException, URISyntaxException, InvalidKeyException, StorageException {
        FileStoreBuilder builder = FileStoreUtil.getFileStoreBuilder();
        fileStore = builder.buildReadOnly();
        segmentStore = newSegmentStore(Proc.of(builder.buildProcBackend(fileStore)));
    }

    @AfterClass
    public static void tearDown() {
        segmentStore = null;
        if (fileStore != null) {
            fileStore.close();
        }
    }

    @Test
    public void rootTest() {
        Optional<NodeState> head = segmentStore.head();
        assertEquals(Optional.of(fileStore.getHead()), head);
    }

    @Test
    public void journalTest() {
        Iterable<JournalEntry> journal = segmentStore.journalEntries();
        assumeFalse("Cannot run with empty journal", isEmpty(journal));

        limit(journal, 20).forEach(entry -> {
            assertTrue(entry.getRoot().exists());
            assertNotNull(entry.segmentId());
            assertTrue(entry.recordNumber() >= 0);
        });

        assertEquals(fileStore.getHead(), journal.iterator().next().getRoot());
    }

    @Test
    public void tarsTest() {
        Iterable<Tar> tars = segmentStore.tars();
        assumeFalse("Cannot run with empty segment store", isEmpty(tars));

        tars.forEach(tar -> {
            assertTrue(tar.name().startsWith("data"));
            assertTrue(tar.size() > 0);
            assertTrue(tar.segments().iterator().hasNext());
        });
    }

    @Test
    public void segmentsTest() {
        Segment segment = asStream(segmentStore.tars())
                .flatMap(asStream(Tar::segments))
                .filter(Segment.isOfType(DATA))
                .findFirst()
                .orElseThrow(() ->
                     new AssumptionViolatedException("Cannot run with empty segment store"));

        assertNotNull(segment.id());
        assertTrue(segment.length() > 0);
        assertSame(segment.type(), DATA);
        assertNotNull(segment.data());
        assertEquals(segment.length(), segment.data().length());
        assertTrue(segment.exists());
        assertTrue(segment.hexDump(true).contains(" 0aK"));

        SegmentMetaData metaData = segment.metaData();
        assertNotNull(metaData);
        assertTrue(metaData.version() >= 10);
        assertTrue(metaData.generation() >= 0);
        assertTrue(metaData.fullGeneration() >= 0);

        Map<String, String> info = metaData.info();
        assertNotNull(info);
        assertTrue(info.containsKey("wid"));
        assertTrue(info.containsKey("sno"));
        assertTrue(info.containsKey("t"));

        Iterable<Record> records = segment.records();
        assertTrue(records.iterator().hasNext());
        Record record = records.iterator().next();
        assertEquals(segment.id(), record.segmentId());
        assertTrue(record.number() >= 0);
        assertTrue(record.offset() >= 0);
        assertTrue(asList(Type.values()).contains(record.type()));

        Optional<NodeState> nodeFromRecord = asStream(records)
                .filter(Record.isOfType(NODE))
                .findFirst()
                .flatMap(Record::root);

        assertTrue(nodeFromRecord.isPresent());
        assertTrue(nodeFromRecord.get().exists());
    }

    @Test
    public void segmentReferencesTest() {
        Segment segment = asStream(segmentStore.tars())
                .flatMap(asStream(Tar::segments))
                .filter(Segment.isOfType(DATA))
                .flatMap(asStream(Segment::references))
                .filter(Segment::exists)
                .findFirst()
                .orElseThrow(() ->
                    new AssumptionViolatedException("No existing references found"));

        assertNotNull(segment.id());
        assertTrue(segment.length() > 0);
        assertSame(segment.type(), DATA);
        assertNotNull(segment.data());
        assertEquals(segment.length(), segment.data().length());
        assertTrue(segment.exists());
        assertTrue(segment.hexDump(true).contains(" 0aK"));
    }

    @Test
    public void bulkSegmentTest() {
        Segment bulkSegment = asStream(segmentStore.tars())
                .flatMap(asStream(Tar::segments))
                .filter(Segment.isOfType(BULK))
                .findFirst()
                .orElseThrow(() ->
                    new AssumptionViolatedException("No bulk segment found"));

        assertNotNull(bulkSegment.toString());
        assertTrue(bulkSegment.exists());
        assertEquals(BULK, bulkSegment.type());
        assertNotNull(bulkSegment.id());
        assertTrue(bulkSegment.length() > 0);
        assertFalse(bulkSegment.records().iterator().hasNext());
        assertFalse(bulkSegment.references().iterator().hasNext());
        assertTrue(bulkSegment.hexDump(false).startsWith("00000000"));
        assertTrue(bulkSegment.hexDump(true).startsWith("Segment"));
    }

    @Test
    public void resolveNodeTest() {
        asStream(segmentStore.journalEntries())
            .limit(10)
            .forEach(entry ->
                 assertEquals(
                     Optional.of(entry.getRoot()),
                     segmentStore.node(entry.segmentId(), entry.recordNumber())));
    }

}
