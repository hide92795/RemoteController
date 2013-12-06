package net.arnx.jsonic.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSONEventType;
import net.arnx.jsonic.JSONException;
import net.arnx.jsonic.io.InputSource;
import net.arnx.jsonic.util.LocalCache;

public class JSONParser {
	static final int BEFORE_ROOT = 0;
	static final int AFTER_ROOT = 1;
	static final int BEFORE_NAME = 2;
	static final int AFTER_NAME = 3;
	static final int BEFORE_VALUE = 4;
	static final int AFTER_VALUE = 5;
	
	private static final int[] ESCAPE_CHARS = new int[128];
	
	static {
		for (int i = 0; i < 32; i++) {
			ESCAPE_CHARS[i] = 3;
		}
		ESCAPE_CHARS['"'] = 1;
		ESCAPE_CHARS['\''] = 1;
		ESCAPE_CHARS['\\'] = 2;
		ESCAPE_CHARS[0x7F] = 3;
	}
	
	private InputSource in;
	
	private int maxDepth;
	private boolean interpretterMode;
	private boolean ignoreWhirespace;
	private LocalCache cache;
	
	private int state = BEFORE_ROOT;
	private List<JSONEventType> stack = new ArrayList<JSONEventType>();
	
	private JSONEventType type;
	private Object value;
	private boolean first;
	private boolean active;
	
	public JSONParser(InputSource in, int maxDepth, boolean interpretterMode, boolean ignoreWhirespace, LocalCache cache) {
		this.in = in;
		this.maxDepth = maxDepth;
		this.interpretterMode = interpretterMode;
		this.ignoreWhirespace = ignoreWhirespace;
		this.cache = cache;
		
		this.active = stack.size() < maxDepth;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public boolean isInterpretterMode() {
		return interpretterMode;
	}
	
	public boolean isIgnoreWhitespace() {
		return ignoreWhirespace;
	}
	
	public Object getValue() {
		return value;
	}
	
	public int getDepth() {
		if (type == JSONEventType.START_OBJECT || type == JSONEventType.START_ARRAY) {
			return stack.size();
		} else {
			return stack.size() + 1;
		}
	}
	
	public JSONEventType next() throws IOException {
		JSONEventType type = null;
		do {
			set(null, null, false);
			switch (state) {
			case BEFORE_ROOT:
				state = beforeRoot();
				break;
			case AFTER_ROOT:
				state = afterRoot();
				break;
			case BEFORE_NAME:
				state = beforeName();
				break;
			case AFTER_NAME:
				state = afterName();
				break;
			case BEFORE_VALUE:
				state = beforeValue();
				break;
			case AFTER_VALUE:
				state = afterValue();
				break;
			}
			
			if (getDepth() <= getMaxDepth()) {
				type = getType();
			}
		} while (state != -1 && type == null);
		
		return type;
	}
	
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
			throw createParseException(in, "json.parse.EmptyInputError");
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
	
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
		case -1:
			return -1;
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
		case '"':
			in.back();
			set(JSONEventType.NAME, parseString(in, false), false);
			return AFTER_NAME;
		case '}':
			if (isFirst() && getBeginType() == JSONEventType.START_OBJECT) {
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
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
	
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
		case ':':
			return BEFORE_VALUE;
		case -1:
			throw createParseException(in, "json.parse.ObjectNotClosedError");
		default:
			throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
		}
	}
	
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
		case '{':
			push(JSONEventType.START_OBJECT);
			return BEFORE_NAME;
		case '[':
			push(JSONEventType.START_ARRAY);
			return BEFORE_VALUE;
		case '"':
			in.back();
			set(JSONEventType.STRING, parseString(in, false), true);
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
	
	void push(JSONEventType type) {
		this.type = type;
		stack.add(type);
		first = true;
		active = stack.size() < maxDepth;
	}
	
	void set(JSONEventType type, Object value, boolean isValue) {
		this.type = type;
		this.value = value;
		if (isValue) first = false;
	}
	
	void pop() {
		JSONEventType beginType = stack.remove(stack.size()-1);
		if (beginType == JSONEventType.START_OBJECT) {
			this.type = JSONEventType.END_OBJECT;
		} else if (beginType == JSONEventType.START_ARRAY) {
			this.type = JSONEventType.END_ARRAY;
		} else {
			throw new IllegalStateException();
		}
		first = false;
		active = stack.size() < maxDepth;
	}
	
	JSONEventType getBeginType() {
		return (!stack.isEmpty()) ? stack.get(stack.size()-1) : null;
	}
	
	JSONEventType getType() {
		return type;
	}
	
	boolean isFirst() {
		return first;
	}
	
