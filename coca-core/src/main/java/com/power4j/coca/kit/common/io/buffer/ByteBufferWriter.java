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

import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public class ByteBufferWriter extends AbstractBufferAccess
		implements BufferWriter<ByteBufferWriter>, ByteOrderAccess<ByteBufferWriter> {

	public static ByteBufferWriter of(ByteBuffer buffer, ByteOrder order) {
		return new ByteBufferWriter(buffer, order);
	}

	public static ByteBufferWriter of(ByteBuffer buffer) {
		return new ByteBufferWriter(buffer, null);
	}

	/**
	 * 构造方法
	 * @param buffer buffer
	 * @param order 字节序
	 */
	ByteBufferWriter(ByteBuffer buffer, @Nullable ByteOrder order) {
		super(buffer, order);
	}

	// ~ Writer
	// ===================================================================================================

	@Override
	public ByteBufferWriter writeByte(byte b) {
		buffer.put(b);
		return this;
	}

	@Override
	public ByteBufferWriter writeByteRepeat(byte b, int count) {
		if (count < 0) {
			throw new IllegalArgumentException("count 不能小于0");
		}
		while (count-- > 0) {
			buffer.put(b);
		}
		return this;
	}

	@Override
	public ByteBufferWriter writeChar(char val) {
		buffer.putChar(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeBytes(byte[] src, int offset, int length) {
		buffer.put(src, offset, length);
		return this;
	}

	@Override
	public ByteBufferWriter writeShort(short val) {
		buffer.putShort(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeInt(int val) {
		buffer.putInt(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeLong(long val) {
		buffer.putLong(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeFloat(float val) {
		buffer.putFloat(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeDouble(double val) {
		buffer.putDouble(val);
		return this;
	}

	@Override
	public ByteBufferWriter writeString(String str, Charset charset, int fixedSize, byte filler) {
		byte[] raw = str.getBytes(charset);
		if (raw.length < fixedSize) {
			int fillPos = raw.length;
			raw = Arrays.copyOf(raw, fixedSize);
			if (filler != BYTE_ZERO) {
				while (fillPos < raw.length) {
					raw[fillPos++] = filler;
				}
			}
		}
		buffer.put(raw);
		return this;
	}

	@Override
	public ByteBufferWriter writeFixedString(String str, int fixedSize) {
		return writeString(str, StandardCharsets.UTF_8, fixedSize, BYTE_ZERO);
	}

	@Override
	public ByteBufferWriter discard(int length) {
		if (length > 0) {
			int result = Math.min(buffer.position(), length);
			buffer.position(buffer.position() - result);
		}
		return this;
	}

	@Override
	public ByteBufferWriter discardAll() {
		return discard(hasWritten());
	}

	@Override
	public ByteBufferWriter reserve(int length) {
		if (length > 0) {
			buffer.position(Math.min(buffer.position() + length, buffer.capacity()));
		}
		return this;
	}

	@Override
	public int hasWritten() {
		return buffer.position();
	}

	@Override
	public int writeableBytes() {
		return buffer.capacity() - buffer.position();
	}

	// ~ util
	// ===================================================================================================

	@Override
	public ByteOrder order() {
		return buffer.order();
	}

	@Override
	public ByteBufferWriter order(ByteOrder order) {
		buffer.order(order);
		return this;
	}

}
