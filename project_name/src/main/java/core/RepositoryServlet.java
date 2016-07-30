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

/**
 * Simple repository servlet to serve as controller between Views (JSP pages)
 * and Repositories.
 * 
 * http://stackoverflow.com/questions/30550189/what-is-service-method-in-
 * httpservlet-class
 * 
 * @author Fernando Felix do Nascimento Junior
 */
public class RepositoryServlet<R extends GenericRepository<?>> extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private R repository;
	private String templatePage;

	protected String doAction(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action != null) {
			try {
				Method method = this.getClass().getMethod(action, HttpServletRequest.class, HttpServletResponse.class);
				method.invoke(this, request, response);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				throw new ServletException("Servlet action " + "does not exist or can not be accessed.", e);
			}
		}
		return action;
	}

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

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Session entityManager = (Session) request.getSession(true).getAttribute("entity_manager");
		repository = createRepository(entityManager);
		templatePage = getRespository().getEntityClass().getSimpleName().toLowerCase() + ".jsp";
		super.service(request, response);
		if (!response.isCommitted()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("WEB-INF/" + templatePage);
			dispatcher.forward(request, response);
		}
	}

	protected R getRespository() {
		return repository;
	}

}
