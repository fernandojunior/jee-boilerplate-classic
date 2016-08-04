package core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import core.EntityModel;

/**
 * <code>
 	public static void main(String[] args) {
		QueryStringBuilder<Message> queryBuilder = new QueryStringBuilder<Message>(Message.class);
		queryBuilder.like("id", 1).gt("profile.id", 2);
		queryBuilder.between("id", 1, 10).descOrder("id");
		queryBuilder.asCount(true);
		queryBuilder.addJoin("LEFT JOIN", "profile");
		System.out.println(queryBuilder.build());
	}
 * </code>
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class QueryStringBuilder<E extends EntityModel> {

	private Class<E> entityClass;
	private boolean count = false;
	private Set<String> joinClauses = new LinkedHashSet<String>();
	private Set<String> whereClauses = new LinkedHashSet<String>();
	private Set<String> orderByClauses = new LinkedHashSet<String>();
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public QueryStringBuilder(Class<E> entityClass) {
		this.entityClass = entityClass;
	}

	private Class<E> getEntityClass() {
		return entityClass;
	}

	private String getAlias() {
		return getEntityName().toLowerCase();
	}

	private String getEntityName() {
		return getEntityClass().getSimpleName();
	}

	private String adjustPropertyName(String propertyName) {
		return getAlias() + "." + propertyName;
	}

	private String generateParameterName() {
		String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		int length = 3;
		Random rand = new java.util.Random();

		String parameterName = ":";
		for (int i = 0; i < length; i++)
			parameterName += lexicon.charAt(rand.nextInt(lexicon.length()));
		parameterName += "_";

		if (parameters.keySet().contains(parameterName))
			return generateParameterName();

		return parameterName;
	}

	private QueryStringBuilder<E> appendWhereClause(String clause) {
		whereClauses.add(clause);
		return this;
	}

	private QueryStringBuilder<E> and(String... clause) {
		return appendWhereClause("AND " + String.join(" ", clause));
	}

	private String findAllSelectFrom() {
		return "SELECT DISTINCT " + getAlias() + " FROM " + getEntityName() + " " + getAlias();
	}

	private String countAllSelectFrom() {
		return "SELECT count(DISTINCT " + getAlias() + ") FROM " + getEntityName() + " " + getAlias();
	}

	public QueryStringBuilder<E> addParameter(String name, Object value) {
		parameters.put(name, value);
		return this;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public QueryStringBuilder<E> asCount(boolean count) {
		this.count = count;
		return this;
	}

	/**
	 * @param path
	 * 
	 * @param operator
	 *            IS [NOT] NULL, IS [NOT] EMPTY
	 * @return this
	 */
	public QueryStringBuilder<E> conditional(String path, String operator) {
		path = adjustPropertyName(path);
		String parameterName = generateParameterName();
		and(path, operator, parameterName);
		return this;
	}

	public QueryStringBuilder<E> isNull(String propertyName) {
		return conditional(propertyName, "IS NULL");
	}

	public QueryStringBuilder<E> isNotNull(String propertyName) {
		return conditional(propertyName, "IS NOT NULL");
	}

	public QueryStringBuilder<E> isEmpty(String propertyName) {
		return conditional(propertyName, "IS EMPTY");
	}

	public QueryStringBuilder<E> isNotEmpty(String propertyName) {
		return conditional(propertyName, "IS NOT EMPTY");
	}

	/**
	 * @param path
	 *            Path expression
	 * @param operator
	 *            =, >, >=, <, <=, <>, [NOT] LIKE, [NOT] IN
	 * @param value
	 *            Path expression value
	 * @return this
	 */
	public QueryStringBuilder<E> conditional(String path, String operator, Object value) {
		path = adjustPropertyName(path);
		String parameterName = generateParameterName();
		and(path, operator, parameterName);
		addParameter(parameterName, value);
		return this;
	}

	public QueryStringBuilder<E> eq(String propertyName, Object value) {
		return conditional(propertyName, "=", value);
	}

	public QueryStringBuilder<E> gt(String propertyName, Object value) {
		return conditional(propertyName, ">", value);
	}

	public QueryStringBuilder<E> ge(String propertyName, Object value) {
		return conditional(propertyName, ">=", value);
	}

	public QueryStringBuilder<E> lt(String propertyName, Object value) {
		return conditional(propertyName, "<", value);
	}

	public QueryStringBuilder<E> le(String propertyName, Object value) {
		return conditional(propertyName, "<=", value);
	}

	public QueryStringBuilder<E> ne(String propertyName, Object value) {
		return conditional(propertyName, "<>", value);
	}

	public QueryStringBuilder<E> like(String propertyName, Object value) {
		return conditional(propertyName, "LIKE", value);
	}

	public QueryStringBuilder<E> notLike(String propertyName, Object value) {
		return conditional(propertyName, "NOT LIKE", value);
	}

	public QueryStringBuilder<E> in(String propertyName, Object value) {
		return conditional(propertyName, "IN", value);
	}

	public QueryStringBuilder<E> notIn(String propertyName, Object value) {
		return conditional(propertyName, "NOT IN", value);
	}

	public QueryStringBuilder<E> between(String propertyName, Object startValue, Object endValue) {
		propertyName = adjustPropertyName(propertyName);
		String startParameterName = generateParameterName();
		String endParamterName = generateParameterName();
		and(propertyName, "BETWEEN", startParameterName, "AND", endParamterName);
		addParameter(startParameterName, startValue);
		addParameter(endParamterName, endValue);
		return this;
	}

	/**
	 * Example: this.addJoin("JOIN FETCH", "pub.magazines", "mag")
	 * 
	 * @param spec
	 *            Join spec: [LEFT [OUTER] | INNER] JOIN [FETCH]
	 * @param path
	 *            Join association path expression
	 * @return this
	 */
	public QueryStringBuilder<E> addJoin(String spec, String path) {
		path = adjustPropertyName(path);
		List<String> identifiers = Arrays.asList("LEFT", "OUTER", "INNER", "JOIN", "FETCH");
		String[] specArray = spec.trim().replaceAll("\\s+", " ").split(" ");

		for (String s : specArray)
			if (!identifiers.contains(s.toUpperCase()))
				throw new PersistenceException("Join spec " + s + " is not valid.");

		joinClauses.add(String.join(" ", spec, path));
		return this;
	}

	public QueryStringBuilder<E> addOrder(String propertyName, String orderDirection) throws PersistenceException {
		if (!Arrays.asList("ASC", "DESC").contains(orderDirection))
			throw new PersistenceException("Invalid order direction " + orderDirection);
		orderByClauses.add(adjustPropertyName(propertyName) + " " + orderDirection);
		return this;
	}

	public QueryStringBuilder<E> ascOrder(String propertyName) throws PersistenceException {
		return addOrder(propertyName, "ASC");
	}

	public QueryStringBuilder<E> descOrder(String propertyName) throws PersistenceException {
		return addOrder(propertyName, "DESC");
	}

	public String build() {
		return String.join(" ", buildSelectFrom(), buildJoinClauses(), buildWhereClauses(), buildOrderByClauses());
	}

	private String buildSelectFrom() {
		if (count)
			return countAllSelectFrom();
		return findAllSelectFrom();
	}

	private String buildJoinClauses() {
		if (joinClauses.size() == 0)
			return "";
		return " " + String.join(", ", joinClauses);
	}

	private String buildWhereClauses() {
		if (whereClauses.size() == 0)
			return "";
		return "WHERE 1 = 1 " + String.join(" ", whereClauses);
	}

	private String buildOrderByClauses() {
		if (orderByClauses.size() == 0)
			return "";
		return "ORDER BY " + String.join(", ", orderByClauses);
	}

	public String toString() {
		return build();
	}

	public Query createQuery(EntityManager entityManager) {
		Query query = entityManager.createQuery(this.build());
		for (Entry<String, Object> entry : this.getParameters().entrySet())
			query.setParameter(entry.getKey(), entry.getValue());
		return query;
	}

}
