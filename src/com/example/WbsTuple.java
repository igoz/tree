package com.example;

public class WbsTuple implements Tuple{
	private int id;
	private String name;
	private int parent_id;

	public WbsTuple(int id, String name, int parent_id) {

		this.id = id;
		this.name = name;
		this.parent_id = parent_id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {

		return id;
	}

	public String getName() {
		return name;
	}

	public int getParent_id() {
		return parent_id;
	}

	@Override
	public String toString() {
		return id + " " + name + " " + parent_id;
	}
}
