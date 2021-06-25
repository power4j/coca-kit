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

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/24
 * @since 1.0
 */
public class Pair<K, V> {

	@Nullable
	private K key;

	@Nullable
	private V value;

	public static <K, V> Pair<K, V> of(@Nullable K key, @Nullable V value) {
		Pair<K, V> pair = new Pair<>();
		pair.setKey(key);
		pair.setValue(value);
		return pair;
	}

	@Nullable
	public K getKey() {
		return key;
	}

	public void setKey(@Nullable K key) {
		this.key = key;
	}

	@Nullable
	public V getValue() {
		return value;
	}

	public void setValue(@Nullable V value) {
		this.value = value;
	}

}
