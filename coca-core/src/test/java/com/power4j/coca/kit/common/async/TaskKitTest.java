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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/17
 * @since 1.0
 */
class TaskKitTest {

	@Test
	void runLater() {
		String msg = "test";
		TaskKit.runLater(() -> willThrow(msg), null, null, (o, e) -> {
			Assertions.assertNotNull(o);
			assertEquals(msg, e.getMessage());
		});

		// Must gather than 20 ms on windows
		long delay = 100;
		TaskKit.runLater(() -> delay(100), null, (o) -> {
			Assertions.assertTrue(o.toMillis() >= delay);
		}, null);
	}

	void willThrow(String msg) throws Exception {
		throw new Exception(msg);
	}

	void delay(long ms) {
		try {
			Thread.sleep(ms);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}