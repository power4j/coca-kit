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

package com.power4j.coca.kit.common.lang;

import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/7/1
 * @since 1.0
 * @param <S> source type
 * @param <T> target type
 */
public class ValueMapper<S, T> {

	@Nullable
	private Map<S, T> rules;

	public static <S, T> ValueMapper<S, T> of(Collection<Pair<S, T>> rules) {
		ValueMapper<S, T> valueMapper = new ValueMapper<>();
		for (Pair<S, T> pair : rules) {
			valueMapper.rule(pair.getKey(), pair.getValue());
		}
		return valueMapper;
	}

	public void rule(@Nullable S src, @Nullable T target) {
		if (Objects.isNull(rules)) {
			rules = new HashMap<>(2);
		}
		rules.put(src, target);
	}

	@Nullable
	public T map(@Nullable S src, @Nullable T defaultValue) {
		if (Objects.isNull(rules) || rules.isEmpty()) {
			return defaultValue;
		}
		return rules.get(src);
	}

}
