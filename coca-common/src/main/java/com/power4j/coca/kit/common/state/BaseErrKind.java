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

package com.power4j.coca.kit.common.state;

import com.power4j.coca.kit.common.text.Display;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class BaseErrKind<K extends Comparable<K>, T extends Comparable<T>> extends BaseErr<T> implements ErrKind<K, T> {

	private final K kind;

	protected BaseErrKind(K kind, T code, @Nullable String message) {
		super(code, message);
		this.kind = Objects.requireNonNull(kind);
	}

	@Override
	public K getKind() {
		return kind;
	}

	@Override
	public String display() {
		Object kindValue = kind;
		if (kind instanceof Display) {
			kindValue = ((Display) kind).display();
		}
		return String.format("[%s] - %s", kindValue, super.display());
	}

}
