class Node {

    public Object that;
    public Node prev, next;

    // constructor
    Node(Object t, Node Prev = null, Node Next = null) {
          that = t;
          prev = Prev;
          next = Next;
    }
    
}