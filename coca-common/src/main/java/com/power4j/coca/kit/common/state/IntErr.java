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

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
public class IntErr extends BaseErrKind<String, Integer> {

	public IntErr(String kind, int code, @Nullable String message) {
		super(kind, code, message);
	}

	public static IntErr of(String kind, int code, @Nullable String message) {
		return new IntErr(kind, code, message);
	}

	public static IntErr of(int code, @Nullable String message) {
		return new IntErr(ErrKind.NO_KIND, code, message);
	}

	@Override
	public String display() {
		return String.format("[kind %s,error %d] - %s", getKind(), getCode(), getMessage());
	}

}
