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
class StrErrTest {

	@Test
	void display() {
		// "[kind %d,error %s] - %s"
		StrErr err = StrErr.of("E100", null);
		Assertions.assertEquals("[kind: ?,error: E100] - ok", err.display());

		err = StrErr.of("E100", "fail");
		Assertions.assertEquals("[kind: ?,error: E100] - fail", err.display());

		err = StrErr.of("global", "E100", "fail");
		Assertions.assertEquals("[kind: global,error: E100] - fail", err.display());
	}

}
