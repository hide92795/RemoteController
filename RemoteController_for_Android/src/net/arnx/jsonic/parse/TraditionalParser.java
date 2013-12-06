package net.arnx.jsonic.parse;

import java.io.IOException;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.io.InputSource;
import net.arnx.jsonic.util.LocalCache;

public class TraditionalParser extends JSONParser {
	private InputSource in;
	
	private boolean emptyRoot = false;
	private long nameLineNumber = Long.MAX_VALUE;
	
	public TraditionalParser(InputSource in, int maxDepth, boolean interpretterMode, boolean ignoreWhirespace, LocalCache cache) {
		super(in, maxDepth, interpretterMode, ignoreWhirespace, cache);
		this.in = in;
	}
	
	@Override
	int beforeRoot() throws IOException {
		int n = in.next();
		if (n == 0xFEFF) n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return BEFORE_ROOT;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return BEFORE_ROOT;
		case '{':
			push(JSONEventType.START_OBJECT);
			return BEFORE_NAME;
		case '[':
			push(JSONEventType.START_ARRAY);
			return BEFORE_VALUE;
		case -1:
			if (isInterpretterMode()) {
				return -1;
			}
		default:
			if (n != -1) in.back();
			emptyRoot = true;
			push(JSONEventType.START_OBJECT);
			return BEFORE_NAME;
		}
	}
	
	@Override
	int afterRoot() throws IOException {
		int n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return AFTER_ROOT;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return AFTER_ROOT;
		case -1:
			return -1;
		case ',':
			if (isInterpretterMode()) {
				return BEFORE_ROOT;
			}
		case '{':
		case '[':
			if (isInterpretterMode()) {
				in.back();
				return BEFORE_ROOT;
			}
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
	
	@Override
	int beforeName() throws IOException {
		int n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return BEFORE_NAME;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return BEFORE_NAME;
		case '"':
		case '\'':
			in.back();
			set(JSONEventType.NAME, parseString(in, true), false);
			return AFTER_NAME;
		case '-':
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
			in.back();
			Object num = parseNumber(in);
			set(JSONEventType.NAME, (num != null) ? num.toString() : null, false);
			return AFTER_NAME;
		case '}':
			if (isFirst()) {
				pop();
				if (getBeginType() == null) {
					if (emptyRoot) {
						throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
					} else {
						return AFTER_ROOT;
					}
				} else {
					nameLineNumber = in.getLineNumber();
					return AFTER_VALUE;							
				}
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case -1:
			pop();
			if (getBeginType() == null && emptyRoot) {
				return -1;
			} else {
				throw createParseException(in, "json.parse.ObjectNotClosedError");
			}
		default:
			in.back();
			Object literal = parseLiteral(in, true);
			set(JSONEventType.NAME, (literal != null) ? literal.toString() : null, false);
			return AFTER_NAME;
		}
	}

	@Override
	int afterName() throws IOException {
		int n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return AFTER_NAME;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return AFTER_NAME;
		case ':':
			return BEFORE_VALUE;
		case '{':
		case '[':
			in.back();
			return BEFORE_VALUE;
		case -1:
			throw createParseException(in, "json.parse.ObjectNotClosedError");
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
	
	@Override
	int beforeValue() throws IOException {
		int n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return BEFORE_VALUE;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return BEFORE_VALUE;
		case '{':
			push(JSONEventType.START_OBJECT);
			return BEFORE_NAME;
		case '[':
			push(JSONEventType.START_ARRAY);
			return BEFORE_VALUE;
		case '"':
		case '\'':
			in.back();
			set(JSONEventType.STRING, parseString(in, true), true);
			nameLineNumber = in.getLineNumber();
			return AFTER_VALUE;
		case '-':
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
			in.back();
			set(JSONEventType.NUMBER, parseNumber(in), true);
			nameLineNumber = in.getLineNumber();
			return AFTER_VALUE;
		case ',':
			if (getBeginType() == JSONEventType.START_OBJECT) {
				set(JSONEventType.NULL, null, true);
				return BEFORE_NAME;
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				set(JSONEventType.NULL, null, true);
				return BEFORE_VALUE;
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case '}':
			if (getBeginType() == JSONEventType.START_OBJECT) {
				set(JSONEventType.NULL, null, true);
				in.back();
				return AFTER_VALUE;
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case ']':
			if (getBeginType() == JSONEventType.START_ARRAY) {
				if (isFirst()) {
					pop();
					if (getBeginType() == null) {
						return AFTER_ROOT;
					} else {
						nameLineNumber = in.getLineNumber();
						return AFTER_VALUE;
					}
				} else {
					set(JSONEventType.NULL, null, true);
					in.back();
					return AFTER_VALUE;
				}
			} else{
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case -1:
			if (getBeginType() == JSONEventType.START_OBJECT) {
				in.back();
				set(JSONEventType.NULL, null, true);
				return AFTER_VALUE;
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				throw createParseException(in, "json.parse.ArrayNotClosedError");
			} else {
				throw new IllegalStateException();
			}
		default:
			in.back();
			Object literal = parseLiteral(in, true);
			set(getType(), literal, true);
			nameLineNumber = in.getLineNumber();
			return AFTER_VALUE;
		}
	}
	
	@Override
	int afterValue() throws IOException {
		int n = in.next();
		switch (n) {
		case ' ':
		case '\t':
		case '\r':
		case '\n':
			in.back();
			String ws = parseWhitespace(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.WHITESPACE, ws, false);
			}
			return AFTER_VALUE;
		case '/':
			in.back();
			String comment = parseComment(in);
			if (!isIgnoreWhitespace()) {
				set(JSONEventType.COMMENT, comment, false);
			}
			return AFTER_VALUE;
		case ',':
			if (getBeginType() == JSONEventType.START_OBJECT) {
				return BEFORE_NAME;
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				return BEFORE_VALUE;
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);						
			}
		case '}':
			if (getBeginType() == JSONEventType.START_OBJECT) {
				pop();
				if (getBeginType() == null) {
					if (emptyRoot) {
						throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
					} else {
						return AFTER_ROOT;
					}
				} else {
					return AFTER_VALUE;							
				}
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);						
			}
		case ']':
			if (getBeginType() == JSONEventType.START_ARRAY) {
				pop();
				if (getBeginType() == null) {
					return AFTER_ROOT;
				} else {
					return AFTER_VALUE;							
				}
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);						
			}
		case -1:
			if (getBeginType() == JSONEventType.START_OBJECT) {
				pop();
				if (getBeginType() == null && emptyRoot) {
					return -1;
				} else {
					throw createParseException(in, "json.parse.ObjectNotClosedError");
				}
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				throw createParseException(in, "json.parse.ArrayNotClosedError");
			} else {
				throw new IllegalStateException();
			}
		default:
			if (in.getLineNumber() > nameLineNumber) {
				in.back();
				nameLineNumber = Long.MAX_VALUE;
				if (getBeginType() == JSONEventType.START_OBJECT) {
					return BEFORE_NAME;
				} else if (getBeginType() == JSONEventType.START_ARRAY) {
					return BEFORE_VALUE;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		}
	}
}
