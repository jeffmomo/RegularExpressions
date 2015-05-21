public class Node
{
    private char c;
    Node next;
    
    public Node(char c)
    {
        this.c = c;
    }
    
    public char getChar()
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
