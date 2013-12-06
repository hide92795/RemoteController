/*
 * Copyright 2007-2012 Hidekatsu Izuno
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.arnx.jsonic.JSON.Context;
import net.arnx.jsonic.io.InputSource;
import net.arnx.jsonic.parse.JSONParser;
import net.arnx.jsonic.parse.ScriptParser;
import net.arnx.jsonic.parse.TraditionalParser;

public class JSONReader {
	private Context context;
	private JSONParser parser;
	private JSONEventType type;

	JSONReader(Context context, InputSource in, boolean multilineMode, boolean ignoreWhitespace) {
		this.context = context;

		switch (context.getMode()) {
		case STRICT:
			parser = new JSONParser(in, context.getMaxDepth(), multilineMode, ignoreWhitespace, context.getLocalCache());
			break;
		case SCRIPT:
			parser = new ScriptParser(in, context.getMaxDepth(), multilineMode, ignoreWhitespace, context.getLocalCache());
			break;
		default:
			parser = new TraditionalParser(in, context.getMaxDepth(), multilineMode, ignoreWhitespace, context.getLocalCache());
		}
	}

	public JSONEventType next() throws IOException {
		type = parser.next();
		return type;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue(Class<T> cls) throws IOException {
		return (T) context.convertInternal(getValue(), cls);
	}

	public Object getValue(Type t) throws IOException {
		return context.convertInternal(getValue(), t);
	}

	public Map<?, ?> getMap() throws IOException {
		return (Map<?, ?>) getValue();
	}

	public List<?> getList() throws IOException {
		return (List<?>) getValue();
	}

	public String getString() throws IOException {
		return (String) parser.getValue();
	}

	public BigDecimal getNumber() throws IOException {
		return (BigDecimal) parser.getValue();
	}

	public Boolean getBoolean() throws IOException {
		return (Boolean) parser.getValue();
	}

	Object getValue() throws IOException {
		if (type == null) {
			throw new IllegalStateException("you should call next.");
		}

		int ilen = 0;
		int[] istack = new int[8];

		int olen = 0;
		Object[] ostack = new Object[16];

		do {
			switch (type) {
			case START_OBJECT:
			case START_ARRAY:
				istack = iexpand(istack, ilen + 1);
				istack[ilen++] = olen;
				break;
			case NAME:
			case STRING:
			case NUMBER:
			case BOOLEAN:
			case NULL:
				Object value = parser.getValue();
				ostack = oexpand(ostack, olen + 1);
				ostack[olen++] = value;
				break;
			case END_ARRAY: {
				int start = istack[--ilen];
				int len = olen - start;
				List<Object> array = new ArrayList<Object>(len);
				for (int i = start; i < olen; i++) {
					array.add(ostack[i]);
				}
				olen = start;
				ostack = oexpand(ostack, olen + 1);
				ostack[olen++] = array;
				break;
			}
			case END_OBJECT:
				int start = istack[--ilen];
				int len = olen - start;
				Map<Object, Object> object = new LinkedHashMap<Object, Object>((len < 2) ? 4 : (len < 4) ? 8 : (len < 12) ? 16 : (int) (len / 0.75f) + 1);
				for (int i = start; i < olen; i += 2) {
					object.put(ostack[i], ostack[i + 1]);
				}
				olen = start;
				ostack = oexpand(ostack, olen + 1);
				ostack[olen++] = object;
				break;
			default:
				break;
			}

			if (parser.isInterpretterMode() && ilen == 0) {
				break;
			}
		} while ((type = parser.next()) != null);

		return ostack[0];
	}

	public int getDepth() {
		return parser.getDepth();
	}

	private int[] iexpand(int[] array, int min) {
		if (min > array.length) {
			int[] narray = new int[array.length * 3 / 2 + 1];
			System.arraycopy(array, 0, narray, 0, array.length);
			array = narray;
		}
		return array;
	}

	private Object[] oexpand(Object[] array, int min) {
		if (min > array.length) {
			Object[] narray = new Object[array.length * 3 / 2 + 1];
			System.arraycopy(array, 0, narray, 0, array.length);
			array = narray;
		}
		return array;
	}
}
