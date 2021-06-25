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

import com.power4j.coca.kit.common.exception.AssertionFailException;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/6
 * @since 1.0
 */
@Data
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 错误代码，总是有值
	 */
	private String code;

	/**
	 * 错误消息
	 * <ul>
	 * <li>返回成功,可能没有值</li>
	 * <li>返回失败,一定有值</li>
	 * </ul>
	 */
	@Nullable
	private String message;

	/**
	 * 业务数据
	 */
	@Nullable
	private T data;

	/**
	 * 提示
	 */
	@Nullable
	private String hint;

	public static <T> Result<T> create(String code, @Nullable String message, @Nullable T data, @Nullable String hint) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setMessage(message);
		result.setData(data);
		result.setHint(hint);
		return result;
	}

	public boolean codeEquals(@Nullable String expected, boolean caseSensitive) {
		return caseSensitive ? code.equals(expected) : code.equalsIgnoreCase(expected);
	}

	public boolean codeNotEquals(@Nullable String expected, boolean caseSensitive) {
		return !codeEquals(expected, caseSensitive);
	}

	public boolean codeEquals(@Nullable String expected) {
		return codeEquals(expected, true);
	}

	public boolean codeNotEquals(@Nullable String expected) {
		return !codeEquals(expected, true);
	}

	/**
	 * 取业务数据
	 * @param codePredicate 错误码断言
	 * @param func 负责产生一个异常对象
	 * @param <E> 异常类型
	 * @return 断言成功返回data
	 * @throws E 断言失败抛出异常
	 */
	@Nullable
	public <E extends Exception> T checkAndGetData(Predicate<String> codePredicate, Function<Result<T>, E> func)
			throws E {
		if (codePredicate.test(code)) {
			return data;
		}
		throw func.apply(this);
	}

	public T requiredData() {
		if (Objects.isNull(data)) {
			throw new AssertionFailException("data is null");
		}
		return data;
	}

	/**
	 * 转换数据
	 * @param func 转换器
	 * @param <U> 业务数类型
	 * @return 返回新的 Result 实例
	 */
	public <U> Result<U> map(Function<T, U> func) {
		return create(code, message, func.apply(data), hint);
	}

	/**
	 * 转换数据
	 * @param func 转换器,业务数据不是null时会被调用
	 * @param <U> 业务数类型
	 * @return 返回新的 Result 实例
	 */
	public <U> Result<U> mapIfPresent(Function<T, U> func) {
		return create(code, message, Objects.nonNull(data) ? func.apply(data) : null, hint);
	}

	/**
	 * 根据错误代码进行数据转换
	 * @param codePredicate 错误码断言
	 * @param func 转换器
	 * @param <U> 业务数类型
	 * @return 返回新的 Result 实例,错误码断言成失败data为null
	 */
	public <U> Result<U> mapOnCode(Predicate<String> codePredicate, Function<T, U> func) {
		if (codePredicate.test(code)) {
			return create(code, message, func.apply(data), hint);
		}
		return create(code, message, null, hint);
	}

}
