package per.itachi.test.struts2.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.struts2.model.DefaultAjaxOutModel;

public class DefaultAjaxAction {
	
	private Logger logger = LoggerFactory.getLogger(DefaultAjaxAction.class);
	
	private DefaultAjaxOutModel defaultAjaxResult;
	
	public String execute() {
		logger.info("The address of current action {} is {}.", this.getClass().getSimpleName(), this);
		defaultAjaxResult = new DefaultAjaxOutModel();
		defaultAjaxResult.setName("output string");
		return "success";
	}

	public DefaultAjaxOutModel getDefaultAjaxResult() {
		return defaultAjaxResult;
	}

	public void setDefaultAjaxResult(DefaultAjaxOutModel defaultAjaxResult) {
		this.defaultAjaxResult = defaultAjaxResult;
	}
}
