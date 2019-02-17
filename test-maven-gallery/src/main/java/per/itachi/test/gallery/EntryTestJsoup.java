package per.itachi.test.gallery;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EntryTestJsoup {

	public static void main(String[] args) {
		if (args.length <= 0) {
			return;
		}
		try {
			String strHtmlFilePath = args[0];
			Document document = Jsoup.parse(new File(strHtmlFilePath), "GBK");
			Elements elementsP = document.select("p");
			for (Element element : elementsP) {
				System.out.println(element.text());
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
