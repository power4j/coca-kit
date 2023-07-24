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

import com.power4j.coca.kit.common.io.buffer.ByteData;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

/**
 * PKCS7/PKCS5 填充算法 <ui>
 * <li>PKCS7块大小为 1~255</li>
 * <li>PKCS5是PKCS7的子集,块大小固定为8</li> </ui>
 *
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/9
 * @since 1.0
 */
@UtilityClass
public class PkcsPadding {

	private static final int U8_MAX = 0XFF;

	// ~ High level API
	// ===================================================================================================

	/**
	 * PKCS填充
	 * @param src 原始数据
	 * @param offset 偏移量
	 * @param length 长度
	 * @param blockSize 对齐的块大小,单位是字节.PKCS5 固定8,PKCS7 1~255
	 * @return 返回填充后的数据拷贝
	 * @throws IllegalArgumentException length < 0 或者 blockSize非法
	 */
	public byte[] paddingCopy(byte[] src, int offset, int length, int blockSize) {
		int extra = paddingCount(length, blockSize);
		byte[] data = Arrays.copyOfRange(src, offset, offset + length + extra);
		Arrays.fill(data, length, length + extra, (byte) extra);
		return data;
	}

	/**
	 * PKCS填充
	 * @param src 原始数据
	 * @param blockSize 对齐的块大小,单位是字节.PKCS5 固定8,PKCS7 1~255
	 * @return 返回填充后的数据拷贝
	 * @throws IllegalArgumentException blockSize非法
	 */
	public byte[] paddingCopy(byte[] src, int blockSize) {
		return paddingCopy(src, 0, src.length, blockSize);
	}

	/**
	 * PKCS填充,可能会引起扩容
	 * @param blockSize 块大小
	 * @return 返回新的BinData
	 */
	public ByteData appendPadding(ByteData byteData, final int blockSize) {
		return byteData.writeBytes(makePaddingBytes(byteData.readableBytes(), blockSize));
	}

	/**
	 * PKCS移除填充
	 * @param byteData 经过算法填充的数据
	 * @return 返回新的BinData
	 * @throws IllegalArgumentException 输入数据不包含有效的填充数据
	 */
	public ByteData dropPadding(ByteData byteData) {
		int count = extractPaddingSize(byteData.buffer(), 0, byteData.writeIndex());
		byteData.writeIndexAdvance(-count);
		return byteData;
	}

	// ~ Low level API
	// ===================================================================================================

	/**
	 * 计算需要额外填充的字节数
	 * @param length 源数据
	 * @param blockSize 对齐的块大小,单位是字节.1~255
	 * @return 返回填充的字节数
	 * @throws IllegalArgumentException length < 0 或者 blockSize非法
	 */
	public int paddingCount(int length, int blockSize) {
		if (blockSize < 1 || blockSize > U8_MAX) {
			throw new IllegalArgumentException("invalid block size:" + blockSize);
		}
		int mod = length % blockSize;
		return mod == 0 ? blockSize : (blockSize - mod);
	}

	/**
	 * 产生对齐所需的填充数据
	 * @param length 源数据长度
	 * @param blockSize 对齐的块大小,单位是字节.1~255
	 * @return 填充数据
	 * @throws IllegalArgumentException length < 0 或者 blockSize非法
	 */
	public byte[] makePaddingBytes(int length, int blockSize) {
		byte[] padding = new byte[paddingCount(length, blockSize)];
		Arrays.fill(padding, (byte) padding.length);
		return padding;
	}

	/**
	 * 计算填充数据的长度
	 * @param src 已经填充源数据
	 * @param offset 起始偏移量
	 * @param length 长度
	 * @return 返回填充长度,同同时也是填充值
	 * @throws IllegalArgumentException 输入数据不包含有效的填充数据
	 */
	public int extractPaddingSize(byte[] src, int offset, int length) {
		byte pad = src[offset + length - 1];
		if (pad < 0) {
			throw new IllegalArgumentException("invalid padding data");
		}
		for (byte i = 1; i <= pad; ++i) {
			if (src[offset + length - i] != pad) {
				throw new IllegalArgumentException("invalid padding data");
			}
		}
		return pad;
	}

	/**
	 * 计算填充数据的长度
	 * @param src 已经填充源数据
	 * @return 返回填充长度,同同时也是填充值
	 * @throws IllegalArgumentException 输入数据不包含有效的填充数据
	 */
	public int extractPaddingSize(byte[] src) {
		return extractPaddingSize(src, 0, src.length);
	}

}
