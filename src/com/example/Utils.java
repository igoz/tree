package com.example;

import java.util.*;

public class Utils {
	private static Random rnd = new Random();

	private static List<List<Tuple>> generateTaskTree(int wbs_id_offset, int act_id_offset, int wbsSize,
																										int actSize, int k) {
		Map<Integer, Tuple> wbsTupleMap = new HashMap<>();
		List<Tuple> activitiesTupleList = new ArrayList<>();

		int lvlOneQnt = 1;
		int lvlTwoQnt;
		int lvlThreeQnt;

		int tmplvlTwoQnt = rnd.nextInt(3) + 1;
		lvlTwoQnt = tmplvlTwoQnt > wbsSize - 2 ? 1 : tmplvlTwoQnt;
		lvlThreeQnt = wbsSize - lvlOneQnt - lvlTwoQnt;

		WbsTuple root = new WbsTuple(wbs_id_offset, "WBS " + k, 0);
		wbsTupleMap.put(wbs_id_offset, root);
		wbs_id_offset++;

		List<Integer> idxs = new ArrayList<>();
		for (int i = 1; i <= lvlTwoQnt; i++) {
			wbsTupleMap.put(wbs_id_offset, new WbsTuple(wbs_id_offset, "WBS " + k + "." + i, root.getId()));
			idxs.add(wbs_id_offset);
			wbs_id_offset++;
		}

		if (lvlThreeQnt - idxs.size() > 0) {
			int[] ids = rnd.ints(lvlThreeQnt - idxs.size(), (wbs_id_offset - lvlTwoQnt), wbs_id_offset).toArray();
			for (int i = 0; i < ids.length; i++) {
				idxs.add(ids[i]);
			}
		}

		List<Integer> actIdxs = new ArrayList<>();
		for (int i = 1; i <= lvlThreeQnt; i++) {
			WbsTuple parent = (WbsTuple) wbsTupleMap.get(idxs.get(i - 1));
			wbsTupleMap.put(wbs_id_offset, new WbsTuple(wbs_id_offset, parent.getName() + "." + i, parent.getId()));
			actIdxs.add(wbs_id_offset);
			wbs_id_offset++;
		}

		if (actSize - actIdxs.size() > 0 && actIdxs.size() > 0) {
			int[] actIds = rnd.ints(actSize - actIdxs.size(), (wbs_id_offset - lvlThreeQnt), wbs_id_offset).toArray();
			for (int i = 0; i < actIds.length; i++) {
				actIdxs.add(actIds[i]);
			}
		}

		for (int i = 1; i <= actSize; i++) {
			WbsTuple parent = (WbsTuple) wbsTupleMap.get(actIdxs.get(i - 1));
			String name = parent.getName().substring(4);
			activitiesTupleList.add(new ActivitiesTuple(act_id_offset, "Activity" + name + "." + i,
					parent.getId(), rnd.nextInt(200) + 100));
			act_id_offset++;
		}

		List<List<Tuple>> result = new ArrayList<>();
		List<Tuple> tmp = new ArrayList<>();
		for (Tuple elem: wbsTupleMap.values()) {
			tmp.add(elem);
		}
		result.add(tmp);
		result.add(activitiesTupleList);

		return result;
	}

	public static List<List<Tuple>> generateData(int n, int m) {
		final int TASK_ELEMENT_QNT = 40 + rnd.nextInt(10);
		List<List<Tuple>> result = new ArrayList<>();
		int wbs_offset = 1;
		int act_offset = 1;

		int treeQnt = n % 10 == 0 ? n / 10 : n / 10 + 1;

		int[] wbsQnts = new int[treeQnt];
		int[] actQnts = new int[treeQnt];

		for (int i = 0; i < treeQnt - 1; i++) {
			wbsQnts[i] = 10;
			actQnts[i] = m / treeQnt;
		}
		wbsQnts[treeQnt - 1] = 10 + n - treeQnt * 10;
		actQnts[treeQnt - 1] = m / treeQnt + Math.abs(m - m / treeQnt * treeQnt);
		if (wbsQnts[treeQnt - 1] < 3) {
			wbsQnts[treeQnt - 2] -= 3 - wbsQnts[treeQnt - 1];
			wbsQnts[treeQnt - 1] = 3;
		}

		for (int i = 1; i <= treeQnt; i++) {
			List<List<Tuple>> tmp = (generateTaskTree(wbs_offset, act_offset, wbsQnts[i - 1], actQnts[i - 1], i));
			wbs_offset += wbsQnts[i - 1];
			act_offset += actQnts[i - 1];
			result.add(tmp.get(0));
			result.add(tmp.get(1));
		}

		return result;
	}
}
