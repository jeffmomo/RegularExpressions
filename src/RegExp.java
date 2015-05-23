public class RegExp
{
	private Executor _executor;




	public RegExp(String text)
	{
		new Parser(text).parse();
		_executor = new Executor(text);
	}

	public boolean execute(String match)
	{
		return _executor.execute(match);
	}






}
