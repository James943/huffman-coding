package huffman;

import huffman.tree.Branch;
import huffman.tree.Leaf;
import huffman.tree.Node;

import java.util.*;
/**
 * The class implementing the Huffman coding algorithm.
 */
public class Huffman {

    /**
     * Build the frequency table containing the unique characters from the String `input' and the number of times
     * that character occurs.
     *
     * @param   input   The string.
     * @return          The frequency table.
     */
	public static Map<Character, Integer> freqTable (String input) {
		if (input == null || input == "") {
			return null;	
		} else {
			Map<Character, Integer> ft = new HashMap<>();
			// makes new mapping from each unique character in the input string to the frequency of it occurring
			for (int i = 0; i < input.length(); i++) {
				char character = input.charAt(i);
				if (ft.containsKey(character)) {
					ft.put(character, ft.get(character) + 1);
				} else {
					ft.put(character, 1);
				}
			}
			return ft;
		}
	}

    /**
     * Given a frequency table, construct a Huffman tree.
     *
     * First, create an empty priority queue.
     *
     * Then make every entry in the frequency table into a leaf node and add it to the queue.
     *
     * Then, take the first two nodes from the queue and combine them in a branch node that is
     * labelled by the combined frequency of the nodes and put it back in the queue. The right hand
     * child of the new branch node should be the node with the larger frequency of the two.
     *
     * Do this repeatedly until there is a single node in the queue, which is the Huffman tree.
     *
     * @param freqTable The frequency table.
     * @return          A Huffman tree.
     */
	public static Node treeFromFreqTable(Map<Character, Integer> freqTable) {
		// checks whether the given frequency table is empty, and returns an empty tree is so
		if (freqTable == null) {
			return null;
		} else {
			// creates an empty priority queue
			PQueue queue = new PQueue();
			// makes a leaf node for every entry in the frequency table and adds it to the queue
			for (Map.Entry<Character, Integer> entry : freqTable.entrySet()) {
				queue.enqueue(new Leaf(entry.getKey(), entry.getValue()));
			}
			// takes the first two nodes from the queue and combine them in a branch node that is labelled by the combined
			// frequency of the nodes and puts it back in the queue. This is repeated until there is a single node in the
			// queue, which is then returned
			while (queue.size() > 1) {
				Node firstNode = queue.dequeue();
				Node secondNode = queue.dequeue();
				Branch newBranch = new Branch(firstNode.getFreq() + secondNode.getFreq(), firstNode, secondNode);
				queue.enqueue(newBranch);
			}
			return queue.dequeue();
		}
	}

    /**
     * Construct the map of characters and codes from a tree. Just call the traverse
     * method of the tree passing in an empty list, then return the populated code map.
     *
     * @param tree  The Huffman tree.
     * @return      The populated map, where each key is a character, c, that maps to a list of booleans
     *              representing the path through the tree from the root to the leaf node labelled c.
     */
	public static Map<Character, List<Boolean>> buildCode(Node tree) {
		// calls the traverse method on the tree with an empty list as a parameter, and returns the completed map of characters
		// and their path
		ArrayList<Boolean> newList = new ArrayList<Boolean>();
		return tree.traverse(newList);
	}

    /**
     * Create the huffman coding for an input string by calling the various methods written above. I.e.
     *
     * + create the frequency table,
     * + use that to create the Huffman tree,
     * + extract the code map of characters and their codes from the tree.
     *
     * Then to encode the input data, loop through the input looking each character in the map and add
     * the code for that character to a list representing the data.
     *
     * @param input The data to encode.
     * @return      The Huffman coding.
     */
	public static HuffmanCoding encode(String input) {
		// creates the frequency table using the input string
		Map<Character, Integer> frequencyTable = freqTable(input);
		
		// creates the Huffman tree from the frequency table
		Node huffmanTree = treeFromFreqTable(frequencyTable);
		
		// extracts the map of characters and their codes from the Huffman tree
		Map<Character, List<Boolean>> charactersAndCodes = buildCode(huffmanTree);
		
		// looks up each character in the input string (by looping trough each of them) in the map , and adds the sequence
		// of true/false codes in a list
		ArrayList<Boolean> data = new ArrayList<Boolean>();
		for (int i = 0; i < input.length(); i++) {
			char character = input.charAt(i);
			data.addAll(charactersAndCodes.get(character));
		}
		
		// returns the code and the data as an instance of the HuffmanCoding class
		return new HuffmanCoding(charactersAndCodes, data);
    }

