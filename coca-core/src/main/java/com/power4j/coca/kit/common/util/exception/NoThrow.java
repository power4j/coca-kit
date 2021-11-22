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

package com.power4j.coca.kit.common.util.exception;

import com.power4j.coca.kit.common.util.function.RunAny;
import com.power4j.coca.kit.common.util.function.SupplyAny;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/11/22
 * @since 1.0
 */
public class NoThrow {

	public static State<Void> run(RunAny anyRun) {
		try {
			anyRun.run();
			return State.offer(null);
		}
		catch (Throwable e) {
			return State.exceptionally(e);
		}
	}

	public static <T> State<T> apply(SupplyAny<T> anySup) {
		try {
			return State.offer(anySup.get());
		}
		catch (Throwable e) {
			return State.exceptionally(e);
		}
	}

	public static class State<T> {

		@Nullable
		private final T object;

		@Nullable
		private final Throwable exception;

		private static <T> State<T> exceptionally(Throwable e) {
			return new State<>(null, e);
		}

		private static <T> State<T> offer(T object) {
			return new State<>(object, null);
		}

		State(@Nullable T object, @Nullable Throwable exception) {
			this.object = object;
			this.exception = exception;
		}

		@Nullable
		public T getObject() {
			return object;
		}

		@Nullable
		public Throwable getException() {
			return exception;
		}

		@SuppressWarnings("unchecked")
		@Nullable
		public <E extends Throwable> E getCastedException() {
			return (E) exception;
		}

		public boolean isError() {
			return Objects.nonNull(exception);
		}

	}

}
