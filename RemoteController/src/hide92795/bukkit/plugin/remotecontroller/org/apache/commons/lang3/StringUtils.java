/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hide92795.bukkit.plugin.remotecontroller.org.apache.commons.lang3;

import java.util.Iterator;
import org.apache.commons.lang.ObjectUtils;

public class StringUtils {
	/**
	 * The empty String {@code ""}.
	 * 
	 * @since 2.0
	 */
	public static final String EMPTY = "";



	// Joining
	// -----------------------------------------------------------------------
	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No separator is added to the joined String. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null) = null
	 * StringUtils.join([]) = ""
	 * StringUtils.join([null]) = ""
	 * StringUtils.join(["a", "b", "c"]) = "abc"
	 * StringUtils.join([null, "", "a"]) = "a"
	 * </pre>
	 * 
	 * @param <T>
	 *            the specific type of values to join together
	 * @param elements
	 *            the values to join together, may be null
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 * @since 3.0 Changed signature to use varargs
	 */
	@SafeVarargs
	public static <T> String join(final T... elements) {
		return join(elements, null);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join(["a", "b", "c"], ';') = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';') = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(final Object[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final long[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final int[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final short[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final byte[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final char[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final float[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final double[] array, final char separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}


	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join(["a", "b", "c"], ';') = "a;b;c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join([null, "", "a"], ';') = ";;a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is
	 *            an error to pass in an end index past the end of the array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is
	 *            an error to pass in an end index past the end of the array
	 * @return the joined String, {@code null} if null array input
	 * @since 2.0
	 */
	public static String join(final Object[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final long[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final int[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final byte[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final short[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final char[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final double[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join([1, 2, 3], ';') = "1;2;3"
	 * StringUtils.join([1, 2, 3], null) = "123"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @param startIndex
	 *            the first index to start joining from. It is an error to pass in an end index past the end of the
	 *            array
	 * @param endIndex
	 *            the index to stop joining from (exclusive). It is an error to pass in an end index past the end of
	 *            the array
	 * @return the joined String, {@code null} if null array input
	 * @since 3.2
	 */
	public static String join(final float[] array, final char separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			buf.append(array[i]);
		}
		return buf.toString();
	}


	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is the same as an empty String (""). Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *) = null
	 * StringUtils.join([], *) = ""
	 * StringUtils.join([null], *) = ""
	 * StringUtils.join(["a", "b", "c"], "--") = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], null) = "abc"
	 * StringUtils.join(["a", "b", "c"], "") = "abc"
	 * StringUtils.join([null, "", "a"], ',') = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null array input
	 */
	public static String join(final Object[] array, final String separator) {
		if (array == null) {
			return null;
		}
		return join(array, separator, 0, array.length);
	}

	/**
	 * <p>
	 * Joins the elements of the provided array into a single String containing the provided list of elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is the same as an empty String (""). Null objects or empty strings within the array are represented by empty strings.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.join(null, *, *, *) = null
	 * StringUtils.join([], *, *, *) = ""
	 * StringUtils.join([null], *, *, *) = ""
	 * StringUtils.join(["a", "b", "c"], "--", 0, 3) = "a--b--c"
	 * StringUtils.join(["a", "b", "c"], "--", 1, 3) = "b--c"
	 * StringUtils.join(["a", "b", "c"], "--", 2, 3) = "c"
	 * StringUtils.join(["a", "b", "c"], "--", 2, 2) = ""
	 * StringUtils.join(["a", "b", "c"], null, 0, 3) = "abc"
	 * StringUtils.join(["a", "b", "c"], "", 0, 3) = "abc"
	 * StringUtils.join([null, "", "a"], ',', 0, 3) = ",,a"
	 * </pre>
	 * 
	 * @param array
	 *            the array of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @param startIndex
	 *            the first index to start joining from.
	 * @param endIndex
	 *            the index to stop joining from (exclusive).
	 * @return the joined String, {@code null} if null array input; or the empty string
	 *         if {@code endIndex - startIndex <= 0}. The number of joined entries is given by {@code endIndex - startIndex}
	 * @throws ArrayIndexOutOfBoundsException
	 *             ife<br/>
	 *             {@code startIndex < 0} or <br/>
	 *             {@code startIndex >= array.length()} or <br/>
	 *             {@code endIndex < 0} or <br/>
	 *             {@code endIndex > array.length()}
	 */
	public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		if (separator == null) {
			separator = EMPTY;
		}

		// endIndex - startIndex > 0: Len = NofStrings *(len(firstString) + len(separator))
		// (Assuming that all Strings are roughly equally long)
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}

		final StringBuilder buf = new StringBuilder(noOfItems * 16);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterator} into a single String containing the provided elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the iteration are represented by empty strings.
	 * </p>
	 * <p>
	 * See the examples here: {@link #join(Object[],char)}.
	 * </p>
	 * 
	 * @param iterator
	 *            the {@code Iterator} of values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.0
	 */
	public static String join(final Iterator<?> iterator, final char separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return ObjectUtils.toString(first);
		}

		// two or more elements
		final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			buf.append(separator);
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}

		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterator} into a single String containing the provided elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is the same as an empty String ("").
	 * </p>
	 * <p>
	 * See the examples here: {@link #join(Object[],String)}.
	 * </p>
	 * 
	 * @param iterator
	 *            the {@code Iterator} of values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 */
	public static String join(final Iterator<?> iterator, final String separator) {

		// handle null, zero and one elements before building a buffer
		if (iterator == null) {
			return null;
		}
		if (!iterator.hasNext()) {
			return EMPTY;
		}
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return ObjectUtils.toString(first);
		}

		// two or more elements
		final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
		if (first != null) {
			buf.append(first);
		}

		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterable} into a single String containing the provided elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. Null objects or empty strings within the iteration are represented by empty strings.
	 * </p>
	 * <p>
	 * See the examples here: {@link #join(Object[],char)}.
	 * </p>
	 * 
	 * @param iterable
	 *            the {@code Iterable} providing the values to join together, may be null
	 * @param separator
	 *            the separator character to use
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.3
	 */
	public static String join(final Iterable<?> iterable, final char separator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator);
	}

	/**
	 * <p>
	 * Joins the elements of the provided {@code Iterable} into a single String containing the provided elements.
	 * </p>
	 * <p>
	 * No delimiter is added before or after the list. A {@code null} separator is the same as an empty String ("").
	 * </p>
	 * <p>
	 * See the examples here: {@link #join(Object[],String)}.
	 * </p>
	 * 
	 * @param iterable
	 *            the {@code Iterable} providing the values to join together, may be null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, {@code null} if null iterator input
	 * @since 2.3
	 */
	public static String join(final Iterable<?> iterable, final String separator) {
		if (iterable == null) {
			return null;
		}
		return join(iterable.iterator(), separator);
	}
}
