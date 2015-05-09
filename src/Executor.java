/**
 * Created by Jeff on 9/05/2015.
 */
public class Executor
{
	private int _position;


	private String _regex;
	private int _len;

	private String _text;

	private int mark;
	private int point;

	private int state = 1;


	private char[] chrs;
	private int[] next1;
	private int[] next2;




	public Executor(String regex)
	{
		_regex = regex;
		_len = regex.length();
	}

	public void execute(String text)
	{
		_text = text;

		try
		{
			expr();

			System.out.println(_text.substring(_position));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private int expr() throws Exception
	{
		if(_position >= _len)
			return 0;

		if(altn() > 0)
			throw new Exception("Syntax Error: did not find expected symbol after '" + _text.substring(0, _position) + "'");

		if(_position >= _len)
			return 0;

		if(checkNext() == '|')
		{
			_position++;
			expr();
		}
		return 0;
	}

	private int altn() throws Exception
	{
		if(_position >= _len)
			return 0;

		if(clause() > 0)
			return 1;

		if(_position >= _len)
			return 0;

		altn();
		return 0;
	}

	private int clause() throws Exception
	{
		if(_position >= _len)
			return 0;

		if(lit() > 0)
			return 1;

		if(_position >= _len)
			return 0;

		if (checkNext() == '*' || checkNext() == '?')
			_position++;

		return 0;
	}

	private int lit() throws Exception
	{
		if(_position >= _len)
			return 0;

		if(checkNext() == '\\')
		{
			_position++;
			return chrEsc();
		}
		else if(checkNext() == '(')
		{
			_position++;
			expr();
			if(checkNext() == ')')
				_position++;
			else
				throw new Exception("Matching ) not found");
		}
		else if(checkNext() == '.')
		{
			_position++;
		}
		else if(checkNext() == '[')
		{
			_position++;
			altn();
			if(checkNext() == ']')
				_position++;
			else
				throw new Exception("Matching ] not found");
		}
		else
		{
			return chr();
		}

		return 0;
	}

	private int chr() throws Exception
	{
		checkNext();
		if("|*?\\()[].".indexOf(checkNext()) != -1)
			return 1;
		else
		{
			_position++;
			return 0;
		}

	}

	private int chrEsc() throws Exception
	{
		checkNext();
		if("|*?\\()[].".indexOf(checkNext()) != -1)
		{
			_position++;
			return 0;
		}
		else
			throw new Exception("Invalid escaped literal: " + "\\" + checkNext());

	}

	private char checkNext() throws Exception
	{
		if(_position >= _len)
			throw new Exception("Unexpected end of input");

		return _text.charAt(_position);
	}
}
