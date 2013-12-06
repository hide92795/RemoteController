package net.arnx.jsonic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

import net.arnx.jsonic.JSON.Context;
import net.arnx.jsonic.io.StringBuilderOutputSource;
import net.arnx.jsonic.util.Base64;
import net.arnx.jsonic.util.BeanInfo;
import net.arnx.jsonic.util.ClassUtil;
import net.arnx.jsonic.util.PropertyInfo;

interface Converter {
	Object convert(Context context, Object value, Class<?> c, Type t) throws Exception;
}

final class NullConverter implements Converter {
	public static final NullConverter INSTANCE = new NullConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) {
		return null;
	}
}

final class PlainConverter implements Converter {
	public static final PlainConverter INSTANCE = new PlainConverter();
	
	private static final Map<Class<?>, Object> PRIMITIVE_MAP = new HashMap<Class<?>, Object>(8);
	
	static {
		PRIMITIVE_MAP.put(boolean.class, false);
		PRIMITIVE_MAP.put(byte.class, (byte)0);
		PRIMITIVE_MAP.put(short.class, (short)0);
		PRIMITIVE_MAP.put(int.class, 0);
		PRIMITIVE_MAP.put(long.class, 0l);
		PRIMITIVE_MAP.put(float.class, 0.0f);
		PRIMITIVE_MAP.put(double.class, 0.0);
		PRIMITIVE_MAP.put(char.class, '\0');
	}
	
	public Object convert(Context context, Object value, Class<?> c, Type t) {
		return value;
	}
	
	public static Object getDefaultValue(Class<?> cls) {
		return PRIMITIVE_MAP.get(cls);
	}
}

final class FormatConverter implements Converter {
	public static final FormatConverter INSTANCE = new FormatConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		Context context2 = context.copy();
		context2.skipHint = context.getHint();
		value = context2.preformatInternal(value);
		StringBuilderOutputSource fs = new StringBuilderOutputSource(new StringBuilder(200));
		try {
			context2.formatInternal(value, fs);
		} catch (IOException e) {
			// no handle
		}
		fs.flush();
		
		context.skipHint = context2.skipHint;
		Object ret =  context.postparseInternal(fs.toString(), c, t);
		context.skipHint = null;
		
		return ret;
	}
}

final class StringSerializableConverter implements Converter {
	public static final StringSerializableConverter INSTANCE = new StringSerializableConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof String) {
			try {
				Constructor<?> con = c.getConstructor(String.class);
				con.setAccessible(true);
				return con.newInstance(value.toString());
			} catch (NoSuchMethodException e) {
				return null;
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}	
}

final class SerializableConverter implements Converter {
	public static final SerializableConverter INSTANCE = new SerializableConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof String) {
			return ClassUtil.deserialize(Base64.decode((String)value));
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class BooleanConverter implements Converter {
	public static final BooleanConverter INSTANCE = new BooleanConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof Boolean) {
			return value;
		} else if (value instanceof BigDecimal) {
			return !value.equals(BigDecimal.ZERO);
		} else if (value instanceof BigInteger) {
			return !value.equals(BigInteger.ZERO);
		} else if (value instanceof Number) {
			return ((Number)value).doubleValue() != 0;
		} else if (value != null){
			String s = value.toString().trim();
			if (s.length() == 0
				|| s.equalsIgnoreCase("0")
				|| s.equalsIgnoreCase("f")
				|| s.equalsIgnoreCase("false")
				|| s.equalsIgnoreCase("no")
				|| s.equalsIgnoreCase("off")
				|| s.equals("NaN")) {
				return false;
			} else {
				return true;
			}
		}
		return PlainConverter.getDefaultValue(c);
	}
}

final class CharacterConverter implements Converter {
	public static final CharacterConverter INSTANCE = new CharacterConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? '1' : '0';
		} else if (value instanceof BigDecimal) {
			return (char)((BigDecimal)value).intValueExact();
		} else if (value instanceof String) {
			String s = value.toString();
			if (s.length() > 0) {
				return s.charAt(0);
			} else {
				return PlainConverter.getDefaultValue(c);
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}
}

