/**
 * Created by Jeff on 9/05/2015.
 */
public class Parser
{
	private int _position;
	private String _text;
	private int _len;

	public Parser(String text)
	{
		_text = text;
		_len = text.length();
	}

	public void parse()
	{
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