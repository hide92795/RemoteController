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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The JSONHint annotation gives the hint for conversion.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONHint {
	/**
	 * The property name 
	 */
	String name() default "";
	
	/**
	 * The format of Number or Date
	 */
	String format() default "";
	
	/**
	 * The Java type for creation
	 */
	Class<?> type() default Object.class;
	
	/**
	 * Ignore this property
	 */
	boolean ignore() default false;
	
	/**
	 * Set the flag that this property is already serialized.
	 */
	boolean serialized() default false;
	
	/**
	 * The default key name for using when converting simple type to struct type.
	 */
	String anonym() default "";
	
	/**
	 * The sort ordinal of property key
	 */
	int ordinal() default -1;
}
