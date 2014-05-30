package butt;

public class Function {
	
	/** Where the function starts in the code. **/
	public int start_line;
	
	/** Function name. **/
	public String name;
	
	/** Function constructor. It requires the start line of the function in the program, and the function name. **/
	public Function(int start_line, String name) {
		this.start_line = start_line;
		this.name = name;
	}
}
