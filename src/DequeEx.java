/**
 * Basically a doubly linked list
 */
public class DequeEx
{
	private NodeEx _head;
	private NodeEx _tail;

	public DequeEx()
	{
		NodeEx mid = new NodeEx(-1);
		_head = _tail = mid;
	}

	// Returns if the stack is still poppable
	public boolean isPoppable()
	{
		return _head.state != -1;
	}

	// Returns if the queue is empty
	public boolean isEmpty()
	{
		return _tail.state == -1;
	}

	// Gets the head of the deque and pops it out
	public int getHead()
	{
		int out = _head.state;
		_head = _head.prev;
		_head.next = null;
		return out;
	}

	// Returns value without popping
	public int peekHead()
	{
		return _head.state;
	}

	// Gets the value of tail and remove it
	public int getTail()
	{
		int out = _tail.state;
		_tail = _tail.next;
		_tail.prev = null;
		return out;
	}

	// Pushes to front (on stack)
	public void pushFront(int i)
	{
		NodeEx n = new NodeEx(i);
		n.prev = _head;
		_head.next = n;
		_head = n;
	}

	// Puts in rear (on queue)
	public void putRear(int i)
	{
		NodeEx n = new NodeEx(i);

		n.next = _tail;
		_tail.prev = n;
		_tail = n;
	}

}
