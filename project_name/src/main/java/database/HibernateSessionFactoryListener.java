package database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.SessionFactory;

/**
 * A Servlet Context Listener to manage Hibernate Session Factory lifecycle
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
@WebListener
public class HibernateSessionFactoryListener implements ServletContextListener {

	private SessionFactory hibernateSessionFactory = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent e) {
		hibernateSessionFactory = HibernateUtil.buildSessionFactory();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent e) {
		hibernateSessionFactory.close();
	}

}
