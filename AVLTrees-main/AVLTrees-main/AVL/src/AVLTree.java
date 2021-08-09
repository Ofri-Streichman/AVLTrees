import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *
 */

public class AVLTree {

	private IAVLNode root = null;
	private IAVLNode min;
	private IAVLNode max;
	private IAVLNode virtualLeaf = new AVLNode();

	public int MAXKEY() {
		return this.max.getKey();
	}
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */
	public boolean empty() {
		if (this.root == null){
			return true;
		}
		if (!this.root.isRealNode()) {
			this.root=null;
			return true;
		}
		return false;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null
	 */
	public String search(int k) {
		IAVLNode node = SearchNode(k);
		if (node!=null && node.isRealNode()) {
			return node.getValue();
		}
		return null;
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
		if (this.empty()||!this.root.isRealNode()) { // insert root
			this.root = (AVLNode) newNode;
			this.max = (AVLNode) newNode;
			this.min = (AVLNode) newNode;

		} else { // tree is not empty
			
			if(this.max == null || this.min == null) { //will add logn to complexity but wont change it...
				this.updatemax(); 
				this.updatemin();
			}
			
			IAVLNode position = this.findPosition(this.root, k);
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
		int rebalanceNum = rebalance(newNode);
		newNode.updatePath();
		return rebalanceNum;

	}

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

					} else if(newNode.getLeft().rankDiffRight() == 1 && 
							newNode.getLeft().rankDiffLeft() == 2){ // case3: node-02 with child-21, terminal
						newNode.demote(); // sol: demote + left rotation + right rotation
						newNode.getLeft().demote();
						node = this.rotateLeft(newNode.getLeft());
						node.promote();
						node = this.rotateRight(newNode);
						cnt += 5;
					}
					else { 	//this case is used only in join!!!!
						newNode.getLeft().promote();
						node = this.rotateRight(newNode);
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

					} else if(newNode.getRight().rankDiffRight() == 2 &&
							newNode.getRight().rankDiffLeft() == 1) { // case3: node-02 with child-21, terminal
						newNode.demote(); // sol: demote + right rotation + left rotation
						newNode.getRight().demote();
						node = this.rotateRight(newNode.getRight());
						node.promote();
						node = this.rotateLeft(newNode);
						cnt += 5;
					}
					else { 	//this case is used only in join!!!!
						newNode.getRight().promote();
						node = this.rotateLeft(newNode);
					}

				}

			}
			node = node.getParent();
		}
		return cnt;
	}


	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. demotion/rotation
	 * - counted as one rebalnce operation, double-rotation is counted as 2. returns
	 * -1 if an item with key k was not found in the tree.
	 */
	public int delete(int k) {
		if (this.search(k)==null) {
			// there is no key with value k in the tree
			return -1;}
		// first, we need to check if we're deleting a saved field(min/max)
		int [] binaryminmax= {0,0};
		if (this.min.getKey()==k) {
			binaryminmax[0]=1;}
		if (this.max.getKey()==k) {
			binaryminmax[1]=1;}
		// initializing a counter for rebalancing actions
		int[] counter = {0};
		//starting recursive function
		this.root=DelRec(k, this.root, counter);
		if (this.root==virtualLeaf) {
			//the tree is empty
			this.root = null;
		}
		// update lost values 
		if (binaryminmax[0]==1) {
			updatemin();}
		if (binaryminmax[1]==1) {
			updatemax();}
		return counter[0];
		}
	
	// a recursive deletion function, returns the node on the right/left with it's subtree balanced and without node k
	private IAVLNode DelRec(int k,IAVLNode root, int[] counter) {
		// First we find the node recursively
		if (!root.isRealNode()) {
			return root;} 			// recursion end
		if (root.getKey()>k) {		
			// we need to turn left
			root.setLeft(DelRec(k, root.getLeft(), counter));}
		if (root.getKey()<k) {		
			// we need to turn right
			root.setRight(DelRec(k, root.getRight(), counter));}
		if(root.getKey()==k) {		
			// we found the node to delete!
			if (!root.getLeft().isRealNode() || !root.getRight().isRealNode()) {
				// node has one or zero sons
				IAVLNode temp = virtualLeaf;
				if (root.getLeft().isRealNode()) { // we need to find if the node has a real son
					temp = root.getLeft();
				}else {
					temp = root.getRight();
				}
				if (!temp.isRealNode()) {
					// root doesn't have kids
					root= virtualLeaf;
				}else {
					temp.setParent(root.getParent());
					root= temp;
				}
			}else {
				//node has two sons
				// first we'll find it's successor
				IAVLNode successor = successor(root);
				// now lets switch them
				Switch(root, successor);
				// now the root and it's successor are switched. lets delete the the root node from the right subtree recursively
				successor.setRight(DelRec(k, successor.getRight(), counter));
				root = successor;
			}
		}
			
			if(!root.isRealNode()) {		// this means our tree only had one node
				return root;
			}
			
			// update the height anvd size of the node
			root.setHeight(Math.max(root.getLeft().getHeight(), root.getRight().getHeight())+1);
			root.setSize(root.getLeft().getSize()+ root.getRight().getSize()+1);

			// let's check if we need to rebalance our subtree
			int rdl = root.rankDiffLeft();
			int rdr = root.rankDiffRight();
			
			if (rdl==2 && rdr==2) {
				root.demote();
				counter[0]+=1;
				return root;
			}
			if (rdl==2 && rdr==1) {
				return root;
			}
			if (rdl==1 && rdr==2) {
				return root;
			}
			if (rdl==3 && rdr==1) {
				IAVLNode y = root.getRight();
				if(y.rankDiffLeft()==1 && y.rankDiffRight()==1) {
					Lrotate(y);
					root.demote();
					y.promote();
					counter[0]+=3;
					return y;
				}
				if(y.rankDiffLeft()==1 && y.rankDiffRight()==2) {
					Lrotate(Rrotate(y.getLeft()));
					root.demote();
					root.demote();
					y.demote();
					y.getParent().promote();
					counter[0]+=6;
					return y.getParent();
				}
				if(y.rankDiffLeft()==2 && y.rankDiffRight()==1) {
					Lrotate(y);
					root.demote();
					root.demote();
					counter[0]+=3;
					return y;
				}
				
			}
			if (rdl==1 && rdr==3) {
				IAVLNode y = root.getLeft();
				if(y.rankDiffLeft()==1 && y.rankDiffRight()==1) {
					Rrotate(y);
					root.demote();
					y.promote();
					counter[0]+=3;
					return y;
				}
				if(y.rankDiffLeft()==2 && y.rankDiffRight()==1) {
					Rrotate(Lrotate(y.getRight()));
					root.demote();
					root.demote();
					y.demote();
					y.getParent().promote();
					counter[0]+=6;
					return y.getParent();
				}
				if(y.rankDiffLeft()==1 && y.rankDiffRight()==2) {
					Rrotate(y);
					root.demote();
					root.demote();
					counter[0]+=3;
					return y;
				}
				
			}
			
			// no need for rotations :)
			return root;
		
	}

		private IAVLNode Lrotate(IAVLNode y) {
			IAVLNode z = y.getParent();
			IAVLNode b = y.getRight();
			IAVLNode a = y.getLeft();
			IAVLNode x = z.getLeft();
			// start rotation
			if (z.getParent()!= null) {
				if (z.getParent().getLeft()==z) {
					z.getParent().setLeft(y);
				}else {
					z.getParent().setRight(y);
				}
			}
			y.setParent(z.getParent());
			z.setRight(a);
			a.setParent(z);
			y.setLeft(z);
			z.setParent(y);
			IAVLNode[] list = {a,b,z,x,y};
			for (IAVLNode node : list) {
				if (node.isRealNode()) {
					node.update();}
			}
			return y;
		}
	
	private IAVLNode Rrotate(IAVLNode y) {
		IAVLNode z = y.getParent();
		IAVLNode b = y.getLeft();
		IAVLNode a = y.getRight();
		IAVLNode x = z.getRight();
		// start rotation
		if (z.getParent()!= null) {
			if (z.getParent().getLeft()==z) {
				z.getParent().setLeft(y);
			}else {
				z.getParent().setRight(y);
			}
		}
		y.setParent(z.getParent());
		z.setLeft(a);
		a.setParent(z);
		y.setRight(z);
		z.setParent(y);
		IAVLNode[] list = {a,b,z,x,y};
		for (IAVLNode node : list) {
			if (node.isRealNode()) {
				node.update();}
		}
		return y;
	}

	private void Switch(IAVLNode n, IAVLNode s) {
		if (n.getRight()==s) {
			if (this.getRoot()==n) {
				s.setParent(null);
				this.root = s;
				n.setParent(s);
			}else {
				s.setParent(n.getParent());
				n.setParent(s);
			}

			n.setRight(s.getRight());
			s.setRight(n);
			IAVLNode temp = s.getLeft();
			s.setLeft(n.getLeft());
			n.setLeft(temp);
			s.getLeft().setParent(s);
			n.getLeft().setParent(n);
		}else {
			//switching left sons
			IAVLNode temp = s.getLeft();
			s.setLeft(n.getLeft());
			n.setLeft(temp);
			s.getLeft().setParent(s);
			n.getLeft().setParent(n);
			//switching Right sons
			temp = s.getRight();
			s.setRight(n.getRight());
			n.setRight(temp);
			s.getRight().setParent(s);
			n.getRight().setParent(n);
			//switching Parents
			temp = s.getParent();
			if (this.getRoot()==n) {
				s.setParent(null);
				this.root = s;
			}else {
				s.setParent(n.getParent());
				if (s.getParent().getKey()>n.getKey()) {
					s.getParent().setLeft(s);
				}else {
					s.getParent().setRight(s);
				}
			}
			n.setParent(temp);
			if (temp==null) {
				//it means s was the root!
				this.root= n;
			}else {
				if (n.getParent().getKey()>s.getKey()) {
					n.getParent().setLeft(n);
				}else {
					n.getParent().setRight(n);
				}
			}
		}
		
	}

		private IAVLNode successor(IAVLNode node) { // returns the successor of node
			node = node.getRight();
		while (node.getLeft().isRealNode()) {
			node = node.getLeft();
		}
		return node;
	}


	/**
	 * SearchNode(int k):
	 * @param int k
	 * @return node with key == k or null if there is no such node in the tree
	 */
	private IAVLNode SearchNode(int k) {
		if (this.empty()){
			return null;
		}
		IAVLNode node = this.getRoot();
		while (node.isRealNode()) {
			if (node.getKey() == k) {
				return node;
			}
			if (node.getKey() > k) {
				node = node.getLeft();
			} else {
				node = node.getRight();
			}
		}
		return null;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		if (this.empty()) { // tree is empty
			return null;
		}
		return this.min.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		if (this.empty()) { // tree is empty
			return null;
		}
		return this.max.getValue(); // this is a saved field in our data structure :)
	}
	/**
	 * public void updatemax()
	 * 
	 * calculates the max key out of our tree and sets the corresponding field if
	 * the tree is empty, it sets it to null
	 * @return 
	 */
	public void updatemax() {
		if (this.empty()) {
			this.max = virtualLeaf;
			return;

		}
		IAVLNode node = this.getRoot();
		IAVLNode max = null;
		while (node.isRealNode()) {
			max = node;
			node = node.getRight();
		}
		this.max = max;
	}

	/**
	 * public void updatemin()
	 * 
	 * calculates the minimal key out of our tree and sets the corresponding field
	 * if the tree is empty, it sets it to null
	 */

	public void updatemin() {
		if (this.empty()) {
			this.min = virtualLeaf;
			return;
		}
		IAVLNode node = this.getRoot();
		IAVLNode min = null;
		while (node.isRealNode()) {
			min = node;
			node = node.getLeft();
		}
		this.min = min;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		
		int[] keysArray = new int[0];

		if (this.empty()) {
			return keysArray;
		}
		
		keysArray = new int[this.getRoot().getSize()];
		
		List<IAVLNode> inOrderNodeList = new LinkedList<IAVLNode>();
		this.inOrderList(this.getRoot(), inOrderNodeList);

		int i = 0;
		for (IAVLNode node : inOrderNodeList) {
			keysArray[i] = node.getKey();
			i++;
		}
		return keysArray;
	}
	

	/**
	 * private void inOrderList(IAVLNode node, List<IAVLNode> inOrderNodeList)
	 *
	 * adds nodes to a linked list, in an "in order walk" order
	 */
	
	private void inOrderList(IAVLNode node, List<IAVLNode> inOrderNodeList) {

		if (!node.isRealNode()) {
			return;
		}
		this.inOrderList(node.getLeft(), inOrderNodeList);
		inOrderNodeList.add(node);
		this.inOrderList(node.getRight(), inOrderNodeList);

	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		
		String[] infoArray = new String[0];
		
		if (this.empty() == true) {
			return infoArray;
		}
		
		infoArray = new String[this.getRoot().getSize()];

		List<IAVLNode> inOrderNodeList = new LinkedList<IAVLNode>();
		this.inOrderList(this.getRoot(), inOrderNodeList);

		int i = 0;
		for (IAVLNode node : inOrderNodeList) {
			infoArray[i] = node.getValue();
			i++;
		}
		return infoArray;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		if (this.empty()) {
			return 0;
		}
		return this.root.getSize();
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

	// returns the height of the tree, 0 if it's empty
	public int getHeight() {
		if (this.empty()) {
			return 0;
		}
		return this.getRoot().getHeight();
	}

	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null (i.e. you can also assume that the tree is not empty) postcondition:
	 * none
	 */
	public AVLTree[] split(int x) {
		IAVLNode xNode = findPosition(this.getRoot(),x);
		return this.split(x, xNode);
	}
	
	private AVLTree[] split(int x, IAVLNode node) {
		IAVLNode pNode = node;
		AVLTree[] result = new AVLTree[2];

		result[0] = new AVLTree();	//smaller than x
		result[1] = new AVLTree();	//bigger than x
		
		//set x's right and left subtrees as the base of the 2 new trees
		if(node.getLeft().isRealNode()) {
			result[0].root = node.getLeft();
		}
		if(node.getRight().isRealNode()) {
			result[1].root = node.getRight();
		}
		pNode = node.getParent();
		
		//disconnect x's children + parent
		disconnectNode(node);
		
		while(pNode != null) {
			IAVLNode duplicatePNode = new AVLNode(pNode.getKey(), pNode.getValue());
			
			//adding to the smaller tree
			if(pNode.getKey() < x) {
				AVLTree smaller = new AVLTree();
				if(pNode.getLeft().isRealNode()) {
					smaller.root = pNode.getLeft();
					smaller.root.setParent(null);
				}
				result[0].join(duplicatePNode, smaller);
				
			}else {
				//adding to the bigger tree
				AVLTree bigger = new AVLTree();
				if(pNode.getRight().isRealNode()) {
					bigger.root = pNode.getRight();
					bigger.root.setParent(null);
				}
				result[1].join(duplicatePNode, bigger);
			}
			
			pNode = pNode.getParent();
			}
		
		return result;
		
	}	
	
	private void disconnectNode(IAVLNode node){
		node.getLeft().setParent(null);
		node.getRight().setParent(null);
		node.setLeft(new AVLNode());
		node.setRight(new AVLNode());
		node.setParent(null);
		
	}
	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation
	 * (|tree.rank - t.rank| + 1). precondition: keys(x,t) < keys() or keys(x,t) >
	 * keys(). t/tree might be empty (rank = -1). postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t) {
	int valtoreturn = Math.abs(this.getHeight()-t.getHeight()) +1;
	
	if (t.empty() && this.empty()) {
		this.max = x;
		this.min = x;
		this.insert(x.getKey(), x.getValue());
		return valtoreturn;
	}
	else if (t.empty()||!t.root.isRealNode()) {
		// t is empty. we can just insert x to this
		this.insert(x.getKey(), x.getValue());
		return valtoreturn;
	
	}else if (this.empty()) {
		// tree is empty. we can just insert x to t and set tree.root <--- t.root
		this.root = t.root;
		this.max = t.max;
		this.min = t.min;
		this.insert(x.getKey(), x.getValue());
		return valtoreturn;
	}
	// t && tree are not empty
	
	// lets check which tree should be on which side
	AVLTree Rtree;
	AVLTree Ltree;
	if (this.getRoot().getKey()>x.getKey()) {
		Rtree = this;
		Ltree = t;
	}else {
		Ltree = this;
		Rtree = t;
	}
	// now lets check which tree is higher
	int heightdiff = Rtree.getRoot().getHeight() - Ltree.getRoot().getHeight();
	if (heightdiff <= 1 && heightdiff >= -1) {
		//trees are equal in height
		x.setRight(Rtree.getRoot());
		x.setLeft(Ltree.getRoot());
		this.root=x;
		x.update();
		this.max = Rtree.max;
		this.min = Ltree.min;
		return 1;
		
	}else if (heightdiff > 0) {
		//Rtree is taller than Ltree
		IAVLNode temp = null;
		temp = Rtree.root; 
		while (temp.getHeight()>Ltree.getRoot().getHeight()) {
//			if (temp.getLeft().isRealNode()) {
				temp = temp.getLeft();
//			}			
		}
		
		/* set x to be:
		 *  	         temp.parent
		 *	     		/
		 *      	   x
		 *     		 /   \
		 * Ltree.root     temp
		 */

		x.setParent(temp.getParent());
		x.setLeft(Ltree.getRoot());
		x.getLeft().setParent(x);
		x.setRight(temp);
		x.getRight().setParent(x);
		if (x.getParent()!=null) {
			x.getParent().setLeft(x);}
		this.root = Rtree.getRoot();
		x.calcRank();
		this.rebalance(x);
		x.updatePath();
		
		
	}else if(heightdiff < 0){
		//Ltree is taller than Rtree
		IAVLNode temp = null;
		temp = Ltree.root;

		while (temp.getHeight()>Rtree.getRoot().getHeight()) {
//			if (temp.getRight().isRealNode()) {
				temp = temp.getRight();
//			}
		}
		/* set x to be:
		 * temp.parent
		 *	     \
		 *        x
		 *      /   \
		 *  temp      Rtree.root
		 */
		x.setParent(temp.getParent());
		x.setRight(Rtree.getRoot());
		x.setLeft(temp);
		x.getRight().setParent(x);
		x.getLeft().setParent(x);
		if (x.getParent()!=null) {
			x.getParent().setRight(x);}
		this.root = Ltree.getRoot();
		x.calcRank();
		this.rebalance(x);
		x.updatePath();
		
	}
	this.max = Rtree.max;
	this.min = Ltree.min;
	return valtoreturn;
}
		


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

	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // returns node's key (for virtual node return -1)
		public String getValue(); // returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); // sets left child
		public IAVLNode getLeft(); // returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); // sets right child
		public IAVLNode getRight(); // returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); // sets parent
		public IAVLNode getParent(); // returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
		public int getSize(); // returns the size of the node's subtree including the node itself
		public void promote(); // promote rank
		public void demote(); // demote rank
		public int getRank(); // returns node's rank
		public int rankDiffRight(); // returns rank difference between the node and it's right child
		public int rankDiffLeft(); // returns rank difference between the node and it's left child
		public void update(); // update node's height and size
		public void updatePath(); // update the height and size of the path from the the giving node to the root
		public void setSize(int i); //sets the node's new size
		public void calcRank(); //calc node's rank according to it's height
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
		private String val;
		private AVLNode left = null;
		private AVLNode right = null;
		private AVLNode parent = null;
		private int height;
		private int size;
		private int rank;

		public AVLNode(int Key, String Val) {
			this.key = Key;
			this.val = Val;
			this.left = new AVLNode();
			this.right = new AVLNode();
			this.height = 0;
			this.size = 1;
			this.rank = 0;
		}

		public AVLNode() {
			this.key = -1;
			this.val = null;
			this.left = null;
			this.right = null;
			this.height = -1;
			this.size = 0;
			this.rank = -1;
		}

		public int getKey() {
			return this.key; // returns key
		}

		public String getValue() {
			return this.val; // returns value
		}

		public void setLeft(IAVLNode node) {
			this.left = (AVLNode) node; // to be replaced by student code
		}

		public IAVLNode getLeft() {
			if (!this.isRealNode()) {
				return null;
			}
			return this.left; // returns the left child of the node if it has one, or null otherwise
		}

		public void setRight(IAVLNode node) {
			this.right = (AVLNode) node; // to be replaced by student code
		}

		public IAVLNode getRight() {
			if (!this.isRealNode()) {
				return null;
			}
			return this.right; // returns the right child of the node if it has one, or null otherwise
		}

		public void setParent(IAVLNode node) {
			this.parent = (AVLNode) node; // sets the given node as the parent of the current node
		}

		public IAVLNode getParent() {
			return this.parent; // to be replaced by student code
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() {
			return ((this.getKey() != -1) ? true : false); // returns true if the key is positive else returns false
		}

		public void setHeight(int height) {
			this.height = height; // to be replaced by student code
		}

		public int getHeight() {
			return this.height; // to be replaced by student code
		}
		public void calcRank() {
			this.rank = 1 + Math.max(this.left.height, this.right.height);
		}

		public int getSize() {
			return this.size;
		}
		
		public void setSize(int i) {
			this.size=(i);
		}

		public void promote() {
			this.rank++;
		}

		public void demote() {
			this.rank--;
		}

		public int getRank() {
			return this.rank;
		}

		public int rankDiffRight() {
			if (this.isRealNode() == false) {
				return 0;
			}
			return (this.rank - this.right.rank);
		}

		public int rankDiffLeft() {
			if (this.isRealNode() == false) {
				return 0;
			}
			return this.rank - this.left.rank;
		}

		public void update() {
			this.size = 1; // the node itself

			if (this.right.isRealNode()) { // adding right subtree size
				this.size += this.right.size;
			}
			if (this.left.isRealNode()) { // adding left subtree size
				this.size += this.left.size;
			}
			this.height = 1 + Math.max(this.left.height, this.right.height);
		}

		public void updatePath() {
			IAVLNode curr = this;
			while (curr != null) {
				curr.update();
				curr = curr.getParent();
			}
		}

	}

}
