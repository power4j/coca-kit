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

import com.sun.jna.Pointer;

/**
 * 指针对象
 * <p>
 *
 * @author CJ (power4j@outlook.com)
 * @date 2021-06-08
 * @since 1.0
 */
public class Ptr {

	private Pointer handle;

	public Ptr(Pointer handle) {
		this.handle = handle;
	}

	public Pointer getHandle() {
		return handle;
	}

	public void setHandle(Pointer handle) {
		this.handle = handle;
	}

}
