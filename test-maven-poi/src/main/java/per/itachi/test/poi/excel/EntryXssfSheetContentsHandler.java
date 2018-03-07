package per.itachi.test.poi.excel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import per.itachi.test.poi.excel.entity.TestExcelEntity;
import per.itachi.test.poi.excel.eventmodel.TestXssfSheetContentsHandler;

public class EntryXssfSheetContentsHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryXssfSheetContentsHandler.class);

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
		try {
			pkg = OPCPackage.open(strExcelFilePath, PackageAccess.READ);
			XSSFReader xssfReader = new XSSFReader(pkg);
			StylesTable styles = xssfReader.getStylesTable();
			CommentsTable comments = new CommentsTable();
			ReadOnlySharedStringsTable sst = new ReadOnlySharedStringsTable(pkg);
			DataFormatter formatter = new DataFormatter();
			SheetContentsHandler handler = new TestXssfSheetContentsHandler(new ArrayList<TestExcelEntity>());
			logger.info("Opened Xssf excel {}.", strExcelFilePath);
			ContentHandler contentHandler = new XSSFSheetXMLHandler(styles, comments, sst, handler, formatter, false);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(contentHandler);
			InputSource source = null;
			Iterator<InputStream> sheets = xssfReader.getSheetsData();
			if (sheets.hasNext()) {
				is = sheets.next();
				source = new InputSource(is);
				lParseStart = System.currentTimeMillis();
				xmlReader.parse(source);
				lParseEnd = System.currentTimeMillis();
				logger.info("SheetContentsHandler spent {} milliseconds parsing {}", lParseEnd- lParseStart, strExcelFilePath);//4811ms
			}
		} 
		catch (InvalidFormatException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (OpenXML4JException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (SAXException e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} 
				catch (IOException e) {
					logger.error("Error occured when closing Xssf excel {}.", strExcelFilePath, e);
				}
			}
			if (pkg != null) {
				try {
					pkg.close();
					pkg = null;
				} 
				catch (IOException e) {
					logger.error("Error occured when closing Xssf excel {}.", strExcelFilePath, e);
				}
			}
		}
	}

}
