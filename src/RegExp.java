// Wrapper class for the regex
public class RegExp
{
	private Executor _executor;




	public RegExp(String text)
	{
		// Parses the regular expression and compiles it if successful in parsing
		boolean parsed = new Parser(text).parse();
		if(!parsed)
		{
			System.err.println("Error in parsing.");
		}
		else
			_executor = new Executor(text);
	}

	// Runs the regex against the string
	public boolean execute(String match)
	{
		return _executor.execute(match);
	}






}
