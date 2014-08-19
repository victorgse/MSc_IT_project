package dbtools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DatabaseAccess {
	
	/**
	 * instance variables
	 */
	private String query;
	private String update;
	private ResultSet RS;
	
	/**
	 * A helper method for getting the names of datasets stored in the database.
	 * @return the names of datasets stored in the database
	 */
	public ArrayList<String> getNamesOfAvailableDatasets() {
		ArrayList<String> datasetsInDatabase = new ArrayList<String>();
		try {
			String query =  "select tablename "
					+ "from sys.systables "
					+ "where tabletype = 'T'";
			RS = queryDatabase(query);
			while (RS.next()) {
				datasetsInDatabase.add(RS.getString("tablename"));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while querying the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
		return datasetsInDatabase;
	}
	
	/**
	 * Fetches the names of a table's fields.
	 * @return the names of a table's fields
	 */
	public ArrayList<String> getNamesOfFieldsOfTable(String tableName, boolean numericOnly) {
		ArrayList<String> tableFields = new ArrayList<String>();
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
	
	/**
	 * Fetches the identifying names of instances from the Full Dataset.
	 * @param desiredLevelOfAnalysis
	 * @param numInstances
	 * @return the identifying names of instances from the Full Dataset
	 */
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
	
	/**
	 * Deletes a dataset table from the database.
	 * @param nameOfDatasetToDelete
	 */
	public void deleteDatasetFromDatabase(String nameOfDatasetToDelete) {
		update =  "DROP TABLE " + nameOfDatasetToDelete;
		updateDatabase(update);
	}
	
	/**
	 * Executes a custom database query.
	 * @param query
	 * @return the results of the custom database query
	 */
	private ResultSet queryDatabase(String query) {
		ResultSet tempRS = null;
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			tempRS = stmt.executeQuery(query);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while querying the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
		return tempRS;
	}
	
	/**
	 * Executes a custom database update.
	 * @param update
	 */
	private void updateDatabase(String update) {
		try {
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB");
			Statement stmt = con.createStatement();
			stmt.executeUpdate(update);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while updating the database.", 
	    			"Error: SQL Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
}
