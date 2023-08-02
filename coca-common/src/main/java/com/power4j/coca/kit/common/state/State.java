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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
public final class State<T, E> implements Display {

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
	 * 检查是否为一个错误 <pre>
	 *     State.error(1).isError()    -> true
	 *     State.ok(null).isError()    -> false
	 * </pre>
	 * @return 返回true表示错误
	 */
	public boolean isError() {
		return error != null;
	}

	/**
	 * 如果是错误,再对错误进行检查 <pre>
	 *     State.error(-1).isErrorAnd(err -> err == -1)   -> true
	 *     State.error(-1).isErrorAnd(err -> err == 0 )   -> false
	 *     State.ok(null).isErrorAnd(err -> true)         -> false, predicate not used
	 * </pre>
	 * @param predicate 断言函数
	 * @return 返回true表示是错误,并且断言成功
	 */
	public boolean isErrorAnd(Predicate<? super E> predicate) {
		return isError() && predicate.test(error);
	}

	/**
	 * 检查不是一个错误 <pre>
	 *     State.error(1).isOk()       -> false
	 *     State.ok(null).isOk()       -> true
	 *     State.ok(1).isOk()          -> true
	 * </pre>
	 * @return 返回true表示错误
	 */
	public boolean isOk() {
		return !isError();
	}

	/**
	 * 如果不是错误,再对返回值进行检查 <pre>
	 *     State.ok(-1).isOkAnd(val -> val == -1)       -> true
	 *     State.ok(-1).isOkAnd(val -> val == 0 )       -> false
	 *     State.error(-1).isOkAnd(val -> true)         -> false, predicate not used
	 * </pre>
	 * @param predicate 断言函数
	 * @return 返回true表示不是错误,并且断言成功
	 */
	public boolean isOkAnd(Predicate<? super T> predicate) {
		return isOk() && predicate.test(value);
	}

	/**
	 * 获取返回值 <pre>
	 *     State.ok(-1).unwrap()         -> -1
	 *     State.ok(null).unwrap()       -> null
	 *     State.error(-1).unwrap()      -> IllegalStateException
	 * </pre>
	 * @return T 原始返回值
	 * @throws IllegalStateException 如果没有返回值则抛出异常
	 * @see State#isError()
	 * @see State#unwrapOrElse(Supplier)
	 */
	@Nullable
	public T unwrap() {
		if (isError()) {
			throw new IllegalStateException("State is error");
		}
		return value;
	}

	/**
	 * 获取返回值,并且断言非值不是{@code null} <pre>
	 *     State.ok("1").unwrapNonNull()    -> '1'
	 *     State.error(-1).unwrap()         -> IllegalStateException
	 *     State.ok(null).unwrapNonNull()   -> NullPointerException
	 * </pre>
	 * @return T 原始返回值
	 * @throws IllegalStateException 如果没有返回值则抛出异常
	 * @throws NullPointerException 有值,但是值为null
	 * @see State#isError()
	 * @see State#unwrapOrElse(Supplier)
	 */
	public T unwrapNonNull() {
		final T val = unwrap();
		if (val == null) {
			throw new NullPointerException("value is null");
		}
		return val;
	}

	/**
	 * 获取返回值 <pre>
	 *     State.ok(1).unwrapOr(2)             -> 1
	 *     State.error(-1).unwrapOr(2)         -> 2
	 * </pre>
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
	 * 获取返回值 <pre>
	 *     State.ok(1).unwrapOr(() -> 2)             -> 1
	 *     State.error(-1).unwrapOr(() -> 2)         -> 2
	 * </pre>
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
	 * 获取错误值 <pre>
	 *     State.error(-1).unwrapError()         -> -1
	 *     State.ok(null).unwrapError()          -> IllegalStateException
	 * </pre>
	 * @return E
	 * @throws IllegalStateException 如果没有返回值则抛出异常
	 * @see State#tryUnwrapError()
	 */
	public E unwrapError() {
		return tryUnwrapError().orElseThrow(() -> new IllegalStateException("not an error"));
	}

