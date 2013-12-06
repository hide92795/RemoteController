package net.arnx.jsonic.parse;

import java.io.IOException;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.io.InputSource;
import net.arnx.jsonic.util.LocalCache;

public class ScriptParser extends JSONParser {
	private InputSource in;
	
	public ScriptParser(InputSource in, int maxDepth, boolean interpretterMode, boolean ignoreWhirespace, LocalCache cache) {
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
		case '"':
		case '\'':
			in.back();
			set(JSONEventType.STRING, parseString(in, true), true);
			return AFTER_ROOT;
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
			return AFTER_ROOT;	
		case 't':
			in.back();
			set(JSONEventType.BOOLEAN, parseLiteral(in, "true", Boolean.TRUE), true);
			return AFTER_ROOT;
		case 'f':
			in.back();
			set(JSONEventType.BOOLEAN, parseLiteral(in, "false", Boolean.FALSE), true);
			return AFTER_ROOT;
		case 'n':
			in.back();
			set(JSONEventType.NULL, parseLiteral(in, "null", null), true);
			return AFTER_ROOT;
		case -1:
			if (isInterpretterMode()) {
				return -1;
			}
			throw createParseException(in, "json.parse.EmptyInputError");
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
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
		case '{':
		case '[':
		case '"':
		case '\'':
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
		case 't':
		case 'f':
		case 'n':
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
					return AFTER_ROOT;
				} else {
					return AFTER_VALUE;							
				}
			} else {
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case -1:
			throw createParseException(in, "json.parse.ObjectNotClosedError");
		default:
			in.back();
			set(JSONEventType.NAME, parseLiteral(in, false), false);
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
			return AFTER_VALUE;	
		case 't':
			in.back();
			set(JSONEventType.BOOLEAN, parseLiteral(in, "true", Boolean.TRUE), true);
			return AFTER_VALUE;
		case 'f':
			in.back();
			set(JSONEventType.BOOLEAN, parseLiteral(in, "false", Boolean.FALSE), true);
			return AFTER_VALUE;
		case 'n':
			in.back();
			set(JSONEventType.NULL, parseLiteral(in, "null", null), true);
			return AFTER_VALUE;
		case ']':
			if (isFirst() && getBeginType() == JSONEventType.START_ARRAY) {
				pop();
				if (getBeginType() == null) {
					return AFTER_ROOT;
				} else {
					return AFTER_VALUE;							
				}
			} else{
				throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
			}
		case -1:
			if (getBeginType() == JSONEventType.START_OBJECT) {
				throw createParseException(in, "json.parse.ObjectNotClosedError");
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				throw createParseException(in, "json.parse.ArrayNotClosedError");
			} else {
				throw new IllegalStateException();
			}
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
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
					return AFTER_ROOT;
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
				throw createParseException(in, "json.parse.ObjectNotClosedError");
			} else if (getBeginType() == JSONEventType.START_ARRAY) {
				throw createParseException(in, "json.parse.ArrayNotClosedError");
			} else {
				throw new IllegalStateException();
			}
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
}
