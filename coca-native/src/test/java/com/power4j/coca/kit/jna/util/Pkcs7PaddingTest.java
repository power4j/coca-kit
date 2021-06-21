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

package com.power4j.coca.kit.jna.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/9
 * @since 1.0
 */
class Pkcs7PaddingTest {

	static final byte[] src1 = { (byte) 0x01, (byte) 0x02, (byte) 0x03 };
	static final byte[] src2 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04 };

	@Test
	void padding() {
		byte[] data1 = Pkcs7Padding.padding(src1, 8);
		assertEquals(8, data1.length);
		assertEquals(5, data1[3]);
		assertEquals(5, data1[4]);
		assertEquals(5, data1[5]);
		assertEquals(5, data1[6]);
		assertEquals(5, data1[7]);

		int count1 = Pkcs7Padding.calcPadCount(data1);
		assertEquals(5, count1);

		byte[] data2 = Pkcs7Padding.padding(src2, 4);
		assertEquals(8, data2.length);
		assertEquals(4, data2[4]);
		assertEquals(4, data2[5]);
		assertEquals(4, data2[6]);
		assertEquals(4, data2[7]);
		int count2 = Pkcs7Padding.calcPadCount(data2);
		assertEquals(4, count2);
	}

}