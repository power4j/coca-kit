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

import com.power4j.coca.kit.common.text.Display;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * container to store binary data
 *
 * @author cj
 * @date 2019/3/26
 * @since 1.0
 */
public class ByteData implements Display {

	private static final byte[] EMPTY_BUFFER = new byte[0];

	private byte[] buffer;

	private int writeIndex;

	protected ByteData(int capacity) {
		buffer = capacity == 0 ? EMPTY_BUFFER : new byte[capacity];
		writeIndex = 0;
	}

	protected ByteData(byte[] src, int offset, int length) {
		buffer = Arrays.copyOfRange(src, offset, offset + length);
		writeIndex = length;
	}

	protected ByteData(byte[] buffer, int writeIndex) {
		this.buffer = buffer;
		this.writeIndex = writeIndex;
	}

	// ~ static method
	// ===================================================================================================

	/**
	 * 创建BinData对象
	 * @param capacity 初始容量
	 * @return BinData对象
	 */
	public static ByteData ofCapacity(int capacity) {
		return new ByteData(capacity);
	}

	/**
	 * 创建BinData对象
	 * @return 新的BinData对象
	 */
	public static ByteData ofEmpty() {
		return ByteData.ofCapacity(0);
	}

	/**
	 * 创建BinData对象
	 * @param bytes 源数据
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 新的BinData对象,包含源数据的拷贝
	 */
	public static ByteData copyOf(byte[] bytes, int offset, int length) {
		return new ByteData(bytes, offset, length);
	}

	/**
	 * 创建BinData对象
	 * @param bytes 源数据
	 * @return 新的BinData对象,包含源数据的拷贝
	 */
	public static ByteData copyOf(byte[] bytes) {
		return copyOf(bytes, 0, bytes.length);
	}

	/**
	 * 创建BinData对象
	 * @param byteData 源数据
	 * @return 新的BinData对象
	 */
	public static ByteData copyOf(ByteData byteData) {
		return copyOf(byteData.buffer, 0, byteData.writeIndex);
	}

	/**
	 * 创建BinData对象,用固定值填充
	 * @param val byte 填充值
	 * @param count 数量
	 * @return 新的BinData对象
	 */
	public static ByteData ofRepeat(int val, int count) {
		ByteData byteData = ByteData.ofCapacity(count);
		Arrays.fill(byteData.buffer, 0, count, (byte) val);
		byteData.writeIndex(count);
		return byteData;
	}

	/**
	 * 创建BinData对象,用随机值填充
	 * @param size 数量
	 * @return 新的BinData对象
	 */
	public static ByteData ofRandom(int size) {
		ByteData byteData = ByteData.ofCapacity(size);
		new Random().nextBytes(byteData.buffer);
		byteData.writeIndex(size);
		return byteData;
	}

	/**
	 * 创建BinData对象
	 * @param hexString 16进制字符串,长度必须是偶数
	 * @return 新的BinData对象
	 * @throws IllegalArgumentException hexString不是16进制字符串
	 */
	public static ByteData ofHex(String hexString) {
		try {
			byte[] bytes = Hex.decodeHex(hexString);
			return new ByteData(bytes, bytes.length);
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException("not hex string", e);
		}
	}

	/**
	 * 创建BinData对象
	 * @param base64String BASE64
	 * @return ByteData
	 * @throws IllegalArgumentException base64String 不是base64 编码的字符串
	 */
	public static ByteData ofBase64(String base64String) {
		byte[] bytes = Base64.getDecoder().decode(base64String);
		return new ByteData(bytes, bytes.length);
	}

	// ~ status control
	// ===================================================================================================

	/**
	 * 内部buffer
	 * @return 内部buffer
	 */
	public byte[] buffer() {
		return buffer;
	}

	/**
	 * 容量
	 * @return 返回当前容量
	 */
	public int capacity() {
		return buffer.length;
	}

	/**
	 * 当前写指针
	 * @return 当前写指针位置
	 */
	public int writeIndex() {
		return writeIndex;
	}

