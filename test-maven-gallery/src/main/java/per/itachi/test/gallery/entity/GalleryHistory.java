package per.itachi.test.gallery.entity;

import java.util.Date;

public class GalleryHistory {
	
	public static final String COL_NAME_ID = "ID";
	
	public static final String COL_NAME_GALLERY_LINK = "GALLERY_LINK";
	
	public static final String COL_NAME_WEBSITE = "WEBSITE";
	
	public static final String COL_NAME_STATUS = "STATUS";
	
	public static final String COL_NAME_CREATOR = "CREATOR";
	
	public static final String COL_NAME_CDATE = "CDATE";
	
	public static final String COL_NAME_EDITOR = "EDITOR";
	
	public static final String COL_NAME_EDATE = "EDATE";
	
	private int id;
	
	private String galleryLink;
	
	private String website;
	
	private String title;
	
	private int status;
	
	private String creator;
	
	private Date cdate;
	
	private String editor;
	
	private Date edate;

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

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public Date getEdate() {
		return edate;
	}

	public void setEdate(Date edate) {
		this.edate = edate;
	}

	@Override
	public String toString() {
		return "GalleryHistory [id=" + id + ", galleryLink=" + galleryLink + ", website=" + website + ", title=" + title
				+ ", status=" + status + ", creator=" + creator + ", cdate=" + cdate + ", editor=" + editor + ", edate="
				+ edate + "]";
	}
}
