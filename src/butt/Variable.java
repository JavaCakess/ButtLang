package butt;

import butt.Variable.Type;

public class Variable {

	public enum Type {
		INT("int");

		private String name;
		Type(String name) {
			this.name = name;
		}

		public static Type get(String string) {
			for (Type t : values()) {
				if (t.name.equals(string)) {
					return t;
				}
			}
			return null;
		}
	}

	/** The Name of the variable. **/
	public String name;

	/** The Value of the variable. **/
	public Object value;

	/** The variable type. **/
	public Type type;

	/** The Variable constructor. The first parameter [ Type ] is the type of variable, and the second is the value. **/
	public Variable(String name, Type t) {
		this.type = t;
		if (t == Type.INT) { // Integer
			value = new Integer(0);
		}
		this.name = name;
	}

	public int getInt() {
		return (Integer) value ;
	}

	public void set(String s) {
		Variable var = ButtLang.findVar(s);
		if (var == null) {
			switch (type) {
			case INT:
				value = Integer.parseInt(s);
				break;
			}
			return;
		}
		value = var.get();
	}

	public Object get() {
		if (type == Type.INT) {
			return (Integer) value ;
		}
		return 0;
	}
}