final class ByteConverter implements Converter {
	public static final ByteConverter INSTANCE = new ByteConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1 : 0;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).byteValueExact();
		} else if (value instanceof Number) {
			return ((Number)value).byteValue();
		} else if (value instanceof String) {
			String str = value.toString().trim().toLowerCase();
			if (str.length() > 0) {
				int start = 0;
				if (str.charAt(0) == '+') {
					start++;
				}
				
				int num = 0;
				if (str.startsWith("0x", start)) {
					num = Integer.parseInt(str.substring(start+2), 16);
				} else {
					num = Integer.parseInt(str.substring(start));
				}
				
				return (byte)((num > 127) ? num-256 : num);
			} else {
				return PlainConverter.getDefaultValue(c);
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}
}

final class ShortConverter implements Converter {
	public static final ShortConverter INSTANCE = new ShortConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1 : 0;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).shortValueExact();
		} else if (value instanceof Number) {
			return ((Number)value).shortValue();
		} else  if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				int start = 0;
				if (str.charAt(0) == '+') {
					start++;
				}
				
				if (str.startsWith("0x", start)) {
					return (short)Integer.parseInt(str.substring(start+2), 16);
				} else {
					return (short)Integer.parseInt(str.substring(start));
				}
			} else {
				return PlainConverter.getDefaultValue(c);
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}	
}

final class IntegerConverter  implements Converter {
	public static final IntegerConverter INSTANCE = new IntegerConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1 : 0;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).intValueExact();
		} else if (value instanceof Number) {
			return ((Number)value).intValue();
		} else  if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				int start = 0;
				if (str.charAt(0) == '+') {
					start++;
				}
				
				if (str.startsWith("0x", start)) {
					return Integer.parseInt(str.substring(start+2), 16);
				} else {
					return Integer.parseInt(str.substring(start));
				}
			} else {
				return PlainConverter.getDefaultValue(c);
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}	
}

final class LongConverter implements Converter {
	public static final LongConverter INSTANCE = new LongConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1l : 0l;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).longValueExact();
		} else if (value instanceof Number) {
			return ((Number)value).longValue();
		} else if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				int start = 0;
				if (str.charAt(0) == '+') {
					start++;
				}
				
				if (str.startsWith("0x", start)) {
					return Long.parseLong(str.substring(start+2), 16);
				} else {
					return Long.parseLong(str.substring(start));
				}
			} else {
				return PlainConverter.getDefaultValue(c);
			}					
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}	
}

final class FloatConverter  implements Converter {
	public static final FloatConverter INSTANCE = new FloatConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1.0f : Float.NaN;
		} else if (value instanceof Number) {
			return ((Number)value).floatValue();
		} else if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				return Float.valueOf(str);
			} else {
				return PlainConverter.getDefaultValue(c);
			}					
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}	
}

final class DoubleConverter  implements Converter {
	public static final DoubleConverter INSTANCE = new DoubleConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? 1.0 : Double.NaN;
		} else if (value instanceof Number) {
			return ((Number)value).doubleValue();
		} else if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				return Double.valueOf(str);
			} else {
				return PlainConverter.getDefaultValue(c);
			}					
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return PlainConverter.getDefaultValue(c);
	}	
}

final class BigIntegerConverter  implements Converter {
	public static final BigIntegerConverter INSTANCE = new BigIntegerConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
		}
		
		if (value instanceof Boolean) {
			return (((Boolean)value).booleanValue()) ? BigInteger.ONE : BigInteger.ZERO;
		} else if (value instanceof BigDecimal) {
			return ((BigDecimal)value).toBigIntegerExact();
		} else if (value instanceof BigInteger) {
			return value;
		} else if (value instanceof Number) {
			return BigInteger.valueOf(((Number)value).longValue());
		} else if (value instanceof String) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				int start = 0;
				if (str.charAt(0) == '+') {
					start++;
				}
				
				if (str.startsWith("0x", start)) {
					return new BigInteger(str.substring(start+2), 16);
				} else {
					return new BigInteger(str.substring(start));
				}
			}
			return null;
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}	
}

final class BigDecimalConverter  implements Converter {
	public static final BigDecimalConverter INSTANCE = new BigDecimalConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof BigDecimal) {
			return value;
		} else if (value instanceof String) {
			NumberFormat f = context.getNumberFormat();
			if (f != null) value = f.parse((String)value);
			
			String str = value.toString().trim();
			if (str.length() > 0) {
				if (str.charAt(0) == '+') {
					return new BigDecimal(str.substring(1));
				} else {
					return new BigDecimal(str);
				}
			}
			return null;
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}	
}

