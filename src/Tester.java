import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;

public class Tester
{

	public static void main(String[] args)
	{
		if(args.length == 1)
		{
			String regex = args[0];
			System.err.println(regex);
			RegExp r = new RegExp(regex);

			InputStreamReader ir = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ir);

			try
			{
				String temp = "";
				while ((temp = br.readLine()) != "")
				{
					if(r.execute(temp))
						System.out.println(temp);
				}
			}
			catch(Exception e)
			{

			}
		}
		else
		{
			System.err.println("Incorrect number of arguments");

			InputStreamReader ir = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(ir);

			try
			{
				String regex = br.readLine();
				RegExp r = new RegExp(regex);
				System.err.println(regex);


				String temp = "";
				while ((temp = br.readLine()) != "")
				{
					if(r.execute(temp))
						System.out.println(temp);
				}
			}
			catch(Exception e)
			{

			}
		}
	}
}
