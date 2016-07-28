package database;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * A Http Session Listener for manage Hibernate Session lifecycle
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
@WebListener
public class HibernateSessionListener implements HttpSessionListener {

	public Session hibernateSession = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionCreated(HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent e) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		hibernateSession = sessionFactory.openSession();
		e.getSession().setAttribute("hibernate_session", hibernateSession);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see HttpSessionListener#sessionDestroyed(HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent e) {
		hibernateSession.close();
	}

}
