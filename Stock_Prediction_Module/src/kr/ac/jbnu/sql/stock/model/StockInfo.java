package kr.ac.jbnu.sql.stock.model;

public class StockInfo {
	private String code;
	private String date;
	private int startValue;
	private int highValue;
	private int lowValue;
	private int closeValue;
	private int comparedToYesterday;
	private double fluctuation;
	private int tradingVolume;

	public StockInfo(String _code, String _date, int _startValue,
			int _closeValue, int _highValue, int _lowValue,
			int _comparedToYesterday, double _fluctuation, int _tradingVolume) {
		this.code = _code;
		this.date = _date;
		this.startValue = _startValue;
		this.closeValue = _closeValue;
		this.highValue = _highValue;
		this.lowValue = _lowValue;
		this.comparedToYesterday = _comparedToYesterday;
		this.fluctuation = _fluctuation;
		this.tradingVolume = _tradingVolume;
	}

	public String getCode() {
		return code;
	}

	public String getDate() {
		return date;
	}

	public int getStartValue() {
		return startValue;
	}

	public int getcloseValue() {
		return closeValue;
	}

	public int getHighValue() {
		return highValue;
	}

	public int getLowValue() {
		return lowValue;
	}

	public int getComparedToYesterday() {
		return comparedToYesterday;
	}

	public double getFluctuation() {
		return fluctuation;
	}

	public int getTradingVolume() {
		return tradingVolume;
	}
	
	
}
