package org.openlmis.files.excel;

import lombok.Data;

@Data
public class CellMeta {
	private String name;
	private String display;
	private int column;
	private boolean variable;


	public CellMeta(int column, String name, String display, boolean isVariable){
		this.column = column;
		this.name = name;
		this.display = display;
		this.variable = isVariable;
	}

	@Override
	public String toString() {
		return "Cell{" +
					   "name='" + name + '\'' +
					   ", display='" + display + '\'' +
					   ", column=" + column +
					   ", variable=" + variable +
					   '}';
	}
}
