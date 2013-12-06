/*
 * Copyright 2007-2009 Hidekatsu Izuno
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package net.arnx.jsonic;

/**
 * Signals that an error has been reached unexpectedly while formating or parsing.
 * 
 * <h4>Summary of error codes</h4>
 * <table border="1" cellpadding="1" cellspacing="0">
 * <tr>
 * 	<th bgcolor="#CCCCFF" align="left">code(range)</th>
 * 	<th bgcolor="#CCCCFF" align="left">error code</th>
 * 	<th bgcolor="#CCCCFF" align="left">description</th>
 * </tr>
 * <tr><td>000-099</td><td>(all)</td><td>reserved.</td></tr>
 * <tr><td rowspan="2">100-199</td><td>100</td><td>fails to format.</td></tr>
 * <tr>                            <td>150</td><td>fails to preformat.</td></tr>
 * <tr>                            <td>(others)</td><td>reserved.</td></tr>
 * <tr><td rowspan="2">200-299</td><td>200</td><td>fails to parse.</td></tr>
 * <tr>                            <td>250</td><td>fails to postparse.</td></tr>
 * <tr>                            <td>(others)</td><td>reserved.</td></tr>
 * <tr><td>300-899</td><td>(all)</td><td>reserved.</td></tr>
 * <tr><td>900-</td><td>(all)</td><td>user's area.</td></tr>
 * </table>
 * 
 * @author izuno
 */
public class JSONException extends RuntimeException {
	private static final long serialVersionUID = -8323989588488596436L;

	public static final int FORMAT_ERROR = 100;
	public static final int PREFORMAT_ERROR = 150;
	public static final int PARSE_ERROR = 200;
	public static final int POSTPARSE_ERROR = 250;
	
	private int errorID;
	private long lineNumber = -1l;
	private long columnNumber = -1l;
	private long offset = -1l;
	
	public JSONException(String message, int id, long lineNumber, long columnNumber, long offset) {
		super(message);
		this.errorID = id;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.offset = offset;
	}
	
	public JSONException(String message, int id,  Throwable cause) {
		super(message, cause);
		this.errorID = id;
	}
	
	public JSONException(String message, int id) {
		super(message);
		this.errorID = id;
	}
	
	public int getErrorCode() {
		return errorID;
	}
	
	/**
	 * Returns the line number where the error was found.
	 */
	public long getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Returns the column number where the error was found.
	 */
	public long getColumnNumber() {
		return columnNumber;
	}
	
	/**
	 * Returns the offset in line where the error was found.
	 */
	public long getErrorOffset() {
		return offset;
	}
}
