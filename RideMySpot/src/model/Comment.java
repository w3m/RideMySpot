package model;

public class Comment {
	
	private long ID;
	private long ID_Spot;
	private long ID_User;

	private String m_user;
	private String m_text;
	private double m_note;
	
	public Comment(long iD_Spot, long iD_User, String user, String text, double note){
		setID_Spot(iD_Spot);
		setID_User(iD_User);
		setUser(user);
		setText(text);
		setNote(note);
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public long getID_Spot() {
		return ID_Spot;
	}

	public void setID_Spot(long iD_Spot) {
		ID_Spot = iD_Spot;
	}

	public long getID_User() {
		return ID_User;
	}

	public void setID_User(long iD_User) {
		ID_User = iD_User;
	}

	public String getUser() {
		return m_user;
	}

	public void setUser(String user) {
		this.m_user = user;
	}

	public String getText() {
		return m_text;
	}

	public void setText(String text) {
		this.m_text = text;
	}

	public double getNote() {
		return m_note;
	}

	public void setNote(double note) {
		this.m_note = note;
	}
	
}