final class PatternConverter implements Converter {
	public static final PatternConverter INSTANCE = new PatternConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			return Pattern.compile(value.toString());
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class TimeZoneConverter implements Converter {
	public static final TimeZoneConverter INSTANCE = new TimeZoneConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof String) {
			return TimeZone.getTimeZone(value.toString().trim());
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}	
}

final class LocaleConverter implements Converter {
	public static final LocaleConverter INSTANCE = new LocaleConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			if (src.size() == 1) {
				return new Locale(src.get(0).toString());
			} else if (src.size() == 2) {
				return new Locale(src.get(0).toString(), src.get(1).toString());
			} else if (src.size() > 2) {
				return new Locale(src.get(0).toString(), src.get(1).toString(), src.get(2).toString());
			} else {
				return null;
			}
		} else {
			if (value instanceof Map<?, ?>) {
				value = ((Map<?,?>)value).get(null);
			}
			
			if (value instanceof String) {
				String[] array = value.toString().split("\\p{Punct}");
				
				if (array.length == 1) {
					return new Locale(array[0]);
				} else if (array.length == 2) {
					return new Locale(array[0], array[1]);
				} else if (array.length > 2) {
					return new Locale(array[0], array[1], array[2]);
				} else {
					return null;
				}
			} else if (value != null) {
				throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
			}
		}
		return null;
	}
}

final class FileConverter implements Converter {
	public static final FileConverter INSTANCE = new FileConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			return new File(value.toString().trim());
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class URLConverter implements Converter {
	public static final URLConverter INSTANCE = new URLConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			if (value instanceof File) {
				return ((File)value).toURI().toURL();
			} else if (value instanceof URI) {
				return ((URI)value).toURL();
			} else {
				return new URL(value.toString().trim());
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class URIConverter implements Converter {
	public static final URIConverter INSTANCE = new URIConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			if (value instanceof File) {
				return ((File)value).toURI();
			} else if (value instanceof URL) {
				return ((URL)value).toURI();
			} else {
				return new URI(value.toString().trim());
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class UUIDConverter implements Converter {
	public static final UUIDConverter INSTANCE = new UUIDConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			return UUID.fromString(value.toString().trim());
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class CharsetConverter implements Converter {
	public static final CharsetConverter INSTANCE = new CharsetConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			return Charset.forName(value.toString().trim());
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class ClassConverter implements Converter {
	public static final ClassConverter INSTANCE = new ClassConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value instanceof String) {
			String s = value.toString().trim();
			if (s.equals("boolean")) {
				return boolean.class;
			} else if (s.equals("byte")) {
				return byte.class;
			} else if (s.equals("short")) {
				return short.class;
			} else if (s.equals("int")) {
				return int.class;
			} else if (s.equals("long")) {
				return long.class;
			} else if (s.equals("float")) {
				return float.class;
			} else if (s.equals("double")) {
				return double.class;
			} else {
				try {
					ClassLoader cl = Thread.currentThread().getContextClassLoader();
					return cl.loadClass(value.toString());
				} catch (ClassNotFoundException e) {
					return null;
				}
			}
		} else if (value != null) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		}
		return null;
	}
}

final class CharSequenceConverter implements Converter {
	public static final CharSequenceConverter INSTANCE = new CharSequenceConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		if (value != null) {
			return value.toString();
		}
		return null;
	}
}

final class AppendableConverter implements Converter {
	public static final AppendableConverter INSTANCE = new AppendableConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value != null) {
			Appendable a = (Appendable)context.createInternal(c);
			return a.append(value.toString());
		}
		return null;
	}
}

final class EnumConverter implements Converter {
	public static final EnumConverter INSTANCE = new EnumConverter();
	
	@SuppressWarnings({ "rawtypes" })
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		Enum[] enums = (Enum[])c.getEnumConstants();
		if (value instanceof Number) {
			return enums[((Number)value).intValue()];
		} else if (value instanceof Boolean) {
			return enums[((Boolean)value) ? 1 : 0];
		} else if (value != null) {
			String str = value.toString().trim();
			if (str.length() == 0) {
				return null;
			} else if (Character.isDigit(str.charAt(0))) {
				return enums[Integer.parseInt(str)];
			} else {
				for (Enum e : enums) {
					if (str.equals(e.name())) return e;
				}
				if (context.getEnumStyle() != null) {
					for (Enum e : enums) {
						if (str.equals(context.getEnumStyle().to(e.name()))) return e;
					}
				}
				throw new IllegalArgumentException(str + " is not " + c);
			}
		}
		return null;
	}
}

