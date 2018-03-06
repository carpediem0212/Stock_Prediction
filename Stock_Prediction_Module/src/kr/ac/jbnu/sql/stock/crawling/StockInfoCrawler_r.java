package kr.ac.jbnu.sql.stock.crawling;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.jbnu.sql.stock.persistance.DBHandler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class StockInfoCrawler_r
{
	private Connection conn = null;
	private DBHandler db = new DBHandler();

	public StockInfoCrawler_r()
	{
	}

	public void getStockInfoByDate(String companyCode)
	{
		boolean state = true;
		int pageNum = 1;

		while (state)
		{
			// String url =
			// "http://finance.daum.net/item/quote_yyyyzmmdd_sub.daum?page="+pageNum+"&code="+companyCode+"&modify=1";
			String url = "http://finance.daum.net/item/quote_yyyymmdd_sub.daum?page=" + pageNum + "&code=005380&modify=null";
			conn = Jsoup
					.connect(url)
					.header("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.60 Safari/537.1 CoolNovo/2.0.4.16");
			conn.timeout(5000);
			try
			{
				Document doc = conn.get();
				Elements title = doc.select("table#bbsList tbody tr");
				String content = null;
				ArticleExtractor ae = new ArticleExtractor();
				content = ae.getText(title.toString());

				String[] str = content.split(" ");

				SimpleDateFormat basicFormat = new SimpleDateFormat("yy.MM.dd");

				for (int i = 1; i <= 30; i++)
				{
					Date tempDate1 = basicFormat.parse(str[(8 * i) + 0]);
					Date tempDate2 = basicFormat.parse("05.12.29");
					int resultCompare = tempDate1.compareTo(tempDate2);
					String temp = null;

					if (resultCompare >= 0)
					{
						System.out.print(str[(8 * i) + 0] + " ");
						System.out.print(str[(8 * i) + 1] + " ");
						System.out.print(str[(8 * i) + 2] + " ");
						System.out.print(str[(8 * i) + 3] + " ");
						System.out.print(str[(8 * i) + 4] + " ");
						/*if (str[(8 * i) + 5].charAt(0) == "��")
						{
							temp = str[(8 * i) + 5].replaceAll("��", "-");
						} else if (str[(8 * i) + 5].charAt(0) == '��')
						{
							temp = str[(8 * i) + 5].replaceAll("��", "+");
						} else if (str[(8 * i) + 5].charAt(0) == '-')
						{
							temp = str[(8 * i) + 5].replaceAll("-", "");
						}*/


						System.out.println(i + " " + resultCompare);
						db.insertStockInfoByDate(companyCode, str[(8 * i) + 0], str[(8 * i) + 1], str[(8 * i) + 2], str[(8 * i) + 3],
								str[(8 * i) + 4], str[(8 * i) + 6], str[(8 * i) + 7]);
					} else
					{
						state = false;
					}
				}
			} catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}

			pageNum++;
		}

	}
}
