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
import java.util.function.Supplier;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/17
 * @since 1.0
 */
@UtilityClass
public class Obj {

	/**
	 * 类型转换
	 * @param obj 对象实例
	 * @param <T> 目标类型
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T> T cast(Object obj) {
		return (T) obj;
	}

	/**
	 * 根据是否相同返回备选值
	 * @param original 原始值
	 * @param expected 期望值,用于和原始值进行比较
	 * @param supplier 备选值
	 * @param <T>
	 * @return 相同返回原始值,否则返回备选值
	 */
	@Nullable
	public <T> T keepIfEq(@Nullable T original, @Nullable T expected, Supplier<T> supplier) {
		return Objects.equals(original, expected) ? original : supplier.get();
	}

	/**
	 * 根据是否相同返回备选值
	 * @param original 原始值
	 * @param expected 期望值,用于和原始值进行比较
	 * @param supplier 备选值
	 * @param <T>
	 * @return 不相同返回原始值,否则返回备选值
	 */
	@Nullable
	public <T> T keepIfNq(@Nullable T original, @Nullable T expected, Supplier<T> supplier) {
		return Objects.equals(original, expected) ? original : supplier.get();
	}

	/**
	 * 根据是否为 {@code null} 返回备选值
	 * @param origin 原始值
	 * @param supplier 备选值
	 * @param <T>
	 * @return 原始值为 {@code null} 返回备选值,否则返回原始值
	 */
	public <T> T keepIfNotNull(@Nullable T origin, Supplier<T> supplier) {
		return Objects.isNull(origin) ? supplier.get() : origin;
	}

	/**
	 * 根据断言返回备选值
	 * @param original 原始值
	 * @param predicate 断言
	 * @param supplier 备选值
	 * @param <T>
	 * @return 断言成功返回回原始值,否则返备选值
	 */
	@Nullable
	public <T> T keepIf(@Nullable T original, Predicate<T> predicate, Supplier<T> supplier) {
		return predicate.test(original) ? original : supplier.get();
	}

}
