package database;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.InstantiationException;

/**
 * CRUD Repository to manage entities.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 * @param <T>
 *            Entity model to manage
 */
public class Manager<T extends Model> {

	private Class<T> model;
	private Session session;

	/**
	 * Constructor for generic managers
	 *
	 * @param model
	 * @param session
	 */
	public Manager(Class<T> model, Session session) {
		this.model = model;
		this.session = session;
	}

	/**
	 * Constructor for non-generic Manager subclasses.
	 * {@link http://stackoverflow.com/questions/6624113/get-type-name-for-generic-parameter-of-generic-class}
	 */
	@SuppressWarnings("unchecked")
	public Manager(Session session) {
		this.session = session;
		try {
			this.model = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0]);
		} catch (ClassCastException e) {
			throw new InstantiationException("Constructor reserved only for non-generic subclasses. " + e.getMessage(),
					Manager.class);
		}
	}

	public Class<T> getModel() {
		return model;
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
		return session.createQuery(query, model);
	}

	@SuppressWarnings("deprecation")
	public Criteria createCriteria() {
		return session.createCriteria(model);
	}

	public void delete(T o) {
		session.delete(o);
	}

	public T get(Long id) {
		return (T) session.get(model, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		return createCriteria().list();
	}

	public static <M extends Model> Manager<M> create(Class<M> o, Session session) {
		return new Manager<M>(o, session);
	}

}
