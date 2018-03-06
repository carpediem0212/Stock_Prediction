package kr.ac.jbnu.sql.stock.model;

public class News {
	private String stockCode;
	private String title;
	private String contents;
	private String url;
	private String date;

	public News(String _code, String _title, String _contents, String _url, String _date) {
		this.stockCode = _code;
		this.title = _title;
		this.contents = _contents;
		this.url = _url;
		this.date = _date;
	}

	public void setStockCode(String _code) {
		this.stockCode = _code;
	}
	
	public void setTitle(String _title) {
		this.title = _title;
	}

	public void setContents(String _contents) {
		this.contents = _contents;
	}

	public void setURL(String _url) {
		this.url = _url;
	}

	public void setDate(String _date) {
		this.date = _date;
	}

	public String getStockCode() {
		return this.stockCode;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getContents() {
		return this.contents;
	}

	public String getURL() {
		return this.url;
	}

	public String getDate() {
		return this.date;
	}
}
