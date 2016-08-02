package foo.bar.entities;

import javax.persistence.Entity;

import core.EntityModel;

/**
 * An entity model for messages.
 */
@Entity
public class Message extends EntityModel {

	private String content;

	public Message() {
	}

	public Message(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		return getContent();
	}

}