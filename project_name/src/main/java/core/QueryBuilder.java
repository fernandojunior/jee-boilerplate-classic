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
import javax.persistence.TypedQuery;

import core.EntityModel;

/**
 * A simple query (select statement) builder.
 *
 * References: http://docs.oracle.com/html/E13946_05/ejb3_langref.html
 * http://docs.sqlalchemy.org/en/latest/orm/query.html
 * 
 * {@link org.hibernate.jpa.internal.EntityManagerImpl#createQuery(String)}
 * {@link org.hibernate.internal.AbstractSessionImpl#createQuery(String)}
 * {@link org.hibernate.jpa.internal.QueryImpl}
 * {@link org.hibernate.jpa.spi.AbstractQueryImpl}
 *
 * <code>
 	public static void main(String[] args) {
		QueryBuilder<User> builder = new QueryBuilder<User>(User.class, entityManager);
		builder.select("id", "name").agg("count").distinct(true).join("LEFT JOIN", "profile").like("id", 1)
				.gt("profile.id", 2).between("id", 1, 10).desc("id");
		TypedQuery<User> query = builder.build();
		System.out.println(builder.statement());
		// SELECT COUNT(DISTINCT a_885.id, a_885.name)
		// FROM Message AS a_885 LEFT JOIN a_885.profile
		// WHERE 1 = 1 AND a_885.id LIKE :a_885_p_0 AND a_885.profile.id > :a_885_p_1 AND a_885.id BETWEEN :a_885_p_2 AND :a_885_p_3
		// ORDER BY a_885.id DESC
	}
 * </code>
 *
 * @author Fernando Felix do Nascimento Junior
 */
public class QueryBuilder<E extends EntityModel> {

	private boolean distinct = false;
	private Class<E> entityClass;
	private String agg;
	private String alias;
	private EntityManager entityManager;
	private Set<String> selects = new LinkedHashSet<String>();
	private Set<String> join = new LinkedHashSet<String>();
	private Set<String> where = new LinkedHashSet<String>();
	private Set<String> orderBy = new LinkedHashSet<String>();
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public QueryBuilder(Class<E> entityClass, EntityManager entityManager) {
		this.entityClass = entityClass;
		this.entityManager = entityManager;
		this.alias = randomAlias();
	}

	private Class<E> getEntityClass() {
		return entityClass;
	}

	private String randomAlias() {
		Random random = new Random();
		return "a_" + random.nextInt(10) + "" + random.nextInt(10) + "" + random.nextInt(10);
	}

	private String getAlias() {
		return alias;
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
		return ":" + getAlias() + "_" + "p_" + parameters.size();
	}

	private String adjustPath(String path) {
		return getAlias() + "." + path;
	}