	Object parseString(InputSource in, boolean any) throws IOException {
		StringBuilder sb = active ? cache.getCachedBuffer() : null;
		
		int start = in.next();

		int rest = in.mark();
		int len = 0;
		
		int n = -1;
		while ((n = in.next()) != -1) {
			rest--;
			len++;
			
			if (n < ESCAPE_CHARS.length) {
				int type = ESCAPE_CHARS[n];
				if (type == 0) {
					if (rest == 0 && sb != null) in.copy(sb, len);
				} else if (type == 1) { // "'
					if (n == start) {
						if (len > 1 && sb != null) in.copy(sb, len - 1);
						break;
					} else {
						if (rest == 0 && sb != null) in.copy(sb, len);
					}
				} else if (type == 2) { // escape chars
					if (len > 0 && sb != null) in.copy(sb, len - 1);
					rest = 0;
					
					in.back();
					char c = parseEscape(in);
					if (sb != null) sb.append(c);
				} else { // control chars
					if (any) {
						if (rest == 0 && sb != null) in.copy(sb, len);
					} else {
						throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
					}
				}
			} else {
				if (rest == 0 && sb != null) in.copy(sb, len);
			}
			
			if (rest == 0) {
				rest = in.mark();
				len = 0;
			}
		}
		
		if (n != start) {
			throw createParseException(in, "json.parse.StringNotClosedError");
		}
		return (sb != null) ? cache.getString(sb) : null;
	}
	
	char parseEscape(InputSource in) throws IOException {
		int point = 1; // 0 '\' 1 'u' 2 'x' 3 'x' 4 'x' 5 'x' E
		char escape = '\0';
		
		int n = in.next();
		loop:while ((n = in.next()) != -1) {
			char c = (char)n;
			
			if (point == 1) {
				switch(c) {
				case 'b':
					escape = '\b';
					break loop;
				case 'f':
					escape = '\f';
					break loop;
				case 'n':
					escape = '\n';
					break loop;
				case 'r':
					escape = '\r';
					break loop;
				case 't':
					escape = '\t';
					break loop;
				case 'u':
					point = 2;
					break;
				default:
					escape = c;
					break loop;
				}
			} else {
				int hex = (c >= '0' && c <= '9') ? c-48 :
					(c >= 'A' && c <= 'F') ? c-65+10 :
					(c >= 'a' && c <= 'f') ? c-97+10 : -1;
				if (hex != -1) {
					escape |= (hex << ((5-point)*4));
					if (point != 5) {
						point++;
					} else {
						break loop;
					}
				} else {
					throw createParseException(in, "json.parse.IllegalUnicodeEscape", c);
				}
			}
		}
		
		return escape;
	}
	
