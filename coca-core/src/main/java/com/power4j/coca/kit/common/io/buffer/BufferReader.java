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

import java.nio.BufferUnderflowException;
import java.nio.charset.Charset;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/8
 * @since 1.0
 */
public interface BufferReader<T extends BufferReader<T>> extends ByteOrderAccess<T> {

	/**
	 * 读 byte
	 * @return byte 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	byte readByte();

	/**
	 * 读 char <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return byte 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	char readChar();

	/**
	 * 读 short <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return short 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	short readShort();

	/**
	 * 读 int <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return int 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	int readInt();

	/**
	 * 读 long <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	long readLong();

	/**
	 * 读 float <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	float readFloat();

	/**
	 * 读 double <b>此方法使用默认字节序,如果必要请先设置字节序</b>
	 * @return long 值
	 * @throws BufferUnderflowException 无数据可读
	 */
	double readDouble();

	/**
	 * 读多个byte
	 * @param size 字节数
	 * @return byte 数组
	 * @throws BufferUnderflowException 无数据可读
	 */
	byte[] readBytes(int size);

	/**
	 * 读多个byte到外部
	 * @param dest 写入目标
	 * @param offset dest的写入位置偏移量
	 * @param length 字节数
	 * @throws BufferUnderflowException 无数据可读
	 */
	void transfer(byte[] dest, int offset, int length);

	/**
	 * 读字符串 <b>注意此方法读出来的字符串没有做清理,很可能会包含乱码</b>
	 * @param length 字节数
	 * @param charset 字符集
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 */
	String readAsString(int length, Charset charset);

	/**
	 * 读字符串
	 * @param length 字节数
	 * @param charset 字符集
	 * @param dropTail 查找字符串结束符(0x0),然后清理(将它和之后的数据丢弃)
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 */
	String readString(int length, Charset charset, boolean dropTail);

	/**
	 * readNextString 的简化版,使用 UTF8 字符集，并且清理
	 * @param length 字节数
	 * @return String 对象
	 * @throws BufferUnderflowException 无数据可读
	 */
	String readUtf8String(int length);

	/**
	 * 可读取的数据量
	 * @return 字节数
	 */
	int readableBytes();

	/**
	 * 已经读取的数据量
	 * @return 字节数
	 */
	int hasRead();

	/**
	 * 回退读指针，可用于重复读取数据
	 * @param length 字节数,最大值 {@code hasRead()},超过忽略.小于等于0忽略
	 * @return T
	 */
	T unread(int length);

	/**
	 * 回退读指针，可用于重复读取数据
	 * @return T
	 */
	T unreadAll();

	/**
	 * 跳过读取一部分数据
	 * @param length 字节数,最大值 {@code readableBytes()},超过忽略.小于等于0忽略
	 * @return T
	 */
	T skip(int length);

	/**
	 * 将全部剩余数据读出,并且编码为HEX格式字符串
	 * @return 无数据可读返回空字符串
	 */
	String dumpHex();

	/**
	 * 将全部剩余数据读出,并且编码为Base64格式字符串
	 * @return 无数据可读返回空字符串
	 */
	String dumpBase64();

}
