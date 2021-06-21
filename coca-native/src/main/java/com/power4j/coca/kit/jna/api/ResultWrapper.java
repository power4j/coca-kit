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

package com.power4j.coca.kit.jna.api;

import com.power4j.coca.kit.jna.exception.NativeApiFailureException;
import lombok.Data;

import java.util.function.Function;

/**
 * 封装C API 返回结果
 * <p>
 *
 * @author CJ (power4j@outlook.com)
 * @date 2021-06-08
 * @since 1.0
 */
@Data
public class ResultWrapper<T> {

	protected static final long CODE_ZERO = 0L;

	private long code;

	private T data;

	/**
	 * 包装返回结果
	 * @param code
	 * @param data
	 * @param <T>
	 * @return
	 */
	public static <T> ResultWrapper<T> warp(long code, T data) {
		ResultWrapper<T> wrapper = new ResultWrapper<>();
		wrapper.setCode(code);
		wrapper.setData(data);
		return wrapper;
	}

	/**
	 * 包装返回结果
	 * @param code
	 * @param <T>
	 * @return
	 */
	public static <T> ResultWrapper<T> warp(long code) {
		return warp(code, null);
	}

	public <R> ResultWrapper<R> map(Function<T, R> func) {
		return warp(code, func.apply(data));
	}

	/**
	 * 检查返回码是否相等
	 * @param code
	 * @return
	 */
	public boolean codeEq(int code) {
		return this.code == code;
	}

	/**
	 * 检查返回码是否为成功
	 * @return
	 */
	public boolean codeOk() {
		return code == CODE_ZERO;
	}

	/**
	 * 检查返回码是否为不成功
	 * @return
	 */
	public boolean codeNotOk() {
		return !codeOk();
	}

	public ResultWrapper<T> assertCodeOk() {
		if (codeNotOk()) {
			throw new NativeApiFailureException(code, resolveErrorMessage());
		}
		return this;
	}

	/**
	 * 获取返回码对应的描述信息
	 * @return message
	 */
	public String resolveErrorMessage() {
		return String.format("%04d", code);
	}

}
