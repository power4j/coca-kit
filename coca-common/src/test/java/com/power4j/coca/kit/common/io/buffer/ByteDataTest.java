/*
 * Copyright 2021 ChenJun (power4j@outlook.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.power4j.coca.kit.common.io.buffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class ByteDataTest {

	@Test
	void ofCapacity() {
		ByteData byteData = ByteData.ofCapacity(1);
		Assertions.assertEquals(1, byteData.capacity());
		Assertions.assertEquals(0, byteData.writeIndex());
		Assertions.assertEquals(1, byteData.writableBytes());
		Assertions.assertEquals(0, byteData.readableBytes());
	}

	@Test
	void ofEmpty() {
		ByteData byteData = ByteData.ofEmpty();
		Assertions.assertEquals(0, byteData.capacity());
		Assertions.assertEquals(0, byteData.writeIndex());
		Assertions.assertEquals(0, byteData.writableBytes());
		Assertions.assertEquals(0, byteData.readableBytes());
	}

	@Test
	void copyOf() {
		byte[] src = new byte[] { 0x1, 0x2, 0x3, 0x4 };

		ByteData byteData1 = ByteData.copyOf(src, 1, 2);
		Assertions.assertEquals(2, byteData1.capacity());
		Assertions.assertEquals(2, byteData1.writeIndex());
		Assertions.assertEquals(0, byteData1.writableBytes());
		Assertions.assertEquals(2, byteData1.readableBytes());
		Assertions.assertArrayEquals(new byte[] { 0x2, 0x3 }, byteData1.read(0, -1));

		byteData1 = ByteData.copyOf(src);
		Assertions.assertEquals(4, byteData1.capacity());
		Assertions.assertEquals(4, byteData1.writeIndex());
		Assertions.assertEquals(0, byteData1.writableBytes());
		Assertions.assertEquals(4, byteData1.readableBytes());
		Assertions.assertArrayEquals(src, byteData1.read(0, -1));

		byteData1 = ByteData.copyOf(src);
		ByteData byteData2 = ByteData.copyOf(byteData1);
		Assertions.assertEquals(byteData1.capacity(), byteData2.capacity());
		Assertions.assertEquals(byteData1.writeIndex(), byteData2.writeIndex());
		Assertions.assertEquals(0, byteData2.writableBytes());
		Assertions.assertEquals(4, byteData2.readableBytes());
		Assertions.assertArrayEquals(byteData1.buffer(), byteData1.buffer());
	}

	@Test
	void ofRepeat() {
		ByteData byteData = ByteData.ofRepeat(0x1, 3);
		Assertions.assertEquals(3, byteData.capacity());
		Assertions.assertEquals(3, byteData.writeIndex());
		Assertions.assertEquals(0, byteData.writableBytes());
		Assertions.assertEquals(3, byteData.readableBytes());
		Assertions.assertArrayEquals(new byte[] { 0x1, 0x1, 0x1 }, byteData.buffer());
	}

	@Test
	void ofRandom() {
		ByteData byteData = ByteData.ofRandom(3);
		Assertions.assertEquals(3, byteData.capacity());
		Assertions.assertEquals(3, byteData.writeIndex());
		Assertions.assertEquals(0, byteData.writableBytes());
		Assertions.assertEquals(3, byteData.readableBytes());
	}

	@Test
	void ofHex() {
		ByteData byteData = ByteData.ofHex("0123");
		Assertions.assertEquals(2, byteData.capacity());
		Assertions.assertEquals(2, byteData.writeIndex());
		Assertions.assertEquals(0, byteData.writableBytes());
		Assertions.assertArrayEquals(new byte[] { 0x01, 0x23 }, byteData.buffer());

		Assertions.assertThrows(IllegalArgumentException.class, () -> ByteData.ofHex("123"));
	}

	@Test
	void ofBase64() {
		ByteData byteData = ByteData.ofBase64("AQIDBA==");
		Assertions.assertEquals(4, byteData.capacity());
		Assertions.assertEquals(4, byteData.writeIndex());
		Assertions.assertEquals(0, byteData.writableBytes());
		Assertions.assertArrayEquals(new byte[] { 0x01, 0x2, 0x3, 0x4 }, byteData.buffer());

		Assertions.assertThrows(IllegalArgumentException.class, () -> ByteData.ofBase64("!"));
	}

	@Test
	void writeIndex() {
		ByteData byteData = ByteData.ofCapacity(4);
		Assertions.assertEquals(0, byteData.writeIndex());
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> byteData.writeIndex(-1));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> byteData.writeIndex(5));

		Assertions.assertEquals(1, byteData.writeIndex(1).writeIndex());
	}

	@Test
	void writeIndexAdvance() {
		ByteData byteData = ByteData.ofCapacity(4);

		byteData.writeIndex(1);
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> byteData.writeIndexAdvance(-2));
		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> byteData.writeIndexAdvance(4));

		Assertions.assertEquals(1, byteData.writeIndex(0).writeIndexAdvance(4).writeIndexAdvance(-3).writeIndex());
	}

	@Test
	void writeIndexAdvanceSome() {
		ByteData byteData = ByteData.ofCapacity(4);
		Assertions.assertEquals(0, byteData.writeIndex(1).writeIndexAdvanceSome(-2).writeIndex());
		Assertions.assertEquals(4, byteData.writeIndex(1).writeIndexAdvanceSome(5).writeIndex());
	}

	@Test
	void expandBy() {
		ByteData byteData = ByteData.ofRepeat(0x1, 4).expandBy(1);
		Assertions.assertEquals(5, byteData.capacity());
		Assertions.assertEquals(1, byteData.writableBytes());

		byteData = ByteData.ofRepeat((byte) 0x1, 4).expandBy(-1);
		Assertions.assertEquals(3, byteData.capacity());
		Assertions.assertEquals(0, byteData.writableBytes());

		Assertions.assertEquals(2, ByteData.ofRandom(2).expandBy(1).writeIndex());
		Assertions.assertEquals(1, ByteData.ofRandom(2).expandBy(-1).writeIndex());
		Assertions.assertEquals(2, ByteData.ofRandom(2).expandBy(1).readableBytes());
		Assertions.assertEquals(1, ByteData.ofRandom(2).expandBy(-1).readableBytes());
		Assertions.assertEquals(0, ByteData.ofRandom(1).expandBy(-2).readableBytes());
		Assertions.assertEquals(0, ByteData.ofRandom(1).expandBy(-2).capacity());
	}

	@Test
	void expand() {
		Assertions.assertTrue(ByteData.ofRandom(2).expand().readableBytes() > 0);
	}

	@Test
	void ensureWrite() {
		Assertions.assertEquals(1, ByteData.ofEmpty().ensureWriteBytes(1).writableBytes());
		Assertions.assertEquals(1, ByteData.ofCapacity(1).ensureWriteBytes(1).writableBytes());
		Assertions.assertEquals(2, ByteData.ofCapacity(1).ensureWriteBytes(2).writableBytes());
	}

	@Test
	void zeroMemory() {
		Assertions.assertArrayEquals(new byte[] { 0, 0 }, ByteData.ofRepeat(0x1, 2).zeroMemory().buffer());
		Assertions.assertArrayEquals(new byte[] { 0, 0, 0 },
				ByteData.ofRepeat(0x1, 2).expandBy(1).zeroMemory().buffer());
	}

	@Test
	void read() {
		Assertions.assertEquals(0, ByteData.copyOf(new byte[] { 0, 1 }).readAt(0));
		Assertions.assertEquals(1, ByteData.copyOf(new byte[] { 0, 1 }).readAt(1));

		Assertions.assertArrayEquals(new byte[] { 0 }, ByteData.copyOf(new byte[] { 0, 1 }).read(1));
		Assertions.assertArrayEquals(new byte[] { 0, 1 }, ByteData.copyOf(new byte[] { 0, 1 }).read(2));
		Assertions.assertArrayEquals(new byte[] { 0, 1 }, ByteData.copyOf(new byte[] { 0, 1 }).readAll());

		Assertions.assertArrayEquals(new byte[] { 1 }, ByteData.copyOf(new byte[] { 0, 1, 2 }).read(1, 1));
		Assertions.assertArrayEquals(new byte[] { 1, 2 }, ByteData.copyOf(new byte[] { 0, 1, 2 }).read(1, 2));
		Assertions.assertArrayEquals(new byte[] { 0, 1, 2 }, ByteData.copyOf(new byte[] { 0, 1, 2 }).read(0, -1));
		Assertions.assertArrayEquals(new byte[0], ByteData.copyOf(new byte[] { 0, 1, 2 }).read(0, 0));
		Assertions.assertArrayEquals(new byte[0], ByteData.copyOf(new byte[] { 0, 1, 2 }).read(2, 0));
		Assertions.assertArrayEquals(new byte[0], ByteData.copyOf(new byte[] { 0, 1, 2 }).read(3, -1));
		Assertions.assertArrayEquals(new byte[0], ByteData.copyOf(new byte[] { 0, 1, 2 }).read(3, 0));

		Assertions.assertThrows(IndexOutOfBoundsException.class, () -> ByteData.copyOf(new byte[] { 0, 1, 2 }).read(4));
		Assertions.assertThrows(IndexOutOfBoundsException.class,
				() -> ByteData.copyOf(new byte[] { 0, 1, 2 }).read(3, 1));

	}

	@Test
	void readSome() {
		Assertions.assertArrayEquals(new byte[] { 0, 1 }, ByteData.copyOf(new byte[] { 0, 1 }).readSome(3));
		Assertions.assertArrayEquals(new byte[] { 1 }, ByteData.copyOf(new byte[] { 0, 1 }).readSome(1, 3));

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> ByteData.copyOf(new byte[] { 0, 1, 2 }).readSome(-1));
		Assertions.assertThrows(IllegalArgumentException.class,
				() -> ByteData.copyOf(new byte[] { 0, 1, 2 }).readSome(0, -1));
	}

	@Test
	void readString() {
		byte[] src = "a它".getBytes(StandardCharsets.UTF_8);
		Assertions.assertEquals("a", ByteData.copyOf(src).readString(0, 1, StandardCharsets.UTF_8));
		Assertions.assertEquals(new String(new byte[] { (byte) 0xE5 }),
				ByteData.copyOf(src).readString(1, 1, StandardCharsets.UTF_8));

		src = "a它".getBytes(StandardCharsets.US_ASCII);
		Assertions.assertEquals(new String(new byte[] { 0x3F }),
				ByteData.copyOf(src).readString(1, 1, StandardCharsets.US_ASCII));

		Assertions.assertEquals("", ByteData.ofEmpty().readString(0, -1, StandardCharsets.US_ASCII));
	}

	@Test
	void readUtf8String() {
		byte[] src = "a它".getBytes(StandardCharsets.UTF_8);
		Assertions.assertEquals("a", ByteData.copyOf(src).readUtf8String(0, 1));
		Assertions.assertEquals(new String(new byte[] { (byte) 0xE5 }), ByteData.copyOf(src).readUtf8String(1, 1));
	}

	@Test
	void readHexLower() {
		Assertions.assertEquals("", ByteData.ofEmpty().readHexLower(0, -1));
		Assertions.assertEquals("123a", ByteData.copyOf(new byte[] { 0x12, 0x3A }).readHexLower(0, -1));
		Assertions.assertEquals("3a", ByteData.copyOf(new byte[] { 0x12, 0x3A }).readHexLower(1, 1));
	}

	@Test
	void readHexUpper() {
		Assertions.assertEquals("", ByteData.ofEmpty().readHexUpper(0, -1));
		Assertions.assertEquals("123A", ByteData.copyOf(new byte[] { 0x12, 0x3A }).readHexUpper(0, -1));
		Assertions.assertEquals("3A", ByteData.copyOf(new byte[] { 0x12, 0x3A }).readHexUpper(1, 1));
	}

	@Test
	void readBase64() {
		Assertions.assertEquals("", ByteData.ofEmpty().readBase64(0, -1));
		Assertions.assertEquals("AQIDBA==", ByteData.copyOf(new byte[] { 0x01, 0x2, 0x3, 0x4 }).readBase64(0, -1));
	}

	@Test
	void readTo() {
		byte[] buff = new byte[5];
		ByteData.ofEmpty().readTo(0, buff, 1, 0, 0xF);
		Assertions.assertArrayEquals(new byte[] { 0, 0, 0, 0, 0 }, buff);

		buff = new byte[5];
		int padCount = ByteData.copyOf(new byte[] { 0x12, 0x34 }).readTo(0, buff, 1, 1, 0xF);
		Assertions.assertEquals(0, padCount);
		Assertions.assertArrayEquals(new byte[] { 0, 0x12, 0, 0, 0 }, buff);

		buff = new byte[5];
		padCount = ByteData.copyOf(new byte[] { 0x12, 0x34 }).readTo(0, buff, 1, 4, 0xF);
		Assertions.assertEquals(2, padCount);
		Assertions.assertArrayEquals(new byte[] { 0, 0x12, 0x34, 0xF, 0xF }, buff);

		buff = new byte[5];
		padCount = ByteData.copyOf(new byte[] { 0x12, 0x34 }).readTo(1, buff, 1, 4, 0xF);
		Assertions.assertEquals(3, padCount);
		Assertions.assertArrayEquals(new byte[] { 0, 0x34, 0xF, 0xF, 0xF }, buff);

		buff = new byte[] { 1, 1, 1, 1, 1 };
		padCount = ByteData.copyOf(new byte[] { 0x12, 0x34 }).readTo(1, buff, 1);
		Assertions.assertEquals(4, padCount);
		Assertions.assertArrayEquals(new byte[] { 0x34, 1, 1, 1, 1 }, buff);

		buff = new byte[] { 1, 1, 1, 1, 1 };
		padCount = ByteData.copyOf(new byte[] { 0x12, 0x34 }).readTo(0, buff, 1);
		Assertions.assertEquals(3, padCount);
		Assertions.assertArrayEquals(new byte[] { 0x12, 0x34, 1, 1, 1 }, buff);
	}

	@Test
	void writeByteData() {
		ByteData src = ByteData.ofRepeat(0xF, 2);
		Assertions.assertArrayEquals(new byte[] { 0xA, 0xF, 0xF }, ByteData.ofRepeat(0xA, 1).write(src).readAll());
	}

	@Test
	void writeInt8() {
		Assertions.assertArrayEquals(new byte[] { 1 }, ByteData.ofEmpty().writeInt8(1).readAll());
	}

	@Test
	void writeInt16() {
		Assertions.assertArrayEquals(new byte[] { 0x12, 0x34 },
				ByteData.ofEmpty().writeInt16(0x1234, ByteOrder.BIG_ENDIAN).readAll());
		Assertions.assertArrayEquals(new byte[] { 0x34, 0x12 },
				ByteData.ofEmpty().writeInt16(0x1234, ByteOrder.LITTLE_ENDIAN).readAll());
	}

	@Test
	void writeInt32() {
		Assertions.assertArrayEquals(new byte[] { 0x12, 0x34, 0x56, 0x78 },
				ByteData.ofEmpty().writeInt32(0x12345678, ByteOrder.BIG_ENDIAN).readAll());
		Assertions.assertArrayEquals(new byte[] { 0x78, 0x56, 0x34, 0x12 },
				ByteData.ofEmpty().writeInt32(0x12345678, ByteOrder.LITTLE_ENDIAN).readAll());
	}

	@Test
	void writeInt64() {
		byte[] data = ByteData.ofEmpty().writeInt64(0x1122334455667788L, ByteOrder.BIG_ENDIAN).readAll();
		Assertions.assertArrayEquals(new byte[] { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88 }, data);

		data = ByteData.ofEmpty().writeInt64(0x1122334455667788L, ByteOrder.LITTLE_ENDIAN).readAll();
		Assertions.assertArrayEquals(new byte[] { (byte) 0x88, 0x77, 0x66, 0x55, 0x44, 0x33, 0x22, 0x11 }, data);
	}

	@Test
	void writeFloat() {
		Assertions.assertArrayEquals(new byte[] { 0x3D, (byte) 0xCC, (byte) 0xCC, (byte) 0xCD },
				ByteData.ofEmpty().writeFloat(0.1F, ByteOrder.BIG_ENDIAN).readAll());
		Assertions.assertArrayEquals(new byte[] { (byte) 0xCD, (byte) 0xCC, (byte) 0xCC, 0x3D },
				ByteData.ofEmpty().writeFloat(0.1F, ByteOrder.LITTLE_ENDIAN).readAll());
	}

	@Test
	void writeDouble() {
		byte[] data = ByteData.ofEmpty().writeDouble(0.1D, ByteOrder.BIG_ENDIAN).readAll();
		Assertions.assertArrayEquals(new byte[] { 0x3F, (byte) 0xB9, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99,
				(byte) 0x99, (byte) 0x9A }, data);

		data = ByteData.ofEmpty().writeDouble(0.1D, ByteOrder.LITTLE_ENDIAN).readAll();
		Assertions.assertArrayEquals(new byte[] { (byte) 0x9A, (byte) 0x99, (byte) 0x99, (byte) 0x99, (byte) 0x99,
				(byte) 0x99, (byte) 0xB9, 0x3F }, data);
	}

	@Test
	void writeBytes() {
		byte[] src = new byte[] { 0x11, 0x22, 0x33, 0x44 };
		Assertions.assertArrayEquals(src, ByteData.ofEmpty().writeBytes(src).readAll());
		Assertions.assertArrayEquals(new byte[] { 0x22, 0x33 }, ByteData.ofEmpty().writeBytes(src, 1, 2).readAll());
	}

	@Test
	void dataEquals() {

		Assertions.assertTrue(ByteData.ofEmpty().dataEquals(ByteData.copyOf(new byte[0])));

		Assertions.assertTrue(ByteData.ofEmpty().dataEquals(new byte[0]));
	}

	@Test
	void bufferWriter() {
		Assertions.assertEquals(0, ByteData.ofRepeat(1, 1).bufferWriter(ByteOrder.BIG_ENDIAN).writeableBytes());
		Assertions.assertEquals(1,
				ByteData.ofRepeat(1, 1).expandBy(1).bufferWriter(ByteOrder.BIG_ENDIAN).writeableBytes());
		Assertions.assertThrows(BufferOverflowException.class,
				() -> ByteData.ofRepeat(1, 1).bufferWriter(ByteOrder.BIG_ENDIAN).writeByte(1));
	}

	@Test
	void bufferReader() {
		Assertions.assertEquals(1, ByteData.ofRepeat(1, 1).bufferReader(ByteOrder.BIG_ENDIAN).readableBytes());
		Assertions.assertEquals(1,
				ByteData.ofRepeat(1, 1).expandBy(1).bufferReader(ByteOrder.BIG_ENDIAN).readableBytes());
	}

	@Test
	void testToString() {
	}

	@Test
	void display() {
		Assertions.assertEquals("[2]12ab", ByteData.ofHex("12AB").display());
	}

}