	/**
	 * 尝试获取错误值 <pre>
	 *     State.error(-1).tryUnwrapError()         -> Optional(-1)
	 *     State.ok(null).tryUnwrapError()          -> Optional(empty)
	 * </pre>
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
	 * {@code State<T,E>} 转换为 {@code State<U,E>} <pre>
	 *     State.ok(1).map(val -> "val "+ val)         -> State.ok("val 1")
	 *     State.error(1).map(val -> "val "+ val)      -> State.error(1)
	 * </pre>
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
	 * {@code State<T,E>} 转换为 {@code State<T,F>} <pre>
	 *     State.error(1).mapError(err -> "error "+ err)      -> State.error("error 1")
	 *     State.ok(1).mapError(err -> "error "+ err)         -> State.ok(1)
	 * </pre>
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
	 *     State.ok(1).mapOr(val -> val + 1,-1)           -> 2
	 *     State.ok(1).mapOr(val -> val + 10,-1)          -> 11
	 *     State.error(1).mapOr(val -> val + 1,-1)        -> -1 ,fallback to default value
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
	 *     State.ok(1).mapOr(val -> val + 1, () -> -1)          -> 2
	 *     State.ok(1).mapOr(val -> val + 10,() -> -1)          -> 11
	 *     State.error(1).mapOr(val -> val + 1,() -> -1)        -> -1 ,fallback to supplier
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
	 *     State.ok(1).and(State.ok("1"))           -> State.ok("1"), use second
	 *     State.ok(1).and(State.error("1"))        -> State.error("1"), use second
	 *     State.error(1).and(State.ok("1"))        -> State.error(1), original error
	 *     State.error(1).and(State.error("1"))     -> State.error(1) original error
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
	 *     State.ok(1).andThen(val -> State.ok("1"))           -> State.ok("1")
	 *     State.ok(1).andThen(val -> State.error("1"))        -> State.error("1")
	 *     State.error(1).andThen(val -> State.ok("1"))        -> State.error(1), original error
	 *     State.error(1).andThen(val -> State.error("1"))     -> State.error(1), original error
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
	 *     State.error(1).or(State.ok("1"))           -> State.ok("1"), use second
	 *     State.error(1).or(State.error("1"))        -> State.error("1"), use second
	 *     State.ok(1).or(State.ok("1"))              -> State.ok(1), original ok
	 *     State.ok(1).or(State.error("1"))           -> State.ok(1) original ok
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
	 *     State.error(1).orElse(err -> State.ok("1"))           -> State.ok("1")
	 *     State.error(1).orElse(err -> State.error(""))         -> State.error("")
	 *     State.ok(1).orElse(err -> State.ok("1"))              -> State.ok(1), original ok
	 *     State.ok(1).orElse(err -> State.error(""))            -> State.ok(1) original ok
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
	 * 移除一层嵌套State <pre>
	 *     State.flatten(State.ok(State.ok(1)))             ->  State.ok(1)
	 *     State.flatten(State.ok(State.error(1)))          ->  State.error(1)
	 *     State.flatten(State.ok(State.ok(State.ok(1))))   ->  State.ok(State.ok(1))
	 *     State.flatten(State.error(1))                    ->  State.error(1)
	 * </pre>
	 * @param state 嵌套State对象
	 * @return 返回内层State对象
	 * @param <T> 值类型
	 * @param <E> 错误类型
	 */
	public static <T, E> State<T, E> flatten(State<? extends State<T, E>, E> state) {
		return state.andThen(Function.identity());
	}

	@Override
	public String display() {
		if (isOk()) {
			if (value == null) {
				return "Ok(null)";
			}
			else {
				if (value instanceof Optional && !((Optional<?>) value).isPresent()) {
					return "Ok(wrapped null)";
				}
				return "Ok";
			}
		}
		return "Error(" + error + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		State<?, ?> state = (State<?, ?>) o;
		if (state.isError() && isError()) {
			return new EqualsBuilder().append(error, state.error).isEquals();
		}
		else if (state.isOk() && isOk()) {
			return new EqualsBuilder().append(value, state.value).isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (isError()) {
			return new HashCodeBuilder(17, 37).append(error).toHashCode();
		}
		else {
			return new HashCodeBuilder(17, 37).append(value).toHashCode();
		}
	}

}
