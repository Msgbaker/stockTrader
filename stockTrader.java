import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

public class stockTrader{
	public static void main(String[] arg) throws FileNotFoundException{
		Scanner in = new Scanner(new File("tiny.txt"));
		String symbol=in.next();
		int n = in.nextInt();
		int[] years=new int[n];
		int[] months=new int[n];
		double[] prices=new double[n];
		loadData(in, years, months, prices);
		printData(symbol, years, months, prices);
		printExtermas(symbol, prices, months, years);
	}
	
	public static void printExtermas(String symbol, double[]prices, int[]months, int[] years){
		int indexHigh=indexOfHighest(prices);
		int indexLow=indexOfLowest(prices);
		System.out.printf("%s highest: %.2f in %s %d %n",symbol,prices[indexHigh],months(indexHigh),years[indexHigh]);
		System.out.printf("%s lowest: %.2f in %s %d %n",symbol,prices[indexLow],months(indexLow),years[indexLow]);
	}
	
	public static int indexOfHighest(double[]prices){
		double highest=0;
		int index=0;
		for(int i=0;i<prices.length-1;i++){
			if(prices[i+1]<prices[i]){highest=prices[i]; index=i;}
		}
		return index;
	}
	
	public static int indexOfLowest(double[]prices){
		double lowest=prices[0];
		int index=0;
		for(int i=0;i<prices.length-1;i++){
			if(prices[i+1]<lowest){lowest=prices[i+1];index=i;}
		}
		return index;
	}
	
	public static String months(int n){
		String[] months={"Jan","Feb","Mar","Apr","May","Jun",
						"Jul","Aug","Sep","Oct","Nov","Dec"};
		return months[n];
	}
	
	public static void printData(String symbol,int[] years, int[] months, double[] prices){
		System.out.println(symbol);
		for(int i=0;i<months.length;i++){
			System.out.printf("%s %d: $%.2f %n",months(i),years[i],prices[i]);
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
