
class TDoubleListImp 
{
	Node head, tail;
	int elementcount;

	TDoubleListImp() {
		head = null;
		tail = null;
		elementcount = 0;
	}
	
	int GetItemsInContainer() {
		return elementcount;
	}
	
	void AddAtHead(Object t) {
		Node oldhead = head;
		head = new Node(t, null, oldhead);
		if (oldhead == null) tail = head;
		else oldhead.prev = head;
		elementcount++;
	}
	
	void AddAtTail(Object t) {
		Node oldtail = tail;
		tail = new element(t, oldtail, null);
		if (oldtail == null) head = tail;
		else oldtail.next = tail;
		elementcount++;
	}
	
	void Flush() {
		head = tail = null;
		elementcount = 0;
	}
	
	boolean IsEmpty() {
		return (elementcount == 0);
	}
	
	Object PeekHead() {
		return head.that;       // error if list is empty
	}
	
	Object PeekTail() {
		return tail.that;       // error if list is empty
	}
	
	/*
	void ForEach(IterFunc iter, void *args) {
		for (Node ptr = head; ptr; ptr = ptr.next)
			iter(ptr.that, args);
	}
	
	Object FirstThat(CondFunc cond, void *args) {
		for (element *ptr = head; ptr; ptr = ptr->next)
			if (cond(ptr->that, args)) return &ptr->that;
		return null;
	}
	
	Object LastThat(CondFunc cond, void *args) {
		for (element *ptr = tail; ptr; ptr = ptr->prev)
			if (cond(ptr->that, args)) return &ptr->that;
		return null;
	}
	*/

	void DetachAtHead() {
		if (head) {
			element *newhead = head->next;
			//delete head;
			head = newhead;
			if (newhead == null) tail = null;
			elementcount--;
		}
	}
	
	void TDoubleListImp<T>::DetachAtTail() {
		if (tail) {
			Node newtail = tail.prev;
			//delete tail;
			tail = newtail;
			if (newtail == null) head = null;
			elementcount--;
		}
	}
}
