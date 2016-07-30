package core;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A Http Session Listener to create session scoped entity managers
 * 
 * @author Fernando Felix do Nascimento Junior
 */
@WebListener
public class EntityManagerListener implements HttpSessionListener {

	public Session entityManager = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent e) {
		SessionFactory sessionFactory = HibernateUtil.getEntityManagerFactory();
		entityManager = sessionFactory.openSession();
		e.getSession().setAttribute("entity_manager", entityManager);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent e) {
		entityManager.close();
	}

}
