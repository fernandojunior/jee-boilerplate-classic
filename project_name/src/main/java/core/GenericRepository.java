package core;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.InstantiationException;

/**
 * Generic CRUD Repository for entities.
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class GenericRepository<T extends EntityModel> {

	private Class<T> entityClass;
	private Session entityManager;

	/**
	 * Constructor for generic repositories
	 *
	 * @param model
	 * @param entityManager
	 */
	public GenericRepository(Class<T> model, Session entityManager) {
		this.entityClass = model;
		this.entityManager = entityManager;
	}

	/**
	 * Constructor for non-generic BaseRepository subclasses.
	 * {@link http://stackoverflow.com/questions/6624113/get-type-name-for-generic-parameter-of-generic-class}
	 */
	@SuppressWarnings("unchecked")
	public GenericRepository(Session entityManager) {
		this.entityManager = entityManager;
		try {
			this.entityClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);
		} catch (ClassCastException e) {
			throw new InstantiationException("Constructor reserved only for non-generic subclasses. " + e.getMessage(),
					GenericRepository.class);
		}
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public Session getEntityManager() {
		return entityManager;
	}

	public Transaction beginTransaction() {
		return entityManager.beginTransaction();
	}

	public void commit() {
		entityManager.getTransaction().commit();
	}

	public Long save(T o) {
		return (Long) entityManager.save(o);
	}

	public void update(T o) {
		o.setDateUpdated(new Date());
		entityManager.update(o);
	}

	public void saveOrUpdate(T o) {
		o.setDateUpdated(new Date());
		entityManager.saveOrUpdate(o);
	}

	public Query<T> createQuery(String query) {
		return entityManager.createQuery(query, entityClass);
	}

	@SuppressWarnings("deprecation")
	public Criteria createCriteria() {
		return entityManager.createCriteria(entityClass);
	}

	public void delete(T o) {
		entityManager.delete(o);
	}

	public T get(Long id) {
		return (T) entityManager.get(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return createCriteria().list();
	}

	public static <E extends EntityModel> GenericRepository<E> create(Class<E> o, Session entityManager) {
		return new GenericRepository<E>(o, entityManager);
	}

}
