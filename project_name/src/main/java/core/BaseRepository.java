package core;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.InstantiationException;

/**
 * Basic CRUD Repository for entities.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 * @param <T>
 *            Base entity model
 */
public class BaseRepository<T extends BaseEntity> {

	private Class<T> entityClass;
	private Session session;

	/**
	 * Constructor for generic repositories
	 *
	 * @param model
	 * @param session
	 */
	public BaseRepository(Class<T> model, Session session) {
		this.entityClass = model;
		this.session = session;
	}

	/**
	 * Constructor for non-generic BaseRepository subclasses.
	 * {@link http://stackoverflow.com/questions/6624113/get-type-name-for-generic-parameter-of-generic-class}
	 */
	@SuppressWarnings("unchecked")
	public BaseRepository(Session session) {
		this.session = session;
		try {
			this.entityClass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);
		} catch (ClassCastException e) {
			throw new InstantiationException("Constructor reserved only for non-generic subclasses. " + e.getMessage(),
					BaseRepository.class);
		}
	}

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public Session getSession() {
		return session;
	}

	public Long save(T o) {
		return (Long) session.save(o);
	}

	public void update(T o) {
		o.setDateUpdated(new Date());
		session.update(o);
	}

	public void saveOrUpdate(T o) {
		o.setDateUpdated(new Date());
		session.saveOrUpdate(o);
	}

	public Query<T> createQuery(String query) {
		return session.createQuery(query, entityClass);
	}

	@SuppressWarnings("deprecation")
	public Criteria createCriteria() {
		return session.createCriteria(entityClass);
	}

	public void delete(T o) {
		session.delete(o);
	}

	public T get(Long id) {
		return (T) session.get(entityClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return createCriteria().list();
	}

	public static <M extends BaseEntity> BaseRepository<M> create(Class<M> o, Session session) {
		return new BaseRepository<M>(o, session);
	}

}
