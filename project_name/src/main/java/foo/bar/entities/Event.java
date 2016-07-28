package foo.bar.entities;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import core.BaseEntity;

/**
 * The persistent class for the event database table.
 * 
 */
@Entity
public class Event extends BaseEntity {

	private String title;

	private Date date;

	public Event() {
	}

	public Event(String title, Date date) {
		this.title = title;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}