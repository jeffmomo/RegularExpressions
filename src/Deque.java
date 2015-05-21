public class Deque
{
    private Node head;
    private Node tail;
    
    public Deque()
    {
        
    }
    
    // takes from top of deque
    public char pop()
    {
        char c = head.getChar();
        head = head.getNext();
        return c;
    }

    
    // adds to top of deque
    public void push(char c)
    {
        Node n = new Node(c);
        n.setNext(head);
        head = n;
        
        // initialise for empty deque
        if( tail == null)
            tail = n;
    }
    
    // adds to end of deque
    public void put(char c)
    {
        Node n = new Node(c);
        // initialise for empty deque
        if(tail == null)
        {
            head = n;
        } else {
            tail.setNext(n);
        }
        tail = n;
    }
}
