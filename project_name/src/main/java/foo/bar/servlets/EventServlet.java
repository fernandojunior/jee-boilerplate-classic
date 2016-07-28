package foo.bar.servlets;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import core.BaseRepository;
import foo.bar.entities.Event;
import foo.bar.repositories.EventRepository;

/**
 * Servlet implementation class Test
 */
@WebServlet("/event")
public class EventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Session session = null;

	/**
	 * {@inheritDoc}
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		session = (Session) request.getSession(true).getAttribute("hibernate_session");
		session.beginTransaction();

		BaseRepository<Event> eventRepository = new EventRepository(session);
		Event hello = new Event("Hello", new Date());
		Event world = new Event("World", new Date());
		eventRepository.save(hello);
		eventRepository.save(world);

		session.getTransaction().commit();

		request.setAttribute("data", eventRepository.getAll());
		RequestDispatcher dispatcher = request.getRequestDispatcher("events.jsp");
		dispatcher.forward(request, response);
	}

}
