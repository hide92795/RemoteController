package net.arnx.jsonic;

import java.io.Flushable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Struct;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.arnx.jsonic.JSON.Context;
import net.arnx.jsonic.JSON.Mode;
import net.arnx.jsonic.io.OutputSource;
import net.arnx.jsonic.util.Base64;
import net.arnx.jsonic.util.BeanInfo;
import net.arnx.jsonic.util.ClassUtil;
import net.arnx.jsonic.util.PropertyInfo;

interface Formatter {
	boolean format(Context context, Object src, Object o, OutputSource out) throws Exception;
}

final class NullFormatter implements Formatter {
	public static final NullFormatter INSTANCE = new NullFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		out.append("null");
		return false;
	}
}

final class PlainFormatter implements Formatter {
	public static final PlainFormatter INSTANCE = new PlainFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		out.append(o.toString());
		return false;
	}
}

final class StringFormatter implements Formatter {
	public static final StringFormatter INSTANCE = new StringFormatter();

	private static final int[] ESCAPE_CHARS = new int[128];

	static {
		for (int i = 0; i < 32; i++) {
			ESCAPE_CHARS[i] = -1;
		}
		ESCAPE_CHARS['\b'] = 'b';
		ESCAPE_CHARS['\t'] = 't';
		ESCAPE_CHARS['\n'] = 'n';
		ESCAPE_CHARS['\f'] = 'f';
		ESCAPE_CHARS['\r'] = 'r';
		ESCAPE_CHARS['"'] = '"';
		ESCAPE_CHARS['\\'] = '\\';
		ESCAPE_CHARS['<'] = -2;
		ESCAPE_CHARS['>'] = -2;
		ESCAPE_CHARS[0x7F] = -1;
	}


	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		serialize(context, o.toString(), out);
		return false;
	}

	static void serialize(final Context context, final String s, final OutputSource out) throws Exception {
		out.append('"');
		int start = 0;
		final int length = s.length();
		for (int i = 0; i < length; i++) {
			int c = s.charAt(i);
			if (c < ESCAPE_CHARS.length) {
				int x = ESCAPE_CHARS[c];
				if (x == 0) {
					// no handle
				} else if (x > 0) {
					if (start < i) out.append(s, start, i);
					out.append('\\');
					out.append((char)x);
					start = i + 1;
				} else if (x == -1 || (x == -2 && context.getMode() != Mode.STRICT)) {
					if (start < i) out.append(s, start, i);
					out.append("\\u00");
					out.append("0123456789ABCDEF".charAt(c / 16));
					out.append("0123456789ABCDEF".charAt(c % 16));
					start = i + 1;
				}
			} else if (c == '\u2028') {
				if (start < i) out.append(s, start, i);
				out.append("\\u2028");
				start = i + 1;
			} else if (c == '\u2029') {
				if (start < i) out.append(s, start, i);
				out.append("\\u2029");
				start = i + 1;
			}
		}
		if (start < length) out.append(s, start, length);
		out.append('"');
	}
}

final class TimeZoneFormatter implements Formatter {
	public static final TimeZoneFormatter INSTANCE = new TimeZoneFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, ((TimeZone)o).getID(), out);
	}
}

final class CharsetFormatter implements Formatter {
	public static final CharsetFormatter INSTANCE = new CharsetFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, ((Charset)o).name(), out);
	}
}

final class InetAddressFormatter implements Formatter {
	public static final InetAddressFormatter INSTANCE = new InetAddressFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		Class<?> inetAddressClass = ClassUtil.findClass("java.net.InetAddress");
		try {
			String text = (String)inetAddressClass.getMethod("getHostAddress").invoke(o);
			return StringFormatter.INSTANCE.format(context, src, text, out);
		} catch (Exception e) {
			return NullFormatter.INSTANCE.format(context, src, null, out);
		}
	}
}

final class CharacterDataFormatter implements Formatter {
	public static final CharacterDataFormatter INSTANCE = new CharacterDataFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, ((CharacterData)o).getData(), out);
	}
}

final class NumberFormatter implements Formatter {
	public static final NumberFormatter INSTANCE = new NumberFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		if (f != null) {
			StringFormatter.serialize(context, f.format(o), out);
		} else {
			out.append(o.toString());
		}
		return false;
	}
}

