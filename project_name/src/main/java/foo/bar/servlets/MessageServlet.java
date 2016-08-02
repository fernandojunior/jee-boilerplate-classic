package foo.bar.servlets;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.RepositoryServlet;
import foo.bar.entities.Message;
import foo.bar.repositories.MessageRepository;

/**
 * Servlet implementation class Message
 */
@WebServlet("/message")
public class MessageServlet extends RepositoryServlet<MessageRepository>implements Serializable {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		get(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String action = request.getParameter("action");
			if (action != null)
				doAction(action, request, response);
			else
				post(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
			request.setAttribute("danger", e.getMessage());
			get(request, response);
		}
	}

	public void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("id") != null)
			detail(request, response);
		else
			all(request, response);
	}

	public void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));
		Message message = getRespository().find(id);
		request.setAttribute("message", message);
	}

	public void all(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Message> messages = getRespository().findAll();
		Collections.reverse(messages);
		request.setAttribute("messages", messages);
	}

	public void post(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getRespository().beginTransaction();
		String message = request.getParameter("message");
		Message hello = new Message(message);
		getRespository().save(hello);
		getRespository().commit();
		all(request, response);
		request.setAttribute("success", "Message was successfully created.");
	}

	public void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long id = Long.parseLong(request.getParameter("id"));
		Message message = getRespository().find(id);
		if (message == null) {
			request.setAttribute("warning", "Message does not exist.");
		} else {
			getRespository().beginTransaction();
			getRespository().remove(message);
			getRespository().commit();
			request.setAttribute("success", "Message was successfully deleted.");
		}
		all(request, response);
	}

}
