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

import com.power4j.coca.kit.common.io.buffer.ByteData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/9
 * @since 1.0
 */
class PkcsPaddingTest {

	static final byte[] src1 = { (byte) 0x01, (byte) 0x02, (byte) 0x03 };
	static final byte[] src2 = { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04 };

	@Test
	void padding() {
		byte[] data1 = PkcsPadding.paddingCopy(src1, 8);
		assertEquals(8, data1.length);
		assertEquals(5, PkcsPadding.extractPaddingSize(data1));
		assertEquals(5, data1[3]);
		assertEquals(5, data1[4]);
		assertEquals(5, data1[5]);
		assertEquals(5, data1[6]);
		assertEquals(5, data1[7]);

		byte[] data2 = PkcsPadding.paddingCopy(src2, 4);
		assertEquals(8, data2.length);
		assertEquals(4, PkcsPadding.extractPaddingSize(data2));
		assertEquals(4, data2[4]);
		assertEquals(4, data2[5]);
		assertEquals(4, data2[6]);
		assertEquals(4, data2[7]);

		byte[] data3 = PkcsPadding.paddingCopy(src2, 1, 3, 4);
		assertEquals(4, data3.length);
		assertEquals(1, PkcsPadding.extractPaddingSize(data3));
		assertEquals(1, data3[3]);
	}

	@Test
	void paddingCount() {
		assertEquals(4, PkcsPadding.paddingCount(0, 4));
		assertEquals(3, PkcsPadding.paddingCount(1, 4));
		assertEquals(4, PkcsPadding.paddingCount(4, 4));

		assertEquals(8, PkcsPadding.paddingCount(0, 8));
		assertEquals(3, PkcsPadding.paddingCount(5, 8));
		assertEquals(8, PkcsPadding.paddingCount(8, 8));
	}

	@Test
	void paddingByteData() {
		ByteData data1 = PkcsPadding.appendPadding(ByteData.ofCapacity(32).writeBytes(src1), 4);
		assertEquals(4, data1.readableBytes());
		assertEquals(1, data1.bufferReader().skip(3).readByte());

		data1 = PkcsPadding.dropPadding(data1);
		assertEquals(3, data1.readableBytes());
		assertArrayEquals(src1, data1.readAll());

		ByteData data2 = PkcsPadding.appendPadding(ByteData.ofCapacity(32).writeBytes(src2), 4);
		assertEquals(8, data2.readableBytes());
		assertEquals(4, data2.bufferReader().skip(3).readByte());

		data2 = PkcsPadding.dropPadding(data2);
		assertEquals(4, data2.readableBytes());
		assertArrayEquals(src2, data2.readAll());
	}

}
