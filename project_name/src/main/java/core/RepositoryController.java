package core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Simple repository servlet to serve as controller between Views (JSP pages)
 * and Repositories.
 * 
 * http://stackoverflow.com/questions/30550189/what-is-service-method-in-
 * httpservlet-class
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class RepositoryController<R extends GenericRepository<?>> extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private R repository;

	@SuppressWarnings("unchecked")
	private Class<R> getRepositoryClass() {
		return ((Class<R>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	private R createRepository(Session entityManager) throws ServletException {
		try {
			return getRepositoryClass().getConstructor(Session.class).newInstance(entityManager);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected R getRespository() {
		return repository;
	}

	private Session getEntityManager(HttpServletRequest request) {
		return (Session) request.getSession(true).getAttribute("entity_manager");
	}

	protected void forward(String path, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

	protected void redirect(String path, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(path);
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Session entityManager = getEntityManager(request);
		repository = createRepository(entityManager);
		String template = getRespository().getEntityName().toLowerCase() + ".jsp";

		Transaction transaction = repository.getEntityManager().getTransaction();
		try {
			transaction.begin();
			super.service(request, response);
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null && transaction.isActive())
				transaction.rollback();
			throw new ServletException(e);
		}

		if (!response.isCommitted())
			forward("WEB-INF/" + template, request, response);
	}

	protected void doAction(String action, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Method method = this.getClass().getMethod(action, HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, request, response);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			throw new ServletException("Servlet action " + action + " does not exist or can not be accessed.", e);
		}
	}

}
