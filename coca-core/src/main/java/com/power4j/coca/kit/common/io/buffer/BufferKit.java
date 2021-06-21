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

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import lombok.experimental.UtilityClass;

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
	 * 从HEX字符串创建
	 * @param hexStr
	 * @return ByteBuffer
	 */
	public ByteBuffer fromHex(String hexStr) {
		return wrap(HexUtil.decodeHex(hexStr));
	}

	/**
	 * 从BASE64字符串创建
	 * @param base64Str
	 * @return ByteBuffer
	 */
	public ByteBuffer fromBase64(String base64Str) {
		return wrap(Base64.decode(base64Str));
	}

}
