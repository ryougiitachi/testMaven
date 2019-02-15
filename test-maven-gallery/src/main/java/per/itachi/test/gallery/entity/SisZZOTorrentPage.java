package per.itachi.test.gallery.entity;

public class SisZZOTorrentPage {
	
	private String TorrentFileName;
	
	private String referPageLink;
	
	private String referPageFilePath;
	
	private String threadDirPath;

	public String getTorrentFileName() {
		return TorrentFileName;
	}

	public void setTorrentFileName(String torrentFileName) {
		TorrentFileName = torrentFileName;
	}

	public String getReferPageLink() {
		return referPageLink;
	}

	public void setReferPageLink(String referPageLink) {
		this.referPageLink = referPageLink;
	}

	public String getReferPageFilePath() {
		return referPageFilePath;
	}

	public void setReferPageFilePath(String referPageFilePath) {
		this.referPageFilePath = referPageFilePath;
	}

	public String getThreadDirPath() {
		return threadDirPath;
	}

	public void setThreadDirPath(String threadDirPath) {
		this.threadDirPath = threadDirPath;
	} 
}
