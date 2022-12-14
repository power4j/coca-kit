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

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/11
 * @since 1.0
 */
@UtilityClass
public class BufferKit {

	/**
	 * 创建buffer
	 * @param length 长度
	 * @return ByteBuffer
	 */
	public ByteBuffer allocate(int length) {
		return ByteBuffer.allocate(length);
	}

	/**
	 * 包装字节数组,与该数组共享元素
	 * @param buf 数据源
	 * @param offset 读偏移
	 * @param length 长度
	 * @return ByteBuffer
	 */
	public ByteBuffer wrap(byte[] buf, int offset, int length) {
		return ByteBuffer.wrap(buf, offset, length);
	}

	/**
	 * 包装字节数组,与该数组共享元素
	 * @param buf 数据源
	 * @return ByteBuffer
	 */
	public ByteBuffer wrap(byte[] buf) {
		return wrap(buf, 0, buf.length);
	}

	/**
	 * 复制数据
	 * @param buf 数据源
	 * @param offset 读偏移
	 * @param length 长度
	 * @return ByteBuffer
	 */
	public ByteBuffer copyFrom(byte[] buf, int offset, int length) {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		buffer.put(buf, offset, length);
		return buffer;
	}

	/**
	 * 复制数据
	 * @param buf 数据源
	 * @return ByteBuffer
	 */
	public ByteBuffer copyFrom(byte[] buf) {
		return copyFrom(buf, 0, buf.length);
	}

	/**
	 * 复制数据
	 * @param buf 数据源
	 * @param size 目标长度,大于buf.length 截断,小于则使用filler填充
	 * @param filler 填充值
	 * @return 返回新的ByteBuffer
	 */
	public ByteBuffer copyToFit(byte[] buf, int size, int filler) {
		if (size <= buf.length) {
			return copyFrom(buf, 0, size);
		}
		ByteBuffer buffer = allocate(size);
		buffer.put(buf);
		for (int i = buf.length; i < size; ++i) {
			buffer.put((byte) filler);
		}
		return buffer;
	}

	/**
	 * 从HEX字符串创建
	 * @param hexStr
	 * @return ByteBuffer
	 * @throws IllegalArgumentException 输入的字符串不是HEX编码
	 */
	public ByteBuffer fromHex(String hexStr) {
		try {
			return wrap(Hex.decodeHex(hexStr));
		}
		catch (DecoderException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * 从BASE64字符串创建
	 * @param base64Str
	 * @return ByteBuffer
	 */
	public ByteBuffer fromBase64(String base64Str) {
		return wrap(Base64.decodeBase64(base64Str));
	}

}
