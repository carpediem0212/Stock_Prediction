package kr.ac.jbnu.sql.stock.persistance;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import com.mysql.jdbc.PreparedStatement.ParseInfo;

import kr.ac.jbnu.sql.stock.model.News;
import kr.ac.jbnu.sql.stock.model.StockInfo;

public class DBHandler {
	private DBConnect db = DBConnect.getDBInstance();

	public DBHandler() {

	}

	/*
	 * This Method insert infomation of a news in database
	 */
	public void insertNewsData(News news) {
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			sql = "INSERT INTO `stock_estimation`.`news` (`code`, `title`, `contents`, `url`, `date`) VALUES (?, ?, ?, ?, ?);";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, news.getStockCode());
			pstmt.setString(2, news.getTitle());
			pstmt.setString(3, news.getContents());
			pstmt.setString(4, news.getURL());
			pstmt.setString(5, news.getDate());

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}

	/*
	 * This Method checks whether the data is existed. If data is exist, return
	 * newsID. Else Return 0;
	 */
	public int hasNewsData(String code, String title) {
		db.connect();
		int newsID = 0;
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT * FROM stock_estimation.news where code=? and title =?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, title);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				newsID = rs.getInt("id");
			}

			rs.close();

			return newsID;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return newsID;
	}

	/*
	 * This Method gets a newsData in database via id.
	 */
	public News selectNewsDataForID(int newsID, String code) {
		db.connect();
		News news = null;
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT * FROM stock_estimation.news where id=? and code=?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setInt(1, newsID);
			pstmt.setString(2, code);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				news = new News(rs.getString("code"), rs.getString("title"), rs.getString("contents"),
						rs.getString("url"), rs.getString("date"));
			}

			rs.close();

			return news;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return news;
	}

	/*
	 * This Method inserts bag of words about a news.
	 */
	public void insertBagOfWord(HashMap<String, Integer> contents, int newsID, String stockCode) {
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			for (String key : contents.keySet()) {
				if (contents.get(key) > 2) {
					sql = "INSERT INTO `stock_estimation`.`contents` (`news_id`, `stock_code`, `word`, `frequency`) VALUES (?, ?, ?, ?);";
					pstmt = db.getConnection().prepareStatement(sql);
					pstmt.setInt(1, newsID);
					pstmt.setString(2, stockCode);
					pstmt.setString(3, key);
					pstmt.setInt(4, contents.get(key));
					pstmt.execute();
				} else {
					continue;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}
	
	/*
	 * This Method inserts Stock infomation 
	 */
	public void insertStockInfo(StockInfo stockInfo) {
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			sql = "INSERT INTO `stock_estimation`.`stock_info` "
					+ "(`code`, `date`, `start_value`, `high_value`, `low_value`, `close_value`, `compare`, `fluctuation`, `trading_volume`) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
			
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, stockInfo.getCode());
			pstmt.setString(2, stockInfo.getDate());
			pstmt.setInt(3, stockInfo.getStartValue());
			pstmt.setInt(4, stockInfo.getHighValue());
			pstmt.setInt(5, stockInfo.getcloseValue());
			pstmt.setInt(6, stockInfo.getcloseValue());
			pstmt.setInt(7, stockInfo.getComparedToYesterday());
			pstmt.setDouble(8, stockInfo.getFluctuation());
			pstmt.setInt(9, stockInfo.getTradingVolume());
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}
	
	/*
	 * This Method checks whether the data is existed. If data is exist, return
	 * newsID. Else Return 0;
	 */
	public boolean hasStockInfo(String code, String date) {
		db.connect();
		boolean hasData = false;

		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code = ? and date = date_format(?,'%y-%m-%d');";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				hasData = true;
			}

			rs.close();

			return hasData;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return hasData;
	}
	
	/*
	 * This Method count offage
	 */
	public double getOffageAtOpen(String code, String date) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		
		int offage = 0;
		int openValue = 0;
		int closeValue = 0;

		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date = date_format(?,'%y-%m-%d');";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				openValue = rs.getInt("start_value");
				closeValue = rs.getInt("close_value");
				offage = closeValue - openValue;
			}

			rs.close();
			
			return ((double) offage / (double) openValue) * 100;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return 0.0;
	}

	/*
	 * This Method count offage
	 */
	public double getOffageBeforeOpen(String code, String date) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		int offage = 0;
		int openValue = 0;
		int closeValue = 0;
		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date <= date_format(?,'%y-%m-%d') order by date desc;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				openValue = rs.getInt("start_value");
			}

			if (rs.next()) {
				closeValue = rs.getInt("close_value");
			} else {
				closeValue = openValue;
			}

			offage = openValue - closeValue;

			rs.close();

			return ((double) offage / (double) closeValue) * 100;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return 0.0;
	}
	
	/*
	 * This Method count offage
	 */
	public double getOffageAfterClose(String code, String date) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		
		int offage = 0;
		int openValue = 0;
		int closeValue = 0;
		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date >= date_format(?,'%y-%m-%d') order by date asc;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				closeValue = rs.getInt("close_value");
			}

			if (rs.next()) {
				openValue = rs.getInt("start_value");
			} else {
				openValue = closeValue;
			}

			offage = openValue - closeValue;

			rs.close();
			
			return ((double) offage / (double) closeValue) * 100;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return 0.0;
	}
	
	/*
	 * This Method count offage
	 */
	public double getOffageCloseDay(String code, String date) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		int offage = 0;
		int openValue = 0;
		int closeValue = 0;
		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date >= date_format(?,'%y-%m-%d') order by date asc;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				openValue = rs.getInt("start_value");
			}

			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date <= date_format(?,'%y-%m-%d') order by date desc;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				closeValue = rs.getInt("end_value");
			}
			offage = openValue - closeValue;

			rs.close();
			return ((double) offage / (double) closeValue) * 100;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return 0.0;
	}
	
	/*
	 * 
	 */
	public void updateClassfication(String code, int id, String classfication){
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			sql = "UPDATE `stock_estimation`.`news` SET `classification`=? WHERE `code`=? and `id`=?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, classfication);
			pstmt.setString(2, code);
			pstmt.setInt(3, id);

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}
	
	/*
	 * 
	 */
	public HashMap<String, Integer> getWordsOfNews(int _newsId, String code) {
		HashMap<String, Integer> wordInfoOfNews = new HashMap<String, Integer>();
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT * FROM stock_estimation.contents where news_id = ? and stock_code = ?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setInt(1, _newsId);
			pstmt.setString(2, code);
			
			rs = pstmt.executeQuery();
			while (rs.next()) {
				wordInfoOfNews.put(rs.getString("word"), rs.getInt("frequency"));
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return wordInfoOfNews;
	}
	
	/*
	 * 
	 */
	public String getNewsClassification(String code, int newsId) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		
		String classification = null;
		try {
			sql = "SELECT classification FROM stock_estimation.news where code =? and id = ?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setInt(2, newsId);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				classification = rs.getString("classification");
			}

			rs.close();
			
			return classification;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return null;
	}
	
	/*
	 * 
	 */
	public int[] getCloseDuringNDay(String code, String targetDate) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;

		int n = 10;
		
		int endValueOfDays[] = new int[10];

		try {
			sql = "SELECT * FROM stock_estimation.stock_info where code=? and date <= date_format(?,'%y-%m-%d');";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, code);
			pstmt.setString(2, targetDate);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				if (n <= 0) {
					break;
				}

				endValueOfDays[10 - n] = rs.getInt("compare");

				n--;
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return endValueOfDays;
	}
	
	public void insertNewsData1(int id, String title, HashMap<String, Integer> contents, String date,
			String _classification) {
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			sql = "INSERT INTO `news` (`id`, `title`, `date`, `classification`) VALUES (?, ?, ?, ?)";
			pstmt = db.getConnection().prepareStatement(sql);

			pstmt.setInt(1, id);
			pstmt.setString(2, title);
			pstmt.setString(3, date);
			pstmt.setString(4, _classification);

			pstmt.execute();

			for (String key : contents.keySet()) {
				if (contents.get(key) > 2) {
					sql = "INSERT INTO `contents` (`newsid`, `word`, `frequency`) VALUES (?, ?, ?);";
					pstmt = db.getConnection().prepareStatement(sql);

					pstmt.setInt(1, id);
					pstmt.setString(2, key);
					pstmt.setInt(3, contents.get(key));
					pstmt.execute();
				} else {
					continue;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}

	public void insertStockInfoByDate(String code, String date, String start, String high, String low, String end,
			String fluctuations, String deal) {
		System.out.println("dd");
		db.connect();
		PreparedStatement pstmt;
		String sql = null;

		try {
			sql = "INSERT INTO `stock_info_by_date` (`code`, `date`, `start`, `high`, `low`, `end`, `fluctuation`, `deal`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			pstmt = db.getConnection().prepareStatement(sql);

			pstmt.setString(1, code);
			pstmt.setString(2, date);
			pstmt.setString(3, start);
			pstmt.setString(4, high);
			pstmt.setString(5, low);
			pstmt.setString(6, end);
			pstmt.setString(7, fluctuations);
			pstmt.setString(8, deal);

			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}
	}
	
	public String[] getCloseDuringNDay1(String _targetDate) {
		int n = 10;
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;

		String endValueOfDays[] = new String[10];
		for (int i = 0; i < 10; i++) {
			endValueOfDays[i] = new String();
		}

		try {
			sql = "SELECT end FROM stock_info_by_date where date <= ?";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, _targetDate);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				if (n <= 0) {
					break;
				}

				endValueOfDays[10 - n] = rs.getString("end");
				// System.out.println(rs.getString("end"));

				n--;
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return endValueOfDays;
	}

	public Vector getWordsOfWholeWords() {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		Vector array = new Vector();

		try {
			sql = "SELECT word FROM contents group by word;";
			pstmt = db.getConnection().prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				array.add(rs.getString("word"));
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return array;
	}

	public HashMap<String, Integer> getWordsOfDoc(int _newsNum) {
		HashMap<String, Integer> wordInfoOfNews = new HashMap<String, Integer>();
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT * FROM contents where newsid = ?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setInt(1, _newsNum);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				wordInfoOfNews.put(rs.getString("word"), rs.getInt("frequency"));
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return wordInfoOfNews;
	}

	public int getNewsNum() {
		int newsNum = 0;
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT count(id) FROM news;";
			pstmt = db.getConnection().prepareStatement(sql);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				newsNum = rs.getInt("count(id)");
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return newsNum;
	}

	public Double getFluctuation(String date) {
		String fluctuation = null;
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		try {
			sql = "SELECT fluctuation FROM stock_info_by_date where date >= ? order by date asc;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, date);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				fluctuation = rs.getString("fluctuation");
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return Double.parseDouble(fluctuation.replace("%", ""));
	}

	public boolean isStockInfo(String date) {
		db.connect();
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = null;
		boolean existStockInfo = false;
		try {
			sql = "SELECT * FROM stock_estimation.stock_info_by_date where date = ?;";
			pstmt = db.getConnection().prepareStatement(sql);
			pstmt.setString(1, date);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				existStockInfo = true;
			}

			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.disconnect();
		}

		return existStockInfo;
	}

	
}