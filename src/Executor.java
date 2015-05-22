/**
 * Created by Jeff on 9/05/2015.
 */
public class Executor
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
	private int _position;


	private String _regex;
	private int _len;

	private String _text;

	private int mark;
	private int point;

        private int startState = 1;
	private int state = 0;

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
        
        private void setState( int state, char c, int n1, int n2)
        {
            chrs[state] = c;
            next1[state] = n1;
            next2[state] = n2;
        }

	// Checks the rewriting of expr
	private int expr() throws Exception
	{
            // Check position is not out of bounds
            if(_position >= _len)
                return 0;

            // initial state
            int r = altn();
            // Check that altn correctly parsed 
            if( r > 0)
                throw new Exception("Syntax Error: did not find expected symbol after '" + _text.substring(0, _position) + "'");

            // If still input then continue matching
            if(_position >= _len)
                return r;

            // Lookahead 1, and checks for disjunction
            if(checkNext() == '|')
            {
                /*if(next1[r-1] == next2[r-1])
                    next2[r-1] = state;
                next1[r-1] = state;*/
                
                // record first branch location
                int t1 = r;
                
                // set initial state and move to new empty state to build branching machine
                r = state;
                state++;
                
                _position++;
                // second branch location
                int t2 = expr();
                
                setState(state, (char)0, t1, t2);
                return r;
            }
            return r;
	}

	// Rewrites altn
	private int altn() throws Exception
	{
		if(_position >= _len)
			return 0;

		// Check if clause parsed correctly (i.e. if it consumed a char, so that it doesnt go into left recursion)
		int r = clause();
                if(r > 0)
                    return r;

		if(_position >= _len)
			return r;

		// Does another altn if still input left
		altn();

		return r;
	}

	// Rewrites clause
	private int clause() throws Exception
	{
		if(_position >= _len)
			return 0;

		// Check for correct parse of lit
                int r = lit();
		if( r > 0)
                    return r;

		if(_position >= _len)
                    return r;

		// If the * or ? decorator symbols exist, then just skip over it
		if (checkNext() == '*')
                {                    
                    _position++;
                    setState(state, (char)0, state+1, r);
                    r = state;
                    state++;
                    
                } else if (checkNext() == '?')
                {
                    // TODO not sure if works
                    _position++;
                    int t2 = state;
                    state++;
                    setState(state, (char)0, r, t2);
                }
		return r;
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
				throw new Exception("Matching ) not found");
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
                            throw new Exception("Matching ] not found");
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
		if("|*?\\()[].".indexOf(checkNext()) != -1)
		{
			_position++;
			return 0;
		}
		else
			throw new Exception("Invalid escaped literal: " + "\\" + checkNext());
	}

	// Returns the next character. Handles bounds checking too
	private char checkNext() throws Exception
	{
		if(_position >= _len)
			throw new Exception("Unexpected end of input");

		return _text.charAt(_position);
	}
}