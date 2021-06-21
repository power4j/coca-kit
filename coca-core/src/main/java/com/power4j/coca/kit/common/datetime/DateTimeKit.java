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

package com.power4j.coca.kit.common.datetime;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/2
 * @since 1.0
 */
@UtilityClass
public class DateTimeKit {

	public LocalDateTime utcNow() {
		return LocalDateTime.now(ZoneOffset.UTC);
	}

	// ~ Converter
	// ===================================================================================================

	@Nullable
	public Date toDate(@Nullable LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	// ~ Time Zone
	// ===================================================================================================

	@Nullable
	public ZonedDateTime toZonedDateTime(@Nullable LocalDateTime dateTime, ZoneOffset offset) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.atZone(offset);
	}

	@Nullable
	public ZonedDateTime toZonedDateTimeUtc(@Nullable LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return toZonedDateTime(dateTime, ZoneOffset.UTC);
	}

	@Nullable
	public LocalDateTime toLocalDateTime(@Nullable ZonedDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.toLocalDateTime();
	}

}
