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
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
class ByteBufferWriterTest {

	@Test
	void noSpace() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(1));
		writer.writeByte((byte) 0x1);
		assertThrows(BufferOverflowException.class, () -> {
			writer.writeByte((byte) 0x1);
		});
	}

	@Test
	void writeByte() {
		final int size = 5;
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(size));
		for (int i = 1; i <= size; ++i) {
			writer.writeByte((byte) i);
			Assertions.assertEquals(i, writer.hasWritten());
			Assertions.assertEquals(size - i, writer.writeableBytes());
		}
	}

	@Test
	void writeByteRepeat() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(32));
		Assertions.assertEquals(32, writer.writeableBytes());
		writer.writeByteRepeat((byte) 0x1, 8);
		Assertions.assertEquals(8, writer.hasWritten());
		Assertions.assertEquals(24, writer.writeableBytes());
		writer.writeByteRepeat((byte) 0x2, 2);
		Assertions.assertEquals(10, writer.hasWritten());
		Assertions.assertEquals(22, writer.writeableBytes());

	}

	@Test
	void writeChar() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(3));
		writer.writeChar('1');
		Assertions.assertEquals(2, writer.hasWritten());
		Assertions.assertEquals(1, writer.writeableBytes());
	}

	@Test
	void writeBytes() {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length; ++i) {
			data[i] = (byte) i;
		}
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(10));
		writer.writeBytes(data, 3, 2);
		Assertions.assertEquals(2, writer.hasWritten());
		Assertions.assertEquals(8, writer.writeableBytes());
		Assertions.assertEquals(3, writer.getBuffer().array()[0]);
		Assertions.assertEquals(4, writer.getBuffer().array()[1]);
	}

	@Test
	void writeShort() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(3));
		writer.writeShort((short) 1);
		Assertions.assertEquals(2, writer.hasWritten());
		Assertions.assertEquals(1, writer.writeableBytes());
	}

	@Test
	void writeInt() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(Integer.BYTES));
		writer.writeInt(1);
		Assertions.assertEquals(Integer.BYTES, writer.hasWritten());
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void writeLong() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(Long.BYTES));
		writer.writeLong(1);
		Assertions.assertEquals(Long.BYTES, writer.hasWritten());
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void writeFloat() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(Float.BYTES));
		writer.writeFloat(1f);
		Assertions.assertEquals(Float.BYTES, writer.hasWritten());
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void writeDouble() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(Double.BYTES));
		writer.writeDouble(1D);
		Assertions.assertEquals(Double.BYTES, writer.hasWritten());
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void writeString() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(4));
		writer.writeString("123", StandardCharsets.UTF_8, 0, (byte) 0);
		Assertions.assertEquals(3, writer.hasWritten());
		Assertions.assertEquals(1, writer.writeableBytes());

		writer.discardAll();
		Assertions.assertEquals(4, writer.writeableBytes());
		writer.writeString("123", StandardCharsets.UTF_8, 0, (byte) 0);
		Assertions.assertEquals(3, writer.hasWritten());
		Assertions.assertEquals(1, writer.writeableBytes());
	}

	@Test
	void writeFixedString() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(4));
		writer.writeString("12", StandardCharsets.UTF_8, 4, (byte) 0);
		Assertions.assertEquals(4, writer.hasWritten());
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void discard() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(4));
		writer.writeByteRepeat((byte) 1, 4);
		writer.discard(1);
		Assertions.assertEquals(1, writer.writeableBytes());

		writer.discard(4);
		Assertions.assertEquals(4, writer.writeableBytes());

		writer.discard(100);
		Assertions.assertEquals(4, writer.writeableBytes());
	}

	@Test
	void discardAll() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(4));
		writer.writeByteRepeat((byte) 1, 3);
		writer.discardAll();
		Assertions.assertEquals(4, writer.writeableBytes());

		writer.writeByteRepeat((byte) 1, 4);
		writer.discardAll();
		Assertions.assertEquals(4, writer.writeableBytes());
	}

	@Test
	void reserve() {
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.allocate(4));
		writer.reserve(1);
		Assertions.assertEquals(3, writer.writeableBytes());

		writer.writeByteRepeat((byte) 1, 2);
		Assertions.assertEquals(1, writer.writeableBytes());

		writer.reserve(100);
		Assertions.assertEquals(0, writer.writeableBytes());
	}

	@Test
	void sharedData() {
		final byte[] src = { (byte) 1, (byte) 2, (byte) 3, (byte) 4 };
		final byte val1 = (byte) 0x10;
		final byte val2 = (byte) 0x20;
		ByteBufferWriter writer = ByteBufferWriter.of(BufferKit.wrap(src));
		writer.writeByte(val1);
		writer.writeByte(val2);
		Assertions.assertEquals(val1, src[0]);
		Assertions.assertEquals(val2, src[1]);
	}

}