	/**
	 * 重置当前写指针位置
	 * @param value 位置
	 * @throws IndexOutOfBoundsException 写指针位置超过上下限
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeIndex(int value) {
		if (value > buffer.length || value < 0) {
			throw new IndexOutOfBoundsException("write index overflow");
		}
		writeIndex = value;
		return this;
	}

	/**
	 * 写指针移动
	 * @param distance 距离,负数向前移动,正数向后移动
	 * @throws IndexOutOfBoundsException 写指针位置超过上下限
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeIndexAdvance(int distance) {
		writeIndex(writeIndex + distance);
		return this;
	}

	/**
	 * 写指针移动
	 * @param max 最大距离,负数向前移动,正数向后移动
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeIndexAdvanceSome(int max) {
		int value = writeIndex + max;
		if (value < 0) {
			value = 0;
		}
		if (value > buffer.length) {
			value = buffer.length;
		}
		writeIndex(value);
		return this;
	}

	/**
	 * 调整容量,并调整写指针使其不会越界
	 * @param count 增量,负数缩容,正数扩容
	 * @return 返回当前ByteData对象
	 */
	public ByteData expandBy(int count) {
		int capacity = buffer.length;
		int newCapacity = capacity + count;
		if (newCapacity <= 0) {
			buffer = EMPTY_BUFFER;
			writeIndex = 0;
			return this;
		}
		if (newCapacity != capacity) {
			buffer = Arrays.copyOf(buffer, newCapacity);
			writeIndex = Math.min(writeIndex, newCapacity);
		}
		return this;
	}

	/**
	 * 自动扩展容量
	 * @return 返回当前ByteData对象
	 */
	public ByteData expand() {
		expandBy(Math.max(1, buffer.length >> 1));
		return this;
	}

	/**
	 * 保证可写容量
	 * @param size 数据量
	 * @return 返回当前ByteData对象
	 */
	public ByteData ensureWriteBytes(int size) {
		int more = size - writableBytes();
		if (more > 0) {
			expandBy(more);
		}
		return this;
	}

	/**
	 * 缓冲区清零
	 * @return 返回当前ByteData对象
	 */
	public ByteData zeroMemory() {
		Arrays.fill(buffer, (byte) 0);
		return this;
	}

	/**
	 * 可读数据的长度
	 * @return 可读数据的字节数
	 */
	public int readableBytes() {
		return readableBytes(0);
	}

	/**
	 * 可读数据的长度
	 * @param offset 偏移量
	 * @return 可读数据的字节数
	 */
	public int readableBytes(int offset) {
		if (offset >= writeIndex) {
			return 0;
		}
		return writeIndex - offset;
	}

	/**
	 * 可写容量
	 * @return 可读数据的字节数
	 */
	public int writableBytes() {
		return buffer.length - writeIndex;
	}

	// ~ read operation
	// ===================================================================================================

	/**
	 * 读出可读数据
	 * @param offset 可读数据偏移量 {@code [0,writeIndex)}
	 * @param count 长度 {@code [0,readableBytes(offset))},负数表示全部可读数据
	 * @return 返回数据拷贝,如果读偏移量大于等于写指针,返回空数组
	 * @throws IndexOutOfBoundsException offset < 0
	 * @throws IllegalArgumentException 超过最大可读数据
	 */
	public byte[] read(int offset, int count) {
		assertReadPos(offset);
		if (count < 0) {
			count = readableBytes(offset);
		}
		assertReadable(offset, count);
		return Arrays.copyOfRange(buffer, offset, offset + count);
	}

	/**
	 * 读出可读数据
	 * @param count 长度 {@code [0,readableBytes())},负数表示全部可读数据
	 * @return 数据拷贝
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public byte[] read(int count) {
		return read(0, count);
	}

	/**
	 * 读出可读数据
	 * @return 数据拷贝
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public byte[] readAll() {
		return read(0, readableBytes());
	}

	/**
	 * 读出可读数据
	 * @param offset 可读数据偏移量 {@code [0,writeIndex)}
	 * @param maxCount 长度上限
	 * @return 数据拷贝
	 * @throws IndexOutOfBoundsException 偏移量越界
	 * @throws IllegalArgumentException {@code maxCount} 小于0
	 */
	public byte[] readSome(int offset, int maxCount) {
		assertReadPos(offset);
		if (maxCount < 0) {
			throw new IllegalArgumentException("max count < 0");
		}
		return Arrays.copyOfRange(buffer, offset, Math.min(offset + maxCount, writeIndex));
	}

