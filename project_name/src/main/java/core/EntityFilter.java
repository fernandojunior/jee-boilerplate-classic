package core;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.Type;

import util.DateUtil;
import util.Parser;

/**
 * 
 * Ex: <code>
 * EntityFilter<Message> filter = EntityFilter.create(Message.class, entityManager);
 * filter.between("date", "01/01/2011", "02/08/2012").like("other.id", 1).like("id", "89").list(); // or filter.uniqueResult;
 * </code>
 * 
 * @author Fernando Felix do Nascimento Junior
 * 
 */
public class EntityFilter<E extends EntityModel> {

	private Class<E> entityClass;
	private Criteria criteria;
	private Session entityManager;

	@SuppressWarnings("deprecation")
	public EntityFilter(Class<E> entityClass, Session entityManager) {
		this.entityClass = entityClass;
		this.criteria = entityManager.createCriteria(entityClass);
		this.entityManager = entityManager;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	/**
	 * Converte um valor conforme a classe retornada do tipo de uma propriedade
	 * de uma determinada entidade
	 * 
	 * @param entityClass
	 *            A entidade
	 * @param propertyName
	 *            O nome da propriedade
	 * @param value
	 *            O valor a ser convertido
	 * @return O valor convertido
	 * @throws ParseException
	 */
	public Object parseValue(String propertyName, Object value) throws QueryException {
		return parseValue(getPropertyType(propertyName), value);
	}

	/**
	 * Coverte um valor conforme a classe retornada de um tipo de propriedade
	 * 
	 * @param type
	 *            O tipo de propriedade
	 * @param value
	 *            O valor a ser convertido
	 * @return O valor convertido
	 * @throws ParseException
	 */
	public Object parseValue(Type propertyType, Object value) throws QueryException {
		try {
			return Parser.parseValue(propertyType.getReturnedClass(), value);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new QueryException(e);
		}
	}

	public Type getPropertyType(String propertyName) throws QueryException {
		return HibernateUtil.getPropertyType(entityClass, propertyName, entityManager.getSessionFactory());
	}

	public EntityFilter<E> add(Criterion expression) {
		criteria.add(expression);
		return this;
	}

	public EntityFilter<E> like(String propertyName, Object value) throws QueryException {
		return add(Restrictions.like(propertyName, parseValue(propertyName, value)));
	}

	public EntityFilter<E> between(String propertyName, Object lowValue, Object highValue) throws QueryException {
		lowValue = parseValue(propertyName, lowValue);
		highValue = parseValue(propertyName, highValue);

		if (getPropertyType(propertyName).getReturnedClass().equals(Date.class)) {
			lowValue = DateUtil.lowDateTime((Date) lowValue);
			highValue = DateUtil.highDateTime((Date) highValue);
		}

		return add(Restrictions.between(propertyName, lowValue, highValue));
	}

	public EntityFilter<E> lt(String propertyName, Object value) throws QueryException {
		return add(Restrictions.lt(propertyName, value));
	}

	public EntityFilter<E> gt(String propertyName, Object value) throws QueryException {
		return add(Restrictions.gt(propertyName, value));
	}

	public EntityFilter<E> le(String propertyName, Object value) throws QueryException {
		return add(Restrictions.le(propertyName, value));
	}

	public EntityFilter<E> ge(String propertyName, Object value) throws QueryException {
		return add(Restrictions.ge(propertyName, value));
	}

	public EntityFilter<E> isNotNull(String propertyName) throws QueryException {
		return add(Restrictions.isNotNull(propertyName));
	}

	public EntityFilter<E> isNull(String propertyName) throws QueryException {
		return add(Restrictions.isNull(propertyName));
	}

	public EntityFilter<E> ascOrder(String propertyName) {
		criteria.addOrder(Order.asc(propertyName));
		return this;
	}

	public EntityFilter<E> descOrder(String propertyName) throws QueryException {
		criteria.addOrder(Order.desc(propertyName));
		return this;
	}

	public EntityFilter<E> disjunction(Criterion... expressions) throws QueryException {
		Disjunction disjunction = Restrictions.disjunction();

		for (Criterion e : expressions)
			disjunction.add(e);

		criteria.add(disjunction);
		return this;
	}

	public EntityFilter<E> conjunction(Criterion... expressions) throws QueryException {
		Conjunction conjunction = Restrictions.conjunction();

		for (Criterion c : expressions)
			conjunction.add(c);

		criteria.add(conjunction);
		return this;
	}

	public EntityFilter<E> setMaxResults(int maxResults) throws QueryException {
		criteria.setMaxResults(maxResults);
		return this;
	}

	@SuppressWarnings("unchecked")
	public List<E> list() {
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public E uniqueResult() {
		return (E) criteria.uniqueResult();
	}

	public static <T extends EntityModel> EntityFilter<T> create(Class<T> entityClass, Session entityManager) {
		return new EntityFilter<>(entityClass, entityManager);
	}

}
