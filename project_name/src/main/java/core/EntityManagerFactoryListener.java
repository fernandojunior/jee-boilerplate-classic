package core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.hibernate.SessionFactory;

/**
 * A Servlet Context Listener to build an entity manager factory
 * 
 * @author Fernando Felix do Nascimento Junior
 */
@WebListener
public class EntityManagerFactoryListener implements ServletContextListener {

	private SessionFactory entityManagerFactory = null;

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent e) {
		entityManagerFactory = HibernateUtil.buildEntityManagerFactory();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent e) {
		entityManagerFactory.close();
	}

}
