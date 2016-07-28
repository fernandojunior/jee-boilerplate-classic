package foo.bar.repositories;

import org.hibernate.Session;

import core.BaseRepository;
import foo.bar.entities.Event;

/**
 * Repository for events.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
public class EventRepository extends BaseRepository<Event> {

	public EventRepository(Session session) {
		super(session);
	}

}
