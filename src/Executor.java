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


	//##########TODO: Add the wildcard symbol################


	private int _position;



	private int _compTextLen;
	private String _compText;
	private int _len;

	private String _text;

	private int mark;
	private int point;
	private int _currState = 0;
	private boolean _failure = false;
	private boolean _success = false;

	private boolean _delayedIncPoint = false;


	private int state = 1;

	private char[] chrs = new char[65535];
	private int[] next1 = new int[65535];
	private int[] next2 = new int[65535];

	private DequeEx _deq = new DequeEx();



	public Executor(String regex)
	{
		_text = regex;
		_len = regex.length();

		compile();

		chrs[0] = '\0';
		next1[0] = 0;
		next2[0] = 0;
	}

	private void compile()
	{
		try
		{
			expr();
                        //printFSM();

			//System.err.println(_text.substring(_position));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public boolean execute(String text)
	{
		mark = point = 0;
		_failure = _success = false;
		_currState = 0;
		_delayedIncPoint = false;
		_deq = new DequeEx();



		_compText = text;
		_compTextLen = text.length() - 1;

		_deq.pushFront(1);

		while(!_failure)
		{
			evalStates();

			evalPossible();
		}

		if(_success)
			System.err.println("Success!");
		else
			System.err.println("Failure to match");

		return _success;
	}

	private boolean isBranch(int onState)
	{
		//return chrs[onState] == '\0';
		return next1[onState] != next2[onState];
	}

	private void evalStates()
	{

		if(_delayedIncPoint)
		{
			_delayedIncPoint = false;
			incPoint();
		}

		if(_failure)
			return;

		while(_deq.isPoppable())
		{
			int look = _deq.getHead();

			if(look == 0)
			{
				//SUCCESS
				_failure = true;
				_success = true;
				//System.err.println("Success");
				return;
			}


			if (isBranch(look))
			{
				_deq.pushFront(next1[look]);
				_deq.pushFront(next2[look]);
			}
			else if (chrs[look] == (char)0)
			{
				_deq.pushFront(next1[look]);
			}
			else if (chrs[look] == _compText.charAt(point) || chrs[look] == (char)65535)
			{
				_deq.putRear(next1[look]);
			}

		}



		if(_deq.isEmpty() )
		{
			if(_failure)
				return;

			incMark();
			_deq.pushFront(1);
			evalStates();
		}
		else
		{
			_delayedIncPoint = true;
			//incPoint();
		}



	}

	private void incPoint()
	{
		point++;
		if(point > _compTextLen)
		{
			incMark();
		}
	}
	private void incMark()
	{
		mark++;
		if(mark > _compTextLen)
		{
			_failure = true;
			return;
		}
		point = mark;
	}

	private void evalPossible()
	{
		if(_failure)
			return;
		while(!_deq.isEmpty())
		{
			int look = _deq.getTail();
			if (isBranch(look))
			{
				_deq.pushFront(next1[look]);
				_deq.pushFront(next2[look]);
			}
			else if (chrs[look] == (char)0 && next1[look] != 0)
			{
				_deq.putRear(next1[look]);
			}
			else
			{
				_deq.pushFront(next1[look]);
			}
		}

		if(_deq.peekHead() == 0)
		{
			//SUCCESS
			_failure = true;
			_success = true;
			//System.err.println("Success");
			return;
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
                if(checkTextEnd())
                {
                    setState(state, (char)0, 0, 0);
                }
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
            //System.out.println(checkNext());
            // Check if clause parsed correctly (i.e. if it consumed a char, so that it doesnt go into left recursion)
            int r = clause();
            
           
            

            // check for end of text and set end state if so
            if(checkTextEnd())
            {
                setState(state, (char)0, 0, 0);
                return r;
            }

            // Does another altn if still input left
            if ("|)]".indexOf(checkNext()) == -1)
            { 
                altn();
            } 
            if(checkTextEnd())
            {
                setState(state, (char)0, 0, 0);
                return r;
            }
            return r;
	}

	// Rewrites clause
	private int clause() throws Exception
	{
            // record initial state
            int r = state;
            // go to new state and build literal machine
            state++;
            // record first start location
            int t1 = lit();
            
            // If the * or ? decorator symbols exist, then just skip over it
            if (checkNext() == '*')
            {                    
                _position++;
                // set state to point to initial state
                setState(state, (char)0, r, r);
                
                // go to next empty state for both branches to point to
                state++;
                
                // initial branch state to point to literal machine or to currerent empty
                setState(r, (char)0, t1, state);
                
                // check for end of text and set end state if so
                if(checkTextEnd())
                    setState(state, (char)0, 0, 0);
            } else if (checkNext() == '?')
            {
                _position++;
                // set state to point to next state
                setState(state, (char)0, state+1, state+1);
                
                // go to next empty state for both branches to point to
                state++;
                
                // initial branch state to point to literal machine or to currerent empty
                setState(r, (char)0, t1, state);       
                
                // check for end of text and set end state if so
                if(checkTextEnd())
                    setState(state, (char)0, 0, 0);
            } else
            {
                // if no branches
                setState(r, (char)0, t1, t1);
            }
            return r;
	}

	// Rewrites literal
	private int lit() throws Exception
	{
            int r = state;
            int t1 = -1;
            // Does escaping if the escape symbol is detected
            if(checkNext() == '\\')
            {
                _position++;
                r = chrEsc();
            }
            
            // Parses brackets
            else if(checkNext() == '(')
            {
                _position++;
                r = expr();
                if(checkNext() == ')')
                {
                    _position++;
                    if(checkTextEnd())
                        setState(state, (char)0, 0, 0);
                }
                else
                    throw new Exception("Matching ) not found");
            }
            // Parses the wildcard symbol
            else if(checkNext() == '.')
            {
                // set state to consume wildcard character
                setState(state, (char)65535, state+1, state+1);
                state++;
                _position++;
                
                // check for end of text and set end state if so
                if(checkTextEnd())
                    setState(state, (char)0, 0, 0);
            }
            // Parses brackets for alternations
            else if(checkNext() == '[')
            {
                _position++;
                r = spec();
                if(checkNext() == ']')
                {
                    _position++;
                    if(checkTextEnd())
                        setState(state, (char)0, 0, 0);
                }
                else
                    throw new Exception("Matching ] not found");
            }
            else
            {
                return chr();
            }

            return r;
	}
        
        // Checks for special characters inside a list
	private int spec() throws Exception    
        {
            // record initial state
            int r = state;
            // go to new state to build machine
            state++; 
            int t1 = state;
            // make state machine containing character
            setState(state, _text.charAt(_position), state+1, state+1);
            state++;
            // move position forward as it does not matter what it is looking at
            _position++;
            
            // if from here we see a ], it indicates the end of the list and we return
            if(checkNext() == ']')
            {
                setState(r, (char)0, t1, t1);
                return r;
            }
            // otherwise branch
            
            // set end state of first branch
            int d = state;
            state++;
            // create second branch
            int t2 = spec();
            // set initial state to branch
            setState(r, (char)0, t1, t2); 
            // set first branch destination to same as second branch
            setState(d, (char)0, state, state);
            
            return r;
        }

	// Checks if next char is of the vocabulary
	private int chr() throws Exception
	{
            setState(state,  checkNext(), state+1, state+1);
            state++;
            _position++;
            // check for end of text and set end state if so
            if(checkTextEnd())
                setState(state, (char)0, 0, 0);
            return state-1;
	}
        

	// Checks if next char is not of vocabulary
	private int chrEsc() throws Exception
	{
            setState(state,  checkNext(), state+1, state+1);
            state++;
            _position++;
            // check for end of text and set end state if so
            if(checkTextEnd())
                setState(state, (char)0, 0, 0);
            return state-1;
	}

	// Returns the next character. Handles bounds checking too
	private char checkNext() throws Exception
	{
            if(_position >= _len)
	            return (char)0;
                //throw new Exception("Unexpected end of input");

            return _text.charAt(_position);
	}
        
        private boolean checkTextEnd()
        {
            return (_position >= _len);
        }
        
//        private void printFSM()
//        {
//            System.out.println("state");
//            for (int i = 0; i<= state;i++)
//            {
//                if(i<10)
//                    System.out.print(i + "  ");
//                else
//                    System.out.print(i + " ");
//            }
//            System.out.print("\n");
//
//            System.out.println("char");
//            for (int i = 0; i<= state;i++)
//            {
//                System.out.print(chrs[i] + "  ");
//            }
//            System.out.print("\n");
//
//            System.out.println("n1");
//            for (int i = 0; i<= state;i++)
//            {
//                if(next1[i]<10)
//                    System.out.print(next1[i] + "  ");
//                else
//                    System.out.print(next1[i] + " ");
//            }
//            System.out.print("\n");
//
//            System.out.println("n2");
//            for (int i = 0; i<= state;i++)
//            {
//                if(next1[i]<10)
//                    System.out.print(next2[i] + "  ");
//                else
//                    System.out.print(next2[i] + " ");
//            }
//            System.out.print("\n");
//        }
}