final class EnumFormatter implements Formatter {
	public static final EnumFormatter INSTANCE = new EnumFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		if (context.getEnumStyle() != null) {
			return StringFormatter.INSTANCE.format(context, src, context.getEnumStyle().to(((Enum<?>)o).name()), out);
		} else {
			return NumberFormatter.INSTANCE.format(context, src, ((Enum<?>)o).ordinal(), out);
		}
	}
}

final class FloatFormatter implements Formatter {
	public static final FloatFormatter INSTANCE = new FloatFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		if (f != null) {
			StringFormatter.serialize(context, f.format(o), out);
		} else {
			double d = ((Number) o).doubleValue();
			if (Double.isNaN(d) || Double.isInfinite(d)) {
				if (context.getMode() != Mode.SCRIPT) {
					out.append('"');
					out.append(o.toString());
					out.append('"');
				} else if (Double.isNaN(d)) {
					out.append("Number.NaN");
				} else {
					out.append("Number.");
					out.append((d > 0) ? "POSITIVE" : "NEGATIVE");
					out.append("_INFINITY");
				}
			} else {
				out.append(o.toString());
			}
		}
		return false;
	}
}

final class DateFormatter implements Formatter {
	public static final DateFormatter INSTANCE = new DateFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		Date date = (Date) o;
		DateFormat f = context.getDateFormat();
		if (f != null) {
			StringFormatter.serialize(context, f.format(o), out);
		} else if (context.getMode() == Mode.SCRIPT) {
			out.append("new Date(");
			out.append(Long.toString(date.getTime()));
			out.append(")");
		} else {
			out.append(Long.toString(date.getTime()));
		}
		return false;
	}
}

final class CalendarFormatter implements Formatter {
	public static final CalendarFormatter INSTANCE = new CalendarFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return DateFormatter.INSTANCE.format(context, src, ((Calendar)o).getTime(), out);
	}	
}

final class BooleanArrayFormatter implements Formatter {
	public static final BooleanArrayFormatter INSTANCE = new BooleanArrayFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		out.append('[');
		boolean[] array = (boolean[]) o;
		for (int i = 0; i < array.length; i++) {
			out.append(String.valueOf(array[i]));
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class ByteArrayFormatter implements Formatter {
	public static final ByteArrayFormatter INSTANCE = new ByteArrayFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		StringFormatter.serialize(context, Base64.encode((byte[]) o), out);
		return false;
	}
}

final class SerializableFormatter implements Formatter {
	public static final SerializableFormatter INSTANCE = new SerializableFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, Base64.encode(ClassUtil.serialize(o)), out);
	}
}

