package per.itachi.test.gallery.entity;

import java.util.List;

public class NineSixxxNetPage {
	
	private String currUrlLink;
	
	private String tmpFilePath;
	
	private List<String> imageCompleteUrlList;

	public String getCurrUrlLink() {
		return currUrlLink;
	}

	public void setCurrUrlLink(String currUrlLink) {
		this.currUrlLink = currUrlLink;
	}

	public String getTmpFilePath() {
		return tmpFilePath;
	}

	public void setTmpFilePath(String tmpFilePath) {
		this.tmpFilePath = tmpFilePath;
	}

	public List<String> getImageCompleteUrlList() {
		return imageCompleteUrlList;
	}

	public void setImageCompleteUrlList(List<String> imageCompleteUrlList) {
		this.imageCompleteUrlList = imageCompleteUrlList;
	}
}