	/**
	 * 读出可读数据
	 * @param maxCount 长度上限
	 * @return 数据拷贝
	 * @throws IndexOutOfBoundsException 偏移量越界,{@code maxCount} 小于0
	 */
	public byte[] readSome(int maxCount) {
		return readSome(0, maxCount);
	}

	/**
	 * 将可读数据作为字符串读取
	 * @param offset 偏移量
	 * @param length 长度,负数表示全部可读数据
	 * @param charset 字符集
	 * @return String
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public String readString(int offset, int length, Charset charset) {
		assertReadPos(offset);
		if (length < 0) {
			length = readableBytes(offset);
		}
		assertReadable(offset, length);
		return new String(buffer, offset, length, charset);
	}

	/**
	 * 将可读数据作为字符串读取
	 * @param offset 偏移量
	 * @param length 长度,负数表示全部可读数据
	 * @return String
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public String readUtf8String(int offset, int length) {
		return readString(offset, length, StandardCharsets.UTF_8);
	}

	/**
	 * 读出可读数据,并编码为HEX字符串
	 * @param offset 偏移量
	 * @param length 长度,负数表示全部可读数据
	 * @return 小写16进制字符串
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public String readHexLower(int offset, int length) {
		return Hex.encodeHexString(read(offset, length), true);
	}

	/**
	 * 读出可读数据,并编码为HEX字符串
	 * @param offset 偏移量
	 * @param length 长度,负数表示全部可读数据
	 * @return 大写16进制字符串
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public String readHexUpper(int offset, int length) {
		return Hex.encodeHexString(read(offset, length), false);
	}

	/**
	 * 读出可读数据,并编码为BASE64字符串
	 * @param offset 偏移量
	 * @param length 长度,负数表示全部可读数据
	 * @return 大写16进制字符串
	 * @throws IndexOutOfBoundsException 可读数据访问越界
	 */
	public String readBase64(int offset, int length) {
		return Base64.getEncoder().encodeToString(read(offset, length));
	}

	/**
	 * 读出可读数据,写入目标数组.数据不足用填充值进行填充
	 * @param offset 读区域偏移
	 * @param dest 目标区域
	 * @param destOffset 目标的偏移量
	 * @param length 填充长度
	 * @param padding 填充值,以byte截断
	 * @return 返回填值padding的填充次数
	 * @throws IndexOutOfBoundsException 可读数据访问越界,目标目标区域访问越界,填充长度小于0
	 */
	public int readTo(int offset, byte[] dest, int destOffset, int length, int padding) {
		assertReadPos(offset);
		int readable = readableBytes(offset);
		int count = 0;
		for (; count < length; ++count) {
			dest[destOffset + count] = (count < readable) ? buffer[offset + count] : (byte) padding;
		}
		return Math.max(0, length - readable);
	}

	/**
	 * 读出可读数据,写入目标数组.数据不足用0进行填充
	 * @param offset 读区域偏移
	 * @param dest 目标数组
	 * @param padding 填充值,以byte截断
	 * @return 返回填值padding的填充次数
	 * @throws IndexOutOfBoundsException 可读数据访问越界,目标目标区域访问越界,填充长度 小于0
	 */
	public int readTo(int offset, byte[] dest, int padding) {
		return readTo(offset, dest, 0, dest.length, padding);
	}

	// ~ Write operation
	// ===================================================================================================

	/**
	 * 写入数据
	 * @param src 源数据
	 * @return 返回当前对象
	 */
	public ByteData write(ByteData src) {
		return writeBytes(src.buffer, 0, src.writeIndex);
	}

