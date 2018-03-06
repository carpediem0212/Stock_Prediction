package kr.ac.jbnu.sql.stock.training;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.ac.jbnu.sql.stock.persistance.DBHandler;

public class RSICalculator {
	public RSICalculator() {
	}
	
	public double[] getCloseGap(String code){
		int up = 0;
		int down = 0;
		
		DBHandler db = new DBHandler();
		int a[] = db.getCloseDuringNDay(code, "2015-07-25");
		int b[] = new int[a.length];
		
		for(int i=0; i < 10; i++){
			if(a[i] > 0){
				up += a[i];
			}else if(a[i] < 0){
				down += a[i];
			}
		}
	
		double _RS = (up/9.0) / ((-down)/9.0);
		
		double _RSI = 100 - 100 * ( 1 / (1+_RS));
		
		System.out.println("��� :: "+up + "/"+ down + "/" + _RS + "/" + _RSI);
		
		if(_RSI < 0 || _RSI > 100){
			System.out.println("�� ����");
		}
		
		double weightValues[] = {1.0, 1.0, 1.0};
		
		if(_RSI >= 70 && _RSI<95){
			System.out.println("�϶� 1.48 ����ġ");
			weightValues[1] = 1.48;
		}else if(_RSI >= 95){
			System.out.println("�϶� 1.25 ����ġ");
			weightValues[1] = 1.25;
		}else if(_RSI <= 30 && _RSI>5){
			System.out.println("��� 1.48 ����ġ");
			weightValues[0] = 1.48;
		}else if(_RSI <= 5){
			System.out.println("��� 1.25����ġ");
			weightValues[0] = 1.25;
		}else{
			System.out.println("RSI ����ġ �ο� ����");
		}
		
		return weightValues;
		//String a = getDateDayOfTargetDay("2015-09-15");
	}
	
	private String getDateDayOfTargetDay(String date){
		String day = "" ;
	     
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
	    Date mDate =null;
	    
		try {
			mDate = dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	     
	    Calendar calendar = Calendar.getInstance() ;
	    calendar.setTime(mDate);
	    int numOfDay = calendar.get(Calendar.DAY_OF_WEEK) ;
	    	     
	    System.out.println(numOfDay);
	    switch(numOfDay){
	        case 1:
	            day = "��";
	            break ;
	        case 2:
	            day = "��";
	            break ;
	        case 3:
	            day = "ȭ";
	            break ;
	        case 4:
	            day = "��";
	            break ;
	        case 5:
	            day = "��";
	            break ;
	        case 6:
	            day = "��";
	            break ;
	        case 7:
	            day = "��";
	            break ;
	    }
	     
	    return day ;
	}
}
