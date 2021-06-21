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

package com.power4j.coca.kit.common.model;

import lombok.Data;
import org.springframework.lang.Nullable;

import java.io.Serializable;

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
	 * 用户提示
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

}