final class ShortArrayFormatter implements Formatter {
	public static final ShortArrayFormatter INSTANCE = new ShortArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		short[] array = (short[]) o;
		out.append('[');
		for (int i = 0; i < array.length; i++) {
			if (f != null) {
				StringFormatter.serialize(context, f.format(array[i]), out);
			} else {
				out.append(String.valueOf(array[i]));
			}
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class IntArrayFormatter implements Formatter {
	public static final IntArrayFormatter INSTANCE = new IntArrayFormatter();

	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		int[] array = (int[]) o;
		out.append('[');
		for (int i = 0; i < array.length; i++) {
			if (f != null) {
				StringFormatter.serialize(context, f.format(array[i]), out);
			} else {
				out.append(String.valueOf(array[i]));
			}
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class LongArrayFormatter implements Formatter {
	public static final LongArrayFormatter INSTANCE = new LongArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		long[] array = (long[]) o;
		out.append('[');
		for (int i = 0; i < array.length; i++) {
			if (f != null) {
				StringFormatter.serialize(context, f.format(array[i]), out);
			} else {
				out.append(String.valueOf(array[i]));
			}
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class FloatArrayFormatter implements Formatter {
	public static final FloatArrayFormatter INSTANCE = new FloatArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		float[] array = (float[]) o;
		out.append('[');
		for (int i = 0; i < array.length; i++) {
			if (Float.isNaN(array[i]) || Float.isInfinite(array[i])) {
				if (context.getMode() != Mode.SCRIPT) {
					out.append('"');
					out.append(Float.toString(array[i]));
					out.append('"');
				} else if (Double.isNaN(array[i])) {
					out.append("Number.NaN");
				} else {
					out.append("Number.");
					out.append((array[i] > 0) ? "POSITIVE" : "NEGATIVE");
					out.append("_INFINITY");
				}
			} else if (f != null) {
				StringFormatter.serialize(context, f.format(array[i]), out);
			} else {
				out.append(String.valueOf(array[i]));
			}
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class DoubleArrayFormatter implements Formatter {
	public static final DoubleArrayFormatter INSTANCE = new DoubleArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		NumberFormat f = context.getNumberFormat();
		double[] array = (double[]) o;
		out.append('[');
		for (int i = 0; i < array.length; i++) {
			if (Double.isNaN(array[i]) || Double.isInfinite(array[i])) {
				if (context.getMode() != Mode.SCRIPT) {
					out.append('"');
					out.append(Double.toString(array[i]));
					out.append('"');
				} else if (Double.isNaN(array[i])) {
					out.append("Number.NaN");
				} else {
					out.append("Number.");
					out.append((array[i] > 0) ? "POSITIVE" : "NEGATIVE");
					out.append("_INFINITY");
				}
			} else if (f != null) {
				StringFormatter.serialize(context, f.format(array[i]), out);
			} else {
				out.append(String.valueOf(array[i]));
			}
			if (i != array.length - 1) {
				out.append(',');
				if (context.isPrettyPrint()) {
					out.append(' ');
				}
			}
		}
		out.append(']');
		return true;
	}
}

final class ObjectArrayFormatter implements Formatter {
	public static final ObjectArrayFormatter INSTANCE = new ObjectArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		final Object[] array = (Object[]) o;
		final JSONHint hint = context.getHint();
		
		Class<?> lastClass = null;
		Formatter lastFormatter = null;

		out.append('[');
		int i = 0;
		for (; i < array.length; i++) {
			Object item = array[i];
			if (item == src)
				item = null;

			if (i != 0)
				out.append(',');
			if (context.isPrettyPrint()) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
			context.enter(i, hint);
			item = context.preformatInternal(item);
			if (item == null) {
				NullFormatter.INSTANCE.format(context, src, item, out);
			} else if (hint == null) {
				if (item.getClass().equals(lastClass)) {
					lastFormatter.format(context, src, item, out);
				} else {
					lastFormatter = context.formatInternal(item, out);
					lastClass = item.getClass();
				}
			} else {
				context.formatInternal(item, out);
			}
			context.exit();
		}
		if (context.isPrettyPrint() && i > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append(']');
		return true;
	}
}

final class SQLArrayFormatter implements Formatter {
	public static final SQLArrayFormatter INSTANCE = new SQLArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		Object array;
		try {
			array = ((java.sql.Array)o).getArray();
		} catch (SQLException e) {
			array = null;
		}
		if (array == null) array = new Object[0];
		return ObjectArrayFormatter.INSTANCE.format(context, src, array, out);
	}
}

final class StructFormmatter implements Formatter {
	public static final StructFormmatter INSTANCE = new StructFormmatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		Object value;
		try {
			value = ((Struct)o).getAttributes();
		} catch (SQLException e) {
			value = null;
		}
		if (value == null) value = new Object[0];
		return ObjectArrayFormatter.INSTANCE.format(context, src, o, out);
	}
}

final class ByteFormatter implements Formatter {
	public static final ByteFormatter INSTANCE = new ByteFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		out.append(Integer.toString(((Byte)o).byteValue() & 0xFF));
		return false;
	}
}

final class ClassFormatter implements Formatter {
	public static final ClassFormatter INSTANCE = new ClassFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, ((Class<?>)o).getName(), out);
	}
}

final class LocaleFormatter implements Formatter {
	public static final LocaleFormatter INSTANCE = new LocaleFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, ((Locale)o).toString().replace('_', '-'), out);
	}
}

final class CharArrayFormatter implements Formatter {
	public static final CharArrayFormatter INSTANCE = new CharArrayFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return StringFormatter.INSTANCE.format(context, src, String.valueOf((char[])o), out);
	}
}

final class ListFormatter implements Formatter {
	public static final ListFormatter INSTANCE = new ListFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		final List<?> list = (List<?>)o;
		final JSONHint hint = context.getHint();
		final int length = list.size();
		
