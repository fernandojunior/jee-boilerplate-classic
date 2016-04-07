package database;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

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

	public Manager(Class<T> model, Session session) {
		this.model = model;
		this.session = session;
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

	public Query createQuery(String query) {
		return session.createQuery(query);
	}

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
