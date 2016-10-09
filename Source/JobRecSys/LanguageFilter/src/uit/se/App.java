package uit.se;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class App {

	
	
	public static double count(String text)
	{
		text = text.toLowerCase();
		String unicode = "áàảãạâấầẫẩậăắằặẵẳóòỏõọôốồổỗộơớờởỡợíìĩỉịýỳỷỹêếềễểệéèẻẽẹđ";
		double cnt = 0;
		for(char c : text.toCharArray())
		{
			for(char u : unicode.toCharArray())
			{
				if(c == u)
				{
					cnt++;
					break;
				}					
			}	
		}		
		return cnt;
	}
	
	public static boolean isVietnamese(String sentence)
	{		
		double val = count(sentence)/ sentence.length();
		System.out.println(val);
		return count(sentence)/ sentence.length() > 0.08d;
	}
	
	public static void main(String[] args) {
		
		BufferedReader buf = null;
		
		try {
			buf = new BufferedReader(new InputStreamReader(new FileInputStream("Job.txt"), "UTF-8"));
			String data = "";
			PrintWriter english = new PrintWriter(new OutputStreamWriter(new FileOutputStream("EN.txt"), "UTF-8"));
			PrintWriter vietnamese = new PrintWriter(new OutputStreamWriter(new FileOutputStream("VI.txt"), "UTF-8"));
			PrintWriter english_id = new PrintWriter(new OutputStreamWriter(new FileOutputStream("EN_ID.txt"), "UTF-8"));
			PrintWriter vietnamese_id = new PrintWriter(new OutputStreamWriter(new FileOutputStream("VI_ID.txt"), "UTF-8"));
			
			while ((data = buf.readLine()) != null) {
				if(isVietnamese(data))
				{
					Scanner scan = new Scanner(data);
					scan.useDelimiter("\t");
					vietnamese_id.println(scan.next() + ",");
					vietnamese.println(data);
				}
				else					
				{
					Scanner scan = new Scanner(data);
					scan.useDelimiter("\t");
					english_id.println(scan.next() + ",");
					english.println(data);
				}
			}
			vietnamese.close();
			english.close();
			buf.close();
		} catch (UnsupportedEncodingException e) {
			buf = null;			
		} catch (FileNotFoundException e) {
			buf = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			
		}
		
	}

}
