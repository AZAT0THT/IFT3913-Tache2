/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;

public class EndianUtilsManualTest {

    @Test
    public void rejectsThreeByteBigEndianInput() {
        byte[] bytes = {0x12, 0x34, 0x56};

        assertThrows(EndianUtils.BufferUnderrunException.class,
            () -> EndianUtils.readUIntBE(
                new ByteArrayInputStream(bytes)));
    }

    @Test
    public void readsBigEndianLongValues() throws Exception {
        byte[] bytes = {
            0x01, 0x02, 0x03, (byte) 0x84,
            (byte) 0x85, 0x06, 0x07, 0x08};

        assertEquals(0x0102038485060708L,
            EndianUtils.readLongBE(new ByteArrayInputStream(bytes)));

        assertEquals(0L,
            EndianUtils.readLongBE(
                new ByteArrayInputStream(new byte[8])));
    }

    @Test
    public void readsLittleEndianLongValues() throws Exception {
        byte[] bytes = {
            0x01, 0x02, 0x03, (byte) 0x84,
            (byte) 0x85, 0x06, 0x07, 0x08};

        assertEquals(0x0807068584030201L,
            EndianUtils.readLongLE(new ByteArrayInputStream(bytes)));

        assertEquals(0L,
            EndianUtils.readLongLE(
                new ByteArrayInputStream(new byte[8])));
    }

    @Test
    public void rejectsSevenByteBigEndianLongInput() {
        byte[] bytes = {1, 2, 3, 4, 5, 6, 7};

        assertThrows(EndianUtils.BufferUnderrunException.class,
            () -> EndianUtils.readLongBE(
                new ByteArrayInputStream(bytes)));
    }

    @Test
    public void rejectsSevenByteLittleEndianLongInput() {
        byte[] bytes = {1, 2, 3, 4, 5, 6, 7};

        assertThrows(EndianUtils.BufferUnderrunException.class,
            () -> EndianUtils.readLongLE(
                new ByteArrayInputStream(bytes)));
    }
}
