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

import com.power4j.coca.kit.common.concurrent.CheckedRun;
import com.power4j.coca.kit.common.lang.Obj;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
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

	public void runLater(CheckedRun run, Consumer<Duration> onComplete) {
		runLater(run, null, onComplete, null);
	}

	public void runLater(CheckedRun run, BiConsumer<Duration, Throwable> onError) {
		runLater(run, null, null, onError);
	}

	public void runLater(CheckedRun run, Consumer<Duration> onComplete, BiConsumer<Duration, Throwable> onError) {
		runLater(run, null, onComplete, onError);
	}

	public void runLater(CheckedRun run, @Nullable Executor executor, @Nullable Consumer<Duration> onComplete,
			@Nullable BiConsumer<Duration, Throwable> onError) {
		submit(run, executor, onComplete).exceptionally(e -> {
			assert e.getClass().equals(WrappedException.class);
			WrappedException wrappedException = (WrappedException) e;
			if (Objects.nonNull(onError)) {
				onError.accept(Duration.between(wrappedException.getTimestamp(), Instant.now()), e);
			}
			else {
				log.debug(e.getMessage(), e);
			}
			return null;
		});
	}

	public CompletableFuture<Void> submit(CheckedRun run, @Nullable Executor executor,
			@Nullable Consumer<Duration> onComplete) {
		return submit(run, executor).thenAccept(duration -> {
			if (Objects.nonNull(onComplete)) {
				onComplete.accept(duration);
			}
		});
	}

	public CompletableFuture<Duration> submit(CheckedRun run, @Nullable Executor executor) {
		final Instant scheduleAt = Instant.now();
		return CompletableFuture.supplyAsync(() -> {
			try {
				run.run();
				return Duration.between(scheduleAt, Instant.now());
			}
			catch (Exception e) {
				throw new WrappedException(scheduleAt, e);
			}
		}, Obj.keepIfNotNull(executor, ForkJoinPool::commonPool));
	}

	static class WrappedException extends RuntimeException {

		@Getter
		private final Instant timestamp;

		public WrappedException(Instant timestamp, Throwable cause) {
			super(cause);
			this.timestamp = timestamp;
		}

	}

}
