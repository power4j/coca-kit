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
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public class AbstractBufferAccess {

	protected final static byte STR_END = 0x0;

	protected final static byte BYTE_ZERO = 0x0;

	protected final static int NOT_FOUND = -1;

	protected ByteBuffer buffer;

	/**
	 * 构造方法
	 * @param buffer buffer
	 * @param order 字节序,如果是null表示使用ByteBuffer默认字节序(BIG_ENDIAN)
	 */
	public AbstractBufferAccess(ByteBuffer buffer, @Nullable ByteOrder order) {
		this.buffer = buffer;
		if (Objects.nonNull(order)) {
			this.buffer.order(order);
		}
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public ByteBuffer getBufferCopy() {
		return buffer.duplicate();
	}

	// ~ util
	// ===================================================================================================

	public static int firstPos(byte[] src, byte val) {
		int pos = 0;
		while (pos < src.length) {
			if (src[pos++] == val) {
				return pos;
			}
		}
		return NOT_FOUND;
	}

	protected void expand(int newCapacity) {
		ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
		newBuffer.put(buffer);
		buffer = newBuffer;
	}

}