	private QueryBuilder<E> and(String... clause) {
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
	public QueryBuilder<E> select(String... paths) {
		for (String path : paths)
			selects.add(adjustPath(path));
		return this;
	}

	/**
	 * Indicate if DISTINCT keyword must be specified or not in select clause.
	 *
	 * {@link QueryBuilder#select(String...)}
	 *
	 * @param distinct
	 *            true to specify, false otherwise
	 * @return this
	 */
	public QueryBuilder<E> distinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	/**
	 * Apply an aggregate function on the selected paths.
	 *
	 * Reference: 10.2.7.4. JPQL Aggregate Functions
	 *
	 * {@link QueryBuilder#select(String...)} {@link QueryBuilder#unagg()}
	 *
	 * @param function
	 *            "AVG" || "MAX" || "MIN" || "SUM" || "COUNT" | null
	 * @return this
	 */
	public QueryBuilder<E> agg(String function) {
		if (function == null)
			return this;
		function = function.toUpperCase();
		if (!Arrays.asList("AVG", "MAX", "MIN", "SUM", "COUNT").contains(function))
			throw new PersistenceException("Invalid aggregate function " + function + ".");
		this.agg = function;
		return this;
	}

	/**
	 * Remove the aggregate function from select clause
	 *
	 * {@link QueryBuilder#agg()}
	 *
	 * @return this
	 */
	public QueryBuilder<E> unagg() {
		this.agg = null;
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
	public QueryBuilder<E> conditional(String path, String operator) {
		return and(adjustPath(path), operator, nextParameterName());
	}

	public QueryBuilder<E> isNull(String path) {
		return conditional(path, "IS NULL");
	}

	public QueryBuilder<E> isNotNull(String path) {
		return conditional(path, "IS NOT NULL");
	}

	public QueryBuilder<E> isEmpty(String path) {
		return conditional(path, "IS EMPTY");
	}

	public QueryBuilder<E> isNotEmpty(String path) {
		return conditional(path, "IS NOT EMPTY");
	}

	/**
	 * Return a shallow copy of the named parameter value map of the query.
	 * 
	 * @return Shallow copy of the named parameter value map of the query.
	 */
	public Map<String, Object> getParameters() {
		return new HashMap<String, Object>(parameters);
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
	public QueryBuilder<E> conditional(String path, String operator, Object value) {
		return and(adjustPath(path), operator, addParameter(value));
	}

	public QueryBuilder<E> eq(String path, Object value) {
		return conditional(path, "=", value);
	}

	public QueryBuilder<E> gt(String path, Object value) {
		return conditional(path, ">", value);
	}

	public QueryBuilder<E> ge(String path, Object value) {
		return conditional(path, ">=", value);
	}

	public QueryBuilder<E> lt(String path, Object value) {
		return conditional(path, "<", value);
	}

	public QueryBuilder<E> le(String path, Object value) {
		return conditional(path, "<=", value);
	}

	public QueryBuilder<E> ne(String path, Object value) {
		return conditional(path, "<>", value);
	}

	public QueryBuilder<E> like(String path, Object value) {
		return conditional(path, "LIKE", value);
	}

	public QueryBuilder<E> notLike(String path, Object value) {
		return conditional(path, "NOT LIKE", value);
	}

	public QueryBuilder<E> in(String path, Object value) {
		return conditional(path, "IN", value);
	}

	public QueryBuilder<E> notIn(String path, Object value) {
		return conditional(path, "NOT IN", value);
	}

	public QueryBuilder<E> between(String path, Object startValue, Object endValue) {
		return and(adjustPath(path), "BETWEEN", addParameter(startValue), "AND", addParameter(endValue));
	}

	public QueryBuilder<E> exists(QueryBuilder<?> queryBuilder) {
		if (this == queryBuilder || this.getAlias().equals(queryBuilder.getAlias()))
			throw new PersistenceException("QueryBuilder parameter " + getAlias() + " can't be itself.");
		and("EXISTS", ("(" + queryBuilder + ")").replaceAll("\n", " "));
		parameters.putAll(queryBuilder.getParameters());
		return this;
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
	public QueryBuilder<E> join(String spec, String path) {
		path = adjustPath(path);
		List<String> keywords = Arrays.asList("LEFT", "OUTER", "INNER", "JOIN", "FETCH");
		String[] specPartials = spec.trim().replaceAll("\\s+", " ").split(" ");

		for (String specPartial : specPartials)
			if (!keywords.contains(specPartial.toUpperCase()))
				throw new PersistenceException("Join spec " + specPartial + " is not valid.");

		join.add(String.join(" ", spec, path));
		return this;
	}

	public QueryBuilder<E> orderBy(String path, String orderDirection) throws PersistenceException {
		if (!Arrays.asList("ASC", "DESC").contains(orderDirection))
			throw new PersistenceException("Invalid order direction " + orderDirection);
		orderBy.add(adjustPath(path) + " " + orderDirection);
		return this;
	}

	public QueryBuilder<E> asc(String path) throws PersistenceException {
		return orderBy(path, "ASC");
	}

	public QueryBuilder<E> desc(String path) throws PersistenceException {
		return orderBy(path, "DESC");
	}

	/**
	 * 10.2.1.1. JPQL Select Statement
	 *
	 * @return selectClause fromClause [whereClause] [groupby_clause]
	 *         [orderbyClause]
	 */
	public String statement() {
		return String.join("\n", selectClause(), fromClause(), whereClause(), orderByClause());
	}

	/**
	 * Reference 10.2.7. JPQL SELECT Clause
	 *
	 * {@link QueryBuilder#agg(String)} {@link QueryBuilder#distinct(boolean)}
	 * {@link QueryBuilder#select(String...)}
	 *
	 * @return SELECT [AGGREGATE]([DISTINCT] alias | selects)
	 */
	public String selectClause() {
		String aggragate = this.agg != null ? this.agg : "";
		String distinct = this.distinct ? "DISTINCT" : "";
		String selects = this.selects.isEmpty() ? getAlias() : String.join(", ", this.selects);
		return String.format("SELECT %s(%s %s)", aggragate, distinct, selects);
	}

	public String fromClause() {
		return String.join(" ", "FROM", getEntityName(), "AS", getAlias(), joinClause());
	}

	public String joinClause() {
		if (join.size() == 0)
			return "";
		return String.join(", ", join);
	}

	public String whereClause() {
		if (where.size() == 0)
			return "";
		return "WHERE 1 = 1 " + String.join(" ", where);
	}

	public String orderByClause() {
		if (orderBy.size() == 0)
			return "";
		return "ORDER BY " + String.join(", ", orderBy);
	}

	/**
	 * Build a Query based on this queryBuilder.
	 * 
	 * @return A Query
	 */
	public TypedQuery<E> build() {
		return build(entityClass);
	}

	/**
	 * Build a TypedQuery based on this queryBuilder.
	 * 
	 * @param type
	 *            The type of the query
	 * @return A typed query
	 */
	public <T> TypedQuery<T> build(Class<T> type) {
		TypedQuery<T> query = entityManager.createQuery(this.statement(), type);
		for (Entry<String, Object> entry : this.getParameters().entrySet())
			query.setParameter(entry.getKey(), entry.getValue());
		return query;
	}

	public String toString() {
		return statement();
	}

}
