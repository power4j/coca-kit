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

import lombok.experimental.UtilityClass;

/**
 * PKCS7/PKCS5 填充算法
 *
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/9
 * @since 1.0
 */
@UtilityClass
public class Pkcs7Padding {

	/**
	 * 数据对其并填充
	 * @param src 没有对齐的原始数据
	 * @param offset 偏移量
	 * @param length 长度
	 * @param blockSize 对齐的块大小,单位是字节.PKCS5 固定8,PKCS7 1~255
	 * @return 返回填充后的数据
	 */
	public byte[] padding(byte[] src, int offset, int length, int blockSize) {
		blockSize &= 0xFF;
		int mod;
		byte[] copy;
		if ((mod = length % blockSize) == 0) {
			copy = new byte[length + blockSize];
		}
		else {
			copy = new byte[length + blockSize - mod];
		}
		System.arraycopy(src, offset, copy, 0, length);
		fill(copy, src.length);
		return copy;
	}

	/**
	 * 数据对其并填充
	 * @param src 没有对齐的原始数据
	 * @param blockSize 对齐的块大小,单位是字节.PKCS5 固定8,PKCS7 1~255
	 * @return 返回填充后的数据
	 */
	public byte[] padding(byte[] src, int blockSize) {
		return padding(src, 0, src.length, blockSize);
	}

	/**
	 * 添加填充字符
	 * @param data 需要填充的数据,必须提前预留填充空间
	 * @param offset 填充偏移量
	 * @return 返回实际填充字节数
	 */
	public int fill(byte[] data, int offset) {
		byte code = (byte) (data.length - offset);

		while (offset < data.length) {
			data[offset] = code;
			offset++;
		}

		return code;
	}

	/**
	 * 计算填充字符的数量,只能处理本算法填充的数据
	 * @param data 包含填充字符的数据
	 * @return 填充字符的数量
	 * @throws IllegalArgumentException 输入数据不包含有效的填充数据
	 */
	public int calcPadCount(byte[] data) {
		int count = data[data.length - 1] & 0xff;

		boolean failed = (count > data.length | count == 0);

		for (int i = 0; i < data.length; i++) {
			failed |= (data.length - i <= count) & (data[i] != (byte) count);
		}

		if (failed) {
			throw new IllegalArgumentException("输入数据不包含有效的填充数据");
		}

		return count;
	}

}
