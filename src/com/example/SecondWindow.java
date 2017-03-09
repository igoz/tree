package com.example;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.*;

public class SecondWindow {
	private JTree tree;
	private JPanel panel1;
	private JLabel title;
	private JButton okBtn;
	private JScrollPane sp;
	private static JFrame frame;

	public SecondWindow() {
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	private DefaultMutableTreeNode buildTree(List<Tuple> wbsData, List<Tuple> actData) {
		Map<Integer, DefaultMutableTreeNode> wbsMap = new TreeMap<>();
		Map<Integer, List<DefaultMutableTreeNode>> wbsMapParent = new TreeMap<>();
		Map<Integer, List<DefaultMutableTreeNode>> actMapParent = new TreeMap<>();
		Map<Integer, Double> actSum = DBWorker.sumActToWbs();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
		wbsMap.put(0, root);

		for (Tuple elem: wbsData) {
			WbsTuple tmp = (WbsTuple) elem;
			DefaultMutableTreeNode node;
			if (actSum.containsKey(tmp.getId())) {
				node = new DefaultMutableTreeNode(tmp.getName() + " (" + actSum.get(tmp.getId()).intValue() + ")");
			} else {
				node = new DefaultMutableTreeNode(tmp.getName());
			}
			wbsMap.put(tmp.getId(), node);
			if (wbsMapParent.containsKey(tmp.getParent_id())) {
				wbsMapParent.get(tmp.getParent_id()).add(node);
			} else {
				List<DefaultMutableTreeNode> list = new ArrayList<>();
				list.add(node);
				wbsMapParent.put(tmp.getParent_id(), list);
			}
		}

		for (Tuple elem: actData) {
			ActivitiesTuple tmp = (ActivitiesTuple) elem;
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(tmp.getName() + " (" + (int) tmp.getQuantity() + ")");
			if (actMapParent.containsKey(tmp.getWbs_id())) {
				actMapParent.get(tmp.getWbs_id()).add(node);
			} else {
				List<DefaultMutableTreeNode> list = new ArrayList<>();
				list.add(node);
				actMapParent.put(tmp.getWbs_id(), list);
			}
		}
		
		for (Map.Entry<Integer, List<DefaultMutableTreeNode>> elem: wbsMapParent.entrySet()) {
			int parentId = elem.getKey();
			List<DefaultMutableTreeNode> list = elem.getValue();
			DefaultMutableTreeNode parentNode = wbsMap.get(parentId);
			for (DefaultMutableTreeNode listElem: list) {
				parentNode.add(listElem);
			}
		}

		for (Map.Entry<Integer, List<DefaultMutableTreeNode>> elem: actMapParent.entrySet()) {
			int parentId = elem.getKey();
			List<DefaultMutableTreeNode> list = elem.getValue();
			DefaultMutableTreeNode parentNode = wbsMap.get(parentId);
			for (DefaultMutableTreeNode listElem: list) {
				parentNode.add(listElem);
			}
		}

		return root;
	}

	public static void show() {
		frame = new JFrame("SecondWindow");
		frame.setContentPane(new SecondWindow().panel1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private void createUIComponents() {
		List<List<Tuple>> data = DBWorker.loadData();
		List<Tuple> wbsData = data.get(0);
		List<Tuple> actData = data.get(1);

		DefaultMutableTreeNode root = buildTree(wbsData, actData);

		tree = new JTree(root);
		tree.setRootVisible(false);
	}
}
