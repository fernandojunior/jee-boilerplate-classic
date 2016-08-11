package foo.bar.repositories;

import java.util.List;

import org.hibernate.Session;

import core.GenericRepository;
import foo.bar.entities.Message;

/**
 * Repository for messages.
 * 
 * @author Fernando Felix do Nascimento Junior
 *
 */
public class MessageRepository extends GenericRepository<Message> {

	public MessageRepository(Session session) {
		super(session);
	}

	public List<Message> filterByContentAndId(String content, String id) {
		return this.createQueryBuilder().like("content", content).like("id", id).build().getResultList();
	}

}