	Object parseNumber(InputSource in) throws IOException {
		int point = 0; // 0 '(-)' 1 '0' | ('[1-9]' 2 '[0-9]*') 3 '(.)' 4 '[0-9]' 5 '[0-9]*' 6 'e|E' 7 '[+|-]' 8 '[0-9]' 9 '[0-9]*' E
		StringBuilder sb = active ? cache.getCachedBuffer() : null;
		
		int n = -1;
		
		int rest = in.mark();
		int len = 0;
		loop:while ((n = in.next()) != -1) {
			rest--;
			len++;
			
			char c = (char)n;
			switch(c) {
			case '+':
				if (point == 7) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 8;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
				break;
			case '-':
				if (point == 0) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 1;
				} else if (point == 7) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 8;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
				break;
			case '.':
				if (point == 2 || point == 3) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 4;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
				break;
			case 'e':
			case 'E':
				if (point == 2 || point == 3 || point == 5 || point == 6) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 7;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
				break;
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
				if (point == 0 || point == 1) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = (c == '0') ? 3 : 2;
				} else if (point == 2 || point == 5 || point == 9) {
					if (rest == 0 && sb != null) in.copy(sb, len);
				} else if (point == 4) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 5;
				} else if (point == 7 || point == 8) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 9;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
				break;
			default:
				if (point == 2 || point == 3 || point == 5 || point == 6 || point == 9) {
					if (len > 1 && sb != null) in.copy(sb, len - 1);
					in.back();
					break loop;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", c);
				}
			}
			
			if (rest == 0) {
				rest = in.mark();
				len = 0;
			}
		}
		
		return (sb != null) ? cache.getBigDecimal(sb) : null;
	}
	
	Object parseLiteral(InputSource in, String expected, Object result) throws IOException {
		int pos = 0;
		int n = -1;
		while ((n = in.next()) != -1) {
			char c = (char)n;
			if (pos < expected.length() && c == expected.charAt(pos++)) {
				if (pos == expected.length()) {
					return (stack.size() < maxDepth) ? result : null;
				}
			} else {
				break;
			}
		}
		
		throw createParseException(in, "json.parse.UnrecognizedLiteral", expected.substring(0, pos));
	}

	Object parseLiteral(InputSource in, boolean asValue) throws IOException {
		int point = 0; // 0 'IdStart' 1 'IdPart' ... !'IdPart' E
		StringBuilder sb = active ? cache.getCachedBuffer() : null;
		
		int n = -1;
		while ((n = in.next()) != -1) {
			if (n == '\\') {
				in.back();
				n = parseEscape(in);
			}
			
			if (point == 0) {
				if (Character.isJavaIdentifierStart(n)) {
					if (!active && (n == 'n' || n == 't' || n == 'f') && sb != null) {
						sb = cache.getCachedBuffer();
					}
					
					if (sb != null) sb.append((char)n);
					point = 1;
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
			} else if (point == 1 && (Character.isJavaIdentifierPart(n) || n == '.')) {
				if (!active && sb != null && sb.length() == 5) {
					sb = null;
				}
				
				if (sb != null) sb.append((char)n);
			} else {
				in.back();
				break;
			}
		}
		
		String str = (sb != null) ? cache.getString(sb) : null;
		if (asValue && str != null) {
			if ("null".equals(str)) {
				type = JSONEventType.NULL;
				return null;
			} else if ("true".equals(str)) {
				type = JSONEventType.BOOLEAN;
				return (active) ? Boolean.TRUE : null;
			} else if ("false".equals(str)) {
				type = JSONEventType.BOOLEAN;
				return (active) ? Boolean.FALSE : null;
			}
		}
		type = JSONEventType.STRING;
		return (active) ? str : null;
	}
	
	String parseComment(InputSource in) throws IOException {
		int point = 0; // 0 '/' 1 '*' 2  '*' 3 '/' E or  0 '/' 1 '/' 4  '\r|\n|\r\n' E
		StringBuilder sb = !isIgnoreWhitespace() ? cache.getCachedBuffer() : null;
		
		int n = -1;
		
		int rest = in.mark();
		int len = 0;
		loop:while ((n = in.next()) != -1) {
			rest--;
			len++;
			
			switch(n) {
			case '/':
				if (point == 0) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 1;
				} else if (point == 1) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 4;
				} else if (point == 3) {
					if (len > 1 && sb != null) in.copy(sb, len);
					break loop;
				} else if (point == 2 || point == 4) {
					if (rest == 0 && sb != null) in.copy(sb, len);
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
				break;
			case '*':
				if (point == 1) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 2;
				} else if (point == 2) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 3;
				} else if (point == 3 || point == 4) {
					if (rest == 0 && sb != null) in.copy(sb, len);
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
				break;
			case '\r':
			case '\n':
				if (point == 2 || point == 3) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 2;
				} else if (point == 4) {
					if (n == '\r') {
						n = in.next();
						in.back();
						if (n == '\n') {
							if (rest == 0 && sb != null) in.copy(sb, len);
							break;
						}
					}
					if (len > 1 && sb != null) in.copy(sb, len);
					break loop;	
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
				break;
			default:
				if (point == 3) {
					if (rest == 0 && sb != null) in.copy(sb, len);
					point = 2;
				} else if (point == 2 || point == 4) {
					if (rest == 0 && sb != null) in.copy(sb, len);
				} else {
					throw createParseException(in, "json.parse.UnexpectedChar", (char)n);
				}
			}
			
			if (rest == 0) {
				rest = in.mark();
				len = 0;
			}
		}
		
		return (sb != null) ? cache.getString(sb) : null;
	}
	
	String parseWhitespace(InputSource in) throws IOException {
		StringBuilder sb = !isIgnoreWhitespace() ? cache.getCachedBuffer() : null;
		
		int n = -1;
		
		int rest = in.mark();
		int len = 0;
		while ((n = in.next()) != -1) {
			rest--;
			len++;
			
			if (n == ' ' || n == '\t' || n == '\r' || n == '\n') {
				if (rest == 0 && sb != null) in.copy(sb, len);
			} else {
				if (len > 1 && sb != null) in.copy(sb, len - 1);
				in.back();
				break;
			}
			
			if (rest == 0) {
				rest = in.mark();
				len = 0;
			}
		}
		
		return (sb != null) ? cache.getString(sb) : null;
	}
	
	JSONException createParseException(InputSource in, String id) {
		return createParseException(in, id, (Object[])null);
	}
	
	JSONException createParseException(InputSource in, String id, Object... args) {
		String message = cache.getMessage(id, args);
		return new JSONException("" + in.getLineNumber() + ": " + message + "\n" + in.toString() + " <- ?",
				JSONException.PARSE_ERROR, in.getLineNumber(), in.getColumnNumber(), in.getOffset());
	}
}
