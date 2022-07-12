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

package com.power4j.coca.kit.common.concurrent;

import com.power4j.coca.kit.common.util.function.CheckedSupplier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/9/13
 * @since 1.0
 */
public class CompletableFutureKit {

	/**
	 * 包装 CheckedRunnable
	 * @param runnable CheckedRunnable,如果抛出异常,可以通过 {@link CompletionException#getCause()}
	 * 方法获取
	 * @return CompletableFuture
	 */
	public static CompletableFuture<Void> runAsync(CheckedRunnable runnable, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			try {
				runnable.run();
			}
			catch (Exception e) {
				throw new CompletionException(e.getMessage(), e);
			}
		}, executor);
	}

	/**
	 * 包装 CheckedRunnable
	 * @param runnable CheckedRunnable,如果抛出异常,可以通过 {@link CompletionException#getCause()}
	 * 方法获取
	 * @return CompletableFuture
	 */
	public static CompletableFuture<Void> runAsync(CheckedRunnable runnable) {
		return CompletableFuture.runAsync(() -> {
			try {
				runnable.run();
			}
			catch (Exception e) {
				throw new CompletionException(e.getMessage(), e);
			}
		});
	}

	/**
	 * 包装 CheckedSupplier
	 * @param supplier CheckedSupplier,如果抛出异常,可以通过 {@link CompletionException#getCause()}
	 * 方法获取
	 * @param executor Executor
	 * @param <U> Result Type
	 * @return CompletableFuture
	 */
	public static <U> CompletableFuture<U> supplyAsync(CheckedSupplier<U> supplier, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.apply();
			}
			catch (Exception e) {
				throw new CompletionException(e.getMessage(), e);
			}
		}, executor);
	}

	/**
	 * 包装 CheckedSupplier
	 * @param supplier CheckedSupplier,如果抛出异常,可以通过 {@link CompletionException#getCause()}
	 * 方法获取
	 * @param <U> Result Type
	 * @return CompletableFuture
	 */
	public static <U> CompletableFuture<U> supplyAsync(CheckedSupplier<U> supplier) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.apply();
			}
			catch (Exception e) {
				throw new CompletionException(e.getMessage(), e);
			}
		});
	}

}
