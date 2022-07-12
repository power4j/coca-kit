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

import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public class ByteBufferReader extends AbstractBufferAccess
		implements BufferReader<ByteBufferReader>, ByteOrderAccess<ByteBufferReader> {

	public static ByteBufferReader of(ByteBuffer buffer, ByteOrder order) {
		return new ByteBufferReader(buffer, order);
	}

	public static ByteBufferReader of(ByteBuffer buffer) {
		return new ByteBufferReader(buffer, null);
	}

	/**
	 * 构造方法
	 * @param buffer ByteBuffer 对象
	 * @param order 字节序
	 */
	ByteBufferReader(ByteBuffer buffer, @Nullable ByteOrder order) {
		super(buffer.asReadOnlyBuffer(), order);
	}

	// ~ Reader
	// ===================================================================================================

	@Override
	public ByteOrder order() {
		return buffer.order();
	}

	@Override
	public ByteBufferReader order(ByteOrder order) {
		buffer.order(order);
		return this;
	}

	@Override
	public byte readByte() {
		return buffer.get();
	}

	@Override
	public char readChar() {
		return buffer.getChar();
	}

	@Override
	public byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	@Override
	public void transfer(byte[] dest, int offset, int length) {
		if (length > readableBytes()) {
			throw new BufferUnderflowException();
		}
		while (length-- > 0) {
			dest[offset++] = readByte();
		}
	}

	@Override
	public short readShort() {
		return buffer.getShort();
	}

	@Override
	public int readInt() {
		return buffer.getInt();
	}

	@Override
	public long readLong() {
		return buffer.getLong();
	}

	@Override
	public float readFloat() {
		return buffer.getFloat();
	}

	@Override
	public double readDouble() {
		return buffer.getDouble();
	}

	@Override
	public String readAsString(int length, Charset charset) {
		return readString(length, charset, false);
	}

	@Override
	public String readString(int length, Charset charset, boolean dropTail) {
		byte[] raw = readBytes(length);
		int pos;
		if (dropTail && (pos = firstPos(raw, STR_END)) != NOT_FOUND) {
			return new String(raw, 0, pos - 1, charset);
		}
		return new String(raw, charset);
	}

	@Override
	public String readUtf8String(int length) {
		return readString(length, StandardCharsets.UTF_8, true);
	}

	@Override
	public int readableBytes() {
		return buffer.remaining();
	}

	@Override
	public int hasRead() {
		return buffer.position();
	}

	@Override
	public ByteBufferReader unread(int length) {
		if (length > 0) {
			int pos = buffer.position() - length;
			buffer.position(Math.max(pos, 0));
		}
		return this;
	}

	@Override
	public ByteBufferReader unreadAll() {
		return unread(readableBytes());
	}

	@Override
	public ByteBufferReader skip(int length) {
		if (length > 0) {
			int pos = buffer.position() + length;
			buffer.position(Math.min(pos, buffer.limit()));
		}
		return this;
	}

	@Override
	public String dumpHex() {
		int size = readableBytes();
		if (size > 0) {
			return Hex.encodeHexString(readBytes(size));
		}
		return "";
	}

	@Override
	public String dumpBase64() {
		int size = readableBytes();
		if (size > 0) {
			return Base64.getEncoder().encodeToString(readBytes(size));
		}
		return "";
	}

	@Override
	protected void expand(int newCapacity) {
		throw new ReadOnlyBufferException();
	}

}
