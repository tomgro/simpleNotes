package pl.oke;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
	private String id;
	private String content;
	private String lastMod;
	
	public Note(String id, String content, String lastMod) {
		super();
		this.id = id;
		this.content = content;
		this.lastMod = lastMod;
	}
	
	public Note(String content, String lastMod) {
		super();
		this.content = content;
		this.lastMod = lastMod;
	}

	public String getContent() {
		return content;
	}

	public String getLastMod() {
		return lastMod;
	}

	public String getId() {
		return id;
	}
}
