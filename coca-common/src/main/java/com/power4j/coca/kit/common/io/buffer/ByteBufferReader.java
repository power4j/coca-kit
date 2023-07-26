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

import java.math.BigInteger;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public class ByteBufferReader {

	private static final BigInteger U64_MASK = BigInteger.ONE.shiftLeft(Long.SIZE).subtract(BigInteger.ONE);

	protected final static byte STR_END = 0x0;

	protected final static int NOT_FOUND = -1;

	protected ByteBuffer buffer;

	public static ByteBufferReader of(ByteBuffer buffer, @Nullable ByteOrder order) {
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
		this.buffer = buffer.asReadOnlyBuffer();
		if (Objects.nonNull(order)) {
			this.buffer.order(order);
		}
	}

	// ~ Reader
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
	 * @return T
	 */
	public ByteBufferReader order(ByteOrder order) {
		buffer.order(order);
		return this;
	}

	/**
	 * 读 char <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return byte 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public char readChar() {
		return buffer.getChar();
	}

	/**
	 * 读多个byte,并进行填充
	 * @param length 目标长度
	 * @param val 填充字节,可读数据不足时使用
	 * @param padLeft 是否在左边填充填字节,可读数据不足时使用
	 * @return byte 数组
	 * @throws BufferUnderflowException 无数据可读
	 */
	public byte[] readOrFill(int length, byte val, boolean padLeft) {
		int readable = readableBytes();
		if (length > readable) {
			byte[] bytes = new byte[length];
			if (padLeft) {
				Arrays.fill(bytes, 0, length - readable, val);
				transfer(bytes, length - readable, readable);
			}
			else {
				transfer(bytes, 0, readable);
				Arrays.fill(bytes, readable, length, val);
			}
			return bytes;
		}
		return readBytes(length);
	}

	/**
	 * 读多个byte
	 * @param size 字节数
	 * @return byte 数组
	 * @throws BufferUnderflowException 无数据可读
	 */
	public byte[] readBytes(int size) {
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		return bytes;
	}

	/**
	 * 读 byte
	 * @return byte 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public byte readByte() {
		return buffer.get();
	}

	/**
	 * 读 short
	 * @return short 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public short readShort() {
		return buffer.getShort();
	}

	/**
	 * 读 int
	 * @return int 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public int readInt() {
		return buffer.getInt();
	}

	/**
	 * 读 long
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public long readLong() {
		return buffer.getLong();
	}

	/**
	 * 读无符号整数
	 * @return byte 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public int readU8() {
		return Byte.toUnsignedInt(buffer.get());
	}

	/**
	 * 读无符号整数
	 * @return short 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public int readU16() {
		return Short.toUnsignedInt(buffer.getShort());
	}

	/**
	 * 读无符号整数
	 * @return int 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public long readU32() {
		return Integer.toUnsignedLong(buffer.getInt());
	}

	/**
	 * 读无符号整数
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public BigInteger readU64() {
		return BigInteger.valueOf(buffer.getLong()).and(U64_MASK);
	}

	/**
	 * 读 float <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public float readFloat() {
		return buffer.getFloat();
	}

	/**
	 * 读 double <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	public double readDouble() {
		return buffer.getDouble();
	}

	/**
	 * 读字符串
	 * @param length 最大字符长度
	 * @param charset 字符集
	 * @param dropTail 丢弃字符串结束符(0x0)以及之后的数据,用于处理C风格字符串
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 */
	public String readString(int length, Charset charset, boolean dropTail) {
		byte[] raw = readBytes(length);
		int pos;
		if (dropTail && (pos = findFirst(raw, STR_END)) != NOT_FOUND) {
			return new String(raw, 0, pos - 1, charset);
		}
		return new String(raw, charset);
	}

	/**
	 * readString 的简化版,使用 UTF8 字符集，并且丢弃字符串结束符(0x0)以及之后的数据
	 * @param length 最大字符长度
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 */
	public String readString(int length) {
		return readString(length, StandardCharsets.UTF_8, true);
	}

	/**
	 * 读Unicode字符串
	 * @param length 字节长度,必须是偶数
	 * @param charset unicode 字符集,如 {@code StandardCharsets.UTF_16BE}
	 * @param dropTail 丢弃字符串结束符(0x0000)以及之后的数据,用于处理C风格字符串
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 * @see StandardCharsets#UTF_16BE
	 * @see StandardCharsets#UTF_16LE
	 * @see StandardCharsets#UTF_16
	 */
	public String readUnicodeString(int length, Charset charset, boolean dropTail) {
		byte[] raw = readBytes(length);
		if (dropTail) {
			for (int i = 0; i < length; i += 2) {
				if (raw[i] == STR_END && raw[i + 1] == STR_END) {
					return new String(raw, 0, i, charset);
				}
			}
		}
		return new String(raw, charset);
	}

	/**
	 * 读多个byte到外部
	 * @param dest 写入目标
	 * @param offset dest的写入位置偏移量
	 * @param length 字节数
	 * @throws BufferUnderflowException 无数据可读
	 */
	public ByteBufferReader transfer(byte[] dest, int offset, int length) {
		if (length > readableBytes()) {
			throw new BufferUnderflowException();
		}
		while (length-- > 0) {
			dest[offset++] = readByte();
		}
		return this;
	}

	/**
	 * 读多个byte到外部
	 * @param dest 写入目标
	 * @throws BufferUnderflowException 无数据可读
	 */
	public ByteBufferReader transfer(byte[] dest) {
		return transfer(dest, 0, dest.length);
	}

	/**
	 * 可读取的数据量
	 * @return 字节数
	 */
	public int readableBytes() {
		return buffer.remaining();
	}

	/**
	 * 已经读取的数据量
	 * @return 字节数
	 */
	public int hasRead() {
		return buffer.position();
	}

	/**
	 * 回退读指针，可用于重复读取数据
	 * @param length 字节数,最大值 {@code hasRead()},超过忽略.小于等于0忽略
	 * @return T
	 */
	public ByteBufferReader unread(int length) {
		if (length > 0) {
			int pos = buffer.position() - length;
			buffer.position(Math.max(pos, 0));
		}
		return this;
	}

	/**
	 * 回退读指针，可用于重复读取数据
	 * @return T
	 */
	public ByteBufferReader unreadAll() {
		return unread(hasRead());
	}

	/**
	 * 跳过读取一部分数据
	 * @param length 字节数,最大值 {@code readableBytes()},超过忽略.小于等于0忽略
	 * @return T
	 */
	public ByteBufferReader skip(int length) {
		if (length > 0) {
			int pos = buffer.position() + length;
			buffer.position(Math.min(pos, buffer.limit()));
		}
		return this;
	}

	/**
	 * 将全部剩余数据读出,并且编码为HEX格式字符串
	 * @return 无数据可读返回空字符串
	 */
	public String dumpHex() {
		int size = readableBytes();
		if (size > 0) {
			return Hex.encodeHexString(readBytes(size));
		}
		return "";
	}

	/**
	 * 将全部剩余数据读出,并且编码为Base64格式字符串
	 * @return 无数据可读返回空字符串
	 */
	public String dumpBase64() {
		int size = readableBytes();
		if (size > 0) {
			return Base64.getEncoder().encodeToString(readBytes(size));
		}
		return "";
	}

	/**
	 * 访问内部Buffer
	 * @return ByteBuffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * 查找
	 * @param src 源
	 * @param val 需要查找的值
	 * @return 返回{@code val}第一次出现的索引,-1 表示不存在
	 */
	public static int findFirst(byte[] src, byte val) {
		int pos = 0;
		while (pos < src.length) {
			if (src[pos++] == val) {
				return pos;
			}
		}
		return NOT_FOUND;
	}

}
