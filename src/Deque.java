public class Deque
{
    private Node head;
    private Node tail;
	private Node start;
    
    public Deque()
    {
        start = new Node(-1);
	    head = tail = start;
    }
    
    // takes from top of deque
    public int pop()
    {
	    int c = head.getChar();
        head = head.getNext();
        return c;
    }

	public boolean isPoppable()
	{
		return head.getChar() != -1;
	}
	public boolean isEmpty()
	{
		return head == null;
	}

    
    // adds to top of deque
    public void push(int c)
    {
        Node n = new Node(c);
        n.setNext(head);
        head = n;
        
        // initialise for empty deque

    }
    
    // adds to end of deque
    public void put(int c)
    {
        Node n = new Node(c);
        // initialise for empty deque

	    tail.setNext(n);
        tail = n;
    }
}
