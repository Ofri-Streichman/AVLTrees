package src;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *
 */
public class MeasureAVL {
	IAVLNode root;
	int size;
	IAVLNode min;
	IAVLNode max;

	public MeasureAVL() {
		IAVLNode virtualNode = new AVLNode();
		this.root = virtualNode;
		this.min = null;
		this.max = null;
		this.size = 0;
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() { // Complexity: O(1)
		return !this.root.isRealNode();
	}



	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. promotion/rotation - counted
	 * as one rebalnce operation, double-rotation is counted as 2. returns -1 if an
	 * item with key k already exists in the tree.
	 */
	public int insert(int k, String i) {

		IAVLNode newNode = new AVLNode(k, i);
		if (this.empty()) { // insert root
			this.root = (AVLNode) newNode;
			this.max = (AVLNode) newNode;
			this.min = (AVLNode) newNode;

		} else { // tree is not empty
			IAVLNode position = this.fingerSearch(k);
			if (position.getKey() == k) { // key already exists
				return -1;

			} else if (position.getKey() > k) { // insert as a left child
				position.setLeft(newNode);
				if (newNode.getKey() <= this.min.getKey()) {
					this.min = newNode;
				}

			} else { // insert as a right child
				position.setRight(newNode);
				if (newNode.getKey() >= this.max.getKey()) {
					this.max = newNode;
				}
			}
			newNode.setParent(position);
		}
		return rebalance(newNode);

	}


	
	
	
	
	private IAVLNode fingerSearch(int k) {
		AVLNode node = (AVLNode)this.max;
		
		while (node.getParent() != null && node.getMin().getKey() > k) {
			node = (AVLNode)node.getParent();
		}
		
		return this.findPosition(node, k);
	}
	
	public int fingerSearchCounter(int k) {
		if (this.max == null) {
			return 0;
		}
		int count = 1;
		AVLNode node = (AVLNode)this.max;
		
		while (node.getParent() != null && node.getMin().getKey() > k) {
			node = (AVLNode)node.getParent();
			count++;
		}
		
		return count + this.findPositionCounter(node, k);
	}
	

	public IAVLNode findPosition(IAVLNode currNode, int k) {
		IAVLNode tmp = null; // always a step before the currNode

		while (currNode.isRealNode()) {
			tmp = currNode;
			if (k < currNode.getKey()) {
				currNode = currNode.getLeft();
			} else if (k > currNode.getKey()) {
				currNode = currNode.getRight();
			} else { // k == currNode.key
				return currNode; // return an inner node
			}
		}
		return tmp; // return a leaf or null if tree is empty
	}
	
	public int findPositionCounter(IAVLNode currNode, int k) {
		IAVLNode tmp = null; // always a step before the currNode
		int cnt = 0;
		
		while (currNode.isRealNode()) {
			tmp = currNode;
			if (k < currNode.getKey()) {
				cnt++;
				currNode = currNode.getLeft();
			} else if (k > currNode.getKey()) {
				cnt++;
				currNode = currNode.getRight();
			} else { // k == currNode.key
				return cnt;
			}
		}
		return cnt; // return a leaf or null if tree is empty
	}




	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() { // Complexity: O(1)
		if (this.empty()) {
			return null;
		}

		return this.min.getValue();
	}

	// Finds the minimum of the tree, this is good for deletion
	private IAVLNode findMin() { // Complexity: O(logn)
		if (this.empty()) {
			return null;
		}

		IAVLNode node = this.getRoot();

		while ((node.getLeft() != null) && (node.getLeft().isRealNode())) {
			node = node.getLeft();
		}

		return node;
	}

	private IAVLNode findMax() {
		if (this.empty()) {
			return null;
		}

		IAVLNode node = this.getRoot();

		while ((node.getRight() != null) && (node.getRight().isRealNode())) {
			node = node.getRight();
		}

		return node;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		// If the tree is empty - return null
		if (this.empty()) {
			return null;
		}

		return this.max.getValue();
	}



	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		return this.size;
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null (i.e. you can also assume that the tree is not empty) postcondition:
	 * none
	 */



	public int rebalance(IAVLNode node) {
		int cnt = 0;
		while (node != null) {
			
			AVLNode newNode = (AVLNode) node;

			if (newNode.rankDiffLeft() == 0) { // problem with left subtree
				if (newNode.rankDiffRight() == 1) { // case 1: node-01, not terminal
					newNode.promote(); // sol: promote
					cnt++;
				} else {
					if (newNode.getLeft().rankDiffLeft() == 1 && // case 2: node-02 with child-12, terminal
							newNode.getLeft().rankDiffRight() == 2) {

						newNode.demote(); // sol: demote + right rotate
						node = this.rotateRight(newNode);
						cnt += 2;

					} else { // case3: node-02 with child-21, terminal
						newNode.demote(); // sol: demote + left rotation + right rotation
						newNode.getLeft().demote();
						node = this.rotateLeft(newNode.getLeft());
						node.promote();
						node = this.rotateRight(newNode);
						cnt += 5;
					}
				}
			}

			else if (newNode.rankDiffRight() == 0) { // problem with right subtree
				if (newNode.rankDiffLeft() == 1) { // case 1: node-01, not terminal
					newNode.promote(); // sol: promote
					cnt++;
				} else {
					if (newNode.getRight().rankDiffLeft() == 2 && // case2: node-20 with child-12, terminal
							newNode.getRight().rankDiffRight() == 1) {

						newNode.demote(); // sol: demote + left rotate
						node = this.rotateLeft(newNode);
						cnt += 2;

					} else { // case3: node-02 with child-21, terminal
						newNode.demote(); // sol: demote + right rotation + left rotation
						newNode.getRight().demote();
						node = this.rotateRight(newNode.getRight());
						node.promote();
						node = this.rotateLeft(newNode);
						cnt += 5;
					}

				}

			}
			newNode.updatePath();
			node = node.getParent();
		}
		return cnt;
	}



	/**
	 * 
	 * ********************* UTIL METHODS*********************************
	 * 
	 * 
	 */




	public IAVLNode rotateRight(IAVLNode node) {

		IAVLNode leftNode = node.getLeft();
		IAVLNode parentNode = node.getParent();

		if (parentNode == null) { // node is root
			this.root = (AVLNode) leftNode;

		} else if (parentNode.getKey() > node.getKey()) { // node is left child
			parentNode.setLeft(leftNode); // updating parentNode's new child

		} else { // node is right child
			parentNode.setRight(leftNode);
		}

		leftNode.setParent(parentNode); // updating leftNode's new parent

		// rotate
		node.setLeft(leftNode.getRight());
		leftNode.getRight().setParent(node);
		leftNode.setRight(node);
		node.setParent(leftNode);

		// sizes and heights update
		node.update();
		leftNode.update();

		return leftNode;
	}
	
	

	public IAVLNode rotateLeft(IAVLNode node) {
		IAVLNode rightNode = node.getRight();
		IAVLNode parentNode = node.getParent();

		if (parentNode == null) { // node is root
			this.root = (AVLNode) rightNode;

		} else if (parentNode.getKey() > node.getKey()) { // node is left child
			parentNode.setLeft(rightNode); // updating parentNode's new child

		} else { // node is right child
			parentNode.setRight(rightNode);
		}

		rightNode.setParent(parentNode); // updating rightNode's new parent

		// rotate
		node.setRight(rightNode.getLeft());
		rightNode.getLeft().setParent(node);
		rightNode.setLeft(node);
		node.setParent(rightNode);

		// sizes and heights update
		node.update();
		rightNode.update();

		return rightNode;
	}



	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // returns node's key (for virtuval node return -1)

		public String getValue(); // returns node's value [info] (for virtuval node return null)

		public void setLeft(IAVLNode node); // sets left child

		public IAVLNode getLeft(); // returns left child (if there is no left child return null)

		public void setRight(IAVLNode node); // sets right child

		public IAVLNode getRight(); // returns right child (if there is no right child return null)

		public void setParent(IAVLNode node); // sets parent

		public IAVLNode getParent(); // returns the parent (if there is no parent return null)

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

		public void setHeight(int height); // sets the height of the node

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

		public int getSize();

		public void promote();

		public void demote();

		public int getRank();

		public int rankDiffRight();

		public int rankDiffLeft();

		public void update();

		public void setRank(int rank);
		public void updatePath();
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file. This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {
		private int key;
		private String info;
		private boolean isVirtual;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private int height;
		private int size;
		private int rank;
		private IAVLNode min;

		public AVLNode() { // Complexity: O(1)
			this.key = -1;
			this.info = null;
			this.isVirtual = true;
			this.height = -1;
			this.rank = -1;
			this.size = 0;
			this.left = null;
			this.right = null;
			this.min = null;
		}

		public AVLNode(int key, String info) { // Complexity: O(1)
			this.key = key;
			this.info = info;
			this.size = 1;
			this.height = 0;
			this.rank = 0;
			this.isVirtual = false;
			this.left = new AVLNode();
			this.right = new AVLNode();
			this.min = this;
		}

		public int getKey() { // Complexity: O(1)
			return this.key;
		}

		public String getValue() { // Complexity: O(1)
			return this.info;
		}

		public void setLeft(IAVLNode node) { // Complexity: O(1)
			this.left = node;
			this.update();
		}

		public IAVLNode getLeft() { // Complexity: O(1)
			return this.left;
		}

		public void setRight(IAVLNode node) { // Complexity: O(1)
			this.right = node;
			this.update();
		}

		public IAVLNode getRight() { // Complexity: O(1)
			return this.right;
		}

		public void setParent(IAVLNode node) { // Complexity: O(1)
			this.parent = node;
		}

		public IAVLNode getParent() { // Complexity: O(1)
			return this.parent;
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() { // Complexity: O(1)
			return !this.isVirtual;
		}

		public void setHeight(int height) { // Complexity: O(1)
			this.height = height;
		}

		public int getHeight() { // Complexity: O(1)
			return this.height;
		}

		public int getSize() { // Complexity: O(1)
			return this.size;
		}

		public void promote() { // Complexity: O(1)
			this.rank++;
		}

		public void demote() { // Complexity: O(1)
			this.rank--;
		}

		public int getRank() { // Complexity: O(1)
			return this.rank;
		}

		public int rankDiffRight() {
			if (this.isRealNode() == false) {
				return 0;
			}
			return (this.rank - this.right.getRank());
		}

		public int rankDiffLeft() {
			if (this.isRealNode() == false) {
				return 0;
			}
			return this.rank - this.left.getRank();
		}

		public IAVLNode getMin() {
			return this.min;
		}

		private void setMin(IAVLNode node) {
			this.min = node;
		}

		public void update() { // Complexity: O(1)
			int size = 1;

			if (this.right.isRealNode()) {
				size += ((AVLNode) this.right).getSize();
			}

			if (this.left.isRealNode()) {
				size += ((AVLNode) this.left).getSize();

				IAVLNode node = ((AVLNode) this.getLeft()).getMin();
				this.setMin(node);

			} else {
				this.setMin(this);
			}

			this.size = size;

			this.setHeight(1 + Math.max(this.getLeft().getHeight(), this.getRight().getHeight()));
		}



		public void setRank(int rank) {
			this.rank = rank;
		}
		public void updatePath() {
			IAVLNode curr = (IAVLNode) this;
			while (curr != null) {
				curr.update();
				curr = curr.getParent();
			}
	}
	
	}

}

