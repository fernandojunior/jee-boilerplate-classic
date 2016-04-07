package database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.SessionFactory;

/**
 * A Servlet Context Listener to manage Hibernate Session Factory lifecycle
 *
 */
@WebListener
public class SessionFactoryListener implements ServletContextListener {

	private SessionFactory sessionFactory = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent e) {
		sessionFactory = HibernateUtil.buildSessionFactory();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent e) {
		sessionFactory.close();
	}

}
