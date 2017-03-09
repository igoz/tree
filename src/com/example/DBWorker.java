package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBWorker {
	private static String url = "jdbc:sqlserver://ИГОРЬ-ПК\\IBICONSERVER:1433;databaseName=wbs_and_activities;";
	private static String user = "sa";
	private static String pass = "20121994";

	public static void dropTables() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try(Connection conn = DriverManager.getConnection(url, user, pass);
				Statement statement = conn.createStatement()) {
			String sql = "DROP TABLE wbs";
			statement.execute(sql);
			sql = "DROP TABLE activities";
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTables() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try(Connection conn = DriverManager.getConnection(url, user, pass);
				Statement statement = conn.createStatement()) {

			String sql = "CREATE TABLE wbs " +
					"(id INTEGER not NULL PRIMARY KEY IDENTITY(1, 1), " +
					" name VARCHAR(255) not NULL, " +
					" parent_id INTEGER, " +
					")";
			statement.executeUpdate(sql);

			sql = "CREATE TABLE activities " +
					"(id INTEGER not NULL PRIMARY KEY IDENTITY(1, 1), " +
					" name VARCHAR(255) not NULL, " +
					" wbs_id INTEGER, " +
					" quantity NUMERIC(16, 6) not NULL " +
					")";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveData(List<Tuple> wbsTupleList, List<Tuple> activitiesTupleList) {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try(Connection conn = DriverManager.getConnection(url, user, pass);
				Statement statement = conn.createStatement()) {

			String sqlI = "Set Identity_Insert wbs On";
			statement.execute(sqlI);

			for (int i = 0; i < wbsTupleList.size(); i++) {
				WbsTuple elem = (WbsTuple) wbsTupleList.get(i);
				String sql = "INSERT INTO wbs (id, name, parent_id) " +
						"VALUES(" + elem.getId() +
						", '" + elem.getName() + "'" +
						", " + (elem.getParent_id() == 0 ? "NULL": elem.getParent_id()) +
						")";
				statement.addBatch(sql);
			}
			statement.executeBatch();

			sqlI = "Set Identity_Insert wbs Off";
			statement.execute(sqlI);

			statement.clearBatch();

			sqlI = "Set Identity_Insert activities On";
			statement.execute(sqlI);

			for (int i = 0; i < activitiesTupleList.size(); i++) {
				ActivitiesTuple elem = (ActivitiesTuple) activitiesTupleList.get(i);
				String sql = "INSERT INTO activities (id, name, wbs_id, quantity) " +
						"VALUES(" + elem.getId() +
						", '" + elem.getName() + "'" +
						", " + elem.getWbs_id() +
						", " + elem.getQuantity() +
						")";
				statement.addBatch(sql);
			}
			statement.executeBatch();

			sqlI = "Set Identity_Insert activities Off";
			statement.execute(sqlI);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<List<Tuple>> loadData() {
		List<Tuple> wbsData = new ArrayList<>();
		List<Tuple> actData = new ArrayList<>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try(Connection conn = DriverManager.getConnection(url, user, pass);
				Statement statement = conn.createStatement()) {
			String sql = "SELECT * FROM wbs";
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				int parentId = rs.getInt("parent_id");
				if (rs.wasNull()) {
					parentId = 0;
				}
				wbsData.add(new WbsTuple(id, name, parentId));
			}
			rs.close();

			sql = "SELECT * FROM activities";
			ResultSet rss = statement.executeQuery(sql);

			while (rss.next()) {
				int id = rss.getInt("id");
				String name = rss.getString("name");
				int wbs_id = rss.getInt("wbs_id");
				double quantity = rss.getDouble("quantity");
				actData.add(new ActivitiesTuple(id, name, wbs_id, quantity));
			}
			rss.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<List<Tuple>> result = new ArrayList<>();
		result.add(wbsData);
		result.add(actData);

		return result;
	}

	public static Map<Integer, Double> sumActToWbs() {
		Map<Integer, Double> result = new HashMap<>();

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try(Connection conn = DriverManager.getConnection(url, user, pass);
				Statement statement = conn.createStatement()) {

			String sql = "SELECT wbs.id, SUM(quantity) AS sumQnt" +
					" FROM wbs JOIN activities ON wbs.id = activities.wbs_id" +
					" GROUP BY wbs.id";
			ResultSet rs1 = statement.executeQuery(sql);

			while (rs1.next()) {
				result.put(rs1.getInt("id"), rs1.getDouble("sumQnt"));
			}

			sql = "SELECT wbs.id, SUM(sumQnt) AS sumWbs\n" +
					"FROM wbs JOIN (\n" +
					"SELECT wbs.id, wbs.parent_id, SUM(quantity) AS sumQnt\n" +
					"FROM wbs JOIN activities ON wbs.id = activities.wbs_id\n" +
					"GROUP BY wbs.id, wbs.parent_id) AS rs ON wbs.id = rs.parent_id\n" +
					"group by wbs.id";
			ResultSet rs2 = statement.executeQuery(sql);

			while (rs2.next()) {
				result.put(rs2.getInt("id"), rs2.getDouble("sumWbs"));
			}

			sql = "SELECT wbs.id, SUM(sumWbs) AS sumWbs\n" +
					"FROM wbs JOIN(\n" +
					"SELECT wbs.id, wbs.parent_id, SUM(sumQnt) AS sumWbs\n" +
					"FROM wbs JOIN (\n" +
					"SELECT wbs.id, wbs.parent_id, SUM(quantity) AS sumQnt\n" +
					"FROM wbs JOIN activities ON wbs.id = activities.wbs_id\n" +
					"GROUP BY wbs.id, wbs.parent_id) AS rs ON wbs.id = rs.parent_id\n" +
					"GROUP BY wbs.id, wbs.parent_id) AS rs ON wbs.id = rs.parent_id\n" +
					"GROUP BY wbs.id";
			ResultSet rs3 = statement.executeQuery(sql);

			while (rs3.next()) {
				result.put(rs3.getInt("id"), rs3.getDouble("sumWbs"));
			}

			rs1.close();
			rs2.close();
			rs3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}
