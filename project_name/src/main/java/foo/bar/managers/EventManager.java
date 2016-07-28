package foo.bar.managers;

import org.hibernate.Session;

import database.Manager;
import foo.bar.entities.Event;

/**
 * CRUD Repository to manage events.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
public class EventManager extends Manager<Event> {

	public EventManager(Session session) {
		super(session);
	}

}
