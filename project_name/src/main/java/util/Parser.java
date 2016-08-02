package util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A Parser provides methods to parse a String value. <br>
 * 
 * Example:
 * 
 * <code>
 * Parser x = new Parser("10");
 * x.getDouble();
 * x.getInteger();
 * x.getObject(Double.class)
 * x.parse(Integer.class, 2)
 * </code>
 * 
 * {@link} https://github.com/mysql/mysql-connector-j/blob/
 * 3289a357af6d09ecc1a10fd3c26e95183e5790ad/src/com/mysql/jdbc/ResultSetImpl.
 * java
 * 
 * @author Fernando Felix do Nascimento Junior
 * 
 */
public class Parser {

	private String value;

	public Parser(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Verifica se o valor for nulo
	 * 
	 * @return
	 */
	public Boolean isNull() {
		if (value != null && !value.equals(""))
			return false;
		return true;
	}

	/**
	 * Lanca excessao se o valor do parametro for nulo
	 * 
	 * @throws ParseException
	 */
	public void throwIfNull() throws ParseException {
		if (isNull())
			throw new ParseException("Value is null", -1);
	}

	public Character getCharacterOrNull() throws ParseException {
		if (isNull())
			return null;

		if (value.length() > 1)
			throw new ParseException("The length of value is greater than 1.", value.length() - 1);

		return value.charAt(0);
	}

	public Character getCharacter() throws ParseException {
		throwIfNull();
		return getCharacterOrNull();
	}

	public String getStringOrNull() throws ParseException {
		return value;
	}

	public String getString() throws ParseException {
		throwIfNull();
		return value;
	}

	public Short getShortOrNull() {
		if (isNull())
			return null;
		return Short.parseShort(value);
	}

	public Short getShort() throws ParseException {
		throwIfNull();
		return getShortOrNull();
	}

	public Integer getIntegerOrNull() throws ParseException {
		if (isNull())
			return null;
		return Integer.parseInt(value);
	}

	public Integer getInteger() throws ParseException {
		throwIfNull();
		return getIntegerOrNull();
	}

	public Byte getByte() throws ParseException {
		throwIfNull();
		return getByteOrNull();
	}

	public Byte getByteOrNull() throws ParseException {
		if (isNull())
			return null;
		return Byte.parseByte(value);
	}

	public Long getLongOrNull() throws ParseException {
		if (isNull())
			return null;
		return Long.parseLong(value);
	}

	public Long getLong() throws ParseException {
		throwIfNull();
		return getLongOrNull();
	}

	public Float getFloatOrNull() throws ParseException {
		if (isNull())
			return null;
		return Float.parseFloat(value);
	}

	public Float getFloat() throws ParseException {
		throwIfNull();
		return getFloatOrNull();
	}

	public Double getDoubleOrNull() throws ParseException {
		if (isNull())
			return null;
		return Double.parseDouble(value);
	}

	public Double getDouble() throws ParseException {
		throwIfNull();
		return getDoubleOrNull();
	}

	public Boolean getBooleanOrNull(Object reference) throws ParseException {
		if (isNull())
			return null;
		if (value.equals(reference))
			return true;
		return false;
	}

	public Boolean getBooleanOrNull() throws ParseException {
		return getBooleanOrNull(true);
	}

	public Boolean getBoolean(Object reference) throws ParseException {
		throwIfNull();
		return getBooleanOrNull(reference);
	}

	public Boolean getBoolean() throws ParseException {
		throwIfNull();
		return getBooleanOrNull();
	}

	public Date getDateOrNull(String format) throws ParseException {
		if (!isNull())
			return DateUtil.stringToDate(value, format);

		return null;
	}

	public Date getDate(String format) throws ParseException {
		throwIfNull();
		return getDateOrNull(format);
	}

	public Date getDateOrNull() throws ParseException {
		if (!isNull())
			return DateUtil.stringToDate(value);
		return null;

	}

	public Date getDate() throws ParseException {
		throwIfNull();
		return getDateOrNull();

	}

	public <T> T getObject(Class<T> type) throws ParseException {
		throwIfNull();
		return getObjectOrNull(type);
	}

	/**
	 * TODO
	 * 
	 * @param type
	 * @param parameterValues
	 * @return
	 * @throws ParseException
	 */
	public <T> T parse(Class<T> type, Object... parameterValues) throws ParseException {
		String[] methodRegexen = { "parse" + type.getSimpleName(), "parse", ".*parse.*" };

		T result = null;
		ArrayList<String> exceptionMessages = new ArrayList<String>();

		for (String methodRegex : methodRegexen) {
			try {
				Pattern pattern = Pattern.compile(methodRegex);
				result = parse(type, pattern, parameterValues);
				break;
			} catch (ParseException e) {
				exceptionMessages.add(e.getMessage());
			}
		}

		if (result == null)
			throw new ParseException(String.join("\n", exceptionMessages), -1);

		return result;
	}

	public <T> T parse(Class<T> type, Pattern methodPattern, Object... parameterValues) throws ParseException {
		ArrayList<String> exceptions = new ArrayList<String>();

		for (Method method : type.getDeclaredMethods()) {
			if (!methodPattern.matcher(method.getName()).matches())
				continue;

			try {
				parse(method, parameterValues);
			} catch (ParseException e) {
				exceptions.add(e.getMessage());
			}
		}

		if (exceptions.size() == 0)
			throw new ParseException(
					"Method name pattern " + methodPattern + " not found for the type " + type.getName() + ".", -1);

		throw new ParseException("Error when parsing with a method name matching the pattern " + methodPattern
				+ " for the type " + type.getName() + ":\n\t" + String.join(", ", exceptions) + ".", -1);
	}

	@SuppressWarnings("unchecked")
	public <T> T parse(Method method, Object... parameterValues) throws ParseException {
		ArrayList<Object> allParameterValues = new ArrayList<Object>();
		Collections.addAll(allParameterValues, value);
		Collections.addAll(allParameterValues, parameterValues);

		try {
			return (T) method.invoke(null, allParameterValues.toArray());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new ParseException(
					e.getMessage() + method.getName() + " " + Arrays.asList(method.getParameterTypes()), -1);
		}
	}

	public <T> T cast(Class<T> type) throws ParseException {
		try {
			return type.cast(value);
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage(), -1);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getObjectOrNull(Class<T> type) throws ParseException {
		if (isNull()) {
			return null;
		} else if (value.getClass().equals(type)) {
			return (T) value;
		} else if (value.equals(type)) {
			return (T) value;
		} else if (type.equals(Boolean.class)) {
			return (T) getBoolean();
		} else if (type.equals(Byte.class)) {
			return (T) getByte();
		} else if (type.equals(Short.class)) {
			return (T) getShort();
		} else if (type.equals(Integer.class)) {
			return (T) getInteger();
		} else if (type.equals(Long.class)) {
			return (T) getLong();
		} else if (type.equals(Float.class)) {
			return (T) getFloat();
		} else if (type.equals(Double.class)) {
			return (T) getDouble();
		} else if (type.equals(Character.class)) {
			return (T) getCharacter();
		} else if (type.equals(String.class)) {
			return (T) getString();
		} else if (type.equals(Date.class)) {
			return (T) getDate();
		} else {
			try {
				return (T) cast(type);
			} catch (ParseException e) {
				throw new ParseException("Parse not supported for type " + type.getName(), -1);
			}
		}

	}

	/**
	 * TODO revisar <br>
	 * Corverte um determinado objeto para outro tipo conforme a classe passada
	 * como parametro
	 * 
	 * @param c
	 *            A classe base
	 * @param value
	 *            O objeto a ser convertido
	 * @return O objeto convertido
	 * @throws ParseException
	 */
	public static Object parseValue(Class<?> type, Object value) throws ParseException {
		if (value.getClass().isArray()) {
			ArrayList<Object> rra = new ArrayList<Object>();
			for (Object object : (Object[]) value)
				rra.add(parseValue(type, object));
			return rra.toArray();
		}
		return new Parser(value.toString()).getObject(type);
	}

	/**
	 * 
	 * TODO revisar <br>
	 * 
	 * @param c
	 * @param values
	 * @return
	 * @throws ParseException
	 */
	public static Object[] parseArray(Class<?> c, Object[] values) throws ParseException {

		Object[] array = (Object[]) Array.newInstance(c, values.length);

		ArrayList<Object> tmp = new ArrayList<Object>();

		for (Object v : values)
			tmp.add(parseValue(c, v));

		array = tmp.toArray(array);

		return array;
	}

	/**
	 * TODO revisar <br>
	 * 
	 * @param values
	 * @return
	 */
	public static Object[] removeDuplicates(Object[] values) {

		Set<Object> tmp = new LinkedHashSet<Object>();

		for (Object o : values)
			tmp.add(o);

		return tmp.toArray();

	}

	/**
	 * TODO revisar <br>
	 * 
	 * @param collection
	 * @return
	 */
	public static <E> E[] toArray(Collection<E> collection) {

		@SuppressWarnings("unchecked")
		E[] t = (E[]) Array.newInstance(collection.iterator().next().getClass(), collection.size());

		return collection.toArray(t);
	}

	/**
	 * 
	 * TODO revisar <br>
	 * 
	 * @param array
	 * @return
	 */
	public static <E> Set<E> asSet(E[] array) {
		Set<E> tmp = new LinkedHashSet<E>();

		for (E o : array)
			tmp.add(o);

		return tmp;
	}

	/**
	 * TODO revisar <br>
	 * 
	 * @param collection
	 */
	public static <E> void removeEmptyValues(Collection<E> collection) {
		removeEmptyValues(collection.iterator());
	}

	/**
	 * TODO revisar <br>
	 * 
	 * @param iterator
	 */
	private static <E> void removeEmptyValues(Iterator<E> iterator) {
		for (Iterator<E> i = iterator; i.hasNext();) {

			E e = i.next();

			if (e == null || e.equals(""))
				i.remove();
		}

	}

	/**
	 * TODO revisar <br>
	 * 
	 * @param c
	 * @param in
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static <X, Y> Y parseValue2(Class<Y> c, X in) throws ParseException {

		return (Y) parseValue(c, in);

	}

}
