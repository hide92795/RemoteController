package net.arnx.jsonic;

/**
 * JSON event types for Pull Parser (getReader). 
 */
public enum JSONEventType {
	/**
	 * Starts JSON object.
	 */
	START_OBJECT,
	
	/**
	 * Ends JSON object.
	 */
	END_OBJECT,
	
	/**
	 * Starts JSON array.
	 */	
	START_ARRAY,
	
	/**
	 * Ends JSON array.
	 */	
	END_ARRAY,
	
	/**
	 * JSON object name.
	 */
	NAME,
	
	/**
	 * JSON string.
	 */
	STRING,
	
	/**
	 * JSON number.
	 */
	NUMBER,
	
	/**
	 * JSON true or false
	 */
	BOOLEAN,
	
	/**
	 * JSON null
	 */
	NULL,
	
	/**
	 * White spaces
	 */
	WHITESPACE,
	
	/**
	 * Single line or Multi line comment.
	 */
	COMMENT
}