final class DateConverter implements Converter {
	public static final DateConverter INSTANCE = new DateConverter();
	private static final Pattern TIMEZONE_PATTERN = Pattern.compile("(?:GMT|UTC)([+-][0-9]{2})([0-9]{2})");
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		Date date = null;
		if (value instanceof Number) {
			date = (Date)context.createInternal(c);
			date.setTime(((Number)value).longValue());
		} else if (value != null) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				DateFormat format = context.getDateFormat();
				if (format != null) {
					date = format.parse(str);
				} else {
					date = convertDate(context, str);
				}
				
				if (date != null && !c.isAssignableFrom(date.getClass())) {
					long time = date.getTime();
					date = (Date)context.createInternal(c);
					date.setTime(time);
				}
			}
		}
		
		if (date instanceof java.sql.Date) {
			Calendar cal = Calendar.getInstance(context.getTimeZone(), context.getLocale());
			cal.setTimeInMillis(date.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			date.setTime(cal.getTimeInMillis());
		} else if (date instanceof java.sql.Time) {
			Calendar cal = Calendar.getInstance(context.getTimeZone(), context.getLocale());
			cal.setTimeInMillis(date.getTime());
			cal.set(Calendar.YEAR, 1970);
			cal.set(Calendar.MONTH, Calendar.JANUARY);
			cal.set(Calendar.DATE, 1);
			date.setTime(cal.getTimeInMillis());
		}
		
		return date;
	}
	
	static Date convertDate(Context context, String value) throws ParseException {
		value = value.trim();
		if (value.length() == 0) {
			return null;
		}
		value = TIMEZONE_PATTERN.matcher(value).replaceFirst("GMT$1:$2");
		
		DateFormat format = null;
		if (Character.isDigit(value.charAt(0))) {
			StringBuilder sb = context.getLocalCache().getCachedBuffer();

			String types = "yMdHmsSZ";
			// 0: year, 1:month, 2: day, 3: hour, 4: minute, 5: sec, 6:msec, 7: timezone
			int pos = (value.length() > 2 && value.charAt(2) == ':') ? 3 : 0;
			boolean before = true;
			int count = 0;
			for (int i = 0; i < value.length(); i++) {
				char c = value.charAt(i);
				if ((pos == 4 || pos == 5 || pos == 6) 
						&& (c == '+' || c == '-')
						&& (i + 1 < value.length())
						&& (Character.isDigit(value.charAt(i+1)))) {
					
					if (!before) sb.append('\'');
					pos = 7;
					count = 0;
					before = true;
					continue;
				} else if (pos == 7 && c == ':'
						&& (i + 1 < value.length())
						&& (Character.isDigit(value.charAt(i+1)))) {
					value = value.substring(0, i) + value.substring(i+1);
					continue;
				}
				
				boolean digit = (Character.isDigit(c) && pos < 8);
				if (before != digit) {
					sb.append('\'');
					if (digit) {
						count = 0;
						pos++;
					}
				}
				
				if (digit) {
					char type = types.charAt(pos);
					if (count == ((type == 'y' || type == 'Z') ? 4 : (type == 'S') ? 3 : 2)) {
						count = 0;
						pos++;
						type = types.charAt(pos);
					}
					if (type != 'Z' || count == 0) sb.append(type);
					count++;
				} else {
					sb.append((c == '\'') ? "''" : c);
				}
				before = digit;
			}
			if (!before) sb.append('\'');
			
			format = new SimpleDateFormat(sb.toString(), Locale.ENGLISH);
		} else if (value.length() > 18) {
			if (value.charAt(3) == ',') {
				String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
				format = new SimpleDateFormat(
						(value.length() < pattern.length()) ? pattern.substring(0, value.length()) : pattern, Locale.ENGLISH);
			} else if (value.charAt(13) == ':') {
				format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
			} else if (value.charAt(18) == ':') {
				String pattern = "EEE MMM dd yyyy HH:mm:ss Z";
				format = new SimpleDateFormat(
						(value.length() < pattern.length()) ? pattern.substring(0, value.length()) : pattern, Locale.ENGLISH);
			} else  {
				format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, context.getLocale());
			}
		} else {
			format = DateFormat.getDateInstance(DateFormat.MEDIUM, context.getLocale());
		}
		format.setLenient(false);
		format.setTimeZone(context.getTimeZone());
		
		return format.parse(value);
	}
}

