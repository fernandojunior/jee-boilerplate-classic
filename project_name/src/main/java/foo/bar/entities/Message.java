package foo.bar.entities;

import javax.persistence.Entity;

import core.EntityModel;

/**
 * An entity model for messages.
 */
@Entity
public class Message extends EntityModel {

	private String message;

	public Message() {
	}

	public Message(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return getMessage();
	}

}