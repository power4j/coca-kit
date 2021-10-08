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

	private final Map<S, T> rules;

	public static <S, T> Builder<S, T> builder() {
		return new Builder<>();
	}

	ValueMapper(Map<S, T> rules) {
		this.rules = rules;
	}

	public T getValue(@Nullable S src, T defaultValue) {
		if (Objects.isNull(src)) {
			return defaultValue;
		}
		return rules.getOrDefault(src, defaultValue);
	}

	public static class Builder<S, T> {

		private final Map<S, T> ruleMap = new HashMap<>();

		public Builder<S, T> rule(S src, T target) {
			ruleMap.put(src, target);
			return this;
		}

		public Builder<S, T> rules(Collection<Pair<S, T>> rules) {
			for (Pair<S, T> pair : rules) {
				ruleMap.put(Objects.requireNonNull(pair.getKey()), Objects.requireNonNull(pair.getValue()));
			}
			return this;
		}

		public ValueMapper<S, T> build() {
			return new ValueMapper<>(ruleMap);
		}

	}

}
