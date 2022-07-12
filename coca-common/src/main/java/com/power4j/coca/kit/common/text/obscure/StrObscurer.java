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

import com.power4j.coca.kit.common.io.codec.Codec;
import com.power4j.coca.kit.common.io.codec.CodecException;
import com.power4j.coca.kit.common.io.codec.Decoder;
import com.power4j.coca.kit.common.io.codec.Encoder;
import com.power4j.coca.kit.common.io.codec.impl.Base64Codec;
import com.power4j.coca.kit.common.lang.Pair;
import com.power4j.coca.kit.common.text.StringPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/10/22
 * @since 1.0
 */
public class StrObscurer {

	public interface EncoderSelector {

		EncoderSelector ALL = (s, list) -> list;

		/**
		 * 选择编码器,以及顺序
		 * @param target 编码对象
		 * @param encoders 可以支持的原始编码器
		 * @return 返回编码器列表
		 */
		List<Encoder<ByteBuffer, ByteBuffer>> select(String target, List<Encoder<ByteBuffer, ByteBuffer>> encoders);

	}

	final static String FLAG_SEPARATOR = StringPool.COMMA;

	final static String BODY_SEPARATOR = StringPool.SPACE;

	private final static Charset CHARSET = StandardCharsets.UTF_8;

	private String flagPrefix = StringPool.PLUS;

	private Codec<ByteBuffer, String> strCodec = new Base64Codec();

	private Map<String, Codec<ByteBuffer, ByteBuffer>> codecRegistry = Collections.emptyMap();

	public static StrObscurer ofEncoders(List<Codec<ByteBuffer, ByteBuffer>> codecs) {
		StrObscurer obscurer = new StrObscurer();
		obscurer.setCodecList(codecs);
		return obscurer;
	}

	/**
	 * 字符串混淆
	 * @param str 原始字符串
	 * @param selector EncoderSelector
	 * @return 返回混淆后的字符串
	 * @throws CodecException 编码失败
	 */
	public String obscure(String str, EncoderSelector selector) throws CodecException {
		List<Encoder<ByteBuffer, ByteBuffer>> encoders = selector.select(str, new ArrayList<>(codecRegistry.values()));
		ByteBuffer buffer = ByteBuffer.wrap(str.getBytes(CHARSET));
		for (Encoder<ByteBuffer, ByteBuffer> encoder : encoders) {
			buffer = encoder.encode(buffer);
		}
		return buildHeader(encoders) + strCodec.encode(buffer);
	}

	/**
	 * 字符串混淆
	 * @param str 原始字符串
	 * @return 返回混淆后的字符串
	 * @throws CodecException 编码失败
	 */
	public String obscure(String str) throws CodecException {
		return obscure(str, EncoderSelector.ALL);
	}

	/**
	 * 字符串解析
	 * @param str 经过混淆的字符串
	 * @return 返回原始字符串
	 * @throws CodecException 编码失败
	 */
	public String parse(String str) throws CodecException {
		Pair<List<String>, String> flagAndBody = parseFlagAndBody(str);
		List<String> flags = Objects.requireNonNull(flagAndBody.getKey());
		ByteBuffer raw = strCodec.decode(flagAndBody.getValue());
		if (!flags.isEmpty()) {
			raw = restoreData(flags, raw);
		}
		return new String(raw.array(), CHARSET);
	}

	public void setStrCodec(Codec<ByteBuffer, String> strCodec) {
		this.strCodec = strCodec;
	}

	/**
	 * 设置算法标记前缀,默认是加号
	 * @param flagPrefix 前缀字符串,建议控制在2个字符内
	 */
	public void setFlagPrefix(String flagPrefix) {
		this.flagPrefix = flagPrefix;
	}

	public void setCodecList(List<Codec<ByteBuffer, ByteBuffer>> codecs) {
		Map<String, Codec<ByteBuffer, ByteBuffer>> map = new TreeMap<>();
		codecs.forEach(o -> map.put(o.name(), o));
		this.codecRegistry = map;
	}

	protected ByteBuffer restoreData(List<String> flags, ByteBuffer buffer) throws CodecException {
		flags = new ArrayList<>(flags);
		Collections.reverse(flags);
		for (String name : flags) {
			Decoder<ByteBuffer, ByteBuffer> decoder = codecRegistry.get(name);
			if (Objects.isNull(decoder)) {
				throw new CodecException("Missing decoder :" + name);
			}
			buffer = decoder.decode(buffer);
		}
		return buffer;
	}

	String encodeFlag(Encoder<?, ?> encoder) {
		return flagPrefix + encoder.name();
	}

	String extractFlag(String value) {
		return StringUtils.removeStart(value, flagPrefix);
	}

	String buildHeader(@Nullable List<? extends Encoder<?, ?>> encoders) {
		if (Objects.isNull(encoders) || encoders.isEmpty()) {
			return StringPool.EMPTY;
		}
		String header = encoders.stream().map(this::encodeFlag).collect(Collectors.joining(FLAG_SEPARATOR));
		return header + BODY_SEPARATOR;
	}

	Pair<List<String>, String> parseFlagAndBody(String input) {
		final int piece = 2;
		String[] flagAndBody = StringUtils.split(input, BODY_SEPARATOR, piece);
		if (flagAndBody.length >= piece) {
			List<String> flags = Stream.of(StringUtils.split(flagAndBody[0], FLAG_SEPARATOR)).map(this::extractFlag)
					.collect(Collectors.toList());
			return Pair.of(flags, flagAndBody[1]);
		}
		return Pair.of(Collections.emptyList(), flagAndBody[0]);
	}

}
