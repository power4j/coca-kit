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

package com.power4j.coca.kit.common.io.buffer;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public interface BufferWriter<T extends BufferWriter<T>> extends ByteOrderAccess<T> {

	/**
	 * 写入 byte
	 * @param b byte
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeByte(byte b);

	/**
	 * 重复写入 byte
	 * @param b byte
	 * @param count 数量
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeByteRepeat(byte b, int count);

	/**
	 * 写入 char <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val char
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeChar(char val);

	/**
	 * 写入byte 数组
	 * @param src byte 数组
	 * @param offset 偏移量
	 * @param length 长度
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeBytes(byte[] src, int offset, int length);

	/**
	 * 写入 short <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val short
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeShort(short val);

	/**
	 * 写入 int <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val int
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeInt(int val);

	/**
	 * 写入 long <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val long
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeLong(long val);

	/**
	 * 写入 float <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val float
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeFloat(float val);

	/**
	 * 写入 double <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @param val double
	 * @return T
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeDouble(double val);

	/**
	 * 写入字符串
	 * @param str 需要写入的字符串
	 * @param charset 字符集
	 * @param fixedSize 定长长度,将数据截断或者填充到指定长度,<= 0 表示不进行定长长度调整
	 * @param filler 填充值,填充时会用到, 否则无意义
	 * @return String 对象
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeString(String str, Charset charset, int fixedSize, byte filler);

	/**
	 * writeString 的简化版,使用 UTF8 字符集，并且使用定长长度修正
	 * @param str 需要写入的字符串
	 * @param fixedSize 定长长度
	 * @return String 对象
	 * @throws BufferOverflowException 无空间可写,需要扩容
	 */
	T writeFixedString(String str, int fixedSize);

	/**
	 * 丢弃已经写入的数据
	 * @param length 字节数,最多 {@code hasWritten},超过取下限,小于等于0无效
	 * @return T
	 */
	T discard(int length);

	/**
	 * 丢弃全部已经写入的数据
	 * @return T
	 */
	T discardAll();

	/**
	 * 移动写指针,使得下一次写操作跳过N字节
	 * @param length 字节数,最多 {@code writeableBytes},超过取下限,小于等于0无效
	 * @return T
	 */
	T reserve(int length);

	/**
	 * 可读取的数据量
	 * @return 字节数
	 */
	int hasWritten();

	/**
	 * 可写入的数据量
	 * @return 字节数
	 */
	int writeableBytes();

}
