public class RegExp
{
	private Parser _parser;
	private Executor _compiler;




	public RegExp(String text)
	{
		new Parser(text).parse();
	}





}
