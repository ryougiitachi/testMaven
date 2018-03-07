package per.itachi.test.poi.excel.eventmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import per.itachi.test.poi.excel.entity.TestExcelEntity;

/**
 * 继承DefaultHandler/ContentHandler方式的模式，以SAX为准，原始的事件模式，以每一个标签作为一个事件，需要知道xlsx中对于不同标签的意思；<br/>
 * 实际执行的是ContentHandler接口中的方法，一般只用到了start/character/end这几个方法。 <br/>
 * 参考文章：http://blog.csdn.net/lipinganq/article/details/78491765 <br/>
 * 参考文章：http://blog.csdn.net/lipinganq/article/details/78509351 <br/>
 * 参考文章：http://blog.csdn.net/lipinganq/article/details/78775195 <br/>
 * */
public class TestXssfDefaultHandler extends DefaultHandler {
	
	private static final int CELL_TYPE_STRING = 1;
	private static final int CELL_TYPE_NUMERIC = 2;
	private static final int CELL_TYPE_DATE = 3;
	
	private SharedStringsTable sst;
	
	private List<TestExcelEntity> entities;
	
	private Map<String, TestExcelEntity> deduplications;
	
	private TestExcelEntity currRowRecord;
	
	private int currRowNum;
	
	private int currCellType;
	
	private String currCellRef;
	
	private boolean isReadingRows;
	private boolean isInRow;
	private boolean isInCell;
	private boolean isInValue;
	
	public TestXssfDefaultHandler(SharedStringsTable sst, List<TestExcelEntity> entities) {
		this.sst = sst;
		this.entities = entities;
		this.deduplications = new HashMap<String, TestExcelEntity>();
	}

	/**
	 * 只对数据区进行了判断，还有样式等区域并没有管。
	 * */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("sheetData")) {//表格区
			this.isReadingRows = true;
		}
		else if (qName.equals("row")) {//行
			this.currRowNum = Integer.parseInt(attributes.getValue("r"));
			if (this.currRowNum >= 2) {
			}
			this.isInRow = true;
		}
		else if (qName.equals("c")) {//单元格
			String strCellType = attributes.getValue("t");
			if (strCellType != null && strCellType.equals("s")) {
				this.currCellType = CELL_TYPE_STRING;
			} 
			else {
				this.currCellType = -1;
			}
			this.currCellRef = attributes.getValue("r");
			this.isInCell = true;
		}
		else if (qName.equals("v")) {//单元格的值
			this.isInValue = true;
		}
		else {
		}
	}

	/**
	 * 加了去重的逻辑，该例子本身没有任何意义，可以以后用来修改复用。
	 * */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("sheetData")) {
			this.deduplications.clear();
			this.isReadingRows = false;
		}
		else if (qName.equals("row")) {
			if (this.currRowNum >= 2) {
				Map<String, TestExcelEntity> deduplications = this.deduplications;
				TestExcelEntity currRowRecord = this.currRowRecord;
				TestExcelEntity prevRowRecord = deduplications.get("");
				if (currRowRecord != null) {
					if (prevRowRecord == null) {
						this.entities.add(currRowRecord);
					} 
				}
				this.currRowRecord = null;
			}
			this.isInRow = false;
		}
		else if (qName.equals("c")) {
			this.isInCell = false;
		}
		else if (qName.equals("v")) {
			this.isInValue = false;
		}
		else {
		}
	
	}
	
	/**
	 * 目前在这个方法里面校验处理数据。
	 * */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (this.isReadingRows && this.isInRow && this.isInCell && this.isInValue && this.currRowNum >= 2) {
			if (this.currCellRef != null && this.currCellRef.length() > 0) {
			}
			switch (this.currCellType) {
			case CELL_TYPE_STRING:
				sst.getEntryAt(getIntFromChars(ch, start, length));
				break;
			case CELL_TYPE_NUMERIC:
				break;
			case CELL_TYPE_DATE:
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 为了给SharedStringsTable用，因为想用字符串需要按键值获取，直接去出来的键值是字符串需要转换为数字。
	 * */
	private int getIntFromChars(char[] ch, int start, int length) {
		int result = 0;
		for (int i = start; i < start + length; ++i) {
			if (ch[i] >= '0' && ch[i] <= '9') {
				result *= 10;
				result += ch[i] - '0';
			}
			else {
				break;
			}
		}
		return result;
	}
}
