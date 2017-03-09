package com.example;

import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void init(int n, int m) {
		DBWorker.dropTables();
		List<Tuple> wbsData = new ArrayList<>();
		List<Tuple> actData = new ArrayList<>();
		DBWorker.createTables();
		List<List<Tuple>> data = Utils.generateData(n, m);
		for (int i = 0; i < data.size(); i+=2) {
			for (Tuple elem: data.get(i)) {
				wbsData.add(elem);
			}
			for (Tuple elem: data.get(i + 1)) {
				actData.add(elem);
			}
		}
		DBWorker.saveData(wbsData, actData);
	}

	public static void main(String[] args) {
		FirstWindow.show();
	}
}
