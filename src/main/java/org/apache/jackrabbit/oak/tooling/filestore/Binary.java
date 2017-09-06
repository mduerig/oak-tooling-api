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

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

/**
 * Instances of the interface represent binary
 * {@link Property property} values.
 */
public interface Binary {
    Equivalence<Binary> EQ = (bin1, bin2) -> {
        if (bin1.size() != bin2.size()) {
            return false;
        }

        try {
            try (
                InputStream s1 = bin1.bytes();
                InputStream s2 = bin2.bytes())
            {
                for (int v1 = s1.read(); v1 != -1; v1 = s1.read()) {
                    if (v1 != s2.read()) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return true;
    };

    /**
     * @return  a new input stream containing all bytes of this binary.
     * The callers must close the stream when done.
     */
    @Nonnull
    InputStream bytes();

    /**
     * @return  the number of bytes contained in this binary.
     */
    long size();
}
