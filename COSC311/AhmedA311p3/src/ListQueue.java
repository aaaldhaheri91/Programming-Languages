


import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Implements the Queue interface using a single-linked list.
 * @param <E> the element type
 * 
 **/
public class ListQueue<E> extends AbstractQueue<E>
        implements Queue<E> {

    // Data Fields
    /** Reference to front of queue. */
    private Node<E> front;
    /** Reference to rear of queue. */
    private Node<E> rear;
    /** Size of queue. */
    private int size;

    // Insert inner class Node<E> here (see Listing 2.1)
    /** A Node is the building block for a single-linked list. */
    private static class Node<E> {
        // Data Fields

        /** The reference to the data. */
        private final E data;
        /** The reference to the next node. */
        private Node<E> next;

        // Constructors
        /**
         * Creates a new node with a null next field.
         * @param dataItem The data stored
         */
        private Node(E dataItem) {
            data = dataItem;
            next = null;
        }

        /**
         * Creates a new node that references another node.
         * @param dataItem The data stored
         * @param nodeRef The node referenced by new node
         */
        private Node(E dataItem, Node<E> nodeRef) {
            data = dataItem;
            next = nodeRef;
        }
    
    } //end class Node

    
    // Methods
    /**
     * Insert an item at the rear of the queue.
     * @post item is added to the rear of the queue.
     * @param item The element to add
     * @return true (always successful)
     */
    @Override
    public boolean offer(E item) {
        // Check for empty queue.
        if (front == null) {
            rear = new Node<>(item);
            front = rear;
        } else {
            // Allocate a new node at end, store item in it, and
            // link it to old end of queue.
            rear.next = new Node<>(item);
            rear = rear.next;
        }
        size++;
        return true;
    }
    
    
    /**
     * Adds node at front
     * @param item the item to add
     */
    private void addFront(E item){
    	front = new Node<E>(item, front);
    	size++;
    }

    
    /**
     * Adds after node
     * @param node the node to add after
     * @param item the item to add
     */
    private void addAfter(Node<E> node, E item){
    	node.next = new Node<E>(item, node.next);
    	size++;
    }
    
    
    /**
     * Adds at a specified index
     * @param index the index
     * @param item the item to add
     */
    public void offer(int index, E item){
    	
    	if(index < 1){
    		throw new IndexOutOfBoundsException(Integer.toString(index));
    	}
    	
    	//search for same unit time to add
    	Event tempEvent1 = (Event) item;
    	Node<E> temp = front;
    	
    	//make sure list is not empty
    	if(temp != null){
    		
    		//creat temp event of head
    		Event tempHead = (Event)front.data;
    		if(tempEvent1.getTime() <= tempHead.getTime()){
    			addFront(item);
    			return;
    		}
    		
	    	while(temp.next != null){
	    		
	    		//get event
	    		Event tempEvent2 = (Event) temp.next.data;
	    		
	    		//get node before desired location and add after
	    		if(tempEvent1.getTime() <= tempEvent2.getTime()){
	    			addAfter(temp, item);
	    			return;
	    		}
	    		
	    		
	    		temp = temp.next;
	    	}
    	}
    	
    	//if did not find larger event time than add to last
    	offer(item);
    	
    	
    }

    /**
     * Remove the entry at the front of the queue and return it
     * if the queue is not empty.
     * @post front references item that was second in the queue.
     * @return The item removed if successful, or null if not
     */
    @Override
    public E poll() {
        E item = peek(); // Retrieve item at front.
        if (item == null) {
            return null;
        }
        // Remove item at front.
        front = front.next;
        size--;
        return item; // Return data at front of queue.
    }

    /**
     * Return the item at the front of the queue without removing it.
     * @return The item at the front of the queue if successful;
     * return null if the queue is empty
     */
    @Override
    public E peek() {
        if (size == 0) {
            return null;
        } else {
            return front.data;
        }
    }

	@Override
	public Iterator<E> iterator() {
		
		return new IterImpl<E>(front);
	}

	@Override
	public int size() {
		
		return size;
	}
	
	
	
	/** Nested class to provide the iterator implementation */
    private class IterImpl<E> implements Iterator<E> {

        /** Reference to the current node */
        Node<E> current;

        /**
         * Create a new IterImpl starting at a specified node
         * @param node The starting node
         */
        public IterImpl(Node<E> start) {
            current = start;
        }

        /**
         * Returns true if the iteration has more elements.
         * In other words, returns true if next will return
         * an element and not throw an exception.
         * @returns true if the iterator has more elements
         */
        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next element in the iteration
         * @returns The next element in the iteration
         * @throws NoSuchElementException if there are no more elements
         */
        @Override
        public E next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            E returnValue = current.data;
            current = current.next;
            return returnValue;
        }

        /**
         * Removes the last item returned by next from the
         * list. This method is not supported by this iterator.
         * @throws UnsupportedOperationException operation not supported
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}

