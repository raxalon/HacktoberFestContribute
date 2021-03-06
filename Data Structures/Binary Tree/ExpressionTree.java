/**
 * Class for Expression Tree ADT. Contains recursive methods to build
 * tree from prefix notation, evaluate expression, print expression in
 * infix notation, deep copy, and print tree to console. 
 * 
 * @author Raxalon
 * @version 04/03/2019
 */

import java.io.*;
import java.util.Scanner;

class ExprTree implements Cloneable {
    // Data member
    private TreeNode root;       // Reference to the root node

    // Constructor
    public ExprTree() {
    	clear();
    }
    
    // Copy constructor (deep copy)
    public ExprTree(ExprTree valueTree) {
    	if (valueTree.root != null)
    		this.root = cloneSub(valueTree.root);
    	else
    		clear();
    }
    
    /**
     * Returns deep copy of tree
     * @see java.lang.Object#clone()
     */
    public Object clone() {
    	ExprTree clone = new ExprTree();
    	if (notEmpty())
    		clone.root = cloneSub(this.root);
    	return clone;
    }
    
    // Expression tree manipulation operations
    /**
     * Gets string in prefix notation from console, converts to stream
     * to pass to recursive cloneSub() to create expression tree.
     */
    public void build() throws IOException {
    	Scanner keyboard = new Scanner(System.in);
    	String prefix = keyboard.nextLine(); // gets input from user
    	keyboard.close();
    	
    	byte[] bytes = prefix.getBytes(); // converts string to byte array
    	ByteArrayInputStream stream = new ByteArrayInputStream(bytes); // converts arr to stream
    	
    	this.root = buildSub(stream);
    	if (stream.available() > 0) // if remaining chars in string
    		System.out.println("Error--More Inputs Than Expected");
    }
    
    /**
     * Prints expression in infix notation from tree to screen.
     */
    public void expression() {
    	if (notEmpty()) {
    		expressionSub(this.root.getLeft());
    		System.out.print(this.root.getElement());
    		expressionSub(this.root.getRight());
    	} else // empty tree
    		System.out.println("No Expression Available");
    }
    
    /**
     * Evaluates expression from tree.
     * @return evaluation in float
     */
    public float evaluate() {
    	if (notEmpty()) {
	    	float eval = evaluateSub(this.root);
	    	if (Float.isInfinite(eval)) { // check for division by zero
	    		System.out.print(" Error--Division by Zero");
	    		return 0;
	    	}
	    	return eval;
    	}
    	// empty tree
    	System.out.println("Unable to Evaluate Empty Tree");
    	return 0;
    }
    
    /**
     * Sets root to null, clearing tree.
     */
    public void clear() {
    	this.root = null;
    }

    public void showStructure()
    // Outputs an expression tree. The tree is output rotated counter-
    // clockwise 90 degrees from its conventional orientation using a
    // "reverse" inorder traversal. This operation is intended for testing
    // and debugging purposes only.
    {
        if (root == null)
            System.out.println("Empty tree");
        else
        {
            System.out.println( );
            showSub(root, 1);
            System.out.println( );
        }
    }

    /**
     * Flips tree.
     */
    public void commute() {
    	if (notEmpty())
    		commuteSub(this.root);
    }
    
    // private helper methods
    private void showSub(TreeNode p, int level)
    // Recursive partner of the showStructure() method. Outputs the
    // subtree whose root node is pointed to by p. Parameter level is the
    // level of this node within the expression tree.
    {
        int j;                                      // Loop counter
        TreeNode right,
                 left;                                  

        if ( p != null )
        {
            // For efficiency, calculate right and left only once
            right = p.getRight( );
            left = p.getLeft( );
            
            showSub(right, level+1);                // Output right subtree
            for ( j = 0 ; j < level ; j++ )         // Tab over to level
                System.out.print("\t");
            System.out.print(" " + p.getElement( ));// Output element
            if ( ( left != null ) &&                // Output "connector"
                 ( right != null ) )
                System.out.print("<");
            else if ( right != null )
                System.out.print("/");
            else if ( left != null )
                System.out.print("\\");
            System.out.println( );
            showSub(left, level+1);                 // Output left subtree
        }
    }
    
