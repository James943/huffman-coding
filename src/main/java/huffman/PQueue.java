package huffman;

import huffman.tree.Node;

import java.util.ArrayList;
import java.util.List;
/**
 * A priority queue of @Node@ objects. Each node has an int as its label representing its frequency.
 * The queue should order objects in ascending order of frequency, i.e. lowest first.
 */
public class PQueue {

    private List<Node> queue;

    public PQueue() {
        queue = new ArrayList<>();
    }

    /**
     * Add a node to the queue. The new node should be inserted at the point where the frequency of next node is
     * greater than or equal to that of the new one.
     * @param n The node to enqueue.
     */
    public void enqueue(Node n) {
    	int position = 0;
    	boolean added = false;
    	while (position < queue.size() && added == false) {
    		if (queue.get(position).getFreq() >= n.getFreq()) {
    			queue.add(position, n);
    			added = true;
    		}
    		position ++;
    	}
    	if (added == false) {
    		queue.add(n);
    	}
    }

    /**
     * Remove a node from the queue.
     * @return  The first node in the queue.
     */
    public Node dequeue() {
    	if (queue.size() != 0) {
    		Node dequeuedNode = queue.get(0);
    		queue.remove(0);
    		return dequeuedNode;
    	} else {
    		return null;
    	}
    }

    /**
     * Return the size of the queue.
     * @return  Size of the queue.
     */
    public int size() {
        return queue.size();
    }
}
