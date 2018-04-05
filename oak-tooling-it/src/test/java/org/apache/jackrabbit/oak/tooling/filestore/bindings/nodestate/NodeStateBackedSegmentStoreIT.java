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

import java.io.File;
import java.io.IOException;

import com.google.common.collect.Iterables;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.apache.jackrabbit.oak.segment.file.ReadOnlyFileStore;
import org.apache.jackrabbit.oak.segment.file.proc.Proc;
import org.apache.jackrabbit.oak.spi.state.NodeState;
import org.apache.jackrabbit.oak.tooling.filestore.api.SegmentStore;
import org.junit.Test;

/**
 * michid document
 */
public class NodeStateBackedSegmentStoreIT {


    // michid implement real tests
    @Test
    public void journalTest() throws IOException, InvalidFileStoreVersionException {
        FileStoreBuilder fsb = fileStoreBuilder(new File("/Users/mduerig/Repositories/adobe.com/publish/segmentstore"));
        ReadOnlyFileStore fs = fsb.buildReadOnly();

        NodeState proc = Proc.builder()
            .withPersistence(fsb.getPersistence())
            .withSegmentIdProvider(fs.getSegmentIdProvider())
            .withSegmentReader(fs.getReader())
            .build();

        SegmentStore ss = NodeStateBackedSegmentStore.newSegmentStore(proc);
        Iterables.limit(ss.journalEntries(), 20)
                .forEach(e -> System.out.println(e.getRoot()));

        fs.close();
    }

}
