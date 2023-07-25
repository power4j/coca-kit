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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author CJ (power4j@outlook.com)
 * @since 1.0
 */
class IntErrTest {

	@Test
	void display() {
		IntErr err = IntErr.of(0xF, null);
		Assertions.assertEquals("[kind 0,error 15] - ok", err.display());

		err = IntErr.of(0x1, "fail");
		Assertions.assertEquals("[kind 0,error 1] - fail", err.display());

		err = IntErr.of(-1, 0x1, "fail");
		Assertions.assertEquals("[kind -1,error 1] - fail", err.display());
	}

}
