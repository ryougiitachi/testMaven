package per.itachi.test.poi.excel.eventmodel;

import java.util.List;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.poi.excel.entity.TestExcelEntity;

/**
 * 事件模式中，相比继承DefaultHandler/ContentHandler的方式，继承SheetContentsHandler可以写更少的代码； <br/>
 * 前者xml标准的接口，需要自己解析xlsx文件每一个标签的内容，需要知道xlsx对于每一个标签的定义取值； <br/>
 * 而后者并不需要知道每一个标签的意义，poi已经在XSSFSheetXMLHandler里面封装了许多解析xlsx的代码，只需要实现SheetContentsHandler即可； <br/>
 * 但相比于前者，后者需要先初始化包括样式表在内的各种内置表对象，而且封装代码可能会执行一些不需要的步骤，不知效率问题像比前者如何； <br/>
 * 总之，前者实现复杂，知识点多，可定制化程度高，可以尽可能控制冗余代码，后者实现简单，代码量少，其他几点相对反之。 <br/>
 * 经测试这两种不同的方式，在什么都不做的情况下，DefaultHandler方式比SheetContentsHandler方式快一些； <br/>
 * 内存占用情况没测，猜测SheetContentsHandler方式占用更多一些。<br/>
 * 参考文章：http://blog.csdn.net/gaoweijiegwj/article/details/77977425 <br/>
 * */
public class TestXssfSheetContentsHandler implements SheetContentsHandler {
	
	private Logger logger = LoggerFactory.getLogger(TestXssfSheetContentsHandler.class);
	
	private List<TestExcelEntity> entities;
	
	private int currRowNum;
	
	public TestXssfSheetContentsHandler(List<TestExcelEntity> entities) {
		this.entities = entities;
	}

	/**
	 * 行数从0开始
	 * */
	public void startRow(int rowNum) {
//		logger.debug("Start row {}", rowNum);
		this.currRowNum = rowNum;
	}

	public void endRow(int rowNum) {
//		logger.debug("End row {}", rowNum);
	}

	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
//		logger.debug("There is a XSSFComment with format {} in {}: {}", formattedValue, cellReference, comment);
		if (this.currRowNum >= 1 && formattedValue != null && formattedValue.length() > 0) {
			TestExcelEntity entity = new TestExcelEntity();
			entities.add(entity);
		}
	}

	public void headerFooter(String text, boolean isHeader, String tagName) {
		logger.debug("headerFooter: {}, {}, {}", text, isHeader, tagName);
	}
}
