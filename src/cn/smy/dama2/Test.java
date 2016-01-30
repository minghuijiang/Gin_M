package cn.smy.dama2;

public class Test {

	public static double Tests(long init, double rate, int year) {
		System.out.print("Start : "+init+ "  rate: "+rate+"  year: "+year);
		double val = init;
		int days = year*365;
		double dailyRate =1+ rate/36500;
		System.out.print(" Val: "+val+ " days: "+days+"  DailyRate: "+dailyRate);
		for(int i=0;i<days;i++){
			val*=dailyRate;
			//System.out.print("\n"+val);
		}
		System.out.println(" Result: "+val);
		return val;
	}
	
	public static void main(String[] args){
		Tests(100000, 4.75, 1);
	}

}
