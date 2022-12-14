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

import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
class ByteBufferReaderTest {

	@Test
	void readNextByte() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		byte val = reader.readByte();
		Assertions.assertEquals(1, val);

		val = reader.readByte();
		Assertions.assertEquals(2, val);

		val = reader.readByte();
		Assertions.assertEquals(3, val);

		val = reader.readByte();
		Assertions.assertEquals(4, val);
	}

	@Test
	void readNextChar() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		char val = reader.readChar();
		Assertions.assertEquals(0x0102, val);

		val = reader.readChar();
		Assertions.assertEquals(0x0304, val);
	}

	@Test
	void transfer() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		byte[] dest1 = reader.readBytes(2);
		Assertions.assertEquals(2, dest1.length);
		Assertions.assertEquals(1, dest1[0]);
		Assertions.assertEquals(2, dest1[1]);

		byte[] dest2 = new byte[5];
		reader.transfer(dest2, 3, 2);
		Assertions.assertEquals(3, dest2[3]);
		Assertions.assertEquals(4, dest2[4]);
	}

	@Test
	void readNextShort() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		short val = reader.readShort();
		Assertions.assertEquals(0x0102, val);

	}

	@Test
	void readNextInt() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		int val = reader.readInt();
		Assertions.assertEquals(0x01020304, val);
	}

	@Test
	void readNextLong() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		long val = reader.readLong();
		Assertions.assertEquals(0x0102030401020304L, val);
	}

	@Test
	void readNextFloat() {
		// memory of float: 123.45
		final byte[] f123_45 = { (byte) 0x42, (byte) 0xF6, (byte) 0xE6, (byte) 0x66 };
		float val = ByteBufferReader.of(BufferKit.wrap(f123_45)).readFloat();
		Assertions.assertEquals(123.45f, val);
	}

	@Test
	void readNextDouble() {
		// memory of double: 123.45
		final byte[] d123_45 = { (byte) 0x40, (byte) 0x5E, (byte) 0xDC, (byte) 0xCC, (byte) 0xCC, (byte) 0xCC,
				(byte) 0xCC, (byte) 0xCD };
		double val = ByteBufferReader.of(BufferKit.wrap(d123_45)).readDouble();
		Assertions.assertEquals(123.45d, val);
	}

	@Test
	void readNextBytesAsString() {
		// C String : "123\0" , ISO_8859_1 encode
		final byte[] str_123 = { (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x00, (byte) 0xCC, (byte) 0xCC,
				(byte) 0xCC, (byte) 0xCC };
		String val = ByteBufferReader.of(BufferKit.wrap(str_123)).readAsString(8, StandardCharsets.ISO_8859_1);
		Assertions.assertArrayEquals(str_123, val.getBytes(StandardCharsets.ISO_8859_1));
	}

	@Test
	void readNextString() {
		// C String : "123\0" , ISO_8859_1 encode
		final byte[] str_123 = { (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x00, (byte) 0xCC, (byte) 0xCC,
				(byte) 0xCC, (byte) 0xCC };
		String val = ByteBufferReader.of(BufferKit.wrap(str_123)).readString(8, StandardCharsets.ISO_8859_1, true);
		Assertions.assertEquals("123", val);

		// C String : "\0" , ISO_8859_1 encode
		final byte[] str_empty = { (byte) 0x00, (byte) 0xCC, (byte) 0xCC, (byte) 0xCC };
		val = ByteBufferReader.of(BufferKit.wrap(str_empty)).readString(4, StandardCharsets.ISO_8859_1, true);
		Assertions.assertEquals("", val);
	}

	@Test
	void readableBytes() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		reader.readByte();
		Assertions.assertEquals(3, reader.readableBytes());

		reader.readShort();
		Assertions.assertEquals(1, reader.readableBytes());
	}

	@Test
	void changeOrder() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		Assertions.assertEquals(ByteOrder.BIG_ENDIAN, ByteBufferReader.of(BufferKit.wrap(data)).order());

		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data)).order(ByteOrder.LITTLE_ENDIAN);
		Assertions.assertEquals(ByteOrder.LITTLE_ENDIAN, reader.order());
		char val = reader.readChar();
		Assertions.assertEquals(0x0201, val);

		val = reader.order(ByteOrder.BIG_ENDIAN).readChar();
		Assertions.assertEquals(0x0304, val);
	}

	@Test
	void hasRead() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));

		reader.readByte();
		int count = reader.hasRead();
		Assertions.assertEquals(1, count);

		reader.readByte();
		count = reader.hasRead();
		Assertions.assertEquals(2, count);
	}

	@Test
	void unread() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		byte val = reader.readByte();
		Assertions.assertEquals(1, val);

		reader.unread(1);
		Assertions.assertEquals(4, reader.readableBytes());

		val = reader.readByte();
		Assertions.assertEquals(1, val);

		reader.unread(-1);
		Assertions.assertEquals(3, reader.readableBytes());

		reader.unread(0);
		Assertions.assertEquals(3, reader.readableBytes());

		reader.unread(100);
		Assertions.assertEquals(4, reader.readableBytes());
	}

	@Test
	void skip() {

		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));

		reader.skip(1);
		Assertions.assertEquals(3, reader.readableBytes());
		reader.skip(2);
		Assertions.assertEquals(1, reader.readableBytes());
		reader.skip(2);
		Assertions.assertEquals(0, reader.readableBytes());
		reader.skip(1);
		Assertions.assertEquals(0, reader.readableBytes());
	}

	@Test
	void expand() {
		byte[] data = new byte[1];
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		assertThrows(ReadOnlyBufferException.class, () -> {
			reader.expand(1);
		});
	}

	@Test
	void dumpBase64() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		String base64 = reader.dumpBase64();
		Assertions.assertEquals("AQIDBA==", base64);

		base64 = reader.dumpBase64();
		Assertions.assertEquals("", base64);
	}

	@Test
	void dumpHex() {
		byte[] data = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		ByteBufferReader reader = ByteBufferReader.of(BufferKit.wrap(data));
		reader.skip(1);
		String hex = reader.dumpHex();
		Assertions.assertEquals("020304", hex);

		hex = reader.dumpHex();
		Assertions.assertEquals("", hex);
	}

}