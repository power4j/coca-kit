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
import org.springframework.lang.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/17
 * @since 1.0
 */
@UtilityClass
public class Guard {

	/**
	 * 根据断言返回备选值
	 * @param original 原始值
	 * @param predicate 断言
	 * @param alternative 备选值
	 * @param <T>
	 * @return 断言成功返回原始值,否则返回备选值
	 */
	@Nullable
	public <T> T keep(@Nullable T original, Predicate<T> predicate, @Nullable T alternative) {
		return predicate.test(original) ? original : alternative;
	}

	/**
	 * 根据断言返回备选值
	 * @param original 原始值
	 * @param predicate 断言
	 * @param alternative 备选值
	 * @param <T>
	 * @return 断言成功返回备选值,否则返回原始值
	 */
	@Nullable
	public <T> T fallback(@Nullable T original, Predicate<T> predicate, @Nullable T alternative) {
		return predicate.test(original) ? alternative : original;
	}

	public <T> T nonNull(@Nullable T origin, T other) {
		return Objects.isNull(origin) ? other : origin;
	}

}
