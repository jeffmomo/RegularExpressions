public class Node
{
    private int c;
    Node next;
    
    public Node(int c)
    {
        this.c = c;
    }
    
    public int getChar()
    {
        return c;
    }
    
    public void setNext(Node n)
    {
        this.next = n;
    }
    
    public Node getNext()
    {
        return next;
    }
}
