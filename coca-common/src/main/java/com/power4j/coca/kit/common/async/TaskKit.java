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

package com.power4j.coca.kit.common.async;

import com.power4j.coca.kit.common.concurrent.CheckedRunnable;
import com.power4j.coca.kit.common.concurrent.CompletableFutureKit;
import com.power4j.coca.kit.common.lang.Obj;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/17
 * @since 1.0
 */
@Slf4j
@UtilityClass
public class TaskKit {

	public Builder builder() {
		return new Builder();
	}

	public void runLater(CheckedRunnable run, Consumer<Duration> onSuccess) {
		runLater(run, null, onSuccess, null);
	}

	public void runLater(CheckedRunnable run, BiConsumer<Duration, Throwable> onError) {
		runLater(run, null, null, onError);
	}

	public void runLater(CheckedRunnable run, Consumer<Duration> onSuccess, BiConsumer<Duration, Throwable> onError) {
		runLater(run, null, onSuccess, onError);
	}

	public void runLater(CheckedRunnable runnable, @Nullable Executor executor, @Nullable Consumer<Duration> onSuccess,
			@Nullable BiConsumer<Duration, Throwable> onError) {
		Builder builder = new Builder().successCallback(onSuccess).errorCallback(onError).executor(executor);
		builder.run(runnable);
	}

	public static class Builder {

		@Nullable
		private Consumer<Duration> successCallback;

		@Nullable
		private BiConsumer<Duration, Throwable> errorCallback;

		@Nullable
		private Executor executor;

		Builder() {
		}

		public Builder successCallback(@Nullable Consumer<Duration> successCallback) {
			this.successCallback = successCallback;
			return this;
		}

		public Builder errorCallback(@Nullable BiConsumer<Duration, Throwable> errorCallback) {
			this.errorCallback = errorCallback;
			return this;
		}

		public Builder executor(@Nullable Executor executor) {
			this.executor = executor;
			return this;
		}

		public CompletableFuture<Instant> run(CheckedRunnable runnable) {
			final Consumer<Duration> successHandler = this.successCallback;
			final BiConsumer<Duration, Throwable> errorHandler = this.errorCallback;
			return CompletableFutureKit.supplyAsync(() -> {
				Instant startAt = Instant.now();
				try {
					runnable.run();
					if (Objects.nonNull(successHandler)) {
						successHandler.accept(Duration.between(startAt, Instant.now()));
					}
				}
				catch (Exception e) {
					if (Objects.nonNull(errorHandler)) {
						errorHandler.accept(Duration.between(startAt, Instant.now()), e);
					}
					else {
						throw new CompletionException(e.getMessage(), e);
					}
				}
				return startAt;
			}, Obj.keepIfNotNull(executor, ForkJoinPool::commonPool));
		}

	}

}
