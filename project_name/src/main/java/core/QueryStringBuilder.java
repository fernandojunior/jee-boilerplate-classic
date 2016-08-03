package core;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import core.EntityModel;

/**
 * <code>
 * QueryStringBuilder<Usuario> queryBuilder = new QueryStringBuilder<Usuario>(Usuario.class);
 * queryBuilder.like("id", ":id"); // => usuario.id
 * queryBuilder.gt("teste.id", ":test_id"); // => teste.id
 * queryBuilder.between("date", "abc", "ax", "as", "as"); // usuario.date
 * queryBuilder.ascOrder("as"); // usuario.as
 * queryBuilder.descOrder("asas.as"); // asas.as
 * System.out.println(queryBuilder.build());
 * </code>
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class QueryStringBuilder<E extends EntityModel> {

	private Class<E> entityClass;
	private boolean count = false;
	private Set<String> leftJoins = new LinkedHashSet<String>();
	private StringBuilder whereClausesBuilder = new StringBuilder("");
	private Set<String> orderByClauses = new LinkedHashSet<String>();
	private Map<String, Object> parameterValues = new HashMap<String, Object>();

	public QueryStringBuilder(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	public QueryStringBuilder<E> asCount(boolean count) {
		this.count = count;
		return this;
	}

	private String findAllSelectFrom() {
		return "SELECT DISTINCT " + getAlias() + " FROM " + getEntityName() + " " + getAlias() + " ";
	}

	private String countAllSelectFrom() {
		return "SELECT count(DISTINCT " + getAlias() + ") FROM " + getEntityName() + " " + getAlias() + " ";
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public String getAlias() {
		return getEntityName().toLowerCase();
	}

	public String getEntityName() {
		return getEntityClass().getSimpleName();
	}

	public boolean isCount() {
		return count;
	}

	private QueryStringBuilder<E> appendWhereClause(String str) {
		whereClausesBuilder.append(" " + str + " ");
		return this;
	}

	public QueryStringBuilder<E> and(String clause) {
		return appendWhereClause("AND").appendWhereClause(clause);
	}

	public String adjustPropertyName(String propertyName) {
		if (propertyName.startsWith(".") || propertyName.endsWith("."))
			throw new PersistenceException("Bad formatting in property name " + propertyName);

		if (!propertyName.startsWith(getAlias()) && !propertyName.contains("."))
			return getAlias() + "." + propertyName;
		return propertyName;
	}

	public String adjustParameterName(String parameterName) {
		if (!parameterName.startsWith(":"))
			return ":" + parameterName;
		else
			return parameterName;
	}

	public QueryStringBuilder<E> expression(String propertyName, String operator, String parameterName,
			Object parameterValue) {
		propertyName = adjustPropertyName(propertyName);
		parameterName = adjustParameterName(parameterName);
		and(propertyName + " " + operator + " " + parameterName);
		if (parameterValue != null)
			addParameterValue(parameterName, parameterValue);
		return this;
	}

	public QueryStringBuilder<E> expression(String propertyName, String operation, String parameterName) {
		return expression(propertyName, operation, parameterName, null);
	}

	public QueryStringBuilder<E> like(String propertyName, String parameterName) {
		return like(propertyName, parameterName, null);
	}

	public QueryStringBuilder<E> like(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, "LIKE", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> eq(String propertyName, String parameterName) {
		return eq(propertyName, parameterName, null);
	}

	public QueryStringBuilder<E> eq(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, "=", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> gt(String propertyName, String parameterName) {
		return gt(propertyName, parameterName, null);
	}

	public QueryStringBuilder<E> gt(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, ">", parameterName);
	}

	public QueryStringBuilder<E> ge(String propertyName, String parameterName) {
		return ge(propertyName, parameterName, null);
	}

	public QueryStringBuilder<E> ge(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, ">=", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> lt(String propertyName, String parameterName) {
		return lt(propertyName, parameterName);
	}

	public QueryStringBuilder<E> lt(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, "<", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> le(String propertyName, String parameterName) {
		return le(propertyName, "<=", parameterName);
	}

	public QueryStringBuilder<E> le(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, "<=", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> ne(String propertyName, String parameterName) {
		return ne(propertyName, parameterName, null);
	}

	public QueryStringBuilder<E> ne(String propertyName, String parameterName, Object parameterValue) {
		return expression(propertyName, "!=", parameterName, parameterValue);
	}

	public QueryStringBuilder<E> between(String propertyName, String startParameterName, String endParamterName,
			Object startParameterValue, Object endParamterValue) {
		propertyName = adjustPropertyName(propertyName);
		startParameterName = adjustParameterName(startParameterName);
		endParamterName = adjustParameterName(endParamterName);
		and(propertyName + " BETWEEN " + startParameterName + " AND " + endParamterName);
		addParameterValue(startParameterName, startParameterValue);
		addParameterValue(endParamterName, endParamterValue);
		return this;
	}

	public void betweenDate(String propertyName, String startParameterName, String endParamterName,
			Date startParameterValue, Date endParamterValue) {
		if (startParameterValue != null)
			startParameterValue = toLowerDate(startParameterValue);

		if (endParamterValue != null)
			endParamterValue = toHigherDate(endParamterValue);

		if (startParameterValue != null && endParamterValue != null) {
			between(propertyName, startParameterName, endParamterName, startParameterValue, endParamterValue);
		} else if (startParameterValue != null) {
			ge(propertyName, startParameterName, startParameterValue);
		} else if (endParamterValue != null) {
			le(propertyName, endParamterName, endParamterValue);
		}
	}

	public QueryStringBuilder<E> addLeftJoin(String leftJoin) {
		leftJoins.add(leftJoin);
		return this;
	}

	public Map<String, Object> getParameterValues() {
		return parameterValues;
	}

	public QueryStringBuilder<E> addParameterValue(String name, Object value) {
		parameterValues.put(name, value);
		return this;
	}

	public QueryStringBuilder<E> ascOrder(String propertyName) throws PersistenceException {
		return addOrder(propertyName, "ASC");
	}

	public QueryStringBuilder<E> descOrder(String propertyName) throws PersistenceException {
		return addOrder(propertyName, "DESC");
	}

	public QueryStringBuilder<E> addOrder(String propertyName, String orderDirection) throws PersistenceException {
		if (!Arrays.asList("ASC", "DESC").contains(orderDirection))
			throw new PersistenceException("Invalid order direction " + orderDirection);
		orderByClauses.add(adjustPropertyName(propertyName) + " " + orderDirection + " ");
		return this;
	}

	public String build() {
		return findAllSelectFrom() + " " + buildLeftJoins() + " " + buildWhereClauses() + " " + buildOrderByClauses();
	}

	public String buildCount() {
		return countAllSelectFrom() + " " + buildLeftJoins() + " " + buildWhereClauses() + " " + buildOrderByClauses();
	}

	private String buildLeftJoins() {
		if (leftJoins.size() == 0)
			return "";

		return " " + String.join(", ", leftJoins) + " ";
	}

	private String buildWhereClauses() {
		return " WHERE 1 = 1 " + whereClausesBuilder.toString();
	}

	private String buildOrderByClauses() {
		return " ORDER BY " + String.join(", ", orderByClauses);
	}

	public String toString() {
		return build();
	}

	public Query createQuery(EntityManager entityManager) {
		Query query = entityManager.createQuery(this.build());
		for (Entry<String, Object> entry : this.getParameterValues().entrySet())
			query.setParameter(entry.getKey(), entry.getValue());
		return query;
	}

	private Date toLowerDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	private Date toHigherDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

}
