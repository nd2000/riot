/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Utility class that provides some simple text formatting methods.
 */
public final class FormatUtils {

	private static final Log log = LogFactory.getLog(FormatUtils.class);
	
	private static NumberFormat numberFormat = new DecimalFormat("0.#");

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	private static final String OP_ADDITION = "+";

	private static final String OP_SUBTRACTION = "-";

	private static final String ENTITY_LT = "&lt;";

	private static final String ENTITY_GT = "&gt;";

	private static final String ENTITY_AMP = "&amp;";

	private static final String ENTITY_QUOT = "&quot;";

	private static final String ALLOW_URI_CHARS = "0123456789" +
		"abcdefghijklmnopqrstuvwxyz" +
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"-._~!$&'()*+,;=:@%?/";

	private static final String HEX = "0123456789ABCDEF";

	private FormatUtils() {
	}

	public static String formatByteSize(long bytes) {
		if (bytes < 1024) {
			return numberFormat.format(bytes) + " Bytes";
		}
		float kb = (float) bytes / 1024;
		if (kb < 1024) {
			return numberFormat.format(kb) + " KB";
		}
		float mb = kb / 1024;
		return numberFormat.format(mb) + " MB";
	}

	/**
	 * <pre>
	 * camelCase -> Camel Case
	 * CamelCASE -> Camel CASE
	 * </pre>
	 */
	public static String camelToTitleCase(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		boolean lastWasLower = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c) || Character.isDigit(c)) {
				if (lastWasLower) {
					sb.append(' ');
				}
			}
			else {
				lastWasLower = true;
				if (i == 0) {
					c = Character.toUpperCase(c);
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * foo-bar -> fooBar
	 * Foo-bAR -> FooBAR
	 * </pre>
	 */
	public static String xmlToCamelCase(String s) {
		StringBuffer sb = new StringBuffer(s);
		int offset = 0;
		int i;
		while ((i = sb.indexOf("-", offset)) >= 0) {
			sb.deleteCharAt(i);
			sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
			offset = i;
		}
		return sb.toString();
	}

	/**
	 * <pre>
	 * foo-bar -> Foo Bar
	 * fooBar  -> Foo Bar
	 * </pre>
	 */
	public static String xmlToTitleCase(String s) {
		return camelToTitleCase(xmlToCamelCase(s));
	}

	/**
	 * <pre>
	 * foo.bar    -> Foo Bar
	 * foo.barBar -> Foo Bar Bar
	 * </pre>
	 */
	public static String propertyToTitleCase(String s) {
		if (s == null) {
			return null;
		}
		return xmlToTitleCase(s.replace('.', '-'));
	}
	/**
	 * <pre>
	 * CamelCase -> camel-case
	 * camelCASE -> camel-case
	 * </pre>
	 */
	public static String camelToXmlCase(String s) {
		if (s == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		boolean lastWasLower = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				if (lastWasLower) {
					sb.append('-');
				}
				c = Character.toLowerCase(c);
			}
			else {
				lastWasLower = true;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * "a", "b", "c" -> "a b c a-b a-b-c"
	 * "a", "b", null -> "a b a-b"
	 */
	public static String combine(String[] s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			if (s[i] != null) {
				sb.append(s[i]);
				if (i < s.length - 1 && s[i + 1] != null) {
					sb.append(' ');
				}
			}
		}
		for (int j = 1; j < s.length; j++) {
			if (s[j] != null) {
				sb.append(' ');
				for (int i = 0; i <= j; i++) {
					sb.append(s[i]);
					if (i < j && s[i + 1] != null) {
						sb.append('-');
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Converts the given String into a valid CSS class name.
	 */
	public static String toCssClass(String s) {
		s = s.replaceAll("[.\\s/]", "-").toLowerCase();
		s = s.replaceAll("[^\\w-_]", "");
		return s;
	}

	/**
	 * Parses a formatted String and returns the value in milliseconds. You can
	 * use one of the following sufixes:
	 * 
	 * <pre>
	 *     s - seconds
	 *     m - minutes
	 *     h - hours
	 *     D - days
	 *     W - weeks
	 *     M - months
	 *     Y - years
	 * </pre>
	 */
	public static long parseMillis(String s) {
		if (s == null) {
			return 0;
		}
		long millis = 0;
		int i = 0;
		int length = s.length();
		while (i < length) {
			long delta = 0;
			char ch = 0;
			for (; i < length; i++) {
				ch = s.charAt(i);
				if (!Character.isDigit(ch)) {
					i++;
					break;
				}
				delta *= 10;
				delta += Character.getNumericValue(ch);
			}
			switch (ch) {
			case 's':
			case 'S':
			default:
				millis += 1000 * delta;
				break;

			case 'm':
				millis += 60 * 1000 * delta;
				break;

			case 'h':
			case 'H':
				millis += 60L * 60 * 1000 * delta;
				break;

			case 'd':
			case 'D':
				millis += 24L * 60 * 60 * 1000 * delta;
				break;

			case 'w':
			case 'W':
				millis += 7L * 24 * 60 * 60 * 1000 * delta;
				break;

			case 'M':
				millis += 30L * 24 * 60 * 60 * 1000 * delta;
				break;

			case 'y':
			case 'Y':
				millis += 365L * 24 * 60 * 60 * 1000 * delta;
				break;
			}
		}
		return millis;
	}

	public static String formatMillis(long millis) {
		int hours = (int) (millis / (1000 * 60 * 60));
		int minutes = (int) (millis / (1000 * 60)) % 60;
		int seconds = (int) (millis / 1000) % 60;
		StringBuffer sb = new StringBuffer();
		if (hours > 0) {
			sb.append(hours);
			sb.append(':');
		}
		if (minutes < 10 && hours > 0) {
			sb.append(0);
		}
		sb.append(minutes);
		sb.append(':');
		if (seconds < 10) {
			sb.append(0);
		}
		sb.append(seconds);
		return sb.toString();
	}

	/**
	 * Returns the extension of the given filename. Examples:
	 * 
	 * <pre>
	 *  	&quot;foo.bar&quot; - &quot;bar&quot;
	 *   &quot;/some/file.name.foo&quot; - &quot;foo&quot;
	 * </pre>
	 * 
	 * The following examples will return an empty String:
	 * 
	 * <pre>
	 *   &quot;foo&quot; 
	 *   &quot;foo.&quot;
	 *   &quot;/dir.with.dots/file&quot;
	 *   &quot;.bar&quot;
	 *   &quot;/foo/.bar&quot;
	 * </pre>
	 */
	public static String getExtension(String filename) {
		if (filename == null) {
			return "";
		}
		int i = filename.lastIndexOf('.');
		if (i <= 0 || i == filename.length() - 1
				|| filename.indexOf('/', i) != -1
				|| filename.indexOf('\\', i) != -1) {

			return "";
		}
		return filename.substring(i + 1);
	}

	/**
	 * Returns the the filename without it's extension.
	 */
	public static String stripExtension(String filename) {
		String extension = getExtension(filename);
		return filename
				.substring(0, filename.length() - extension.length() - 1);
	}

	/**
	 * Parses a formatted String and returns the date. The date to parse starts
	 * with today. You can use one of the following sufixes:
	 * 
	 * <pre>
	 * 	  	  
	 *     D - days
	 *     W - weeks
	 *     M - months
	 *     Y - years
	 * </pre>
	 * 
	 * Days is option, any number without a suffix is treated as a number of
	 * days
	 */
	public static Date parseDate(String s) {
		if (s.startsWith("today")) {
			String op = null;
			int days = 0;
			int months = 0;
			int years = 0;

			s = s.substring(5);
			int i = 0;
			int length = s.length();
			long delta = 0;
			while (i < length) {

				char ch = 0;
				for (; i < length; i++) {
					ch = s.charAt(i);
					if (!Character.isDigit(ch)) {
						i++;
						break;
					}
					delta *= 10;
					delta += Character.getNumericValue(ch);
				}
				switch (ch) {
				case '+':
					op = OP_ADDITION;
					break;
				case '-':
					op = OP_SUBTRACTION;
					break;
				case 'd':
				case 'D':
					if (OP_ADDITION.equals(op)) {
						days += delta;
					}
					else if (OP_SUBTRACTION.equals(op)) {
						days -= delta;
					}
					op = null;
					delta = 0;
					break;

				case 'w':
				case 'W':
					if (OP_ADDITION.equals(op)) {
						days += 7 * delta;
					}
					else if (OP_SUBTRACTION.equals(op)) {
						days -= 7 * delta;
					}
					op = null;
					delta = 0;
					break;

				case 'M':
					if (OP_ADDITION.equals(op)) {
						months += delta;
					}
					else if (OP_SUBTRACTION.equals(op)) {
						months -= delta;
					}
					op = null;
					delta = 0;
					break;

				case 'y':
				case 'Y':
					if (OP_ADDITION.equals(op)) {
						years += delta;
					}
					else if (OP_SUBTRACTION.equals(op)) {
						years -= delta;
					}
					op = null;
					delta = 0;
					break;
				}
				if (delta > 0) {
					if (OP_ADDITION.equals(op)) {
						days += delta;
					}
					else if (OP_SUBTRACTION.equals(op)) {
						days -= delta;
					}
				}

			}
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, days);
			c.add(Calendar.MONTH, months);
			c.add(Calendar.YEAR, years);
			return c.getTime();
		}
		return null;
	}

	public static String formatIsoDate(Date date) {
		return dateFormat.format(date);
	}

	public static String toFilename(String s) {
		s = s.toLowerCase();
		s = s.replaceAll("\\s+", "_");
		s = s.replaceAll("[^a-z_0-9.-]", "");
		return s;
	}

	/**
	 * Turn special characters into escaped characters conforming to XML.
	 * 
	 * @param input the input string
	 * @return the escaped string
	 */
	public static String xmlEscape(String input) {
		if (input == null) {
			return input;
		}
		StringBuffer filtered = new StringBuffer(input.length() + 42);
		char c;
		for (int i = 0; i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '<') {
				filtered.append(ENTITY_LT);
			}
			else if (c == '>') {
				filtered.append(ENTITY_GT);
			}
			else if (c == '&') {
				filtered.append(ENTITY_AMP);
			}
			else if (c == '"') {
				filtered.append(ENTITY_QUOT);
			}
			else {
				filtered.append(c);
			}
		}
		return filtered.toString();
	}

	public static String escapeChars(String s, String chars, char escape) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			if (chars.indexOf(sb.charAt(i)) != -1) {
				sb.insert(i, escape);
				i++;
			}
		}
		return sb.toString();
	}

	public static String regexEscape(String s) {
		return escapeChars(s, ".+*?{[^$", '\\');
	}

	public static String uriEscape(String str) {
		StringBuffer sb = new StringBuffer(str.length());
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if ((ALLOW_URI_CHARS.indexOf(c) == -1) && (c <= 127)) {
				sb.append('%');
				sb.append(HEX.charAt(c / 16));
				sb.append(HEX.charAt(c % 16));
			}
			else
				sb.append(c);
		}
		return sb.toString();
	}
	
	/**
	 * Escapes all HTML special characters in the given array. Dates and 
	 * primitive-wrappers are left as-is, all other objects are converted to
	 * their String representation and escaped using 
	 * {@link HtmlUtils#htmlEscape(String)}.
	 * @since 6.4  
	 */
	public static Object[] htmlEscapeArgs(Object[] args) {
		Object[] escapedArgs = new Object[args.length];
		escapedArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof String) {
				escapedArgs[i] = HtmlUtils.htmlEscape((String) arg);
			}
			else if (ClassUtils.isPrimitiveWrapper(arg.getClass())
					|| arg instanceof Date) {
				
				escapedArgs[i] = arg;
			}
			else {
				escapedArgs[i] = HtmlUtils.htmlEscape(arg.toString());
			}
		}
		return escapedArgs;
	}
	
	/**
	 * Extracts an integer from a String using the first capture group of the 
	 * given regular expression.
	 * @since 6.4
	 */
	public static int parseInt(String s, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			String group = matcher.group(1);
			try {
				return Integer.parseInt(group);
			}
			catch (NumberFormatException e) {
				log.error("Not a valid number: " + group);
			}
		}
		return -1;
	}
}
