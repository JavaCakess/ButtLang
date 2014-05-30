package butt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import butt.Variable.Type;

public class ButtLang {

	public static int MODE = 0; // Normal
	public static boolean program_started = false;
	public static final int MODE_FUNC_DEFINITION = 1;
	public static final int MODE_FUNC_INSIDE = 2;
	
	public static Scanner scan = new Scanner(System.in);
	public static BufferedReader reader;
	
	public static int currln = 0;
	
	public static ArrayList<Function> funcs = new ArrayList<Function>();
	public static ArrayList<String> program = new ArrayList<String>();
	public static Stack<Integer> jump_to = new Stack<Integer>();
	public static ArrayList<Variable> variables = new ArrayList<Variable>();
	
	public static void main(String[] args) {
		String file = inp("ENTER PROGRAM > ");
		try {
			reader = new BufferedReader(new FileReader(file));

			
			/** Load in the program. **/
			String ln;
			while ((ln = reader.readLine()) != null) {
				program.add(ln);
			}
			
			/** Execute the program. **/
			
			for (int i = 0; i < program.size(); i++) {
				currln++;
				String line = program.get(i).trim();
				if (line.equals(">>>")) { // Start of the program.
					program_started = true;
					continue;
				}
				if (MODE == MODE_FUNC_INSIDE) {
					if (line.startsWith("end")) {
						if (program_started) {
							i = jump_to.pop();
							if (jump_to.isEmpty()) {
								MODE = 0;
							}
						}
						continue;
					}
				}
				if (program_started) { // Normal mode.
					if (line.startsWith("call")) {
						String[] ln_args = line.split(" ");
						Function func = findFunc(ln_args[1]);
						jump_to.push(i);
						i = func.start_line - 1;
						MODE = MODE_FUNC_INSIDE;
						continue;
					}
					if (line.startsWith("prn")) {
						
						int prn_mode = 0; // Outside quotes, 1 = outside;
						int var_mode = 0; // Outside variable, 1 = inside;
						String currVarName = "";
						for (int j = 0; j < line.substring(4).length(); j++) {
							char c = line.substring(4).charAt(j);
							if (c == '"') {
								if (prn_mode == 0) prn_mode = 1;
								else 				prn_mode = 0;
								continue;
							}
							if (prn_mode == 1) {
								if (c == '\\') {
									char nextChar = line.substring(4).charAt(j + 1);
									if (nextChar == 'n') {
										prn("\n");
										j++;
										continue;
									}
								}
								prn("" + c);
							} else {
								if (var_mode == 1) {
									if (c != '$')currVarName = currVarName.concat(""+c);
									else {
										prn("" + findVar(currVarName).get());
										currVarName = "";
									}
								}
								if (c == '$') {
									if (var_mode == 0) {
										var_mode = 1;
									} else var_mode = 0;
								}
							}
						}
					}
				}
			
				if (line.startsWith("::")) { // Function definers
					if (MODE == MODE_FUNC_DEFINITION 
						|| MODE == MODE_FUNC_INSIDE) {
						err("Error at line " + currln + ": Cannot declare function inside function", true);
					}
					String[] ln_args = line.substring(2).split(" ");
					/* ARG 0 = FUNCTION NAME
					 * ARG 1... = FUNCTION PARAMETERS
					 */
					funcs.add(new Function(currln, ln_args[0]));
					continue;
				}
				
				if (line.startsWith("$DEF")) { // Defining variables
					String[] ln_args = line.substring(5).split(" ");
					Variable var = new Variable(ln_args[1], Variable.Type.get(ln_args[0]));
					variables.add(var);
					continue;
				}
				
				if (line.startsWith("$")) { // Accessing variables or whatever.
					String[] ln_args = line.substring(1).split(" ");
					Variable var = findVar(ln_args[0]);
					if (ln_args[1].equals("=")) { // Our assignment operator.
						var.set(ln_args[2]);
					}
				}
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
			

	public static Function findFunc(String string) {
		for (Function func : funcs) {
			if (func.name.equals(string)) {
				return func;
			}
		}
		return null;
	}

	public static Variable findVar(String s) {
		for (Variable var : variables) {
			if (var.name.equals(s)) {
				return var;
			}
		}
		return null;
	}
	

	public static Variable parseVar(String d) {
		Variable v = findVar(d);
		if (v != null) {
			return v;
		}
		return new Variable("null", Type.INT);
	}

	public static void err(String s, boolean quit) {
		System.err.print(s);
		if (quit) System.exit(1) ;
	}


	public static void prn(String s) {
		System.out.print(s);
	}
	
	public static String inp(String s) {
		prn(s);
		return scan.nextLine();
	}
	
	
}
