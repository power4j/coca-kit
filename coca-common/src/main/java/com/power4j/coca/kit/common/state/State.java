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
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 工具类,处理返回值和错误传播
 * <p>
 * 用法参考 <a href='https://doc.rust-lang.org/std/result/' >Rust std::result</a> 或者
 * <a href='https://github.com/antitypical/Result' > antitypical/Result</a>
 *
 * @author CJ (power4j@outlook.com)
 * @since 2022.1
 * @param <T> 返回值类型
 * @param <E> 错误类型
 */
public final class State<T, E> {

	@Nullable
	private final T value;

	@Nullable
	private final E error;

	/**
	 * 成功,可能有值
	 * @param data 返回值
	 * @return State 对象
	 * @param <T> 返回值类型
	 */
	public static <T, E> State<T, E> ok(@Nullable T data) {
		return new State<>(data, null);
	}

	/**
	 * 错误
	 * @param err 错误信息
	 * @return State 对象
	 * @param <E> 错误类型
	 */
	public static <T, E> State<T, E> error(E err) {
		return new State<>(null, Objects.requireNonNull(err));
	}

	State(@Nullable T value, @Nullable E error) {
		this.value = value;
		this.error = error;
	}

	/**
	 * 检查是否为一个错误
	 * @return 返回true表示错误
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * 如果是错误,再对错误进行检查
	 * @param predicate 断言函数
	 * @return 返回true表示是错误,并且断言成功
	 */
	public boolean isErrorAnd(Predicate<? super E> predicate) {
		return isError() && predicate.test(error);
	}

	/**
	 * 检查不是一个错误
	 * @return 返回true表示错误
	 */
	public boolean isOk() {
		return !isError();
	}

	/**
	 * 如果不是错误,再对返回值进行检查
	 * @param predicate 断言函数
	 * @return 返回true表示不是错误,并且断言成功
	 */
	public boolean isOkAnd(Predicate<? super T> predicate) {
		return isOk() && predicate.test(value);
	}

	/**
	 * 获取返回值
	 * @return T 原始返回值
	 * @throws IllegalStateException 如果没有返回值则抛出异常
	 * @see State#isError()
	 * @see State#unwrapOrElse(Supplier)
	 */
	@Nullable
	public T unwrap() {
		if (isError()) {
			throw new IllegalStateException("no value object");
		}
		return value;
	}

	/**
	 * 获取返回值
	 * @param defVal 默认值
	 * @return T 如果不是错误则返回原始值,否则返回默认值
	 * @see State#isError()
	 */
	@Nullable
	public T unwrapOr(T defVal) {
		if (isError()) {
			return defVal;
		}
		return value;
	}

	/**
	 * 获取返回值
	 * @param supplier 函数
	 * @return T 如果不是错误则返回原始值,否则由supplier提供返回值
	 */
	@Nullable
	public T unwrapOrElse(Supplier<T> supplier) {
		if (isError()) {
			return supplier.get();
		}
		return value;
	}

	/**
	 * 获取错误值
	 * @return E
	 * @throws IllegalStateException 如果没有返回值则抛出异常
	 * @see State#tryUnwrapError()
	 */
	public E unwrapError() {
		return tryUnwrapError().orElseThrow(() -> new IllegalStateException("not an error"));
	}

	/**
	 * 尝试获取错误值
	 * @return T 如果没有返回值则返回 empty
	 * @see State#isError()
	 *
	 */
	public Optional<E> tryUnwrapError() {
		if (isError()) {
			return Optional.of(error);
		}
		return Optional.empty();
	}

	/**
	 * {@code State<T,E>} 转换为 {@code State<U,E>}
	 * @param func 转换函数
	 * @return State
	 * @param <U> 新的值类型
	 */
	public <U> State<U, E> map(Function<? super T, ? extends U> func) {
		if (isOk()) {
			return State.ok(func.apply(value));
		}
		assert error != null;
		return State.error(error);
	}

	/**
	 * {@code State<T,E>} 转换为 {@code State<T,F>}
	 * @param func 转换函数
	 * @return State
	 * @param <F> 新的错误类型
	 */
	public <F> State<T, F> mapError(Function<? super E, ? extends F> func) {
		if (isOk()) {
			return State.ok(value);
		}
		return State.error(func.apply(error));
	}

