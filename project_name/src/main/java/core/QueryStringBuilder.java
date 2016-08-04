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
 * Reference: http://docs.oracle.com/html/E13946_05/ejb3_langref.html
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

	private String adjustPath(String path) {
		return getAlias() + "." + path;
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

	private QueryStringBuilder<E> addWhereClause(String clause) {
		whereClauses.add(clause);
		return this;
	}

	private QueryStringBuilder<E> and(String... clause) {
		return addWhereClause("AND " + String.join(" ", clause));
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
	 *            Path expression
	 *
	 * @param operator
	 *            IS [NOT] NULL, IS [NOT] EMPTY
	 * @return this
	 */
	public QueryStringBuilder<E> conditional(String path, String operator) {
		path = adjustPath(path);
		String parameterName = generateParameterName();
		and(path, operator, parameterName);
		return this;
	}

	public QueryStringBuilder<E> isNull(String path) {
		return conditional(path, "IS NULL");
	}

	public QueryStringBuilder<E> isNotNull(String path) {
		return conditional(path, "IS NOT NULL");
	}

	public QueryStringBuilder<E> isEmpty(String path) {
		return conditional(path, "IS EMPTY");
	}

	public QueryStringBuilder<E> isNotEmpty(String path) {
		return conditional(path, "IS NOT EMPTY");
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
		path = adjustPath(path);
		String parameterName = generateParameterName();
		and(path, operator, parameterName);
		addParameter(parameterName, value);
		return this;
	}

	public QueryStringBuilder<E> eq(String path, Object value) {
		return conditional(path, "=", value);
	}

	public QueryStringBuilder<E> gt(String path, Object value) {
		return conditional(path, ">", value);
	}

	public QueryStringBuilder<E> ge(String path, Object value) {
		return conditional(path, ">=", value);
	}

	public QueryStringBuilder<E> lt(String path, Object value) {
		return conditional(path, "<", value);
	}

	public QueryStringBuilder<E> le(String path, Object value) {
		return conditional(path, "<=", value);
	}

	public QueryStringBuilder<E> ne(String path, Object value) {
		return conditional(path, "<>", value);
	}

	public QueryStringBuilder<E> like(String path, Object value) {
		return conditional(path, "LIKE", value);
	}

	public QueryStringBuilder<E> notLike(String path, Object value) {
		return conditional(path, "NOT LIKE", value);
	}

	public QueryStringBuilder<E> in(String path, Object value) {
		return conditional(path, "IN", value);
	}

	public QueryStringBuilder<E> notIn(String path, Object value) {
		return conditional(path, "NOT IN", value);
	}

	public QueryStringBuilder<E> between(String path, Object startValue, Object endValue) {
		path = adjustPath(path);
		String startParameterName = generateParameterName();
		String endParamterName = generateParameterName();
		and(path, "BETWEEN", startParameterName, "AND", endParamterName);
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
		path = adjustPath(path);
		List<String> identifiers = Arrays.asList("LEFT", "OUTER", "INNER", "JOIN", "FETCH");
		String[] specArray = spec.trim().replaceAll("\\s+", " ").split(" ");

		for (String s : specArray)
			if (!identifiers.contains(s.toUpperCase()))
				throw new PersistenceException("Join spec " + s + " is not valid.");

		joinClauses.add(String.join(" ", spec, path));
		return this;
	}

	public QueryStringBuilder<E> addOrder(String path, String orderDirection) throws PersistenceException {
		if (!Arrays.asList("ASC", "DESC").contains(orderDirection))
			throw new PersistenceException("Invalid order direction " + orderDirection);
		orderByClauses.add(adjustPath(path) + " " + orderDirection);
		return this;
	}

	public QueryStringBuilder<E> ascOrder(String path) throws PersistenceException {
		return addOrder(path, "ASC");
	}

	public QueryStringBuilder<E> descOrder(String path) throws PersistenceException {
		return addOrder(path, "DESC");
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
