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
            // record initial state
            int r = state;
            
            // go to new state and build altn machine
            state++;
            int t1  = altn();

            // Lookahead 1, and checks for disjunction
            if(checkNext() == '|')
            {
                // if there is disjunction build second branch
                
                
                // first branch end location
                int d = state;
                
                // move to new empty state to build second branch
                state++;
                
                _position++;
                // build second branch and record second branch start location
                int t2 = expr();
                
 
                // set end location of first branch to same as second branch
                setState(d, (char)0, state, state);
                
                // set branching state branching locations
                setState(r, (char)0, t1, t2);
            } else
            {
                // join initial state to start state of altn machine if there is no branch
                setState(r, (char)0, t1, t1);
            }
            return r;
	}

	// Rewrites altn
	private int altn() throws Exception
	{

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

		// Check for correct parse of lit
                int r = lit();
		if( r > 0)
                    return r;

		// If the * or ? decorator symbols exist, then just skip over it
		if (checkNext() == '*')
                {                    
                    _position++;
                    // set state to point to new branching state
                    setState(state, (char)'*', state+1, state+1);
                    // go to next empty state and build branch
                    state++;
                    r = state;
                    
                    // branch state points to literal or to next state
                    setState(state, (char)0,r, state+1);
                    // move to point to next state
                    state++;
                    
                } else if (checkNext() == '?')
                {
                    
                    _position++;
                    // record finishing state of literal
                    int d = state;
                    // go to new empty state and build branching machine
                    state++;
                    setState(state, (char)0, r, state+1);
                    r = state;
                    
                    // make new empty state where branching machine can jump to
                    state++;
                    // set next state of the end state of the literal as the same state as the branching machine's other state
                    setState(d, (char)0, state, state);
                }
		return r;
	}

	// Rewrites literal
	private int lit() throws Exception
	{

            // Does escaping if the escape symbol is detected
            if(checkNext() == '\\')
            {
                    _position++;
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
			throw new Exception("Unexpected end of input");

		return _text.charAt(_position);
	}
}