final class CalendarConverter implements Converter {
	public static final CalendarConverter INSTANCE = new CalendarConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value instanceof Number) {
			Calendar cal = (Calendar)context.createInternal(c);
			cal.setTimeInMillis(((Number)value).longValue());
			return cal;
		} else if (value != null) {
			String str = value.toString().trim();
			if (str.length() > 0) {
				Calendar cal = (Calendar)context.createInternal(c);
				
				DateFormat format = context.getDateFormat();
				if (format != null) {
					cal.setTime(format.parse(str));
				} else {
					cal.setTime(DateConverter.convertDate(context, str));
				}
				return  cal;
			}
		}
		return null;
	}
}

final class InetAddressConverter implements Converter {
	public static final InetAddressConverter INSTANCE = new InetAddressConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			value = ((Map<?,?>)value).get(null);
		} else if (value instanceof List<?>) {
			List<?> src = (List<?>)value;
			value = (!src.isEmpty()) ? src.get(0) : null;
		}
		
		if (value != null) {
			Class<?> inetAddressClass = ClassUtil.findClass("java.net.InetAddress");
			return inetAddressClass.getMethod("getByName", String.class).invoke(null, value.toString().trim());
		}
		return null;
	}
}

final class ArrayConverter implements Converter {
	public static final ArrayConverter INSTANCE = new ArrayConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map<?, ?>) {
			Map<?, ?> src = (Map<?, ?>)value;
			if (!(src instanceof SortedMap<?, ?>)) {
				src = new TreeMap<Object, Object>(src);
			}
			value = src.values();
		}
		
		if (value instanceof Collection) {
			Collection<?> src = (Collection<?>)value;
			Object array = Array.newInstance(c.getComponentType(), src.size());
			Class<?> pc = c.getComponentType();
			Type pt = (t instanceof GenericArrayType) ? 
					((GenericArrayType)t).getGenericComponentType() : pc;
			
			Iterator<?> it = src.iterator();
			JSONHint hint = context.getHint();
			for (int i = 0; it.hasNext(); i++) {
				context.enter(i, hint);
				Array.set(array, i, context.postparseInternal(it.next(), pc, pt));
				context.exit();
			}
			return array;
		} else {
			Class<?> ctype = c.getComponentType();
			if (value instanceof String) {
				if (byte.class.equals(ctype)) {
					return Base64.decode((String)value);
				} else if (char.class.equals(ctype)) {
					return ((String)value).toCharArray();
				}
			}
			Object array = Array.newInstance(ctype, 1);
			Class<?> pc = ctype;
			Type pt = (t instanceof GenericArrayType) ? 
					((GenericArrayType)t).getGenericComponentType() : pc;
			context.enter(0, context.getHint());
			Array.set(array, 0, context.postparseInternal(value, pc, pt));
			context.exit();
			return array;
		}
	}
}

final class CollectionConverter implements Converter {
	public static final CollectionConverter INSTANCE = new CollectionConverter();
	
	@SuppressWarnings("unchecked")
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (value instanceof Map) {
			Map<?, ?> src = (Map<?, ?>)value;
			if (!(src instanceof SortedMap<?, ?>)) {
				src = new TreeMap<Object, Object>(src);
			}
			value = src.values();
		}
		
		Collection<Object> collection = (Collection<Object>)context.createInternal(c);
		t = ClassUtil.resolveParameterizedType(t, Collection.class);
		
		Class<?> pc = Object.class;
		Type pt = Object.class;
		if (t instanceof ParameterizedType) {
			Type[] pts = ((ParameterizedType)t).getActualTypeArguments();
			pt = (pts != null && pts.length > 0) ? pts[0] : Object.class;
			pc = ClassUtil.getRawType(pt);
		}
		
