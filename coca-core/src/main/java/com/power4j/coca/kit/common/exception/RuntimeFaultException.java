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

package com.power4j.coca.kit.common.exception;

/**
 * 不可恢复的运行时故障
 *
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/2
 * @since 1.0
 */
public class RuntimeFaultException extends RuntimeException {

	public RuntimeFaultException(String message) {
		super(message);
	}

	public RuntimeFaultException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeFaultException(Throwable cause) {
		super(cause);
	}

}