    /**
	 * Helper for clone(). Creates deep copy of a tree.
	 * @param node initially root of the tree to be copied
	 * @return root of new tree
	 */
	private TreeNode cloneSub(TreeNode node) {
		ExprTreeNode newRoot = new ExprTreeNode(node.getElement(), null, null);
		if (node.getLeft() != null)
			newRoot.setLeft(cloneSub(node.getLeft()));
		if (node.getRight() != null)
			newRoot.setRight(cloneSub(node.getRight()));
		return newRoot;
	}

	/**
     * Helper for build(). Recursively builds tree from input
     * @param  prefixString  The argument initially consists of the prefix notation of an expression.
     *     SIDE-EFFECT:the state of this param will be changed as it is used.
     * @return root of built tree
     */
    private TreeNode buildSub(ByteArrayInputStream prefixString) throws IOException {
    	int readIn = prefixString.read();
    	if (readIn != -1) { // -1 reached end
    		char element = (char) readIn;
    		if (isNumber(element))
    			return new ExprTreeNode(element, null, null); // num will be leaf
    		if (isOperator(element)) { // branch if operator
    			ExprTreeNode node = new ExprTreeNode(element, null, null);
    			node.setLeft(buildSub(prefixString));
    			node.setRight(buildSub(prefixString));
    			return node;
    		}
    		System.out.println("Error--Invalid Input"); // char other than operator or num
    	}
    	System.out.println("Error--Not Enough Operands"); // reached end of string with incomplete tree
    	return null;
    }
    
    /**
     * Helper for expression(). Prints tree LR in-order
     * @param node initially root of tree to be printed
     */
    private void expressionSub(TreeNode node) {
    	if (node.getLeft() != null) { // traverse left
    		System.out.print("(");
    		expressionSub(node.getLeft());
    	}
    	System.out.print(node.getElement()); // print element
    	if (node.getRight() != null) { // traverse right
    		expressionSub(node.getRight());
    		System.out.print(")");
    	}
    }
    
    /**
     * Helper for evaluate(). Performs operations based on node data
     * @param node initially root of tree to be evaluated
     * @return evaluation in float
     */
    private float evaluateSub(TreeNode node) {
    	if (isOperator(node.getElement())) {
	    	if (node.getLeft() != null && node.getRight() != null) {
		    	if (node.getElement() == '+')
		    		return evaluateSub(node.getLeft()) + evaluateSub(node.getRight());
		    	if (node.getElement() == '-')
		    		return evaluateSub(node.getLeft()) - evaluateSub(node.getRight());
		    	if (node.getElement() == '*')
		    		return evaluateSub(node.getLeft()) * evaluateSub(node.getRight());
		    	if (node.getElement() == '/')
		    		return evaluateSub(node.getLeft()) / evaluateSub(node.getRight());
	    	}
	    	return 0;
    	}
    	return (float) node.getElement() - 48; // char to float conversion
    }
    
    /**
     * Helper for commute(). Switches left and right nodes
     * @param node initially root of tree to be commuted
     */
    private void commuteSub(TreeNode node) {
    	TreeNode temp = node.getLeft();
    	node.setLeft(node.getRight()); // set left to right
    	node.setRight(temp); // set right to left
    	
    	if (node.getLeft() != null)
    		commuteSub(node.getLeft());
    	if (node.getRight() != null)
    		commuteSub(node.getRight());
    }

	/**
	 * Helper function that checks for empty tree
	 * @return true if not empty
	 */
	private boolean notEmpty() {
		if (this.root != null)
			return true;
		return false;
	}

	/**
	 * Helper function that checks if a char is a number
	 * @param element
	 * @return true if number
	 */
	private boolean isNumber(char element) {
		if (element >= '0' && element <= '9')
			return true;
		return false;
	}

	/**
	 * Helper function that checks if a char is an operator
	 * @param element
	 * @return true if operator
	 */
	private boolean isOperator(char element) {
		if (element == '+' || element == '-' || element == '*' || element == '/')
			return true;
		return false;
	}
} // class ExprTree
