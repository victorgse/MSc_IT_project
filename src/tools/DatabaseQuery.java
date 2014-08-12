package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import javax.swing.JOptionPane;

public class DatabaseQuery {
	
	/**
	 * instance variables
	 */
	String query;
	ResultSet RS;
	
	/**
	 * A helper method for getting the names of datasets stored in the database.
	 * @return
	 */ /*
	private TreeSet<String> getNamesOfAvailableDatasets() {
		TreeSet<String> datasetsInDatabase = new TreeSet<String>();
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement();
			String query =  "select tablename "
					+ "from sys.systables "
					+ "where tabletype = 'T'";
			ResultSet RS = stmt.executeQuery(query);
			while (RS.next()) {
				datasetsInDatabase.add(RS.getString("tablename"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Something went wrong when fetching the names of available datasets.");
		}
		return datasetsInDatabase;
	} */
	
	/**
	 * A helper method for fetching a table's schema.
	 * @return
	 */
	public TreeSet<String> getNamesOfFieldsOfTable(String tableName, boolean numericOnly) {
		TreeSet<String> tableFields = new TreeSet<String>();
		query = "select columnname "
				+ "from sys.systables t, sys.syscolumns "
				+ "where TABLEID = REFERENCEID "
				+ "and tablename = '" + tableName + "'";
		if (numericOnly) {
			query += " and CAST(COLUMNDATATYPE AS VARCHAR(128)) = 'NUMERIC(10,2)'";
		}
		RS = queryDatabase(query);
		try {
			while (RS.next()) {
				tableFields.add(RS.getString("columnname"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while querying the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
		return tableFields;
	}
	
	public String[] getNamesOfFullDatasetInstances(int desiredLevelOfAnalysis, int numInstances) {
		String[] namesOfInstances = new String[numInstances];
		if (desiredLevelOfAnalysis == 0) {
			query =  "select * "
					+ "from MCFC_ANALYTICS_FULL_DATASET";
			RS = queryDatabase(query);
		} else if (desiredLevelOfAnalysis == 1) {
			query =  "select DISTINCT player_id, player_forename, player_surname "
					+ "from MCFC_ANALYTICS_FULL_DATASET "
					+ "order by player_id ASC";
			RS = queryDatabase(query);
		} else if (desiredLevelOfAnalysis == 2) {
			query =  "select DISTINCT team "
					+ "from MCFC_ANALYTICS_FULL_DATASET "
					+ "order by team ASC";
			RS = queryDatabase(query);
		}
		try {
			RS.afterLast(); //move to the end of RS
			for (int i = numInstances - 1; i >= 0; i--) {
				RS.previous(); //move to previous result
				if (desiredLevelOfAnalysis == 0) {
					namesOfInstances[i] = RS.getString("date") + " " + RS.getString("player_forename") + " " + RS.getString("player_surname");
				} else if (desiredLevelOfAnalysis == 1) {
					namesOfInstances[i] = RS.getString("player_forename") + " " + RS.getString("player_surname");
				} else if (desiredLevelOfAnalysis == 2) {
					namesOfInstances[i] = RS.getString("team");
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while querying the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
		return namesOfInstances;
	}
	
	private ResultSet queryDatabase(String query) {
		ResultSet RS = null;
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			RS = stmt.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while querying the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
		return RS;
	}
}
