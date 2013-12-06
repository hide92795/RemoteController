/*
 * Copyright 2007-2011 Hidekatsu Izuno
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
package net.arnx.jsonic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class BeanInfo {
	private static final Map<ClassLoader, Map<Class<?>, BeanInfo>> cache = new WeakHashMap<ClassLoader, Map<Class<?>, BeanInfo>>();

	public static BeanInfo get(Class<?> cls) {
		synchronized (cache) {
			BeanInfo info = null;
			Map<Class<?>, BeanInfo> map = cache.get(cls.getClassLoader());
			if (map == null) {
				map = new LinkedHashMap<Class<?>, BeanInfo>(16, 0.75f, true) {
					private static final long serialVersionUID = 1L;

					protected boolean removeEldestEntry(Map.Entry<Class<?>, BeanInfo> eldest) {
						return size() > 1024;
					};
				};
				cache.put(cls.getClassLoader(), map);
			} else {
				info = map.get(cls);
			}

			if (info == null) {
				info = new BeanInfo(cls);
				map.put(cls, info);
			}
			return info;
		}
	}

	public static void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

	private Class<?> type;
	private ConstructorInfo ci;
	private Map<String, PropertyInfo> sprops;
	private Map<String, MethodInfo> smethods;
	private Map<String, PropertyInfo> props;
	private Map<String, MethodInfo> methods;

	private BeanInfo(Class<?> cls) {
		type = cls;

		for (Constructor<?> con : cls.getConstructors()) {
			if (con.isSynthetic()) {
				continue;
			}

			if (ci == null) {
				ci = new ConstructorInfo(cls, null);
			}
			con.setAccessible(true);
			ci.constructors.add(con);
		}

		for (Field f : cls.getFields()) {
			if (f.isSynthetic()) {
				continue;
			}

			boolean isStatic = Modifier.isStatic(f.getModifiers());

			String name = f.getName();
			f.setAccessible(true);
			if (isStatic) {
				if (sprops == null) {
					sprops = Collections.synchronizedMap(new LinkedHashMap<String, PropertyInfo>());
				}
				sprops.put(name, new PropertyInfo(cls, name, f, null, null, isStatic, -1));
			} else {
				if (props == null) {
					props = Collections.synchronizedMap(new LinkedHashMap<String, PropertyInfo>());
				}
				props.put(name, new PropertyInfo(cls, name, f, null, null, isStatic, -1));
			}
		}

		for (Method m : cls.getMethods()) {
			if (m.isSynthetic() || m.isBridge()) {
				continue;
			}

			String name = m.getName();
			Class<?>[] paramTypes = m.getParameterTypes();
			Class<?> returnType = m.getReturnType();

			boolean isStatic = Modifier.isStatic(m.getModifiers());

			MethodInfo mi;
			if (isStatic) {
				if (smethods == null) {
					smethods = Collections.synchronizedMap(new LinkedHashMap<String, MethodInfo>());
				}
				mi = smethods.get(name);
				if (mi == null) {
					mi = new MethodInfo(cls, name, null, isStatic);
					smethods.put(name, mi);
				}
			} else {
				if (methods == null) {
					methods = Collections.synchronizedMap(new LinkedHashMap<String, MethodInfo>());
				}
				mi = methods.get(name);
				if (mi == null) {
					mi = new MethodInfo(cls, name, null, isStatic);
					methods.put(name, mi);
				}
			}
			m.setAccessible(true);
			mi.methods.add(m);

			int type = -1;
			if (name.startsWith("get") && name.length() > 3 && !Character.isLowerCase(name.charAt(3)) && paramTypes.length == 0 && !returnType.equals(void.class)) {
				type = 1;
				name = name.substring(3);
			} else if (name.startsWith("is") && name.length() > 2 && !Character.isLowerCase(name.charAt(2)) && paramTypes.length == 0 && !returnType.equals(void.class)) {
				type = 1;
				name = name.substring(2);
			} else if (name.startsWith("set") && name.length() > 3 && !Character.isLowerCase(name.charAt(3)) && paramTypes.length == 1 && !paramTypes[0].equals(void.class)) {
				type = 2;
				name = name.substring(3);
			} else {
				continue;
			}

			if (name.length() < 2 || !Character.isUpperCase(name.charAt(1))) {
				char[] chars = name.toCharArray();
				chars[0] = Character.toLowerCase(chars[0]);
				name = String.valueOf(chars);
			}

			PropertyInfo prop;
			if (isStatic) {
				if (sprops == null) {
					sprops = Collections.synchronizedMap(new LinkedHashMap<String, PropertyInfo>());
				}
				prop = sprops.get(name);
				if (prop == null) {
					prop = new PropertyInfo(cls, name, null, null, null, isStatic, -1);
					sprops.put(name, prop);
				}
			} else {
				if (props == null) {
					props = Collections.synchronizedMap(new LinkedHashMap<String, PropertyInfo>());
				}
				prop = props.get(name);
				if (prop == null) {
					prop = new PropertyInfo(cls, name, null, null, null, isStatic, -1);
					props.put(name, prop);
				}
			}

			if (type == 1) {
				prop.readMethod = m;
			} else {
				prop.writeMethod = m;
			}
		}

		if (sprops == null)
			sprops = Collections.emptyMap();
		if (smethods == null)
			smethods = Collections.emptyMap();
		if (props == null)
			props = Collections.emptyMap();
		if (methods == null)
			methods = Collections.emptyMap();
	}

	public Object newInstance() {
		try {
			Constructor<?> target = type.getConstructor();
			target.setAccessible(true);
			return target.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public Class<?> getType() {
		return type;
	}

	public ConstructorInfo getConstructor() {
		return ci;
	}

	public PropertyInfo getStaticProperty(String name) {
		return sprops.get(name);
	}

	public MethodInfo getStaticMethod(String name) {
		return smethods.get(name);
	}

	public Collection<PropertyInfo> getStaticProperties() {
		return sprops.values();
	}

	public Collection<MethodInfo> getStaticMethods() {
		return smethods.values();
	}

	public PropertyInfo getProperty(String name) {
		return props.get(name);
	}

	public MethodInfo getMethod(String name) {
		return methods.get(name);
	}

	public Collection<PropertyInfo> getProperties() {
		return props.values();
	}

	public Collection<MethodInfo> getMethods() {
		return methods.values();
	}

	@Override
	public int hashCode() {
		return (type == null) ? 0 : type.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanInfo other = (BeanInfo) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BeanInfo [static properties = " + sprops + ", static methods = " + smethods + ", properties = " + props + ", methods = " + methods + "]";
	}

	static int calcurateDistance(Class<?>[] params, Object[] args) {
		int point = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null) {
				if (!params[i].isPrimitive())
					point += 5;
			} else if (params[i].equals(args[i].getClass())) {
				point += 10;
			} else if (params[i].isAssignableFrom(args[i].getClass())) {
				point += 8;
			} else if (boolean.class.equals(args[i].getClass()) || Boolean.class.equals(args[i].getClass())) {
				if (boolean.class.equals(params[i]) || Boolean.class.equals(params[i].getClass())) {
					point += 10;
				}
			} else if (byte.class.equals(args[i].getClass()) || Byte.class.equals(args[i].getClass())) {
				if (byte.class.equals(params[i]) || short.class.equals(params[i]) || char.class.equals(params[i]) || int.class.equals(params[i]) || long.class.equals(params[i])
						|| float.class.equals(params[i]) || double.class.equals(params[i]) || Byte.class.equals(params[i]) || Short.class.equals(params[i]) || Character.class.equals(params[i])
						|| Integer.class.equals(params[i]) || Long.class.equals(params[i]) || Float.class.equals(params[i]) || Double.class.equals(params[i])) {
					point += 10;
				}
			} else if (short.class.equals(args[i].getClass()) || Short.class.equals(args[i].getClass()) || char.class.equals(args[i].getClass()) || Character.class.equals(args[i].getClass())) {
				if (short.class.equals(params[i]) || char.class.equals(params[i]) || int.class.equals(params[i]) || long.class.equals(params[i]) || float.class.equals(params[i])
						|| double.class.equals(params[i]) || Short.class.equals(params[i]) || Character.class.equals(params[i]) || Integer.class.equals(params[i]) || Long.class.equals(params[i])
						|| Float.class.equals(params[i]) || Double.class.equals(params[i])) {
					point += 10;
				}
			} else if (int.class.equals(args[i].getClass()) || Integer.class.equals(args[i].getClass())) {
				if (int.class.equals(params[i]) || long.class.equals(params[i]) || float.class.equals(params[i]) || double.class.equals(params[i]) || Integer.class.equals(params[i])
						|| Long.class.equals(params[i]) || Float.class.equals(params[i]) || Double.class.equals(params[i])) {
					point += 10;
				}
			} else if (long.class.equals(args[i].getClass()) || Long.class.equals(args[i].getClass())) {
				if (long.class.equals(params[i]) || float.class.equals(params[i]) || double.class.equals(params[i]) || Long.class.equals(params[i]) || Float.class.equals(params[i])
						|| Double.class.equals(params[i])) {
					point += 10;
				}
			} else if (float.class.equals(args[i].getClass()) || Float.class.equals(args[i].getClass())) {
				if (float.class.equals(params[i]) || double.class.equals(params[i]) || Float.class.equals(params[i]) || Double.class.equals(params[i])) {
					point += 10;
				}
			} else if (double.class.equals(args[i].getClass()) || Double.class.equals(args[i].getClass())) {
				if (double.class.equals(params[i]) || Double.class.equals(params[i])) {
					point += 10;
				}
			}
		}
		return point;
	}
}
