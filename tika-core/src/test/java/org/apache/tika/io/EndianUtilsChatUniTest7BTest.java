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
import org.junit.jupiter.api.function.Executable;

import org.apache.tika.io.EndianUtils.BufferUnderrunException;

/**
 * 9 cas de tests généré par ChatUniTest et amélioré manuellement
 */
public class EndianUtilsChatUniTest7BTest {

    @Test
    public void testReadUIntBE() throws Exception {
        byte[] data = new byte[]{0x00, 0x00, 0x00, 0x01};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(1L, result);
    }

    @Test
    public void testReadUIntBEWithBufferUnderrun() throws Exception {
        byte[] data = new byte[]{0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        Executable executable = () -> EndianUtils.readUIntBE(inputStream);
        assertThrows(BufferUnderrunException.class, executable);
    }

    @Test
    public void testReadUIntBEWithNegativeValues() throws Exception {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(4294967294L, result);
    }

    @Test
    public void testReadUIntBEWithZeroValues() throws Exception {
        byte[] data = new byte[]{0x00, 0x00, 0x00, 0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(0L, result);
    }

    @Test
    public void testReadUIntBEWithMaxValues() throws Exception {
        byte[] data = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(4294967295L, result);
    }

    @Test
    public void testReadUIntBEWithLongValues() throws Exception {
        byte[] data = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(0L, result);
    }

    @Test
    public void testReadUIntBEWithLongNegativeValues() throws Exception {
        byte[] data = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(4294967295L, result);
    }

    @Test
    public void testReadUIntBEWithLongZeroValues() throws Exception {
        byte[] data = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(0L, result);
    }

    @Test
    public void testReadUIntBEWithLongMaxValues() throws Exception {
        byte[] data = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        long result = EndianUtils.readUIntBE(inputStream);
        assertEquals(4294967295L, result);
    }
}
