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
public class Obj {

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
	 * 根据是否相同返回备选值
	 * @param original 原始值
	 * @param expected 期望值,用于和原始值进行比较
	 * @param alternative 备选值
	 * @param <T>
	 * @return 相同返回原始值,否则返回备选值
	 */
	@Nullable
	public <T> T keepIfEq(@Nullable T original, @Nullable T expected, @Nullable T alternative) {
		return Objects.equals(original, expected) ? original : alternative;
	}

	/**
	 * 根据是否相同返回备选值
	 * @param original 原始值
	 * @param expected 期望值,用于和原始值进行比较
	 * @param alternative 备选值
	 * @param <T>
	 * @return 不相同返回原始值,否则返回备选值
	 */
	@Nullable
	public <T> T keepIfNq(@Nullable T original, @Nullable T expected, @Nullable T alternative) {
		return Objects.equals(original, expected) ? original : alternative;
	}

	/**
	 * 根据是否为 {@code null} 返回备选值
	 * @param origin 原始值
	 * @param alternative 备选值
	 * @param <T>
	 * @return 原始值为 {@code null} 返回备选值,否则返回原始值
	 */
	public <T> T keepIfNotNull(@Nullable T origin, T alternative) {
		return Objects.isNull(origin) ? alternative : origin;
	}

	/**
	 * 根据断言返回备选值
	 * @param original 原始值
	 * @param predicate 断言
	 * @param alternative 备选值
	 * @param <T>
	 * @return 断言成功返回回原始值,否则返备选值
	 */
	@Nullable
	public <T> T keepIf(@Nullable T original, Predicate<T> predicate, @Nullable T alternative) {
		return predicate.test(original) ? original : alternative;
	}

}