	/**
	 * 写入数据
	 * @param b byte值
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeInt8(int b) {
		ensureWriteBytes(1);
		buffer[writeIndex++] = (byte) b;
		return this;
	}

	/**
	 * 写入数据
	 * @param value short 值
	 * @param order 字节序
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeInt16(int value, ByteOrder order) {
		final int size = Short.BYTES;
		ensureWriteBytes(size);
		bufferWriter(order).writeShort((short) value);
		writeIndexAdvance(size);
		return this;
	}

	/**
	 * 写入数据
	 * @param value int 值
	 * @param order 字节序
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeInt32(int value, ByteOrder order) {
		final int size = Integer.BYTES;
		ensureWriteBytes(size);
		bufferWriter(order).writeInt(value);
		writeIndexAdvance(size);
		return this;
	}

	/**
	 * 写入数据
	 * @param value long 值
	 * @param order 字节序
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeInt64(long value, ByteOrder order) {
		final int size = Long.BYTES;
		ensureWriteBytes(size);
		bufferWriter(order).writeLong(value);
		writeIndexAdvance(size);
		return this;
	}

	/**
	 * 写入数据
	 * @param value long 值
	 * @param order 字节序
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeFloat(float value, ByteOrder order) {
		final int size = Float.BYTES;
		ensureWriteBytes(size);
		bufferWriter(order).writeFloat(value);
		writeIndexAdvance(size);
		return this;
	}

	/**
	 * 写入数据
	 * @param value long 值
	 * @param order 字节序
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeDouble(double value, ByteOrder order) {
		final int size = Double.BYTES;
		ensureWriteBytes(size);
		bufferWriter(order).writeDouble(value);
		writeIndexAdvance(size);
		return this;
	}

	/**
	 * 写入数据
	 * @param src 源数据
	 * @param offset 偏移量
	 * @param length 长度
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeBytes(byte[] src, int offset, int length) {
		expandBy(length);
		for (int i = 0; i < length; ++i) {
			buffer[writeIndex++] = src[offset + i];
		}
		return this;
	}

	/**
	 * 写入数据
	 * @param src 源数据
	 * @return 返回当前ByteData对象
	 */
	public ByteData writeBytes(byte[] src) {
		return writeBytes(src, 0, src.length);
	}

	// ~ Misc
	// ===================================================================================================

	/**
	 * 比较可读数据是否完全相同(数量和每一个元素)
	 * <p>
	 * <b>注意：两个空的BinData比较结果是相同</b>
	 * </p>
	 * @param that 比较对象
	 * @return 返回true表示包含相同数据
	 */
	public boolean dataEquals(ByteData that) {
		return dataEquals(that.read(-1));
	}

	/**
	 * 比较可读数据是否完全相同(数量和每一个元素)
	 * <p>
	 * <b>注意：两个空数组的比较结果是相同</b>
	 * </p>
	 * @param data 对比数据
	 * @return 返回true表示包含相同数据
	 */
	public boolean dataEquals(@Nullable byte[] data) {
		return Arrays.equals(read(-1), data);
	}

	/**
	 * 将可写区域委托给 {@code ByteBufferWriter},注意:写入操作不会同步内部写指针
	 * @param order 字节序
	 * @return ByteBufferWriter
	 * @see ByteBufferWriter
	 */
	public ByteBufferWriter bufferWriter(ByteOrder order) {
		return ByteBufferWriter.of(ByteBuffer.wrap(buffer, writeIndex, writableBytes()), order);
	}

	/**
	 * 将可读区域委托给 {@code ByteBufferReader}
	 * @param order 字节序
	 * @return ByteBufferReader
	 * @see ByteBufferReader
	 */
	public ByteBufferReader bufferReader(ByteOrder order) {
		return ByteBufferReader.of(ByteBuffer.wrap(buffer, 0, readableBytes()), order);
	}

	@Override
	public String toString() {
		return readHexLower(0, readableBytes());
	}

	@Override
	public String display() {
		return String.format("[%d]%s", readableBytes(), readHexLower(0, readableBytes()));
	}

	// ~ Internal
	// ===================================================================================================

	protected void assertReadPos(int pos) {
		if (pos < 0) {
			throw new IndexOutOfBoundsException("Index out of range: " + pos);
		}
	}

	protected void assertReadable(int pos, int length) {
		final int endPos = pos + length;
		if (endPos > writeIndex) {
			throw new IndexOutOfBoundsException("Index out of range: " + endPos);
		}
	}

}
