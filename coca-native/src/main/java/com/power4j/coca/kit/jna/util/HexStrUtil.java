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

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author cj
 * @date 2017/7/5
 * @since 1.0
 */
public class HexStrUtil {

	public static final char CHAR_ZERO = '0';

	public static String encodeHex(byte[] bytes) {
		return HexUtil.encodeHexStr(bytes).toUpperCase();
	}

	public static boolean isHexString(CharSequence hexCharSequence) {
		if (hexCharSequence.length() <= 0 || hexCharSequence.length() % 2 != 0) {
			return false;
		}
		return isHexCharOnly(hexCharSequence);
	}

	public static boolean isHexCharOnly(CharSequence hexCharSequence) {
		return firstInvalidCharPos(hexCharSequence) < 0;
	}

	public static boolean isHexChar(char c) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
			return true;
		default:
			return false;
		}
	}

	/**
	 * 返回第一个非16进制字符的索引([0,hexCharSequence.length -1]). 如果没有找到返回负数
	 * @param hexCharSequence
	 * @return
	 */
	public static int firstInvalidCharPos(CharSequence hexCharSequence) {
		for (int i = 0; i < hexCharSequence.length(); ++i) {
			if (!isHexChar(hexCharSequence.charAt(i))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 16进制格式化
	 * @param val
	 * @return
	 */
	public static String dumpInt8(int val) {
		return StrUtil.padPre(Integer.toHexString(val & 0xFF), Byte.BYTES * 2, CHAR_ZERO);
	}

	/**
	 * 16进制格式化
	 * @param val
	 * @return
	 */
	public static String dumpInt16(int val) {
		return StrUtil.padPre(Integer.toHexString(val & 0xFFFF), Short.BYTES * 2, CHAR_ZERO);
	}

	/**
	 * 16进制格式化
	 * @param val
	 * @return
	 */
	public static String dumpInt32(int val) {
		return StrUtil.padPre(Integer.toHexString(val), Integer.BYTES * 2, CHAR_ZERO);
	}

	/**
	 * 16进制格式化
	 * @param val
	 * @return
	 */
	public static String dumpInt64(long val) {
		return StrUtil.padPre(Long.toHexString(val), Long.BYTES * 2, CHAR_ZERO);
	}

}