    /**
     * Reconstruct a Huffman tree from the map of characters and their codes. Only the structure of this tree
     * is required and frequency labels of all nodes can be set to zero.
     *
     * Your tree will start as a single Branch node with null children.
     *
     * Then for each character key in the code, c, take the list of booleans, bs, corresponding to c. Make
     * a local variable referring to the root of the tree. For every boolean, b, in bs, if b is false you want to "go
     * left" in the tree, otherwise "go right".
     *
     * Presume b is false, so you want to go left. So long as you are not at the end of the code so you should set the
     * current node to be the left-hand child of the node you are currently on. If that child does not
     * yet exist (i.e. it is null) you need to add a new branch node there first. Then carry on with the next entry in
     * bs. Reverse the logic of this if b is true.
     *
     * When you have reached the end of this code (i.e. b is the final element in bs), add a leaf node
     * labelled by c as the left-hand child of the current node (right-hand if b is true). Then take the next char from
     * the code and repeat the process, starting again at the root of the tree.
     *
     * @param code  The code.
     * @return      The reconstructed tree.
     */
	public static Node treeFromCode(Map<Character, List<Boolean>> code) {
		Node root = new Branch(0, null, null);
		// loops through each character in the map and loops through each boolean value in the list for that character and
		// all nodes in a new Huffman tree are filled out along the way
		for (char c: code.keySet()) {
			Node currentNode = root;
			List<Boolean> bs = code.get(c);
			int count = 0;
			for (Boolean b: bs) {
				count++;
				if (b == false) {
					// before the current node moves to its left child, its checked whether a new leaf with a label the same
					// as the current character needs to be created (because it is the last boolean value is being looped
					// through), or if a new left child needs to be made because it hasn't been created already from another
					// character's loop through
					if (count == bs.size()) {
						((Branch) currentNode).setLeft(new Leaf(c, 0));
					} else if (((Branch) currentNode).getLeft() == null) {
						((Branch) currentNode).setLeft(new Branch(0, null, null));
					}
					currentNode = ((Branch) currentNode).getLeft();
				} else if (b == true) {
					// before the current node moves to its right child, its checked whether a new leaf with a label the same
					// as the current character needs to be created (because it is the last boolean value is being looped
					// through), or if a new right child needs to be made because it hasn't been created already from another
					// character's loop through
					if (count == bs.size()) {
						((Branch) currentNode).setRight(new Leaf(c, 0));
					} else if (((Branch) currentNode).getRight() == null) {
						((Branch) currentNode).setRight(new Branch(0, null, null));
					}
					currentNode = ((Branch) currentNode).getRight();
				}
			}
		}
		return root;
	}

	/**
	 * Decode some data using a map of characters and their codes. To do this you need to reconstruct the tree from the
	 * code using the method you wrote to do this. Then take one boolean at a time from the data and use it to traverse
	 * the tree by going left for false, right for true. Every time you reach a leaf you have decoded a single
	 * character (the label of the leaf). Add it to the result and return to the root of the tree. Keep going in this
     * way until you reach the end of the data.
     *
     * @param code  The code.
     * @param data  The encoded data.
     * @return      The decoded string.
     */
	public static String decode(Map<Character, List<Boolean>> code, List<Boolean> data) {
		String decodedString = "";
		Node root = treeFromCode(code);
		Node currentNode = root;
		// each individual boolean in the data is checked whether its false/true and the currentNode moves left/right accordingly
		for (Boolean b: data) {
			if (b == false) {
				currentNode = ((Branch) currentNode).getLeft();
				if (currentNode instanceof Leaf) {
					// a leaf is reached so its decoded character is added to the decoded string, and the current node returns
					// to the root of the tree
					decodedString += ((Leaf) currentNode).getLabel();
					currentNode = root;
				}
			} else {
				currentNode = ((Branch) currentNode).getRight();
				if (currentNode instanceof Leaf) {
					// a leaf is reached so its decoded character is added to the decoded string, and the current node returns
					// to the root of the tree
					decodedString += ((Leaf) currentNode).getLabel();
					currentNode = root;
				}
			}
		}
		return decodedString;
	}
}