		Class<?> lastClass = null;
		Formatter lastFormatter = null;
		
		out.append('[');
		int count = 0;
		while (count < length) {
			Object item = list.get(count);
			if (item == src) item = null;
			
			if (count != 0) out.append(',');
			if (context.isPrettyPrint()) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
			context.enter(count, hint);
			item = context.preformatInternal(item);
			if (item == null) {
				NullFormatter.INSTANCE.format(context, src, item, out);
			} else if (hint == null) {
				if (item.getClass().equals(lastClass)) {
					lastFormatter.format(context, src, item, out);
				} else {
					lastFormatter = context.formatInternal(item, out);
					lastClass = item.getClass();
				}
			} else {
				context.formatInternal(item, out);
			}
			context.exit();
			count++;
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append(']');
		return true;
	}
}

final class IteratorFormatter implements Formatter {
	public static final IteratorFormatter INSTANCE = new IteratorFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		final Iterator<?> t = (Iterator<?>)o;
		final JSONHint hint = context.getHint();
		
		Class<?> lastClass = null;
		Formatter lastFormatter = null;
		
		out.append('[');
		int count = 0;
		while(t.hasNext()) {
			Object item = t.next();
			if (item == src)
				item = null;

			if (count != 0) out.append(',');
			if (context.isPrettyPrint()) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
			context.enter(count, hint);
			item = context.preformatInternal(item);
			if (item == null) {
				NullFormatter.INSTANCE.format(context, src, item, out);
			} else if (hint == null) {
				if (item.getClass().equals(lastClass)) {
					lastFormatter.format(context, src, item, out);
				} else {
					lastFormatter = context.formatInternal(item, out);
					lastClass = item.getClass();
				}
			} else {
				context.formatInternal(item, out);
			}
			context.exit();
			count++;
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append(']');
		return true;
	}
}

final class IterableFormatter implements Formatter {
	public static final IterableFormatter INSTANCE = new IterableFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return IteratorFormatter.INSTANCE.format(context, src, ((Iterable<?>) o).iterator(), out);
	}
}

final class EnumerationFormatter implements Formatter {
	public static final EnumerationFormatter INSTANCE = new EnumerationFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		final Enumeration<?> e = (Enumeration<?>)o;
		final JSONHint hint = context.getHint();
		
		out.append('[');
		int count = 0;
		
		Class<?> lastClass = null;
		Formatter lastFormatter = null;
		while (e.hasMoreElements()) {
			Object item = e.nextElement();
			if (item == src) item = null;

			if (count != 0) out.append(',');
			if (context.isPrettyPrint()) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
			context.enter(count, hint);
			item = context.preformatInternal(item);
			if (item == null) {
				NullFormatter.INSTANCE.format(context, src, item, out);
			} else if (hint == null) {
				if (item.getClass().equals(lastClass)) {
					lastFormatter.format(context, src, item, out);
				} else {
					lastFormatter = context.formatInternal(item, out);
					lastClass = item.getClass();
				}
			} else {
				context.formatInternal(item, out);
			}
			context.exit();
			count++;
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append(']');
		return true;
	}
}

final class MapFormatter implements Formatter {
	public static final MapFormatter INSTANCE = new MapFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		final Map<?, ?> map = (Map<?, ?>)o;
		final JSONHint hint = context.getHint();

		Class<?> lastClass = null;
		Formatter lastFormatter = null;

		out.append('{');
		int count = 0;
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			if (key == null) continue;

			Object value = entry.getValue();
			if (value == src) continue;

			if (count != 0) out.append(',');
			if (context.isPrettyPrint()) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
			StringFormatter.serialize(context, key.toString(), out);
			out.append(':');
			if (context.isPrettyPrint()) out.append(' ');
			context.enter(key, hint);
			value = context.preformatInternal(value);
			if (value == null) {
				NullFormatter.INSTANCE.format(context, src, value, out);
			} else if (hint == null) {
				if (value.getClass().equals(lastClass)) {
					lastFormatter.format(context, src, value, out);
				} else {
					lastFormatter = context.formatInternal(value, out);
					lastClass = value.getClass();
				}
			} else {
				context.formatInternal(value, out);
			}
			context.exit();
			count++;
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append('}');
		return true;
	}
}

