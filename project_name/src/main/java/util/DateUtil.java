package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import java.sql.Timestamp;

/**
 * 
 * TODO Verificar possibilidade de uso do JodaTime
 * 
 * {@link http://javafree.uol.com.br/topic-10264-Somar-10-dias-a-uma-data.html}
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class DateUtil {

	private DateUtil() {
	}

	/**
	 * Retorna o valor do horário minimo para a data de referencia passada. <BR>
	 * <BR>
	 * Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data
	 * retornada por este metodo será "30/01/2009 as 00h:00m:00s e 000ms".
	 * 
	 * @param date
	 *            de referencia.
	 * @return {@link Date} que representa o horário minimo para dia informado.
	 */
	public static Date lowDateTime(Date date) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(date);
		aux.set(Calendar.HOUR_OF_DAY, 0);
		aux.set(Calendar.MINUTE, 0);
		aux.set(Calendar.SECOND, 0);
		aux.set(Calendar.MILLISECOND, 0);
		return aux.getTime();
	}

	/**
	 * Retorna o valor do horário maximo para a data de referencia passada. <BR>
	 * <BR>
	 * Por exemplo se a data for "30/01/2009 as 17h:33m:12s e 299ms" a data
	 * retornada por este metodo será "30/01/2009 as 23h:59m:59s e 999ms".
	 * 
	 * @param date
	 *            de referencia.
	 * @return {@link Date} que representa o horário maximo para dia informado.
	 */
	public static Date highDateTime(Date date) {
		Calendar aux = Calendar.getInstance();
		aux.setTime(date);
		aux.set(Calendar.HOUR_OF_DAY, 23);
		aux.set(Calendar.MINUTE, 59);
		aux.set(Calendar.SECOND, 59);
		aux.set(Calendar.MILLISECOND, 999);
		return aux.getTime();
	}

	/**
	 * Retorna a diferenca entre duas datas
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getDateDifference(Date date1, Date date2) {
		long DAY = 24L * 60L * 60L * 1000L;
		return ((DateUtil.lowDateTime(date1).getTime() - DateUtil.lowDateTime(date2).getTime()) / DAY * -1);
	}

	/**
	 * A data atual
	 */
	public static Date date = new Date();

	/**
	 * O ano atual
	 * 
	 * @return
	 */
	public static int getCurrentYear() {
		return Integer.parseInt(DateUtil.dateToString(date, "yyyy"));
	}

	/**
	 * O mes atual
	 * 
	 * @return
	 */
	public static int getCurrentMonth() {
		return Integer.parseInt(DateUtil.dateToString(date, "mm"));
	}

	/**
	 * Adiciona uma determinada quantidade de meses a uma data
	 * 
	 * @param qt
	 *            A quantidade
	 * @param date
	 *            A data
	 * @return A nova data gerada
	 */
	public static Date addMonth(int qt, Date date) {

		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.add(Calendar.MONTH, qt);

		return c.getTime();

	}

	/**
	 * Adiciona uma determinada quantidade de dias a uma data
	 * 
	 * @param qt
	 *            A quantidade
	 * @param date
	 *            A data
	 * @return A nova data gerada
	 */
	public static Date addDay(int qt, Date date) {
		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, qt);
		return c.getTime();

	}

	/**
	 * O dia de uma data
	 * 
	 * @param date
	 *            A data
	 * @return
	 */
	public static int getDay(Date date) {
		return Integer.parseInt(DateUtil.dateToString(date, "dd"));
	}

	/**
	 * O mes de uma data
	 * 
	 * @param date
	 *            A data
	 * @return
	 */
	public static int month(Date date) {
		return Integer.parseInt(DateUtil.dateToString(date, "MM"));
	}

	/**
	 * O ano de uma data
	 * 
	 * @param date
	 *            A data
	 * @return
	 */
	public static int year(Date date) {
		return Integer.parseInt(DateUtil.dateToString(date, "yyyy"));
	}

	/**
	 * Cria uma data
	 * 
	 * @param year
	 *            O ano da data
	 * @param month
	 *            O mes da data
	 * @param day
	 *            O dia da data
	 * @return A data gerada
	 */
	public static Date date(int year, int month, int day) {
		Calendar c = new GregorianCalendar(year, month - 1, day);

		return c.getTime();

	}

	/**
	 * Primeiro dia do mes conforme a data passada como parametro
	 * 
	 * @param date
	 * @return
	 */
	public static Date firstDay(Date date) {

		int year = year(date);
		int month = month(date);

		return date(year, month, 1);
	}

	/**
	 * Ultimo dia do mes conforme a data passada como parametro
	 * 
	 * @param date
	 * @return
	 */
	public static Date lastDay(Date date) {

		Calendar c = Calendar.getInstance();

		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

		return c.getTime();
	}

	/**
	 * Converte uma data em String em Date
	 * 
	 * @param strdate
	 *            A data em string
	 * @param df
	 *            O formato da data
	 * 
	 * @return
	 */
	public static java.util.Date stringToDate(String strdate, SimpleDateFormat df) throws ParseException {
		df.setLenient(false);
		java.util.Date data = df.parse(strdate);
		return data;
	}

	/**
	 * Converte uma data em String em Date
	 * 
	 * @param strdate
	 *            A data em string
	 * @param format
	 *            o formato da data em String
	 * @return
	 */
	public static java.util.Date stringToDate(String strdate, String format) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return stringToDate(strdate, df);
	}

	/**
	 * Converte uma data em String, com formato dd/MM/yyyy, em Date
	 * 
	 * @param strdate
	 *            A data em string
	 * 
	 * @return
	 */
	public static java.util.Date stringToDate(String strdate) throws ParseException {
		return stringToDate(strdate, "dd/MM/yyyy");
	}

	public static String dateToString(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	/**
	 * Converte um objeto Date em String (em algum formato)
	 * 
	 * @param date
	 *            O objeto Date
	 * @param format
	 *            o formato de saida da data em String
	 * @return
	 */
	public static String dateToString(java.sql.Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	/**
	 * Converte um objeto Date em String (no formato dd/MM/yyyy)
	 * 
	 * @param date
	 *            O objeto Date
	 * @return
	 */
	public static String dateToString(Date date) {
		return dateToString(date, "dd/MM/yyyy");
	}

	/**
	 * Converte um objeto Date em String (no formato dd/MM/yyyy)
	 * 
	 * @param date
	 *            O objeto Date
	 * @return
	 */
	public static String dateToString(java.sql.Date date) {
		return dateToString(date, "dd/MM/yyyy");
	}

	/**
	 * Converte um TimeStamp para string no formato yyyy-MM-dd HH:mm:ss
	 * 
	 * @param dtTime
	 * @return
	 */
	public static String TimeStampToString(Timestamp dtTime) {
		String data;
		data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dtTime);

		return data;

	}

	public static String TimeStampToString2(Timestamp dtTime) {
		String data;

		data = new SimpleDateFormat("dd/MM/yyyy").format(dtTime);

		return data;
	}

	/**
	 * Converte uma String de data (em algum formato) em um objeto Date
	 * 
	 * @param strdate
	 *            A String
	 * @param format
	 *            O formato da data da String
	 * @return O objeto Date
	 * @throws ParseException
	 */
	public static java.sql.Date strToSqlDate(String strdate, String format) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(format);
		java.sql.Date date = new java.sql.Date(df.parse(strdate).getTime());
		return date;
	}

	/**
	 * Converte uma String de data (no formato dd/MM/yyyy) em um objeto Date
	 * 
	 * @param strdate
	 *            A String
	 * @return O objeto Date
	 * @throws ParseException
	 */
	public static java.sql.Date strToSqlDate(String strdate) throws ParseException {
		return strToSqlDate(strdate, "dd/MM/yyyy");
	}

	/**
	 * Dia da semana
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1;
	}

	public static List<Integer> getSemanaCircular() {

		List<Integer> ordem = new ArrayList<Integer>();

		int dayOfWeek = DateUtil.getDayOfWeek(new Date());

		for (int i = 0; i < 7; i++) {

			int as = (i + dayOfWeek) % 7; // array circular

			ordem.add(as);

		}

		return ordem;

	}

	/**
	 * Um conjunto de datas
	 * 
	 * @param qt
	 * @param date
	 * @return
	 */
	public static List<Date> period(int qt, Date date) {

		List<Date> dates = new ArrayList<Date>();

		if (qt >= 0)
			for (int i = 0; i < qt; i++)
				dates.add(addDay(i, date));

		else
			for (int i = qt + 1; i < 1; i++)
				dates.add(addDay(i, date));

		return dates;

	}

	public static List<Date> week(Date date) {

		List<Date> dates = new ArrayList<Date>();

		date = addDay(-getDayOfWeek(date), date);

		for (int i = 0; i < 7; i++) {
			dates.add(addDay(i, date));
		}

		return dates;

	}

	/*
	 * Verifica se a data 1 eh menor que a data 2 e returna True/false
	 */
	public static Boolean compare(Date data1, Date data2) {

		if (data1.before(data2))
			return true; // data2 maior
		else if (data1.after(data2))
			return false; // data1 maior
		else
			return true; // datas iguais
	}

}