	/**
	 * 如果不是错误,则将T转换为U并返回,否则返回默认值 <pre>
	 *     Assertions.assertEquals(1,State.ok(1).mapOr(Function.identity(),2));
	 *     Assertions.assertEquals(2,State.error("ERR").mapOr(Function.identity(),2));
	 * </pre>
	 * @param func 值转换函数
	 * @param defVal 默认值
	 * @return T
	 *
	 */
	@Nullable
	public <U> U mapOr(Function<? super T, ? extends U> func, U defVal) {
		if (isOk()) {
			return func.apply(value);
		}
		return defVal;
	}

	/**
	 * 如果不是错误,则将T转换为U并返回,否则返回计算值 <pre>
	 *     Assertions.assertEquals(1,State.error("ERR").mapOrElse(Function.identity(),() -> 1));
	 *     Assertions.assertEquals(123,State.ok(123).mapOrElse(Function.identity(),() -> 1));
	 * </pre>
	 * @param func 值转换函数
	 * @param supplier 默认值函数
	 * @return T
	 * @see State#isError()
	 *
	 */
	@Nullable
	public <U> U mapOrElse(Function<? super T, ? extends U> func, Supplier<? extends U> supplier) {
		if (isOk()) {
			return func.apply(value);
		}
		return supplier.get();
	}

	/**
	 * Stated对象"与"操作 <pre>
	 *     Assertions.assertEquals(123,State.ok("aaa").and(State.ok(123)).unwrap());
	 *     Assertions.assertEquals("aaa",State.ok(123).and(State.ok("aaa")).unwrap());
	 * </pre>
	 * @param state 具有相同错误类型的State对象
	 * @return State<U,E>
	 */
	public <U> State<U, E> and(State<U, E> state) {
		if (isError()) {
			return State.error(error);
		}
		else {
			return state;
		}
	}

	/**
	 * Stated函数"与"操作 <pre>
	 *     Assertions.assertEquals(123,State.ok("aaa").andThen(o -> State.ok(123)).unwrap());
	 *     Assertions.assertEquals("aaa",State.ok(123).andThen(o -> State.ok("aaa")).unwrap());
	 * </pre>
	 * @param func 转换函数
	 * @return 返回新的 State
	 */
	public <U> State<U, E> andThen(Function<? super T, ? extends State<U, E>> func) {
		if (isError()) {
			return State.error(error);
		}
		else {
			return func.apply(value);
		}
	}

	/**
	 * Stated对象"或"操作 <pre>
	 *     Assertions.assertEquals(1,State.error("xx").or(State.ok(1)).unwrap());
	 *     Assertions.assertEquals("x",State.ok("x").or(State.ok("y")).unwrap());
	 * </pre>
	 * @param state 具有不同错误类型的State对象
	 * @return State<T,F>
	 */
	public <F> State<T, F> or(State<T, F> state) {
		if (isError()) {
			return state;
		}
		else {
			return State.ok(value);
		}
	}

	/**
	 * Stated函数"或"操作 <pre>
	 *     Assertions.assertTrue(State.error("xx").orElse(e -> State.ok(1)).isOk());
	 *     Assertions.assertTrue(State.error("xx").orElse(e -> State.error(1)).isError());
	 *     Assertions.assertTrue(State.ok(1).orElse(e -> State.ok(1)).isOk());
	 *     Assertions.assertTrue(State.ok(1).orElse(e -> State.error(1)).isOk());
	 * </pre>
	 * @param func 转换函数,如果未出错则执行
	 * @return 返回新的 State
	 */
	public <F> State<T, F> orElse(Function<? super E, ? extends State<T, F>> func) {
		if (isError()) {
			return func.apply(error);
		}
		else {
			return State.ok(value);
		}
	}

	/**
	 * 嵌套State平坦化 <pre>
	 *     Assertions.assertEquals(1,State.flatten(State.ok(State.ok(1))).unwrap());
	 * </pre>
	 * @param state 嵌套State对象
	 * @return 返回普通State对象
	 * @param <T> 值类型
	 * @param <E> 错误类型
	 */
	public static <T, E> State<T, E> flatten(State<? extends State<T, E>, E> state) {
		return state.map(State::unwrap);
	}

}