final class ObjectFormatter implements Formatter {
	private Class<?> cls;
	private transient List<PropertyInfo> props;
	
	public  ObjectFormatter(Class<?> cls) {
		this.cls = cls;
	}
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		if (props == null) props = getGetProperties(context, cls);
		
		out.append('{');
		int count = 0;
		final int length = props.size();
		for (int p = 0; p < length; p++) {
			PropertyInfo prop = props.get(p);
			Object value = null;
			Exception cause = null;
			
			try {
				value = prop.get(o);
				if (value == src || (context.isSuppressNull() && value == null)) {
					continue;
				}

				if (count != 0) out.append(',');
				if (context.isPrettyPrint()) {
					out.append('\n');
					int indent = context.getInitialIndent() + context.getDepth() + 1;
					for (int j = 0; j < indent; j++) {
						out.append(context.getIndentText());
					}
				}
			} catch (Exception e) {
				cause = e;
			}
			
			StringFormatter.serialize(context, prop.getName(), out);
			out.append(':');
			if (context.isPrettyPrint()) out.append(' ');
			context.enter(prop.getName(), prop.getReadAnnotation(JSONHint.class));
			if (cause != null) throw cause;
			
			value = context.preformatInternal(value);
			context.formatInternal(value, out);
			context.exit();
			count++;
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append('}');
		return true;
	}
	
	static List<PropertyInfo> getGetProperties(Context context, Class<?> c) {
		Map<String, PropertyInfo> props = new HashMap<String, PropertyInfo>();
		
		// Field
		for (PropertyInfo prop : BeanInfo.get(c).getProperties()) {
			Field f = prop.getField();
			if (f == null || context.ignoreInternal(c, f)) continue;
			
			JSONHint hint = f.getAnnotation(JSONHint.class);
			String name = null;
			int ordinal = prop.getOrdinal();
			if (hint != null) {
				if (hint.ignore()) continue;
				ordinal = hint.ordinal();
				if (hint.name().length() != 0) name = hint.name();
			}
			
			if (name == null) {
				name = context.normalizeInternal(prop.getName());
				if (context.getPropertyStyle() != null) {
					name = context.getPropertyStyle().to(name);
				}
			}
			
			if (!name.equals(prop.getName()) || ordinal != prop.getOrdinal() || f != prop.getReadMember()) {
				props.put(name, new PropertyInfo(prop.getBeanClass(), name, 
					prop.getField(), null, null, prop.isStatic(), ordinal));
			} else {
				props.put(name, prop);
			}
		}
		
		// Method
		for (PropertyInfo prop : BeanInfo.get(c).getProperties()) {
			Method m = prop.getReadMethod();
			if (m == null || context.ignoreInternal(c, m)) continue;
			
			JSONHint hint = m.getAnnotation(JSONHint.class);
			String name = null;
			int ordinal = prop.getOrdinal();
			if (hint != null) {
				if (hint.ignore()) continue;
				ordinal = hint.ordinal();
				if (hint.name().length() != 0) name = hint.name();
			}
			
			if (name == null) {
				name = context.normalizeInternal(prop.getName());
				if (context.getPropertyStyle() != null) {
					name = context.getPropertyStyle().to(name);
				}
			}
			
			if (!name.equals(prop.getName()) || ordinal != prop.getOrdinal()) {
				props.put(name, new PropertyInfo(prop.getBeanClass(), name, 
					null, prop.getReadMethod(), null, prop.isStatic(), ordinal));
			} else {
				props.put(name, prop);
			}
		}
		
		List<PropertyInfo> list = new ArrayList<PropertyInfo>(props.values());
		Collections.sort(list);
		return list;
	}
}

