package core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import core.EntityModel;

/**
 * A simple select statement builder.
 *
 * Reference: http://docs.oracle.com/html/E13946_05/ejb3_langref.html
 *
 * <code>
 	QueryStringBuilder<User> builder = new QueryStringBuilder<User>(User.class);
	builder.aggregate("count").distinct(true).select("id", "name");
	builder.addJoin("LEFT JOIN", "profile");
	builder.like("id", 1).gt("profile.id", 2).between("id", 1, 10);
	builder.descOrder("id");
	System.out.println(builder.build());
	// SELECT COUNT(DISTINCT user.id, user.name)
	// FROM User AS user LEFT JOIN user.profile
	// WHERE 1 = 1 AND user.id LIKE :P0_ AND user.profile.id > :P1_ AND user.id BETWEEN :P2_ AND :P3_
	// ORDER BY user.id DESC
 * </code>
 *
 * @author Fernando Felix do Nascimento Junior
 */
public class QueryStringBuilder<E extends EntityModel> {

	private boolean distinct = false;
	private Class<E> entityClass;
	private String aggragate;
	private Set<String> selects = new LinkedHashSet<String>();
	private Set<String> join = new LinkedHashSet<String>();
	private Set<String> where = new LinkedHashSet<String>();
	private Set<String> orderBy = new LinkedHashSet<String>();
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

	private String addParameter(Object value) {
		String parameterName = nextParameterName();
		parameters.put(nextParameterName(), value);
		return parameterName;
	}

	private String nextParameterName() {
		return ":P" + parameters.size() + "_";
	}

	private String adjustPath(String path) {
		return getAlias() + "." + path;
	}

	private QueryStringBuilder<E> and(String... clause) {
		where.add("AND " + String.join(" ", clause));
		return this;
	}

	/**
	 * Add a path or more in select clause.
	 *
	 * @param paths
	 *            A path expression or more
	 *
	 * @return this
	 */
	public QueryStringBuilder<E> select(String... paths) {
		for (String path : paths)
			selects.add(adjustPath(path));
		return this;
	}

	/**
	 * Indicate if DISTINCT keyword must be specified or not in select clause.
	 *
	 * {@link QueryStringBuilder#select(String...)}
	 *
	 * @param distinct
	 *            true to specify, false otherwise
	 * @return this
	 */
	public QueryStringBuilder<E> distinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	/**
	 * Apply an aggregate function on the selected paths.
	 *
	 * Reference: 10.2.7.4. JPQL Aggregate Functions
	 *
	 * {@link QueryStringBuilder#select(String...)}
	 * {@link QueryStringBuilder#disaggregate()}
	 *
	 * @param function
	 *            "AVG" || "MAX" || "MIN" || "SUM" || "COUNT" | null
	 * @return this
	 */
	public QueryStringBuilder<E> aggregate(String function) {
		if (function == null)
			return this;
		function = function.toUpperCase();
		if (!Arrays.asList("AVG", "MAX", "MIN", "SUM", "COUNT").contains(function))
			throw new PersistenceException("Invalid aggregate function " + function + ".");
		this.aggragate = function;
		return this;
	}

	/**
	 * Remove the aggregate function from select clause
	 *
	 * {@link QueryStringBuilder#aggragate()}
	 *
	 * @return this
	 */
	public QueryStringBuilder<E> disaggregate() {
		this.aggragate = null;
		return this;
	}

	/**
	 * 10.2.5.5. JPQL Conditional Expression Composition
	 *
	 * @param path
	 *            Path expression
	 * @param operator
	 *            IS [NOT] NULL, IS [NOT] EMPTY
	 * @return this
	 */
	public QueryStringBuilder<E> conditional(String path, String operator) {
		return and(adjustPath(path), operator, nextParameterName());
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

	public Map<String, Object> getParameters() {
		return parameters;
	}

	/**
	 * 10.2.5.5. JPQL Conditional Expression Composition
	 *
	 * @param path
	 *            Path expression
	 * @param operator
	 *            =, >, >=, <, <=, <>, [NOT] LIKE, [NOT] IN
	 * @param value
	 *            Path expression value
	 * @return this
	 */
	public QueryStringBuilder<E> conditional(String path, String operator, Object value) {
		return and(adjustPath(path), operator, addParameter(value));
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
		return and(adjustPath(path), "BETWEEN", addParameter(startValue), "AND", addParameter(endValue));
	}

	/**
	 * Reference 10.2.3.5. JPQL Joins
	 *
	 * @param spec
	 *            [LEFT [OUTER] | INNER] JOIN [FETCH]
	 * @param path
	 *            Join association path expression
	 * @return this
	 */
	public QueryStringBuilder<E> addJoin(String spec, String path) {
		path = adjustPath(path);
		List<String> keywords = Arrays.asList("LEFT", "OUTER", "INNER", "JOIN", "FETCH");
		String[] specPartials = spec.trim().replaceAll("\\s+", " ").split(" ");

		for (String specPartial : specPartials)
			if (!keywords.contains(specPartial.toUpperCase()))
				throw new PersistenceException("Join spec " + specPartial + " is not valid.");

		join.add(String.join(" ", spec, path));
		return this;
	}

	public QueryStringBuilder<E> addOrder(String path, String orderDirection) throws PersistenceException {
		if (!Arrays.asList("ASC", "DESC").contains(orderDirection))
			throw new PersistenceException("Invalid order direction " + orderDirection);
		orderBy.add(adjustPath(path) + " " + orderDirection);
		return this;
	}

	public QueryStringBuilder<E> ascOrder(String path) throws PersistenceException {
		return addOrder(path, "ASC");
	}

	public QueryStringBuilder<E> descOrder(String path) throws PersistenceException {
		return addOrder(path, "DESC");
	}

	/**
	 * 10.2.1.1. JPQL Select Statement
	 *
	 * @return selectClause fromClause [whereClause] [groupby_clause]
	 *         [orderbyClause]
	 */
	public String build() {
		return String.join("\n", buildSelectClause(), buildFromClause(), buildWhereClause(), buildOrderByClause());
	}

	/**
	 * Reference 10.2.7. JPQL SELECT Clause
	 *
	 * {@link QueryStringBuilder#aggregate(String)}
	 * {@link QueryStringBuilder#distinct(boolean)}
	 * {@link QueryStringBuilder#select(String...)}
	 *
	 * @return SELECT [AGGREGATE]([DISTINCT] alias | selects)
	 */
	private String buildSelectClause() {
		String aggragate = this.aggragate != null ? this.aggragate : "";
		String distinct = this.distinct ? "DISTINCT" : "";
		String selects = this.selects.isEmpty() ? getAlias() : String.join(", ", this.selects);
		return String.format("SELECT %s(%s %s)", aggragate, distinct, selects);
	}

	private String buildFromClause() {
		return String.join(" ", "FROM", getEntityName(), "AS", getAlias(), buildJoinClause());
	}

	private String buildJoinClause() {
		if (join.size() == 0)
			return "";
		return String.join(", ", join);
	}

	private String buildWhereClause() {
		if (where.size() == 0)
			return "";
		return "WHERE 1 = 1 " + String.join(" ", where);
	}

	private String buildOrderByClause() {
		if (orderBy.size() == 0)
			return "";
		return "ORDER BY " + String.join(", ", orderBy);
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
