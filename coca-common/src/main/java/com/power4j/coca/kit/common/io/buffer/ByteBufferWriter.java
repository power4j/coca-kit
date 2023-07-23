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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public class ByteBufferWriter {

	protected final static byte BYTE_ZERO = 0x0;

	protected ByteBuffer buffer;

	public static ByteBufferWriter of(ByteBuffer buffer, @Nullable ByteOrder order) {
		return new ByteBufferWriter(buffer, order);
	}

	public static ByteBufferWriter of(ByteBuffer buffer) {
		return new ByteBufferWriter(buffer, null);
	}

	public static ByteBufferWriter of(byte[] src, int offset, int length) {
		return new ByteBufferWriter(BufferKit.wrap(src, offset, length), null);
	}

	public static ByteBufferWriter of(byte[] src) {
		return new ByteBufferWriter(BufferKit.wrap(src, 0, src.length), null);
	}

	/**
	 * 构造方法
	 * @param buffer buffer
	 * @param order 字节序
	 */
	ByteBufferWriter(ByteBuffer buffer, @Nullable ByteOrder order) {
		this.buffer = buffer;
		if (Objects.nonNull(order)) {
			this.buffer.order(order);
		}
	}

	// ~ Writer
	// ===================================================================================================

	/**
	 * 写入 byte
	 * @param b byte
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeByte(int b) {
		buffer.put((byte) b);
		return this;
	}

	/**
	 * 重复写入 byte
	 * @param b byte
	 * @param count 数量
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeByteRepeat(int b, int count) {
		if (count < 0) {
			throw new IllegalArgumentException("count 不能小于0");
		}
		while (count-- > 0) {
			buffer.put((byte) b);
		}
		return this;
	}

	/**
	 * 写入 char <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val char
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeChar(int val) {
		buffer.putChar((char) val);
		return this;
	}

	/**
	 * 写入byte 数组
	 * @param src byte 数组
	 * @param offset 偏移量
	 * @param length 长度
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeBytes(byte[] src, int offset, int length) {
		buffer.put(src, offset, length);
		return this;
	}

	/**
	 * 写入byte 数组
	 * @param src byte 数组
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeBytes(byte[] src) {
		buffer.put(src, 0, src.length);
		return this;
	}

	/**
	 * 写入 short <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val 写入的值
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeShort(int val) {
		buffer.putShort((short) val);
		return this;
	}

	/**
	 * 写入 int <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val 写入的值
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeInt(int val) {
		buffer.putInt(val);
		return this;
	}

	/**
	 * 写入 long <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val long
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeLong(long val) {
		buffer.putLong(val);
		return this;
	}

	/**
	 * 写入 float <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val float
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeFloat(float val) {
		buffer.putFloat(val);
		return this;
	}

	/**
	 * 写入 double <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val double
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeDouble(double val) {
		buffer.putDouble(val);
		return this;
	}

	/**
	 * 写入字符串
	 * @param str 需要写入的字符串
	 * @param charset 字符集
	 * @param length 填充长度,表示数据截断或者填充到指定长度,小于0 表示根据{@code str}实际长度写入
	 * @param filler 填充值,填充时会用到, 否则无意义
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeString(String str, Charset charset, int length, byte filler) {
		byte[] raw = str.getBytes(charset);
		if (length < 0) {
			length = raw.length;
		}
		for (int i = 0; i < length; ++i) {
			if (i < raw.length) {
				buffer.put(raw[i]);
			}
			else {
				buffer.put(filler);
			}
		}
		return this;
	}

	/**
	 * writeString 的简化版,使用 UTF8 字符集，并且使用定长长度修正
	 * @param str 需要写入的字符串
	 * @param fixedSize 定长长度
	 * @return ByteBufferWriter
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	public ByteBufferWriter writeFixedString(String str, int fixedSize) {
		return writeString(str, StandardCharsets.UTF_8, fixedSize, BYTE_ZERO);
	}

	/**
	 * 丢弃已经写入的数据
	 * @param length 字节数,最多 {@code hasWritten},超过取下限,小于等于0无效
	 * @return ByteBufferWriter
	 */
	public ByteBufferWriter discard(int length) {
		if (length > 0) {
			int result = Math.min(buffer.position(), length);
			buffer.position(buffer.position() - result);
		}
		return this;
	}

	/**
	 * 丢弃全部已经写入的数据
	 * @return ByteBufferWriter
	 */
	public ByteBufferWriter discardAll() {
		return discard(hasWritten());
	}

	/**
	 * 移动写指针,使得下一次写操作跳过N字节
	 * @param length 字节数,最多 {@code writeableBytes},超过取下限,小于等于0无效
	 * @return ByteBufferWriter
	 */
	public ByteBufferWriter reserve(int length) {
		if (length > 0) {
			buffer.position(Math.min(buffer.position() + length, buffer.capacity()));
		}
		return this;
	}

	/**
	 * 可读取的数据量
	 * @return 字节数
	 */
	public int hasWritten() {
		return buffer.position();
	}

	/**
	 * 可写入的数据量
	 * @return 字节数
	 */
	public int writeableBytes() {
		return buffer.capacity() - buffer.position();
	}

	// ~ misc
	// ===================================================================================================

	/**
	 * 获取当前使用的字节序
	 * @return ByteOrder
	 */
	public ByteOrder order() {
		return buffer.order();
	}

	/**
	 * 设置字节序
	 * @param order ByteOrder
	 * @return ByteBufferWriter
	 */
	public ByteBufferWriter order(ByteOrder order) {
		buffer.order(order);
		return this;
	}

	/**
	 * 访问内部Buffer
	 * @return ByteBuffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * 查看值
	 * @param pos 位置,从0开始
	 * @return 返回内部缓冲区上指定位置的值
	 */
	public byte peekAt(int pos) {
		return buffer.array()[pos];
	}

	/**
	 * 查看值
	 * @param pos 位置,从0开始
	 * @param size 数量,字节数
	 * @return 返回内部缓冲区上指定区域的值
	 */
	public byte[] peekRange(int pos, int size) {
		return Arrays.copyOfRange(buffer.array(), pos, pos + size);
	}

}
