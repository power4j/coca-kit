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

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 * @param <T> 错误值
 */
public interface Err<T extends Comparable<T>> {

	/**
	 * 错误代码
	 * @return 错误代码值
	 */
	T getCode();

	/**
	 * 错误描述
	 * @return 错误信息字符串
	 */
	String getMessage();

}
