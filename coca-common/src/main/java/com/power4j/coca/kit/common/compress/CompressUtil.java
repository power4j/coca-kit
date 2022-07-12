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

package com.power4j.coca.kit.common.compress;

import lombok.experimental.UtilityClass;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2022/7/6
 * @since 1.0
 */
@UtilityClass
public class CompressUtil {

	/**
	 * GZip压缩
	 * @param buffSize 缓冲区大小(字节)
	 * @param inputStream 输入
	 * @param outputStream 输出
	 * @throws IOException
	 */
	public static void gzip(int buffSize, InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buff = new byte[buffSize];
		try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
			int read;
			while ((read = inputStream.read(buff)) != -1) {
				gzip.write(buff, 0, read);
			}
		}
	}

	/**
	 * GZip压缩
	 * @param source 输入
	 * @param outputStream 输出
	 * @throws IOException
	 */
	public static void gzip(byte[] source, OutputStream outputStream) throws IOException {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(source)) {
			gzip(2048, inputStream, outputStream);
		}
	}

	/**
	 * GZip 压缩
	 * @param source 输入
	 * @return 返回压缩后的数据
	 * @throws IOException
	 */
	public static byte[] gzip(byte[] source) throws IOException {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream(source.length)) {
			gzip(source, os);
			return os.toByteArray();
		}
	}

	/**
	 * GZip解压缩
	 * @param buffSize 缓冲区大小(字节)
	 * @param inputStream 输入
	 * @param outputStream 输出
	 * @throws IOException
	 */
	public static void unGzip(int buffSize, InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] buff = new byte[buffSize];
		try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
			int read;
			while ((read = gzipInputStream.read(buff)) != -1) {
				outputStream.write(buff, 0, read);
			}
		}
	}

	/**
	 * GZip解压缩
	 * @param source 输入
	 * @param outputStream 输出
	 * @throws IOException
	 */
	public static void unGzip(byte[] source, OutputStream outputStream) throws IOException {
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source)) {
			unGzip(2048, byteArrayInputStream, outputStream);
		}
	}

	/**
	 * GZip解压缩
	 * @param source 输入
	 * @return 返回解压缩后的数据
	 * @throws IOException
	 */
	public static byte[] unGzip(byte[] source) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream(source.length);
		unGzip(source, os);
		return os.toByteArray();
	}

}
