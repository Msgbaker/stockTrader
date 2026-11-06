import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class stockTrader{
	public static void main(String[] arg) throws FileNotFoundException{
		Scanner in = new Scanner(new File("sp500_1990_2025.txt"));
		String symbol=in.next();
		int n = in.nextInt();
		int[] years=new int[n];
		int[] months=new int[n];
		double[] prices=new double[n];
		loadData(in, years, months, prices);
		//printData(symbol, years, months, prices);
		//printExtermas(symbol, prices, months, years);
		//printBigDrop(symbol, prices, months, years);
		double result = simulate(years, months, prices);
		System.out.printf("Final value: $%,.2f%n", result);
	}
	
	public static final double START_CASH = 10000.0;
	public static final double FEE = 10.0;

	public static double simulate(int[] years, int[] months, double[] prices) {
		double cash = START_CASH;
		int shares = 0;
		double boughtAt = 0.0;

		for (int i = 0; i < prices.length; i++) {
			double[] history = Arrays.copyOf(prices, i + 1);
			String action = decide(history, shares > 0, boughtAt);

        if (action.equals("BUY") && shares == 0) {
            int canBuy = (int) ((cash - FEE) / prices[i]);
            if (canBuy > 0) {
                shares = canBuy;
                cash = cash - shares * prices[i] - FEE;
                boughtAt = prices[i];
                System.out.printf("%s %d: BUY %d shares at $%.2f%n",
						monthName(months[i]), years[i], shares, prices[i]);
				}
			} else if (action.equals("SELL") && shares > 0) {
				cash = cash + shares * prices[i] - FEE;
				System.out.printf("%s %d: SELL %d shares at $%.2f%n",
						monthName(months[i]), years[i], shares, prices[i]);
				shares = 0;
				boughtAt = 0.0;
			}
		}
		return cash + shares * prices[prices.length - 1];
	}
	
	public static String decide(double[] history, boolean holding, double boughtAt){
		//return buyAndHold(history, holding, boughtAt); 
		if(history.length<7)return dipBuyer(history, holding, boughtAt);
		//return movingAverage(history, holding, boughtAt);
		return randomBsGo(history, holding, boughtAt);
	}
	
	public static String randomBsGo(double[] history, boolean holding, double boughtAt){
		double avg=0;
		int n=history.length-1;
		double percentGain=0;
		int avgStart=10;
		if(boughtAt>0)percentGain=(history[n]-boughtAt)/boughtAt*100;
		if(n<=avgStart-1) return "HOLD";
		if(percentGain<=-8)return "HOLD";
		for(int i=n-avgStart;i<n;i++)avg+=history[i];
		avg=avg/avgStart;
		if(history[n]>avg*1.01)return "BUY";
		if(history[n]<avg && percentGain>=10)return "SELL";
		return "HOLD";
	}
	
	public static String movingAverage(double[] history, boolean holding, double boughtAt){
		double avg=0;
		int n=history.length-1;
		if(n<=10) return "HOLD";
		for(int i=n-11;i<n;i++)avg+=history[i];
		avg=avg/12;
		if(history[n]<avg)return "BUY";
		if(history[n]>avg)return "SELL";
		return "HOLD";
	}
	
	public static String buyAndHold(double[] history, boolean holding, double boughtAt){
		return "BUY";
	}
	
	public static String dipBuyer(double[] history, boolean holding, double boughtAt){
		double percentGain=0;
		int n=history.length-1;
		if(history.length<=2) return "HOLD";
		if(boughtAt>0)percentGain=(history[n]-boughtAt)/boughtAt*100;
		if(percentGain>=15)return "SELL";
		if(history[n-2]>history[n-1] && history[n-1]>history[n])return "BUY";
		return "HOLD";
	}
	
	public static void printBigDrop(String symbol, double[] prices, int[] months, int[] years){
		int index=indexOfBiggestDrop(prices);
		double dropMon=prices[index+1];
		double lastMon=prices[index];
		double drop=((dropMon-lastMon)/lastMon)*-100;
		System.out.printf("%s biggest drop: %.2f%% between %s and %s %d %n"
						 ,symbol,drop,monthName(index),monthName(index+1),years[index]);
	}
	
	public static void printExtermas(String symbol, double[]prices, int[]months, int[] years){
		int indexHigh=indexOfHighest(prices);
		int indexLow=indexOfLowest(prices);
		System.out.printf("%s highest: %.2f in %s %d %n"
						 ,symbol,prices[indexHigh],monthName(indexHigh),years[indexHigh]);
		System.out.printf("%s lowest: %.2f in %s %d %n"
						 ,symbol,prices[indexLow],monthName(indexLow),years[indexLow]);
	}
	
	public static int indexOfHighest(double[]prices){
		double highest=prices[0] ;
		int index=0;
		for(int i=0;i<prices.length;i++){
			if(prices[i]>highest){highest=prices[i]; index=i;}
		}
		return index;
	}
	
	public static int indexOfLowest(double[]prices){
		double lowest=prices[0];
		int index=0;
		for(int i=0;i<prices.length;i++){
			if(prices[i]<lowest){lowest=prices[i];index=i;}
		}
		return index;
	}
	
	public static int indexOfBiggestDrop(double[] prices){
		double percentDrop=((prices[1]-prices[0])/prices[0])*100;
		int index=1;
		for(int i=0;i<prices.length-1;i++){
			double drop=((prices[i+1]-prices[i])/prices[i])*100;
			if(drop<percentDrop){percentDrop=drop;index=i;}
		}
		return index;
	}

	public static String monthName(int n){
		String[] months={"Jan","Feb","Mar","Apr","May","Jun",
						"Jul","Aug","Sep","Oct","Nov","Dec"};
		return months[n-1%12];
	}
	
	public static void printData(String symbol,int[] years, int[] months, double[] prices){
		System.out.println(symbol);
		for(int i=0;i<months.length;i++){
			System.out.printf("%s %d: $%.2f %n",monthName(i),years[i],prices[i]);
		}
	}
	
	public static void loadData(Scanner in, int[] years, int[] months, double[] prices){
		for(int i=0;i<months.length;i++){
			years[i]=in.nextInt();
			months[i]=in.nextInt();
			prices[i]=in.nextDouble();
		}
	}
}
