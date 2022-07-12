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

package com.power4j.coca.kit.jna.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Random;

/**
 * @author cj
 * @date 2019/3/26
 * @since 1.0
 */
public class ByteData {

	private static final byte ZERO_BYTE = 0;

	private byte[] buffer;

	/**
	 * PKCS7 打包,可能会内部导致数据发生改变
	 * @param blockSize 块大小
	 * @return 返回新的BinData
	 */
	public static ByteData packPkcs7Block(ByteData byteData, final int blockSize) {
		return ByteData.fromBytes(Pkcs7Padding.padding(byteData.buffer, blockSize));
	}

	/**
	 * PKCS7 解包
	 * @param blockSize 块大小
	 * @return 返回新的BinData
	 */
	public static ByteData unpackPkcs7Block(ByteData byteData, final int blockSize) {
		final byte[] data = byteData.buffer;
		return ByteData.fromBytes(data, 0, data.length - Pkcs7Padding.calcPadCount(data));
	}

	public static ByteData emptyData() {
		return new ByteData(new byte[0]);
	}

	/**
	 * 创建BinData对象,用固定值填充
	 * @param val byte 值
	 * @param size 数量
	 * @return BinData
	 */
	public static ByteData repeat(byte val, int size) {
		byte[] data = new byte[size];
		Arrays.fill(data, 0, size, val);
		return ByteData.fromBytes(data);
	}

	/**
	 * 创建BinData对象,用随机值填充
	 * @param size 数量
	 * @return BinData
	 */
	public static ByteData random(int size) {
		byte[] bytes = new byte[size];
		new Random().nextBytes(bytes);
		return ByteData.fromBytes(bytes);
	}

	/**
	 * 创建BinData对象
	 * @param byteData
	 * @return BinData
	 */
	public static ByteData copyOf(ByteData byteData) {
		return fromBytes(byteData.buffer, 0, byteData.buffer.length);
	}

	/**
	 * 创建BinData对象
	 * @param bytes
	 * @return BinData
	 */
	public static ByteData fromBytes(byte[] bytes) {
		return fromBytes(bytes, 0, bytes.length);
	}

