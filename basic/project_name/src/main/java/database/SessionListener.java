package database;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A Http Session Listener for manage Hibernate Session lifecycle
 *
 */
@WebListener
public class SessionListener implements HttpSessionListener {

	public Session session = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent e) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		session = sessionFactory.openSession();
		e.getSession().setAttribute("database_session", session);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent e) {
		session.close();
	}

}