		if (value instanceof Collection) {
			Collection<?> src = (Collection<?>)value;
						
			if (!Object.class.equals(pc)) {
				Iterator<?> it = src.iterator();
				JSONHint hint = context.getHint();
				for (int i = 0; it.hasNext(); i++) {
					context.enter(i, hint);
					collection.add(context.postparseInternal(it.next(), pc, pt));
					context.exit();
				}
			} else {
				collection.addAll(src);
			}
		} else {
			if (!Object.class.equals(pc)) {
				context.enter(0, context.getHint());
				collection.add(context.postparseInternal(value, pc, pt));
				context.exit();
			} else {
				collection.add(value);
			}
		}
		
		return collection;
	}	
}

final class PropertiesConverter implements Converter {
	public static final PropertiesConverter INSTANCE = new PropertiesConverter();
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		Properties prop = (Properties)context.createInternal(c);
		if (value instanceof Map<?, ?> || value instanceof List<?>) {
			flattenProperties(context.getLocalCache().getCachedBuffer(), value, prop);
		} else if (value != null) {
			prop.setProperty(value.toString(), null);
		}
		return prop;
	}
	
	private static void flattenProperties(StringBuilder key, Object value, Properties props) {
		if (value instanceof Map<?,?>) {
			for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) {
				int pos = key.length();
				if (pos > 0) key.append('.');
				key.append(entry.getKey());
				flattenProperties(key, entry.getValue(), props);
				key.setLength(pos);
			}
		} else if (value instanceof List<?>) {
			List<?> list = (List<?>)value;
			for (int i = 0; i < list.size(); i++) {
				int pos = key.length();
				if (pos > 0) key.append('.');
				key.append(i);
				flattenProperties(key, list.get(i), props);
				key.setLength(pos);
			}
		} else {
			props.setProperty(key.toString(), value.toString());
		}
	}
}

final class MapConverter implements Converter {
	public static final MapConverter INSTANCE = new MapConverter();
	
	@SuppressWarnings("unchecked")
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		Map<Object, Object> map = (Map<Object, Object>)context.createInternal(c);
		t = ClassUtil.resolveParameterizedType(t, Map.class);
		
		Type pt0 = Object.class;
		Type pt1 = Object.class;
		Class<?> pc0 = Object.class;
		Class<?> pc1 = Object.class;
		if (t instanceof ParameterizedType) {
			Type[] pts = ((ParameterizedType)t).getActualTypeArguments();
			pt0 = (pts != null && pts.length > 0) ? pts[0] : Object.class;
			pt1 = (pts != null && pts.length > 1) ? pts[1] : Object.class;
			pc0 = ClassUtil.getRawType(pt0);
			pc1 = ClassUtil.getRawType(pt1);
		}
		
		if (value instanceof Map<?, ?>) {	
			if (Object.class.equals(pc0) && Object.class.equals(pc1)) {
				map.putAll((Map<?,?>)value);
			} else {
				JSONHint hint = context.getHint();
				for (Map.Entry<?, ?> entry : ((Map<?,?>)value).entrySet()) {
					context.enter('.', hint);
					Object key = context.postparseInternal(entry.getKey(), pc0, pt0);
					context.exit();
					
					context.enter(entry.getKey(), hint);
					map.put(key, context.postparseInternal(entry.getValue(), pc1, pt1));
					context.exit();
				}
			}
		} else if (value instanceof List<?>) {
			if (Object.class.equals(pc0) && Object.class.equals(pc1)) {
				List<?> src = (List<?>)value;
				for (int i = 0; i < src.size(); i++) {
					map.put(i, src.get(i));
				}
			} else {
				List<?> src = (List<?>)value;
				JSONHint hint = context.getHint();
				for (int i = 0; i < src.size(); i++) {
					context.enter('.', hint);
					Object key = context.postparseInternal(i, pc0, pt0);
					context.exit();
					
					context.enter(i, hint);
					map.put(key, context.postparseInternal(src.get(i), pc1, pt1));
					context.exit();
				}
			}
		} else {
			JSONHint hint = context.getHint();
			
			Object key = (hint != null && hint.anonym().length() > 0) ? hint.anonym() : null;
			if (Object.class.equals(pc0) && Object.class.equals(pc1)) {
				map.put(value, null);
			} else {
				context.enter('.', hint);
				key = context.postparseInternal(key, pc0, pt0);
				context.exit();
				
				context.enter(key, hint);
				map.put(key, context.postparseInternal(value, pc1, pt1));
				context.exit();
			}
		}
		return map;
	}
}

