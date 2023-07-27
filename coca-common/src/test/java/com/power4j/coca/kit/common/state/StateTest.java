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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class StateTest {

	@Test
	void isError() {
		// 是否错误是根据error对象判断
		State<?, ?> state = new State<>(null, "");
		Assertions.assertTrue(state.isError());
		Assertions.assertFalse(state.isOk());

		state = new State<>("", "");
		Assertions.assertTrue(state.isError());
		Assertions.assertFalse(state.isOk());

		state = new State<>(null, null);
		System.out.println(state.display());
		Assertions.assertFalse(state.isError());
		Assertions.assertTrue(state.isOk());
	}

	@Test
	void isErrorAnd() {
		State<?, Integer> state = State.error(123);
		Assertions.assertTrue(state.isErrorAnd(i -> i == 123));
		Assertions.assertFalse(state.isErrorAnd(i -> i == 1));

		state = State.ok(null);
		Assertions.assertFalse(state.isErrorAnd(i -> true));
	}

	@Test
	void isOkAnd() {
		State<Integer, Integer> state = State.ok(123);
		Assertions.assertTrue(state.isOkAnd(i -> true));
		Assertions.assertFalse(state.isOkAnd(i -> false));

		state = State.error(-1);
		Assertions.assertFalse(state.isOkAnd(i -> true));
	}

	@Test
	void unwrap() {
		State<String, ?> state1 = State.error("xx");
		Assertions.assertThrows(IllegalStateException.class, state1::unwrap);

		State<String, ?> state2 = State.ok("xx");
		Assertions.assertEquals("xx", state2.unwrap());
	}

	@Test
	void unwrapNonNull(){
		Assertions.assertThrows(NullPointerException.class,() -> State.ok(null).unwrapNonNull());
		Assertions.assertEquals("1",State.ok("1").unwrapNonNull());
	}

	@Test
	void unwrapOr() {
		// 错误使用默认值
		State<String, ?> state = State.error("xx");
		Assertions.assertEquals("yy", state.unwrapOr("yy"));

		// 非错误使用原始值
		state = State.ok("xx");
		Assertions.assertEquals("xx", state.unwrapOr("yy"));
	}

	@Test
	void unwrapOrElse() {
		// 错误使用默认值
		State<String, ?> state = State.error("xx");
		Assertions.assertEquals("yy", state.unwrapOrElse(() -> "yy"));

		// 非错误使用原始值
		state = State.ok("xx");
		Assertions.assertEquals("xx", state.unwrapOrElse(() -> "yy"));
	}

	@Test
	void unwrapError() {
		State<?, String> state1 = State.ok("xx");
		Assertions.assertThrows(IllegalStateException.class, state1::unwrapError);

		State<?, String> state2 = State.error("xx");
		Assertions.assertEquals("xx", state2.unwrapError());

	}

	@Test
	void tryUnwrapError() {
		State<?, String> state = State.ok("xx");
		Assertions.assertFalse(state.tryUnwrapError().isPresent());

		state = State.error("xx");
		Assertions.assertEquals("xx", state.tryUnwrapError().get());
	}

	@Test
	void map() {
		State<Integer, String> state = State.error("xx");
		State<String, String> other = state.map(Object::toString);
		Assertions.assertTrue(other.isError());

		state = State.ok(123);
		other = state.map(Object::toString);
		Assertions.assertEquals("123", other.unwrap());
	}

	@Test
	void mapError() {
		State<?, Integer> state = State.error(123);
		State<?, String> other = state.mapError(Object::toString);
		Assertions.assertEquals("123", other.unwrapError());

		state = State.ok(123);
		other = state.mapError(Object::toString);
		Assertions.assertTrue(other.isOk());
	}

	@Test
	void mapOr() {
		Assertions.assertEquals(1, State.ok(1).mapOr(Function.identity(), 2));
		Assertions.assertEquals(2, State.error("ERR").mapOr(Function.identity(), 2));
	}

	@Test
	void mapOrElse() {
		Assertions.assertEquals(1, State.error("ERR").mapOrElse(Function.identity(), () -> 1));
		Assertions.assertEquals(123, State.ok(123).mapOrElse(Function.identity(), () -> 1));
	}

	@Test
	void and() {
		Assertions.assertTrue(State.error("xx").and(State.ok("aaa")).isError());
		Assertions.assertTrue(State.error("xx").and(State.ok(123)).isError());
		Assertions.assertTrue(State.ok(123).and(State.error("xx")).isError());

		Assertions.assertEquals(123, State.ok("aaa").and(State.ok(123)).unwrap());
		Assertions.assertEquals("aaa", State.ok(123).and(State.ok("aaa")).unwrap());
	}

	@Test
	void andThen() {
		Assertions.assertTrue(State.error("xx").andThen(o -> State.ok("aaa")).isError());
		Assertions.assertTrue(State.error("xx").andThen(o -> State.ok(123)).isError());
		Assertions.assertTrue(State.ok(123).andThen(o -> State.error("xx")).isError());

		Assertions.assertEquals(123, State.ok("aaa").andThen(o -> State.ok(123)).unwrap());
		Assertions.assertEquals("aaa", State.ok(123).andThen(o -> State.ok("aaa")).unwrap());
	}

	@Test
	void or() {
		Assertions.assertTrue(State.error("xx").or(State.ok(1)).isOk());
		Assertions.assertTrue(State.error("xx").or(State.error(1)).isError());
		Assertions.assertTrue(State.ok(1).or(State.ok(1)).isOk());
		Assertions.assertTrue(State.ok(1).or(State.error(1)).isOk());

		Assertions.assertEquals(1, State.error("xx").or(State.ok(1)).unwrap());
		Assertions.assertEquals("x", State.ok("x").or(State.ok("y")).unwrap());
	}

	@Test
	void orElse() {
		Assertions.assertTrue(State.error("xx").orElse(e -> State.ok(1)).isOk());
		Assertions.assertTrue(State.error("xx").orElse(e -> State.error(1)).isError());
		Assertions.assertTrue(State.ok(1).orElse(e -> State.ok(1)).isOk());
		Assertions.assertTrue(State.ok(1).orElse(e -> State.error(1)).isOk());

		Assertions.assertEquals(1, State.error("xx").or(State.ok(1)).unwrap());
	}

	@Test
	void flatten() {
		Assertions.assertEquals(1, State.flatten(State.ok(State.ok(1))).unwrap());

		Assertions.assertEquals("xx", State.flatten(State.error("xx")).unwrapError());
	}

	@Test
	void displayTest() {

		State<?, ?> state1 = State.error("failed");
		Assertions.assertEquals("Error(failed)", state1.display());

		state1 = State.error(-1);
		Assertions.assertEquals("Error(-1)", state1.display());

		state1 = State.ok(-1);
		Assertions.assertEquals("Ok", state1.display());

		state1 = State.ok(null);
		Assertions.assertEquals("Ok(null)", state1.display());

		state1 = State.ok(Optional.empty());
		Assertions.assertEquals("Ok(wrapped null)", state1.display());

		state1 = State.ok(Optional.of(1));
		Assertions.assertEquals("Ok", state1.display());
	}

}