	/**
	 * 创建BinData对象
	 * @param byteOrder
	 * @param val
	 * @return BinData
	 */
	public static ByteData fromShort(ByteOrder byteOrder, short... val) {
		ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES * val.length);
		buffer.order(byteOrder);
		for (short n : val) {
			buffer.putShort(n);
		}
		return ByteData.fromBytes(buffer.array());
	}

	/**
	 * 创建BinData对象
	 * @param byteOrder
	 * @param val
	 * @return
	 */
	public static ByteData fromInt(ByteOrder byteOrder, int... val) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * val.length);
		buffer.order(byteOrder);
		for (int n : val) {
			buffer.putInt(n);
		}
		return ByteData.fromBytes(buffer.array());
	}

	/**
	 * 创建BinData对象
	 * @param byteOrder
	 * @param val
	 * @return
	 */
	public static ByteData fromLong(ByteOrder byteOrder, long... val) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * val.length);
		buffer.order(byteOrder);
		for (long n : val) {
			buffer.putLong(n);
		}
		return ByteData.fromBytes(buffer.array());
	}

	/**
	 * 创建BinData对象
	 * @param bytes
	 * @param offset
	 * @param length
	 * @return
	 */
	public static ByteData fromBytes(byte[] bytes, int offset, int length) {
		return new ByteData(bytes, offset, length);
	}

	/**
	 * 创建BinData对象
	 * @param hexString
	 * @return
	 * @throws IllegalArgumentException hexString不是16进制字符串
	 */
	public static ByteData formHex(String hexString) {
		try {
			return new ByteData(Hex.decodeHex(hexString));
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 创建BinData对象
	 * @param base64String
	 * @return
	 * @throws IllegalArgumentException base64String 不是base64 编码的字符串
	 */
	public static ByteData formBase64(String base64String) {
		return ByteData.fromBytes(Base64.getDecoder().decode(base64String));
	}

	public int size() {
		return buffer.length;
	}

	public ByteData(byte[] data) {
		this.buffer = Arrays.copyOfRange(data, 0, data.length);
	}

	public ByteData(byte[] data, int offset, int length) {
		this.buffer = Arrays.copyOfRange(data, offset, offset + length);
	}

	public byte[] getDataCopy() {
		return getDataCopy(-1);
	}

	/**
	 * 获取数据拷贝
	 * @param maxSize
	 * @return
	 */
	public byte[] getDataCopy(int maxSize) {
		if (maxSize < 0) {
			maxSize = buffer.length;
		}
		return Arrays.copyOfRange(buffer, 0, Math.min(buffer.length, maxSize));
	}

	public String getAsString(int offset, int length, Charset charset) {
		return new String(buffer, offset, length, charset);
	}

	public String getAsUtf8String(int offset, int length) {
		return getAsString(offset, length, StandardCharsets.UTF_8);
	}

	public void setData(byte[] data) {
		this.buffer = data;
	}

	public String toHex() {
		return (buffer != null && buffer.length > 0) ? HexStrUtil.encodeHex(buffer) : "";
	}

	public String toBase64() {
		return (buffer != null && buffer.length > 0) ? Base64.getEncoder().encodeToString(buffer) : "";
	}

	public ByteData append(ByteData byteData) {
		return appendBytes(byteData.getDataCopy());
	}

	public ByteData appendByte(byte b) {
		grow(1);
		buffer[buffer.length - 1] = b;
		return this;
	}

	/**
	 * 向外部传输数据， 数据不足用填充值进行填充
	 * @param dest 目标数组
	 * @param offset 目标数组的偏移量
	 * @param len 传输的字节数
	 * @param filler 填充值
	 * @return 返回实际传输的字节数
	 */
	public int transferToFit(byte[] dest, int offset, int len, byte filler) {
		int count = 0;
		for (; count < len; ++count) {
			dest[offset + count] = (count < size()) ? buffer[count] : filler;
		}
		return count;
	}

	/**
	 * 向外部传输数据，数据不足用0进行填充
	 * @param dest 目标数组
	 * @return 返回实际传输的字节数
	 */
	public int transferToFit(byte[] dest) {
		return transferToFit(dest, 0, dest.length, ZERO_BYTE);
	}

	/**
	 * 向外部传输数据， 最多不超过指定的数据量
	 * @param dest 目标数组
	 * @param offset 目标数组的偏移量
	 * @param maxLen 最大传输的字节数
	 * @return 返回实际传输的字节数
	 */
	public int transferSome(byte[] dest, int offset, int maxLen) {
		return transferToFit(dest, offset, Math.min(maxLen, size()), ZERO_BYTE);
	}

	/**
	 * 向外部传输数据，最多不超过目标数组的容量
	 * @param dest 目标数组
	 * @return 返回实际传输的字节数
	 */
	public int transferSome(byte[] dest) {
		return transferSome(dest, 0, dest.length);
	}

	public ByteData appendBytes(byte[] bytes) {
		int writeIndex = buffer.length;
		final int copySize = bytes.length;
		grow(copySize);
		for (byte b : bytes) {
			buffer[writeIndex++] = b;
		}
		return this;
	}

	public ByteData appendBytes(Collection<Byte> bytes) {
		int writeIndex = buffer.length;
		final int copySize = bytes.size();
		grow(copySize);
		for (byte b : bytes) {
			buffer[writeIndex++] = b;
		}
		return this;
	}

	/**
	 * 扩展容量
	 * @param size 大小（增加的)
	 */
	private void grow(int size) {
		if (size > 0) {
			byte[] buffNew = new byte[buffer.length + size];
			System.arraycopy(buffer, 0, buffNew, 0, buffer.length);
			buffer = buffNew;
		}
	}

	public void erase() {
		if (buffer.length > 0) {
			Arrays.fill(buffer, (byte) 0x0);
		}
	}

	/**
	 * 比较数据是否完全相同(数量和每一个元素)
	 * <p>
	 * <b>注意：两个空的BinData比较结果是相同</b>
	 * </p>
	 * @param that
	 * @return
	 */
	public boolean compareData(ByteData that) {
		return compareData(that.buffer);
	}

	/**
	 * 比较数据是否完全相同(数量和每一个元素)
	 * <p>
	 * <b>注意：两个空数组的比较结果是相同</b>
	 * </p>
	 * @param data
	 * @return
	 */
	public boolean compareData(byte[] data) {
		return Arrays.equals(buffer, data);
	}

	@Override
	public String toString() {
		return toHex();
	}

}