final class DynaBeanFormatter implements Formatter {
	public static final DynaBeanFormatter INSTANCE = new DynaBeanFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		out.append('{');
		int count = 0;
		try {
			Class<?> dynaBeanClass = ClassUtil.findClass("org.apache.commons.beanutils.DynaBean");

			Object dynaClass = dynaBeanClass.getMethod("getDynaClass")
					.invoke(o);
			Object[] dynaProperties = (Object[]) dynaClass.getClass()
					.getMethod("getDynaProperties").invoke(dynaClass);

			if (dynaProperties != null && dynaProperties.length > 0) {
				Method getName = dynaProperties[0].getClass().getMethod("getName");
				Method get = dynaBeanClass.getMethod("get", String.class);
				JSONHint hint = context.getHint();
				
				for (Object dp : dynaProperties) {
					Object name = null;
					try {
						name = getName.invoke(dp);
					} catch (InvocationTargetException e) {
						throw e;
					} catch (Exception e) {
					}
					if (name == null) continue;

					Object value = null;
					Exception cause = null;

					try {
						value = get.invoke(o, name);
					} catch (Exception e) {
						cause = e;
					}

					if (value == src || (cause == null && context.isSuppressNull() && value == null)) {
						continue;
					}

					if (count != 0) out.append(',');
					if (context.isPrettyPrint()) {
						out.append('\n');
						int indent = context.getInitialIndent() + context.getDepth() + 1;
						for (int j = 0; j < indent; j++) {
							out.append(context.getIndentText());
						}
					}
					StringFormatter.serialize(context, name.toString(), out);
					out.append(':');
					if (context.isPrettyPrint()) out.append(' ');
					context.enter(name, hint);
					if (cause != null) throw cause;
					value = context.preformatInternal(value);
					context.formatInternal(value, out);
					context.exit();
					count++;
				}
			}
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof Error) {
				throw (Error)e.getCause();
			} else if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException)e.getCause();
			} else {
				throw (Exception)e.getCause();
			}
		} catch (Exception e) {
			// no handle
		}
		if (context.isPrettyPrint() && count > 0) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append('}');
		return true;
	}
}

final class DOMElementFormatter implements Formatter {
	public static final DOMElementFormatter INSTANCE = new DOMElementFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		Element elem = (Element)o;
		out.append('[');
		StringFormatter.serialize(context, elem.getTagName(), out);

		out.append(',');
		if (context.isPrettyPrint()) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth() + 1;
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append('{');
		if (elem.hasAttributes()) {
			NamedNodeMap names = elem.getAttributes();
			for (int i = 0; i < names.getLength(); i++) {
				if (i != 0) {
					out.append(',');
				}
				if (context.isPrettyPrint() && names.getLength() > 1) {
					out.append('\n');
					for (int j = 0; j < context.getDepth() + 2; j++)
						out.append('\t');
				}
				Node node = names.item(i);
				if (node instanceof Attr) {
					StringFormatter.serialize(context, node.getNodeName(), out);
					out.append(':');
					if (context.isPrettyPrint())
						out.append(' ');
					StringFormatter.serialize(context, node.getNodeValue(), out);
				}
			}
			if (context.isPrettyPrint() && names.getLength() > 1) {
				out.append('\n');
				int indent = context.getInitialIndent() + context.getDepth() + 1;
				for (int j = 0; j < indent; j++) {
					out.append(context.getIndentText());
				}
			}
		}
		out.append('}');
		if (elem.hasChildNodes()) {
			NodeList nodes = elem.getChildNodes();
			JSONHint hint = context.getHint();
			for (int i = 0; i < nodes.getLength(); i++) {
				Object value = nodes.item(i);
				if ((value instanceof Element)
						|| (value instanceof CharacterData && !(value instanceof Comment))) {
					out.append(',');
					if (context.isPrettyPrint()) {
						out.append('\n');
						int indent = context.getInitialIndent() + context.getDepth() + 1;
						for (int j = 0; j < indent; j++) {
							out.append(context.getIndentText());
						}
					}
					context.enter(i + 2, hint);
					value = context.preformatInternal(value);
					context.formatInternal(value, out);
					context.exit();
					if (out instanceof Flushable)
						((Flushable) out).flush();
				}
			}
		}
		if (context.isPrettyPrint()) {
			out.append('\n');
			int indent = context.getInitialIndent() + context.getDepth();
			for (int j = 0; j < indent; j++) {
				out.append(context.getIndentText());
			}
		}
		out.append(']');
		return true;
	}
}

final class DOMDocumentFormatter implements Formatter {
	public static final DOMDocumentFormatter INSTANCE = new DOMDocumentFormatter();
	
	public boolean format(final Context context, final Object src, final Object o, final OutputSource out) throws Exception {
		return DOMElementFormatter.INSTANCE.format(context, src, ((Document)o).getDocumentElement(), out);
	}
}