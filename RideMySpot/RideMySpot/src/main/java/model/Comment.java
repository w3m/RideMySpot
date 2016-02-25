package model;

public class Comment {
	
	private long ID;
	private long ID_Spot;
	private long ID_User;

	private String mUser;
	private String mText;
	private float mNote;
	
	public Comment(long iD, long iD_Spot, long iD_User,String user, String text, float note){
		setID(iD);
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
		return mUser;
	}

	public void setUser(String user) {
		this.mUser = user;
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
	}

	public float getNote() {
		return mNote;
	}

	public void setNote(float note) {
		this.mNote = note;
	}
	
}
