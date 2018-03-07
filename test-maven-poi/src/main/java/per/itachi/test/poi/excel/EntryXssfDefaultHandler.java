package per.itachi.test.poi.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import per.itachi.test.poi.excel.entity.TestExcelEntity;
import per.itachi.test.poi.excel.eventmodel.TestXssfDefaultHandler;

public class EntryXssfDefaultHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryXssfDefaultHandler.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("Argument not found.");
			return;
		}
		String strExcelFilePath = args[0];
		logger.info("Opening Xssf excel {}.", strExcelFilePath);
		OPCPackage pkg = null;
		InputStream is = null; 
		long lParseStart, lParseEnd;
		List<TestExcelEntity> list = new ArrayList<TestExcelEntity>();
		try {
			pkg = OPCPackage.open(strExcelFilePath, PackageAccess.READ);
			XSSFReader xssfReader = new XSSFReader(pkg);
			SharedStringsTable sst = xssfReader.getSharedStringsTable();
			logger.info("Opened Xssf excel {}.", strExcelFilePath);
			DefaultHandler handler = new TestXssfDefaultHandler(sst, list);
			XMLReader parser = XMLReaderFactory.createXMLReader();
			parser.setContentHandler(handler);
			is = xssfReader.getSheet("rId1");
			InputSource source = new InputSource(is);
			lParseStart = System.currentTimeMillis();
			parser.parse(source);
			lParseEnd = System.currentTimeMillis();
			logger.info("DefaultHandler spent {} milliseconds parsing {}", lParseEnd- lParseStart, strExcelFilePath);//985ms
		} 
		catch (IOException | OpenXML4JException | SAXException e) {
			logger.error("Error occured when loading excel {}.", strExcelFilePath, e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} 
				catch (IOException e) {
					logger.error("Error occured when closing excel {}.", strExcelFilePath, e);
				}
			}
			if (pkg != null) {
				try {
					pkg.close();
					pkg = null;
				} 
				catch (IOException e) {
					logger.error("Error occured when closing excel {}.", strExcelFilePath, e);
				}
			}
		}

	}

}
