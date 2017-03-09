package com.example;

public class ActivitiesTuple implements Tuple{
	private int id;
	private String name;
	private double quantity;
	private int wbs_id;

	public ActivitiesTuple(int id, String name, int wbs_id, double quantity) {

		this.id = id;
		this.name = name;
		this.wbs_id = wbs_id;
		this.quantity = quantity;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public int getId() {

		return id;
	}

	public String getName() {
		return name;
	}

	public int getWbs_id() {
		return wbs_id;
	}

	public double getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return id + " " + name + " " + wbs_id + " " + quantity;
	}
}
