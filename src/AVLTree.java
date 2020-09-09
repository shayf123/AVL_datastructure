import java.util.Arrays;



/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	IAVLNode root;  //A pointer to the root of the tree
	int n; 			//This is the number of items in the tree
	IAVLNode min;   //A pointer to the node with the minimum key in the tree
	IAVLNode max;   //A pointer to the node with the maximum key in the tree
	
	public static int joinCounter;
	public static int counting;
	public static int maxJoin;


	//This is the only constructor of an AVLTree. It initializes the pointers to null, and number of elements to 0.
	// - O(1) time Complexity
	public AVLTree() {
		this.root = null;
		this.min = null;
		this.max = null;
		this.n = 0;

	}
	//this is the constructor which get an IAVLNode node and build an AVLTree according to his fields.
	//we use this constructor only in split method which will be explained on later 
	public AVLTree(IAVLNode node) {
		if (!node.isRealNode()) {
			this.root=null;
			this.n = 0; 
			this.min = null;
			this.max = null;
		}
		else {
			node.setParent(null);
			this.root = node;
			this.n = node.getSize();
		}
	}
		
	
	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 *
	 */

	//This method return true iff the tree is empty: true iff root == null. 
	//- O(1) time Complexity
	public boolean empty() {
		return this.root == null;
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */

	//This method searches for a node with a given key k. If exists in tree - return the value(info) of the node.
	//If the key k is not found in the tree - returns null
	//Time complexity is O(log n) , as the search is implemented as search on a search tree
	public String search(int k){
		return search(k,this.root);
	}

	//Auxiliary method for the search method. This is the recursive function that actually searches for a node with key k
	//Time complexity is O(log n). Over each level of tree(O(log n) levels) it visits one node and does O(1) computations.
	public String search(int k,IAVLNode node) {//Auxiliary method
		if (node == null) {
			return null;
		}
		if (node.getKey() == k) { //found the key - return the value of the node
			return node.getValue();
		}
		if(node.getKey() > k) { //need to search on right sub tree
			return search(k,node.getLeft());
		}
		if (node.getKey() < k) { //need to search on left sub tree
			return search(k,node.getRight());
		}
		return null; // THIS IS NEVER REACHED
	}


	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * returns -1 if an item with key k already exists in the tree.
	 */

	/*This is the insertion function to a tree. It receives a key k and a value string i
	If there is a node with key k - returns -1. If there is no node with key k - inserts the node to tree,
	After insertion, maintains a BST, and maintains AVL tree by rebalancing the tree.
	Time Complexity is O(log n). There is a final number of methods done in this function, each is at worse case O(log n).
	Each part of the function is documented and the complexity of it is written near part.
	Returned Value is the number of rebalancing operations needed: demotes,promotes, left-rotates, right-rotates, and double rotates(counted as 2).
	Time Complexity is O(log n). There is a final number of methods done in this function, each is at worse case O(log n).

	 */
	public int insert(int k, String i) {
		//Some required pointers.
		AVLNode z = new AVLNode(new AVLNode(), new AVLNode(), null ,k, i);
		IAVLNode newnode =  new AVLNode(new AVLNode(), new AVLNode(), null ,k, i);
		IAVLNode x = this.getRoot();
		IAVLNode y = x;

		//If the tree is empty then there are no keys in it. Here we add the first node in the tree.
		//Time Complexity O(1)
		if(this.empty()) {
			this.root = newnode;
			this.min = newnode;
			this.max = newnode;
			//We create a node with size = 1 by default so no need to set size here
			this.n++;
			return 0;
		}


		//Searches if the key k is already in the tree. If there is a node with key k - returns -1
		//Time Complexity O(log n) as explained in the search method
		if (search(k) != null) 
			return -1; 

		// Here we look where to insert - by making a binary search on the tree - Time Complexity is O(log n)
		while (x.isRealNode()) {
			x.setSize(x.getSize()+1);
			y = x;//keep a pointer to previous x
			if (x.getKey()>k)//go to left sub-tree
				x = x.getLeft();
			else {//go to right sub-tree
				x = x.getRight();
			}
		}

		// In this part we actually insert to the tree. Insertion is only done to an unary node or a leaf.
		//y is the parent that we insert to his left or right side
		int oldRank = y.getHeight(); //Leaf - 0 or Unary - 1

		// Insertion into an unary node - Complexity is O(log n) as we have to update the min/max attributes.
		if(oldRank == 1) { 
			newnode.setParent(y);//change the parent pointer of the newnode to y
			if (y.getKey() > newnode.getKey())
				y.setLeft(newnode);//change the left pointer of y to newnode
			else
				y.setRight(newnode);//change the right pointer of y to newnode
			this.n++;
			this.updateMax();
			this.updateMin();
			return 0;
		}

		//Insertion to a leaf. This might cause unbalanced tree. After insertion, rebalancing function takes action.
		//This is else: oldRank == 0.
		newnode.setParent(y);//change the parent pointer of the newnode to y
		if (y.getKey() > newnode.getKey()) {
			y.setLeft(newnode);//change the left pointer of y to newnode
		}
		else {
			y.setRight(newnode);//change the right pointer of y to newnode
		}

		this.n++;

		//rebalance stage: we need to check y rank-difference and rebalncing it if needed 
		//all rank-difference options of the parent after insertion of new node to his left or right

		//rebalance function takes at worst case - O(log n) time.
		int finalresult = rebalance(y, 0);

		// Updating minimum after insertion and rebalance - takes O(log n) time complexity
		this.updateMin();

		//Updating maximum after insertion and rebalance - takes O(log n) time complexity
		this.updateMax();

		return finalresult;


	}

	/* This function updates the minimum attribute. Time complexity is O(log n). 
	 * The function goes from the root to the left side of the tree as much as possible.
	 * Therefore time complexity is similar to the height of the tree which is O(log n) */
	public void updateMin() {
		IAVLNode cur = this.root;
		if(cur == null)
			return;
		while(cur.getLeft().isRealNode())
			cur = cur.getLeft();
		this.min = cur;
	}

	/*
	 * This function updates the maximum attribute. Time complexity is O(log n).
	 * The function goes from the root to the right side of the tree as much as possible
	 * Therefore time complexity is similar to the height of tree which is O(log n).
	 */
	public void updateMax() {
		IAVLNode cur = this.root;
		if(cur == null)
			return;
		while(cur.getRight().isRealNode())
			cur = cur.getRight();
		this.max = cur;
	}

	/* This is the rebalancing function after an insertion.
	 * This function handles and corrects all un-balanced cases of the tree.
	 * This function follows the powerpoint and the lecture notes strictly.
	 * In this function there are several actions: promotions, demotions, left rotates and right rotates.
	 * Each of these actions take a constant time O(1) as they require a finite number of operations.
	 * Complexity of the function at worst-case is O(log n) as it might be required to visit every level of the tree and rebalance it.
	 * At each level, we rebalance the tree with O(1) time operations,
	 * and therefore the total complexity of the function is similar to the height of tree.
	 * Total Complexity: O(log n)
	 * Returned Value is the number of rebalancing operations needed: demotes,promotes, left-rotates, right-rotates, and double rotates(counted as 2).

	 */
	public int rebalance(IAVLNode y, int counter) {

		// All differences that might be
		int []difference1_1 = {1,1};
		int []difference0_1 = {0,1};
		int []difference1_0 = {1,0};
		int []difference0_2 = {0,2};
		int []difference2_0 = {2,0};
		int []difference2_1 = {2,1};
		int []difference1_2 = {1,2};

		//Here we calculate the difference of the given node y - Time Complexity O(1)
		int[] diff = RankDifference(y);

		// This is a final case - no need to rebalance anymore
		if(Arrays.equals(diff,difference1_1)) 
			return counter;

		//All cases that need to be rebalanced are down.

		//Case 1 in the power-point. This is promotion thats rolls the problem up
		// Time Complexity O(log n) as the problem might roll up
		if(Arrays.equals(diff,difference0_1) || Arrays.equals(diff,difference1_0)) { // CASE 1
			if(y == this.root) {
				y.setHeight(y.getHeight()+1);//promote y
				counter = counter+ 1;
				return counter;
			}

			y.setHeight(y.getHeight()+1); //promote y
			counter = counter + 1;
			return rebalance(y.getParent(), counter);
		}

		// ALL OPTIONS FOR CASE 2: Y is Z in the power-point p26
		// Time Complexity is O(1) as no additional actions are required

		// Case 2 left - we need right rotate (exactly like in powerpoint)
		if(Arrays.equals(diff,difference0_2) ) {
			int[] diff_left = RankDifference(y.getLeft()); // Difference of left kid
			if (Arrays.equals(diff_left,difference1_2)) {
				RightRotate(y);
				y.setHeight(y.getHeight()-1);
				counter += 2; // For rotation and demote
				return counter;
			}

		}

		// Case 2 right - we need left rotate - symmetric case
		if(Arrays.equals(diff,difference2_0)) {
			int[] diff_right = RankDifference(y.getRight()); // Difference of right kid
			if ( Arrays.equals(diff_right,difference2_1)) {
				LeftRotate(y);
				y.setHeight(y.getHeight()-1);
				counter += 2;
				return counter;
			}
		}


		// ALL OPTIONS FOR CASE 3
		// Time Complexity is O(1) - only a finite number of operations is needed

		IAVLNode b = y.getLeft().getRight();
		if(Arrays.equals(diff,difference0_2)) { // This is the case in the powerpoint p27
			int[] diff_left = RankDifference(y.getLeft()); // Difference of left kid
			if (Arrays.equals(diff_left,difference2_1)) {
				LeftRotate(y.getLeft());
				RightRotate(y);			
				b.setHeight(b.getHeight()+1);
				b.getLeft().setHeight(b.getLeft().getHeight()-1);
				b.getRight().setHeight(b.getRight().getHeight()-1);
				counter += 5;
				return counter;
			}
		}

		//Symmetric case 3
		IAVLNode a = y.getRight().getLeft();
		if(Arrays.equals(diff,difference2_0) ) { // This is the symmetric case 3
			int[] diff_right = RankDifference(y.getRight()); // Difference of right kid
			if (Arrays.equals(diff_right,difference1_2)) {
				RightRotate(y.getRight());
				LeftRotate(y);
				a.setHeight(a.getHeight()+1);
				a.getLeft().setHeight(a.getLeft().getHeight()-1);
				a.getRight().setHeight(a.getRight().getHeight()-1);
				counter += 5;
				return counter;
			}
		}

		return counter;//we never get here
	}

	/*
	 * This is a function that calculates the rank difference of a given node. It is not meant for null or virtual nodes.
	 * It computates the rank difference between himself and his left side, and between himself and his right side
	 * It returns an array in the form of: [left difference, right difference]
	 * Time Complexity is O(1) constant as only a finite number of operations is needed
	 */
	public static int [] RankDifference(IAVLNode node){//not meant for null or virtual nodes 
		int []rankdifference = new int[2];
		int leftdifference = node.getHeight()-node.getLeft().getHeight();
		int rightdifference = node.getHeight()-node.getRight().getHeight();
		rankdifference[0] = leftdifference;
		rankdifference[1] = rightdifference;
		return rankdifference;
	}

	/*
	 * This is the right rotation function. This function receives a node y and rotates it with his left element x.
	 * Time Complexity is O(1) as there is a constant number of operations that each take O(1).
	 */
	public void RightRotate(IAVLNode y) {
		IAVLNode x = y.getLeft();
		IAVLNode b = x.getRight();
		y.setLeft(b);
		b.setParent(y);
		if(y.getParent() == null) {
			this.root = x;
			x.setParent(y.getParent());
		}
		else {
			if(y == y.getParent().getLeft()) {
				y.getParent().setLeft(x);
				x.setParent(y.getParent());
			}
			else if(y == y.getParent().getRight()) {
				y.getParent().setRight(x);
				x.setParent(y.getParent());
			}
		}
		x.setRight(y);
		y.setParent(x);

		y.setSize(y.getLeft().getSize() + y.getRight().getSize() + 1);
		x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);

	}

	/*
	 * This is the left rotation function. This function receives a node x and rotates it with his right element y.
	 * Time Complexity is O(1) as there is a constant number of operations that each take O(1).
	 */
	public void LeftRotate(IAVLNode x) {//this method receive only real nodes
		IAVLNode y = x.getRight();//y is the right child of x: we want to rotate between x and y 
		x.setRight(y.getLeft());//change the right pointer of x to left sub-tree of y
		y.getLeft().setParent(x);//change the parent pointer of y.left to x
		if (x.getParent()==null) {//x is the root-->change y to be the root after lerft_rotate
			this.root = y;
			y.setParent(x.getParent());
		}
		else if (x==x.getParent().getLeft()) {//x is left child of it's parent
			x.getParent().setLeft(y);//change the left pointer of x.parent to y
			y.setParent(x.getParent());
		}
		else {//x is the right child of it's parent-->change the pointer of x.parent to y
			x.getParent().setRight(y);
			y.setParent(x.getParent());
		}
		y.setLeft(x);//change left pointer of y to x
		x.setParent(y);

		x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
		y.setSize(y.getLeft().getSize() + y.getRight().getSize() + 1);
	}

	/*
	 * This function does a demoting route of the size of each node from the given node to the root.
	 * This function receives a node and demotes its size. It goes upwards like this till the root and demotes the size of each node.
	 * Time Complexity is O(log n) at the worst case if it is need to pass over each level of the tree and demote the node.
	 */
	public void demoteSize(IAVLNode successParent) {

		while(successParent.getParent() != null) {
			successParent.setSize(successParent.getSize() - 1);
			successParent = successParent.getParent();
		}
		//For the root - one last demote 
		successParent.setSize(successParent.getSize() - 1);
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 */

	/* This is the deletion function from the tree. The function receives a key k and deletes the node with the key k.
	 * If the there is no such node(with key k) it returns -1. This might end in O(log n) time complexity.
	 * The deletion process is identical to the process that was taught at the lectures and is in the power-point.
	 * The deletion of a node takes O(log n) complexity time. The search takes O(log n). The deletion itself is also O(log n).
	 * After a successful deletion of the node, a set of rebalancing operations might be needed.
	 * The rebalancing operations also take up-to O(log n) complexity time.
	 * The total time complexity of a deletion process is O(log n).
	 * Returned Value is the number of rebalancing operations needed: demotes,promotes, left-rotates, right-rotates, and double rotates(counted as 2).
	 */
	public int delete(int k)
	{
		// Searches for a node with a key k. If there is no such node, returns -1.
		// This part takes O(log n) as explained in the search function
		if(this.search(k) == null) // K is not in the tree
			return -1;

		IAVLNode y = this.getRoot();
		IAVLNode z = y;

		// This is a special case where the tree has only a root and we delete it. This part is O(1).
		if(n==1 &&this.getRoot().getKey()==k) {
			this.root=null;
			this.min = null;
			this.max = null;
			this.n=0;
			return 0;
		}

		// This part is the search for the node with key k. If we are here, there exists such node.
		// This part takes up to O(log n) time complexity as we might pass through each level of the tree.
		while (y.isRealNode() && y.getKey() != k) { // Looking for where we want to delete
			z = y;//keep a pointer to previous y

			if (y.getKey()>k)//go to left sub-tree
				y = y.getLeft();
			else {//go to right sub-tree
				y = y.getRight();
			}
		} //After this part, z is the parent of y.

		// Maybe we need to change with successor..

		// If y(the node we want to delete) has 2 kids we need to swap with successor. WC is O(log n).
		if(y.getRight().isRealNode() && y.getLeft().isRealNode()) { 
			IAVLNode tmp = y.getRight();
			while(tmp.getLeft().isRealNode())
				tmp = tmp.getLeft();

			// tmp is the successor, tmp is leaf or unary right node!
			//In order to prevent pointer problems we make new node copy of tmp.
			IAVLNode replace;
			if (tmp.getKey()==y.getRight().getKey()) {//in case that tmp(successor) is the right child of y(the node we delete)
				replace = new AVLNode ((AVLNode)tmp.getRight(), (AVLNode)y.getLeft() ,(AVLNode)y.getParent(), tmp.getKey(), tmp.getValue());
				replace.setHeight(y.getHeight());//maintain the rank of the deleted node
				// Size???
				replace.setSize(y.getSize()-1);
				y.getLeft().setParent(replace);
				tmp.getRight().setParent(replace);
				if(y.getParent() == null) { // y is the root
					this.root = replace;
				}
				else { // y is not the root
					if(y == y.getParent().getLeft()) // if y is the left kid of his parent
						y.getParent().setLeft(replace);
					if(y == y.getParent().getRight()) // if y is the right kid of his parent
						y.getParent().setRight(replace);
				}

				this.n = this.n - 1;
				if(replace.getParent() != null) // If we delete root no need to change sizes...
					this.demoteSize(replace.getParent());

				int finalresult = rebalancedelete(replace, 0);
				this.updateMax();
				this.updateMin();
				return finalresult;
			}
			else { // Successor is not right kid, but somewhere left
				replace = new AVLNode((AVLNode)y.getRight(), (AVLNode)y.getLeft() ,(AVLNode)y.getParent(), tmp.getKey(), tmp.getValue());
				replace.setHeight(y.getHeight());
				replace.setSize(y.getSize());
				// HERE WE CHANGE THE POINTERS TO THE TMP/REPLACE NODE!
				y.getLeft().setParent(replace);
				y.getRight().setParent(replace);
				// NOW WE CHANGED TMP WITH Y
				// WE NEED TO DELETE TMP NOW!!!
				IAVLNode tmpParent = tmp.getParent();
				if(tmp.getRight().isRealNode()) { // tmp is Unary
					tmp.getParent().setLeft(tmp.getRight());
					tmp.getRight().setParent(tmpParent);
					tmp.setRight(null);

				}
				else { // tmp is leaf
					tmp.getParent().setLeft(new AVLNode());
					tmp.setParent(null);
				}
				if(y.getParent() == null) { // y is the root
					this.root = replace;
				}
				else { // y is not the root
					if(y == y.getParent().getLeft()) // if y is the left kid of his parent
						y.getParent().setLeft(replace);
					if(y == y.getParent().getRight()) // if y is the right kid of his parent
						y.getParent().setRight(replace);
				}

				this.n = this.n - 1;
				this.demoteSize(tmpParent);
				int finalresult = rebalancedelete(tmpParent, 0);
				this.updateMin();
				this.updateMax();
				return finalresult;

			}		
		}
		else { // y is unary or leaf himself. no need to change with successor
			//WE NEED TO DELETE Y NOW!
			IAVLNode yParent = y.getParent();

			//The case when the deleted node y is a right-unary node
			if(y.getRight().isRealNode()) {

				if(y == this.root) { // Special case we delete the root
					this.root = y.getRight();
					y.getRight().setParent(null);
					y.setRight(null);
					this.n = this.n-1;
					int finalresult = rebalancedelete(this.root, 0); // Not to send a null, the father of y..
					this.updateMin();
					this.updateMax();
					return finalresult; 
				}
				else { // Normal case we delete someone unary not root
					if(y == y.getParent().getLeft()) // if y is the left kid of his parent
						y.getParent().setLeft(y.getRight());
					if(y == y.getParent().getRight()) // if y is the right kid of his parent
						y.getParent().setRight(y.getRight());
					y.getRight().setParent(yParent);
					y.setRight(null);
				}

			} // The case when the delted node y is a left-unary node
			else if(y.getLeft().isRealNode()) {
				if (y==this.root) {
					this.root = y.getLeft();
					y.getLeft().setParent(null);
					y.setRight(null);
					this.n = this.n-1;
					int finalresult = rebalancedelete(this.root, 0); // Not to send a null, the father of y..
					this.updateMin();
					this.updateMax();
					return finalresult; 
				}
				else {// Normal case we delete someone unary not root
					if(y == y.getParent().getLeft()) // if y is the left kid of his parent
						y.getParent().setLeft(y.getLeft());
					if(y == y.getParent().getRight()) // if y is the right kid of his parent
						y.getParent().setRight(y.getLeft());
					y.getLeft().setParent(yParent);
					y.setLeft(null);
				}
			}
			else { // The case where the deleted node y is a leaf
				if(y == y.getParent().getLeft()) {// if y is the left kid of his parent
					y.getParent().setLeft(new AVLNode());
				}
				if(y == y.getParent().getRight()) {// if y is the right kid of his parent
					y.getParent().setRight(new AVLNode());
				}
			}
			if(yParent != null) //if y was a root and we deleted
				this.demoteSize(yParent);
			this.n = this.n - 1;
			int[] diff = RankDifference(z);
			int []difference2_1 = {2,1};
			int []difference1_2 = {1,2};

			if(Arrays.equals(diff,difference1_2) || Arrays.equals(diff,difference2_1)) { // Left Case of powerpoint p36 and 37
				this.updateMin();
				this.updateMax();	
				return 0;
			}
			int finalresult = rebalancedelete(z, 0);
			this.updateMin();
			this.updateMax();
			return finalresult;
		}

	}

	/*
	 * This is the rebalancing function after a deletion from the tree. This function is recursive.
	 * This function deals with ALL cases that were taught in class and that are in the power-point.
	 * The returned value is the number of rebalancing operations that were needed:
	 * The operations include promotes, demotes, left-rotates, right-rotates, and double-rotates(which are counter as 2).
	 * The complexity is O(log n) as the rebalancing might be need at each and every level of the tree.
	 * The time of each rebalance on a specific level is O(1) as a finite number of operations is needed.
	 * Therefore the total time complexity at the worst case might the height of the tree - O(log n)
	 */
	public int rebalancedelete(IAVLNode z, int counter) {
		//z is the parent of deleted item like in the powerpoint
		// These arrays are the way we check the current's node rank differences. 
		int []difference1_1 = {1,1};
		int []difference0_1 = {0,1};
		int []difference1_0 = {1,0};
		int []difference0_2 = {0,2};
		int []difference2_0 = {2,0};
		int []difference2_1 = {2,1};
		int []difference1_2 = {1,2};

		int []difference2_2 = {2,2};
		int []difference3_1 = {3,1};
		int []difference1_3 = {1,3};

		int[] diff = RankDifference(z);

		// Deleting a leaf case
		//Rank difference of 2,2 - problem is moved up the tree
		if(Arrays.equals(diff,difference2_2 )) { //Powerpoint page 36 middle and page 37 middle
			z.setHeight(z.getHeight()-1);
			counter = counter +1;
			if(z == this.root)
				return counter;
			return rebalancedelete(z.getParent(), counter);
		}
		//Rank Difference of 3,1 - some cases may occur
		if(Arrays.equals(diff, difference3_1)) { //Like in the power-point
			// y is the right kid of z
			IAVLNode y = z.getRight();
			int[] diffY = RankDifference(y);
			//Rank difference of right child is 1,1 - Problem is fixed here and no need to move up-wards the tree
			if(Arrays.equals(diffY, difference1_1)) { // Terminating case of child is 1,1. THIS IS CASE 2 of page 40 pp
				LeftRotate(z);
				z.setHeight(z.getHeight() -1);
				y.setHeight(y.getHeight() + 1);
				counter += 3;
				return counter;
			}
			//Rank difference of right child is 2,1 - Problem is rolled upwards the tree
			if(Arrays.equals(diffY, difference2_1)) { // Case 3 page 41
				LeftRotate(z);
				z.setHeight(z.getHeight() - 2);
				counter = counter + 3;
				if(z.getParent() == this.root)
					return counter;
				return rebalancedelete(z.getParent().getParent(), counter); //z.parent!=root->z.parent.parent!=null
			}
			//Rank difference of right child is 1,2 - Problem is rolled upwards the tree
			if(Arrays.equals(diffY, difference1_2)) { // Case 4 page 42
				RightRotate(y);
				LeftRotate(z);

				z.setHeight(z.getHeight() - 2);
				y.setHeight(y.getHeight() - 1);
				y.getParent().setHeight(y.getParent().getHeight() + 1);
				counter += 6;
				if(z.getParent() == this.root)
					return counter;
				return rebalancedelete(z.getParent().getParent(),counter);
			}

		}
		//The symmetric case - Rank difference of current node is 1,3 - some different cases may occur
		if(Arrays.equals(diff, difference1_3)) { // The symmetric case of the powerpoint
			// y is the left kid of z
			IAVLNode y = z.getLeft();
			int[] diffY = RankDifference(y);
			//Rank difference of left child is 1,1 - rebalancing is fixed here. no need for additional rebalancing.
			if(Arrays.equals(diffY, difference1_1)) { // Symmetric case of case 2 page 40
				RightRotate(z);
				z.setHeight(z.getHeight() -1);
				y.setHeight(y.getHeight() + 1);
				counter += 3;
				return counter;
			}
			//Rank difference of left kid is 1,2 - Some rebalancing is done and then the problem is moved up-wards the tree.
			if(Arrays.equals(diffY, difference1_2)) { //Symmetric case 3 page 41
				RightRotate(z);
				z.setHeight(z.getHeight() - 2);
				counter = counter + 3;
				if(z.getParent() == this.root)
					return counter;
				return rebalancedelete(z.getParent().getParent(),counter);
			}
			//Rank difference of left kid is 2,1 - Some rebalancing is done and then the problem is moved up-wards the tree.
			if(Arrays.equals(diffY, difference2_1)) { //Symmetric case 4 page 42
				LeftRotate(y);
				RightRotate(z);

				z.setHeight(z.getHeight() - 2);
				y.setHeight(y.getHeight() - 1);
				y.getParent().setHeight(y.getParent().getHeight() + 1);
				counter += 6; //Total of 6 operations were needed
				if(z.getParent() == this.root)
					return counter;
				return rebalancedelete(z.getParent().getParent(),counter);

			}

		}

		return counter;
	}



	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 */

	/*
	 * This function returns the minimum node's value
	 * Time Complexity is O(1) as we maintain throughout all operations this pointer.
	 * If the tree is empty then no minimum exists, therefore returns null.
	 */
	public String min() {
		if(this.min == null) //Empty tree
			return null;
		return this.min.getValue();

	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 */

	/*
	 * This function returns the maximum node's value
	 * Time Complexity is O(1) as we maintain throughout all operations this pointer.
	 * If the tree is empty then no maximum exists, therefore returns null.
	 */
	public String max() {
		if(this.max == null) //Empty tree
			return null;
		return this.max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 */

	/*
	 * This function returns a sorted array of all keys in the tree. This function calls a recursive function that does all work.
	 * The recursive function follows an in-order traversal of the tree.
	 * Time Complexity is O(n) as it is needed to pass through each and every node of the tree.
	 * At each node through out the traversal the operation take O(1) as the only thing needed is adding the key to the array.
	 * Therefore time complexity is like the number of nodes in the tree - O(n) total time complexity.
	 */
	public int[] keysToArray(){
		int[] arr = new int[this.n];
		if(this.empty())
			return arr; // n is the number of items

		keysToArray(this.root, arr, 0);
		return arr;
	}
	/*
	 * This is the recursive function that actually builds a sorted array of all keys in the tree.
	 * The function function receives a node, the array built until the point, and then index where to add the next key.
	 * The function returns the index so the next calls will add the keys to the right position in the array.
	 * Total complexity is O(n) as it is needed to visit each and every node of the array.
	 */
	public int keysToArray(IAVLNode node, int[] arr, int index) {
		if(!node.isRealNode())
			return index;
		if(node.getHeight() == 0) {
			arr[index] = node.getKey();
			index++;
			return index;
		}

		index = keysToArray(node.getLeft(), arr,index);
		arr[index] = node.getKey();
		index ++;
		index = keysToArray(node.getRight(), arr,index);

		return index;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 */

	/*
	 * This function returns a sorted array of all info values in the tree. This function calls a recursive function that does all work.
	 * The recursive function follows an in-order traversal of the tree.
	 * Time Complexity is O(n) as it is needed to pass through each and every node of the tree.
	 * At each node through out the traversal the operation take O(1) as the only thing needed is adding the info to the array.
	 * Therefore time complexity is like the number of nodes in the tree - O(n) total time complexity.
	 * The returned array is an array of all info-values that are sorted actually by the keys.
	 */
	public String[] infoToArray()
	{
		String[] arr = new String[this.n];
		if(this.empty())
			return arr; // n is the number of items

		infoToArray(this.root, arr, 0);
		return arr;
	}
	
	/*
	 * This is the recursive function that actually builds a sorted array of all value of nodes in the tree.
	 * The function function receives a node, the array built until the point, and then index where to add the next key.
	 * After each insertion into the array, the index is incremented so the next insertion to the array will be in the next position.
	 * The function returns the index so the next calls will add the keys to the right position in the array.
	 * Total complexity is O(n) as it is needed to visit each and every node of the array.
	 * The returned array is an array of all info-values that are sorted actually by the keys.
	 */
	public int infoToArray(IAVLNode node, String[] arr, int index) {
		if(!node.isRealNode())
			return index;
		if(node.getHeight() == 0) {
			arr[index] = node.getValue();
			index++;
			return index;
		}

		index = infoToArray(node.getLeft(), arr,index);
		arr[index] = node.getValue();
		index ++;
		index = infoToArray(node.getRight(), arr,index);

		return index;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 */
	//return the size of the tree in O(1) time.
	public int size()
	{
		return this.root.getSize();
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 */
	//return the root of the tree in O(1) time.
	public IAVLNode getRoot()
	{
		return this.root;
	}
	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. 
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null
	 * postcondition: none
	 */   
	/*
	 * this is split method. it get as argument an int x which is the key we split the tree according to by the precondition that
	 * a node with x key is in the tree and returns an array of this 2 trees: the first with keys<x and the second with keys>x.
	 * first of all we search the node with x key we will mark that node as y- it cost in w.c O(logn).
	 * afterwards we will initials 2 trees : 
	 * the first is the tree which is left sub-tree of y we will mark him as low
	 * the second is the tree which is right sub-tree of y we will mark him as high
	 * now will follow the algorithm we have seen in class:
	 * in while loop we check if y is a right son ,if so we will join(by join method) the left sub tree of y.parent with low and save the result as low
	 * if y is left child ,if so we will join(by join method) the right sub tree of y.parent with high and save the result as high.
	 * in each iteration we promote y to be y.parent until y==root.
	 * by the algorithm we saw in the class the w.c time complexity is O(logn).
	 */
	public AVLTree[] split(int x)
	{
		
		//SPECIAL CASE - SPLIT BY ROOT
		if(this.root.getKey() == x) {
			AVLTree high = new AVLTree(this.root.getRight());
			high.updateMax();
			high.updateMin();
			
			AVLTree low = new AVLTree(this.root.getLeft());
			low.updateMax();
			low.updateMin();
			
			AVLTree[] ret = new AVLTree[2];
			ret[0] = low;
			ret[1] = high;
			return ret;
		}
		
		IAVLNode y = this.root;
		while (y.getKey() != x) { //Looking for the node with key x
			if (y.getKey()> x)//go to left sub-tree
				y = y.getLeft();
			else//go to right sub-tree
				y = y.getRight();	
		}

		//Y is the node with key x
		
		AVLTree low = new AVLTree(y.getLeft());
		if (!low.empty()) {
			low.updateMax();
			low.updateMin();
		}
		
		AVLTree high = new AVLTree(y.getRight());
		if (!high.empty()) {
			high.updateMax();
			high.updateMin();
		}
		IAVLNode prev = y;
		while(y.getParent() != null) {
			prev = y;
			IAVLNode yParent = y.getParent();
			if(y == y.getParent().getRight()) {		
				AVLTree LowParent = new AVLTree(yParent.getLeft());
				IAVLNode tmp = new AVLNode((AVLNode)yParent.getLeft(),(AVLNode)yParent.getRight(),null,yParent.getKey(),yParent.getValue());
				int res = low.join(tmp, LowParent); //CHANGE AFTER
				if(res > this.maxJoin) // DELETE AFTER
					maxJoin = res; // DELETE AFTER
				this.joinCounter += res; //DELETE AFTER
				this.counting ++;
				
			}
			if(y == y.getParent().getLeft()) {
				AVLTree HighParent = new AVLTree(yParent.getRight());
				IAVLNode tmp = new AVLNode((AVLNode)yParent.getLeft(),(AVLNode)yParent.getRight(),null,yParent.getKey(),yParent.getValue());
				int res = high.join(tmp, HighParent); //DELETE AFTER
				if(res > this.maxJoin) //DELETER AFTER
					maxJoin = res; //DELETE AFTER
				this.joinCounter += res; //DELETE AFTER
				this.counting++;
				
			}
			y = y.getParent();
		}
		// Y is now the root;
		// This is the LAST JOIN NEEDED
		if(prev == y.getLeft()) {
			AVLTree HighParent = new AVLTree(y.getRight());
			int res = high.join(y, HighParent); //CHANGE AFTER
			if(res > this.maxJoin) // DELETE AFTER
				maxJoin = res; // DELETE AFTER
			this.joinCounter += res; //DELETE AFTER
			this.counting++;
		}
		
		if(prev == y.getRight()) {
			AVLTree LowParent = new AVLTree(y.getLeft());
			int res = low.join(y, LowParent); //CHANGE AFTER
			if(res > this.maxJoin) //DELETE AFTER
				maxJoin = res; //DELETE AFTER
			this.joinCounter += res; //DELETE AFTER
			this.counting++;
		}
		if(high.getRoot() != null)
			high.getRoot().setSize(high.getRoot().getLeft().getSize() + high.getRoot().getRight().getSize()+1);
		if(low.getRoot() != null)
			low.getRoot().setSize(low.getRoot().getLeft().getSize() + low.getRoot().getRight().getSize() + 1);
		high.updateMax();
		high.updateMin();
		low.updateMax();
		low.updateMin();
		AVLTree[] ret = new AVLTree[2];
		ret[0] = low;
		ret[1] = high;
		
		return ret; 
	}
	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. 	
	 * Returns the complexity of the operation (rank difference between the tree and t)
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys()
	 * postcondition: none
	 */   
	/*this is Join method it receives IAVLNode x, AVLTree t and return the rank difference between t1 and this plus 1
	 * this method send the arguments to one of two functions joinleft or joinright according to keys and rank of the trees
	 * each method  joinleft and joinright are operate in w.c complexty of O(logn) therefore this method operate in w.c complexty of O(logn)
	 * we will detail joinleft and joinright afterwards
	 */
	public int join(IAVLNode x, AVLTree t)
	{
		//Special case - both trees are empty
		
		if(this.empty() && t.empty()) {
			this.root = x;
			this.min = x;
			this.max = x;
			this.n = 1;
			return 1;
		}
		
		if (this.empty()) {
			int k = t.getRoot().getHeight();
			t.insert(x.getKey(), x.getValue());
			this.root = t.root;
			this.n = t.n; 
			this.min = t.min;
			this.max = t.max;
			return k+2;
			
		}
		if (t.empty()) {
			int k = this.getRoot().getHeight();
			this.insert(x.getKey(), x.getValue());
			return k+2;
			
		}

		//The case where the heights are equal. It is needed to connect both trees to the node x as children.
		//It is needed to check which tree has bigger keys and in the way decide the order of children.
		if(this.root.getHeight() == t.getRoot().getHeight()) {
			//Case 3 with equal heights

			if(this.root.getKey() < t.getRoot().getKey()) {	
				int new_n = this.n + t.n + 1;

				x.setLeft(this.root);
				this.root.setParent(x);

				x.setRight(t.root);
				t.root.setParent(x);
				x.setSize(this.root.getSize() + t.root.getSize() + 1);
				x.setHeight(this.root.getHeight() + 1);
				this.root = x;
				this.n = new_n;
				this.updateMin();
				this.updateMax();

				//this.root.setSize(new_n);
				//this.root.setHeight(this.root.getLeft().getHeight() + 1);
				return 1;
			}

			//Case 4 with equal heights
			if(this.root.getKey() > t.getRoot().getKey()) {
				int new_n = this.n + t.n + 1;

				x.setLeft(t.root);
				t.root.setParent(x);

				x.setRight(this.root);
				this.root.setParent(x);

				x.setSize(this.root.getSize() + t.root.getSize() + 1);
				x.setHeight(this.root.getHeight() + 1);

				this.root = x;
				this.n = new_n;
				this.updateMin();
				this.updateMax();

				//this.root.setSize(new_n);
				//this.root.setHeight(this.root.getLeft().getHeight() + 1);
				return 1;
			}
		}

		//Case 1: 
		if(this.root.getKey() > t.root.getKey() && this.getRoot().getHeight() >= t.getRoot().getHeight()) {
			int val = joinLeft(t, x, this).getJoinedResult();
			this.updateMin();
			this.updateMax();
			return val;
		}

		//Case 3:
		if(this.root.getKey() < t.root.getKey() && this.getRoot().getHeight() <= t.getRoot().getHeight()) {
			AVLTree tmp = new AVLTree();
			tmp.n = this.n;
			tmp.min = this.min;
			tmp.max = this.max;
			tmp.root = this.root;

			this.root = t.root;
			this.n = t.n;
			this.min = t.min;
			this.max = t.max;



			int val = joinLeft(tmp, x, this).getJoinedResult();
			this.updateMin();
			this.updateMax();
			return val;
		}

		//Case 2:
		if(this.root.getKey() < t.root.getKey() && this.getRoot().getHeight() >= t.getRoot().getHeight()) {
			int val = joinRight(this, x, t).getJoinedResult();
			this.updateMin();
			this.updateMax();
			return val;
		}
		//Case 4:
		if(this.root.getKey() > t.root.getKey() && this.getRoot().getHeight() <= t.getRoot().getHeight()) {	
			AVLTree tmp = new AVLTree();
			tmp.n = this.n;
			tmp.min = this.min;
			tmp.max = this.max;
			tmp.root = this.root;

			this.root = t.root;
			this.n = t.n;
			this.min = t.min;
			this.max = t.max;


			int val = joinRight(this,x,tmp).getJoinedResult();
			this.updateMin();
			this.updateMax();
			return val;
		}
		return 0; // not reached
	}
	
	/*
	 * this is joinright method it receives it's arguments from join method in this order : t1.keys < x < t2.keys && rank(t1) >= rank(t2)
	 * the method search on external right path in t1 tree until it find the node with rank(mark as b)<= t2.root.rank this action cost logn in w.c
	 * Afterwards it connect b as the left child of x and t2.root as the right child of x and make x to right child of b.parent
	 * Afterwards it update the sizes of the ancestors of b.parent up to the root by updateSizes method which will detail later
	 * updateSizes operate in w.c complexty of O(logn).
	 * Afterwards it checks if the the tree t1 is need rebalance ,if so the it cost  w.c complexty of O(logn).
	 * the method also check if the rankdifference of b.parent is 2,0 ,if so we will make a left rotate and send to rebalance
	 * the method returns : rank(t1)-rank(t2)+1
	 */
	
	// t1.keys < x < t2.keys && rank(t1) >= rank(t2)
	public IAVLNode joinRight(AVLTree t1, IAVLNode x, AVLTree t2) {
		//This is t1
		if(t2.empty()) {
			int k1 = t1.getRoot().getHeight();
			// k1 = -1 in the forum
			t1.insert(x.getKey(), x.getValue());
			t1.root.setJoinedResult(k1 + 2);
			return t1.root;
		}


		//t1 not empty and t2 not empty
		IAVLNode b = t1.root;
		IAVLNode a = t2.root;
		int k = t2.root.getHeight();
		while(b.getHeight() > k) {
			b = b.getRight();
		}
		// Now b is k-rank
		if(b.getParent() == null) { // b is the the root of t1
			int new_n = t1.n + t2.n+1;
			int rankT1 = t1.getRoot().getHeight();
			int rankT2 = t2.getRoot().getHeight();
			x.setLeft(b);
			b.setParent(x);

			x.setRight(a);
			a.setParent(x);

			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			x.setHeight(k+1);
			x.setJoinedResult(rankT1 - rankT2 + 1);
			this.n = new_n;
			this.root.setSize(new_n);

			return x;
		}
		// b is not root, might be k or k-1 rank
		else {
			// we need to check the case that t1 has 2 items - c and b. And t2 has only a
			int rankT1 = t1.getRoot().getHeight();
			int rankT2 = t2.getRoot().getHeight();

			IAVLNode c = b.getParent();

			x.setLeft(b);
			b.setParent(x);

			x.setRight(a);
			a.setParent(x);

			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			x.setHeight(k+1);

			x.setParent(c);
			c.setRight(x);

			c.setSize(c.getLeft().getSize() + c.getRight().getSize() + 1);
			updateSizes(c.getParent());

			int diffCX = c.getHeight() - x.getHeight();
			if(diffCX == 0) { // c = k+1 rank, x = k+1 rank --> need to rebalance
				if (c.getHeight()-c.getLeft().getHeight() == 2) {
					LeftRotate(c);
					rebalance(c.getParent(),0);
					t1.root.setJoinedResult(rankT1 - rankT2 + 1);
					return t1.root;
				}
				//No need problem of 2,0 and 1,1
				rebalance(c,0);
				t1.root.setJoinedResult(rankT1 - rankT2 + 1);
				return t1.root;
			}
			if(diffCX == 1) {
				t1.root.setJoinedResult(rankT1 - rankT2 + 1);
				return t1.root;
			}

		}
		return null; // not reached
	}

	/*
	 * this is joinright method it receives it's arguments from join method in this order : t1.keys < x < t2.keys && rank(t1) <= rank(t2)
	 * it works symmetrically to joinright but with symmetric cases which means:
	 * the method search on external left path in t2 tree until it find the node with rank(mark as b)<= t1.root.rank this action cost logn in w.c
	 *  Afterwards it connect b as the right child of x and t1.root as the left child of x and make x to left child of b.parent
	 *  Afterwards it update the sizes of the ancestors of b.parent up to the root by updateSizes method which will detail later.
	 *  updateSizes operate in w.c complexty of O(logn).
	 *  the method also check if the rankdifference of b.parent is 0,2 ,if so we will make a right rotate and send to rebalance
	 *  the method returns : rank(t2)-rank(t1)+1
	 */
	
	
	// t1.keys < x < t2.keys && rank(t2) >= rank(t1) 
	public IAVLNode joinLeft(AVLTree t1, IAVLNode x, AVLTree t2) {

		if(t1.empty()) {
			int k2 = t2.getRoot().getHeight();
			// k1 = -1 in the forum
			t2.insert(x.getKey(), x.getValue()); // x will be minimal
			t2.root.setJoinedResult(k2 + 2);
			return t2.root;
		} 

		IAVLNode b = t2.root;
		IAVLNode a = t1.root;
		int k = t1.root.getHeight();
		while(b.getHeight() > k) {
			b = b.getLeft();
		}
		// Now b is k-rank
		// WE can maybe delete here because here heights are equal
		if(b.getParent() == null) { // be is the the root of t2
			int rankT1 = t1.getRoot().getHeight();
			int rankT2 = t2.getRoot().getHeight();
			x.setLeft(a);
			a.setParent(x);

			x.setRight(b);
			b.setParent(x);

			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			x.setHeight(k+1);
			x.setJoinedResult(rankT2 - rankT1 + 1);

			return x;
		}
		// b is not root, might be k or k-1 rank
		else {
			// we need to check the case that t2 has 2 items - c and b. And t1 has only a
			int rankT1 = t1.getRoot().getHeight();
			int rankT2 = t2.getRoot().getHeight();

			IAVLNode c = b.getParent();

			x.setLeft(a);
			a.setParent(x);

			x.setRight(b);
			b.setParent(x);

			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			x.setHeight(k+1);

			x.setParent(c);
			c.setLeft(x);

			c.setSize(c.getLeft().getSize() + c.getRight().getSize() + 1);
			updateSizes(c.getParent());

			int diffCX = c.getHeight() - x.getHeight();
			if(diffCX == 0) { // c = k+1 rank, x = k+1 rank --> need to rebalance
				if (c.getHeight()-c.getRight().getHeight() == 2) {
					RightRotate(c);
					rebalance(c.getParent(),0);
					t2.root.setJoinedResult(rankT2 - rankT1 + 1);
					return t2.root;				
				}
				rebalance(c,0);
				t2.root.setJoinedResult(rankT2 - rankT1 + 1);
				return t2.root;
			}
			if(diffCX == 1) {
				t2.root.setJoinedResult(rankT2 - rankT1 + 1);
				return t2.root;
			}
		}


		return null; // not reached
	}
	/*this method update the sizes of the ancestors of the node argument including him by going in the path up to the root. 
	 * the w.c complexity is O(logn) because the path from the node up to the root is bounded by the height of the tree which is O(logn)
	 */
	public void updateSizes(IAVLNode node) {//we will send the parent of c
		while(node!= null) {
			node.setSize(node.getLeft().getSize() + node.getRight().getSize() + 1);
			node=node.getParent();
		}
	}


	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtuval node return -1)
		public String getValue(); //returns node's value [info] (for virtuval node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

		// Methods Added to the interface that were not included in the original interface
		public void setSize(int size); //sets the size of the node
		public int getSize(); //Returns the size of the node

		public void setJoinedResult(int res); //sets the joinedResult attribute
		public int getJoinedResult(); //Returns the joinedResult attribute
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in 
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode{
		AVLNode right;    //element in the right of this node
		AVLNode left;     //element in the left of this node
		AVLNode parent;   //element that is the parent of this node
		int key;          //the key of the node
		int rank;         //the rank of the node
		String info;      //the info about the node
		int size;         //the size of the node(including himself)
		int joinedResult; //attribute that is used in the join function. This attribute for inner use only

		//Contractor for virtual leafs --> don't have a key to insert as an argument
		public AVLNode() {  
			this.right = null;
			this.left = null;
			this.parent = null;
			this.key = -1;
			this.rank = -1;
			this.info = null;
			this.size = 0; // Size of virtual node is 0
		}

		//The constructor for real nodes
		public AVLNode(AVLNode right, AVLNode left, AVLNode parent,int key, String info){
			this.right = right;
			this.left = left;
			this.parent = parent;
			this.key = key;
			this.info = info;
			this.size = 1;
		}

		/*
		 * From Here goes all Getters/Setters functions
		 * All of them work at O(1) time complexity
		 */

		//Getter for key - O(1) time complexity
		public int getKey(){
			return this.key;
		}

		//Getter for value(info attribute) - O(1) time complexity
		public String getValue(){
			return this.info;
		}

		//Setter for left element - O(1) time complexity
		public void setLeft(IAVLNode node){
			this.left = (AVLNode)node;
		}

		//Getter for left element - O(1) time complexity
		public IAVLNode getLeft(){
			return this.left;
		}

		//Setter for right element - O(1) time complexity
		public void setRight(IAVLNode node){
			this.right = (AVLNode)node;
		}

		//Getter for right element - O(1) time complexity
		public IAVLNode getRight(){
			return this.right;
		}

		//Setter for element above - O(1) time complexity
		public void setParent(IAVLNode node){
			this.parent = (AVLNode)node;
		}

		//Getter for element above - O(1) time complexity
		public IAVLNode getParent(){
			return this.parent;
		}

		//This function return true iff the node is a real node. It returns false iff the node is a virtual leaf. - O(1) time complexity
		public boolean isRealNode(){
			return this.rank != -1;
		}

		// In AVL trees rank = height

		//Setter for Height(Rank) attribute - O(1) time complexity
		public void setHeight(int height){
			this.rank = height;
		}

		//Getter for Height(=Rank) attribute - O(1) time complexity
		public int getHeight(){ 
			return this.rank;
		}

		//Setter for size attribute - O(1) time complexity
		public void setSize(int size) {
			this.size = size;
		}

		//Getter for size attribute - O(1) time complexity
		public int getSize() {
			return this.size;
		}

		//Next functions are inner functions that are used in the join function.

		//Setter for joinedResult attribute - O(1) time complexity
		public void setJoinedResult(int res) {
			this.joinedResult = res;
		}

		//Getter for joinedResult attribute - O(1) time complexity
		public int getJoinedResult() {
			return this.joinedResult;
		}


	}

}


