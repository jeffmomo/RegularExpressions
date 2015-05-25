//Jeff Mo         1196144
//Frankie Yuan    1196194

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Tester
{

	public static void main(String[] args)
	{
		// If the apporpriate amount of arguments supplied, then enter normal mode, taking the argument as the regular expressions and compiles it.
		// It then runs lines from stdin until an empty line is reached.
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
				//e.printStackTrace();
			}
		}
		else
		{
			System.err.println("Incorrect number of arguments. Entering Interactive Mode");

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
				//e.printStackTrace();
			}
		}
	}
}
