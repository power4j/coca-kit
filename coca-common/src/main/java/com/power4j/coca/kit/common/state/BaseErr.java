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

import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class BaseErr<T extends Comparable<T>> implements Err<T> {

	private final T code;

	private final String message;

	protected BaseErr(T code, @Nullable String message) {
		this.code = Objects.requireNonNull(code);
		this.message = message == null ? "ok" : message;
	}

	@Override
	public T getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String display() {
		return code + " - " + message;
	}

}
