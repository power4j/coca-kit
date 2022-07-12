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

package com.power4j.coca.kit.common.io.codec.impl;

import com.power4j.coca.kit.common.compress.CompressUtil;
import com.power4j.coca.kit.common.io.codec.Codec;
import com.power4j.coca.kit.common.io.codec.CodecException;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/10/25
 * @since 1.0
 */
public class BufferGz implements Codec<ByteBuffer, ByteBuffer> {

	public final static String NAME = "gz";

	@Override
	public String name() {
		return NAME;
	}

	@Override
	public ByteBuffer decode(ByteBuffer src) throws CodecException {
		try {
			byte[] data = CompressUtil.unGzip(src.array());
			return ByteBuffer.wrap(data);
		}
		catch (IOException e) {
			throw new CodecException(e.getMessage(), e);
		}
	}

	@Override
	public ByteBuffer encode(ByteBuffer src) throws CodecException {
		try {
			byte[] data = CompressUtil.gzip(src.array());
			return ByteBuffer.wrap(data);
		}
		catch (IOException e) {
			throw new CodecException(e.getMessage(), e);
		}
	}

}
