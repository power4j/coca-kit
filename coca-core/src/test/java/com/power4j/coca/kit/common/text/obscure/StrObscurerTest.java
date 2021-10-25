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

package com.power4j.coca.kit.common.text.obscure;

import com.power4j.coca.kit.common.io.buffer.BufferKit;
import com.power4j.coca.kit.common.io.codec.Codec;
import com.power4j.coca.kit.common.io.codec.CodecException;
import com.power4j.coca.kit.common.io.codec.impl.BufferGz;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/10/25
 * @since 1.0
 */
class StrObscurerTest {

	static class AppendX implements Codec<ByteBuffer, ByteBuffer> {

		private final static int PAD = 10;

		@Override
		public ByteBuffer decode(ByteBuffer src) throws CodecException {
			byte[] data = src.array();
			return BufferKit.copyToFit(data, data.length - PAD, 0);
		}

		@Override
		public String name() {
			return "append10";
		}

		@Override
		public ByteBuffer encode(ByteBuffer src) throws CodecException {
			byte[] data = src.array();
			return BufferKit.copyToFit(data, data.length + PAD, 0);
		}

	}

	@Test
	public void testObscurer() throws CodecException {
		String text = StrObscurerTest.class.getName();
		StrObscurer obscurer = StrObscurer.ofEncoders(Arrays.asList(new BufferGz(), new AppendX()));
		System.out.println("encode :");
		System.out.println(text);
		String enc = obscurer.obscure(text);
		System.out.println("result:");
		System.out.println(enc);
		String dec = obscurer.parse(enc);
		Assertions.assertEquals(text, dec);
	}

	@Test
	public void testObscurerWithSelector() throws CodecException {
		String text = "1234567890A";
		StrObscurer.EncoderSelector selector = (s, l) -> l.stream()
				.filter(enc -> enc.name().equals(BufferGz.NAME) && s.length() > 10).collect(Collectors.toList());
		StrObscurer obscurer = StrObscurer.ofEncoders(Arrays.asList(new BufferGz(), new AppendX()));
		System.out.println("encode :");
		System.out.println(text);
		String enc = obscurer.obscure(text, selector);
		System.out.println("result:");
		System.out.println(enc);
		Assertions.assertTrue(enc.startsWith(BufferGz.NAME));
		String dec = obscurer.parse(enc);
		Assertions.assertEquals(text, dec);
	}

}