final class ObjectConverter implements Converter {
	private Class<?> cls;
	private transient Map<String, PropertyInfo> props;
	
	public ObjectConverter(Class<?> cls) {
		this.cls = cls;
	}
	
	public Object convert(Context context, Object value, Class<?> c, Type t) throws Exception {
		if (props == null) props = getSetProperties(context, cls);
		
		if (value instanceof Map<?, ?>) {
			Object o = context.createInternal(c);
			if (o == null) return null;
			for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) {
				String name = entry.getKey().toString();
				PropertyInfo target = props.get(name);
				if (target == null) target = props.get(toLowerCamel(context, name));
				if (target == null) continue;
				
				context.enter(name, target.getWriteAnnotation(JSONHint.class));
				Class<?> cls = target.getWriteType();
				Type gtype = target.getWriteGenericType();
				if (gtype instanceof TypeVariable<?> && t instanceof ParameterizedType) {
					gtype = resolveTypeVariable((TypeVariable<?>)gtype, (ParameterizedType)t);
					cls = ClassUtil.getRawType(gtype);
				}
				target.set(o, context.postparseInternal(entry.getValue(), cls, gtype));
				context.exit();
			}
			return o;
		} else if (value instanceof List<?>) {
			throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
		} else {
			JSONHint hint = context.getHint();
			if (hint != null && hint.anonym().length() > 0) {
				PropertyInfo target = props.get(hint.anonym());
				if (target == null) return null;
				Object o = context.createInternal(c);
				if (o == null) return null;
				context.enter(hint.anonym(), target.getWriteAnnotation(JSONHint.class));
				Class<?> cls = target.getWriteType();
				Type gtype = target.getWriteGenericType();
				if (gtype instanceof TypeVariable<?> && t instanceof ParameterizedType) {
					gtype = resolveTypeVariable((TypeVariable<?>)gtype, (ParameterizedType)t);
					cls = ClassUtil.getRawType(gtype);
				}
				target.set(o, context.postparseInternal(value, cls, gtype));
				context.exit();
				return o;
			} else {
				throw new UnsupportedOperationException("Cannot convert " + value.getClass() + " to " + t);
			}
		}
	}
	
	private static Map<String, PropertyInfo> getSetProperties(Context context, Class<?> c) {
		Map<String, PropertyInfo> props = new HashMap<String, PropertyInfo>();
		
		// Field
		for (PropertyInfo prop : BeanInfo.get(c).getProperties()) {
			Field f = prop.getField();
			if (f == null || Modifier.isFinal(f.getModifiers()) || context.ignoreInternal(c, f)) continue;
			
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
			
			if (!name.equals(prop.getName()) || ordinal != prop.getOrdinal() || f != prop.getWriteMember()) {
				props.put(name, new PropertyInfo(prop.getBeanClass(), name, 
					prop.getField(), null, null, prop.isStatic(), ordinal));
			} else {
				props.put(name, prop);
			}
		}
		
		// Method
		for (PropertyInfo prop : BeanInfo.get(c).getProperties()) {
			Method m = prop.getWriteMethod();
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
					null, null, prop.getWriteMethod(), prop.isStatic(), ordinal));
			} else {
				props.put(name, prop);
			}
		}
		return props;
	}
	
	private static String toLowerCamel(Context context, String name) {
		StringBuilder sb = context.getLocalCache().getCachedBuffer();
		boolean toUpperCase = false;
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (c == ' ' || c == '_' || c == '-') {
				toUpperCase = true;
			} else if (toUpperCase) {
				sb.append(Character.toUpperCase(c));
				toUpperCase = false;
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 1 && Character.isUpperCase(sb.charAt(0)) && !Character.isUpperCase(sb.charAt(1))) {
			sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		}
		return context.getLocalCache().getString(sb);
	}
	
	private static Type resolveTypeVariable(TypeVariable<?> type, ParameterizedType parent) {
		Class<?> rawType = ClassUtil.getRawType(parent);
		if (rawType.equals(type.getGenericDeclaration())) {
			String tvName = type.getName();
			TypeVariable<?>[] rtypes = ((Class<?>)rawType).getTypeParameters();
			Type[] atypes = parent.getActualTypeArguments();
			
			for (int i = 0; i < rtypes.length; i++) {
				if (tvName.equals(rtypes[i].getName())) return atypes[i];
			}
		}
		
		return type.getBounds()[0];
	}
}