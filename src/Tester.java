/**
 * Created by Jeff on 9/05/2015.
 */
public class Tester
{

	public static void main(String[] args)
	{
		new RegExp("(o[d]?|c*g)|(\\.f*)").parse();
		new RegExp("(a|aa|(a[sdf])|z)|d").parse();
	}
}
