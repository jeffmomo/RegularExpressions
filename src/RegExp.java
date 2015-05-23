public class RegExp
{
	private Parser _parser;
	private Executor _compiler;



	public RegExp(String text)
	{
		new Parser(text).parse();
	}

	public RegExp(String text, String match)
	{
		new Parser(text).parse();
		new Executor(text).execute(match);
	}





}
