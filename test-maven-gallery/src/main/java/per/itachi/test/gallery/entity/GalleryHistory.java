package per.itachi.test.gallery.entity;

import java.util.Date;

public class GalleryHistory {
	
	private int id;
	
	private String galleryLink;
	
	private int status;
	
	private String creator;
	
	private Date cdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGalleryLink() {
		return galleryLink;
	}

	public void setGalleryLink(String galleryLink) {
		this.galleryLink = galleryLink;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCdate() {
		return cdate;
	}

	public void setCdate(Date cdate) {
		this.cdate = cdate;
	}
}
