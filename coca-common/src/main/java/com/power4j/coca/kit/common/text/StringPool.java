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

package com.power4j.coca.kit.common.text;

/**
 * @author CJ (power4j@outlook.com)
 * @date 2021/6/16
 * @since 1.0
 */
public interface StringPool {

	// ~ Punctuation
	// ===================================================================================================

	String AMPERSAND = "&";

	String AT = "@";

	String STAR = "*";

	String BACK_SLASH = "\\";

	String COLON = ":";

	String COMMA = ",";

	String DASH = "-";

	String DOLLAR = "$";

	String DOT = ".";

	String EQUALS = "=";

	String SLASH = "/";

	String HASH = "#";

	String HAT = "^";

	String BRACE_L = "{";

	String BRACE_R = "}";

	String BRACKET_L = "(";

	String BRACKET_R = ")";

	String CHEV_L = "<";

	String CHEV_R = ">";

	String SQ_BRACKET_L = "[";

	String SQ_BRACKET_R = "]";

	String PERCENT = "%";

	String PIPE = "|";

	String PLUS = "+";

	String QUESTION_MARK = "?";

	String EXCLAMATION_MARK = "!";

	String SEMICOLON = ";";

	String SINGLE_QUOTE = "'";

	String BACKTICK = "`";

	String TILDA = "~";

	String UNDERSCORE = "_";

	String QUOTE = "\"";

	// ~ Word
	// ===================================================================================================

	String AND = "and";

	String TRUE = "true";

	String FALSE = "false";

	String NULL = "null";

	String ON = "on";

	String OFF = "off";

	String YES = "yes";

	String NO = "no";

	String N_A = "N/A";

	// ~ Number
	// ===================================================================================================

	String ZERO = "0";

	String ONE = "1";

	String TWO = "2";

	String THREE = "3";

	String FOUR = "4";

	String FIVE = "5";

	String SIX = "6";

	String SEVEN = "7";

	String EIGHT = "8";

	String NINE = "9";

	// ~ Misc
	// ===================================================================================================

	String SPACE = " ";

	String EMPTY = "";

	String TAB = "\t";

	String RETURN = "\r";

	String LF = "\n";

	/**
	 * @deprecated use {@code LF} instead
	 * @see StringPool#LF
	 */
	String NEWLINE = LF;

	String CRLF = "\r\n";

}
