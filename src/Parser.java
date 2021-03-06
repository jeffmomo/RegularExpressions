/**
 * Parses the regex
 */
public class Parser
{
    /*
    Grammar
    E -> A|E | A
    A -> CA | C
    C -> L* | L? | L
    L -> \D | D | ( E ) | . | [S]
    S -> DS | D | ] | ]S
    D -> All Literal Symbols 
    */

	// Position of the current char being read, the regex to parse, and the length of regex
	private int _position;
	private String _text;
	private int _len;

	public Parser(String text)
	{
		// Sets the text and text length
		_text = text;
		_len = text.length();
	}

	public boolean parse()
	{
		try
		{
			// Parse from initial rewrite of expr
			expr();

			// Prints out leftover string if it didnt completely parse


			// Gives warning if didnt completely parse
			if(_len != _position)
			{
				System.err.println("Regex Syntax Error: String did not completely parse");
				System.err.println(_text.substring(_position));
				return false;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}


		return true;
	}

	// Checks the rewriting of expr
	private int expr() throws Exception
	{
		// Check position is not out of bounds
		if(_position >= _len)
			return 0;

		// Check that altn correctly parsed
		if(altn() > 0)
			throw new Exception("Regex Syntax Error: did not find expected symbol after '" + _text.substring(0, _position) + "'");

		// If still input then continue matching
		if(_position >= _len)
			return 0;

		// Lookahead 1, and checks for disjunction
		if(checkNext() == '|')
		{
			_position++;
			return expr();
		}
		return 0;
	}

	// Rewrites altn
	private int altn() throws Exception
	{
		if(_position >= _len)
			return 0;

		// Check if clause parsed correctly (i.e. if it consumed a char, so that it doesnt go into left recursion)
		if(clause() > 0)
			return 1;

		if(_position >= _len)
			return 0;

		// Does another altn if still input left
		altn();

		return 0;
	}

	// Rewrites clause
	private int clause() throws Exception
	{
		if(_position >= _len)
			return 0;

		// Check for correct parse of lit
		if(lit() > 0)
			return 1;

		if(_position >= _len)
			return 0;

		// If the * or ? decorator symbols exist, then just skip over it
		if (checkNext() == '*' || checkNext() == '?')
			_position++;

		return 0;
	}

	// Rewrites literal
	private int lit() throws Exception
	{
		if(_position >= _len)
			return 0;

		// Does escaping if the escape symbol is detected
		if(checkNext() == '\\')
		{
			_position++;
			// Uses a chr parser which only returns 0 for escapable symbols
			return chrEsc();
		}
		// Parses brackets
		else if(checkNext() == '(')
		{
			_position++;
			expr();
			if(checkNext() == ')')
				_position++;
			else
				throw new Exception("Regex Syntax Error: Matching ) not found");
		}
		// Parses the wildcard symbol
		else if(checkNext() == '.')
		{
			_position++;
		}
		// Parses brackets for alternations
		else if(checkNext() == '[')
		{
			_position++;
                        spec();
			if(checkNext() == ']')
                            _position++;
			else
                            throw new Exception("Regex Syntax Error: Matching ] not found");
		}
		else
		{
			return chr();
		}

		return 0;
	}
        
        // Checks for special characters inside a list
	private int spec() throws Exception    
        {
            if(_position >= _len)
                return 0;
            
            // move position forward as it does not matter what it is looking at
            _position++;
            
            // if from here we see a ], it indicates the end of the list
            if(checkNext() == ']')
                return 0;
            
            // otherwise just keep going until you reach ] as all literals including special symbols are parsed
            spec();
            return 1;
        }

	// Checks if next char is of the vocabulary
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
        

	// Checks if next char is not of vocabulary
	private int chrEsc() throws Exception
	{
		checkNext();
		//if("|*?\\()[].".indexOf(checkNext()) != -1)
		{
			_position++;
			return 0;
		}
		/*else
			throw new Exception("Invalid escaped literal: " + "\\" + checkNext());*/
	}

	// Returns the next character. Handles bounds checking too
	private char checkNext() throws Exception
	{
		if(_position >= _len)
			throw new Exception("Regex Syntax Error: Unexpected end of input");

		return _text.charAt(_position);
	}
}
