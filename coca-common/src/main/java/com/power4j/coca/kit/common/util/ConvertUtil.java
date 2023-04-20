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

package com.power4j.coca.kit.common.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/11/23
 * @since 1.0
 */
@UtilityClass
public class ConvertUtil {

	/**
	 * 转换为 List
	 * @param source 源
	 * @param converter 转换器
	 * @param <S> 源来信
	 * @param <R> 目标类型
	 * @return List<R>
	 */
	public <S, R> List<R> toList(Iterable<S> source, Function<? super S, ? extends R> converter) {
		return StreamSupport.stream(source.spliterator(), false).map(converter).collect(Collectors.toList());
	}

	/**
	 * 转换为 Set
	 * @param source 源
	 * @param converter 转换器
	 * @param <S> 源来信
	 * @param <R> 目标类型
	 * @return List<R>
	 */
	public <S, R> Set<R> toSet(Iterable<S> source, Function<? super S, ? extends R> converter) {
		return StreamSupport.stream(source.spliterator(), false).map(converter).collect(Collectors.toSet());
	}

	/**
	 * 转换为 Map
	 * @param source 源
	 * @param keyExtractor KEY 转换器
	 * @param valueExtractor VALUE 转换器
	 * @param <S> 源来信
	 * @param <K> key类型
	 * @param <V> value类型
	 * @return Map<K,V>
	 */
	public <S, K, V> Map<K, V> toMap(Iterable<S> source, Function<? super S, ? extends K> keyExtractor,
			Function<? super S, ? extends V> valueExtractor) {
		return StreamSupport.stream(source.spliterator(), false)
			.collect(Collectors.toMap(keyExtractor, valueExtractor));
	}

}
