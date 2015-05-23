/**
 * Created by Jeff on 23/05/2015.
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

	public boolean isPoppable()
	{
		return _head.state != -1;
	}
	public boolean isEmpty()
	{
		return _tail.state == -1;
	}

	public int getHead()
	{
		int out = _head.state;
		_head = _head.prev;
		_head.next = null;
		return out;
	}

	public int peekHead()
	{
		return _head.state;
	}

	public int getTail()
	{
		int out = _tail.state;
		_tail = _tail.next;
		_tail.prev = null;
		return out;
	}

	public void pushFront(int i)
	{
		NodeEx n = new NodeEx(i);
		n.prev = _head;
		_head.next = n;
		_head = n;
	}

	public void putRear(int i)
	{
		NodeEx n = new NodeEx(i);

		n.next = _tail;
		_tail.prev = n;
		_tail = n;
	}

}
