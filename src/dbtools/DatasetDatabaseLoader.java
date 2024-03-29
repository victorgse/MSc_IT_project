package dbtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Reads an .xls file dataset and inputs it into the embedded Apache Derby database.
 * Makes use of the Apache POI library for reading the .xls file.
 */
public class DatasetDatabaseLoader {
	
	/**
	 * instance variables
	 */
	private FileInputStream fis; //an input stream for reading data from a file
	private HSSFWorkbook wb; //an excel workbook
	private HSSFSheet ws; //an excel worksheet
	private int rowNum; //number of rows of data in dataset
	private int colNum; //number of columns of data in dataset
	private String[][] data; //String matrix for storing the dataset in primary memory
	private String[] dataTypes; //String array for storing the data types of the fields
	private String datasetName; //the name of the new dataset
	
	/**
	 * @return the datasetName
	 */
	public String getNameOfDataset() {
		return datasetName;
	}
	
	/**
	 * @param excel
	 * @return whether the .xls dataset was inserted successfully into the application's database
	 */
	public boolean insertDatasetIntoDatabase(File excel) {
		boolean datasetSuccessfullyInsertedIntoDatabase = false;
		boolean datasetReadSuccessfully = readDataFromExcelFile(excel);
		if (datasetReadSuccessfully) {
			datasetSuccessfullyInsertedIntoDatabase = insertDataIntoDatabase();
		}
		return datasetSuccessfullyInsertedIntoDatabase;
	}

	
	/**
	 * Method that reads an .xls file and inserts its content it into the matrix data.
	 * ===============================================================================
	 * The code for this method was developed with the help
	 * of the following tutorial provided by Oresoft:
	 * https://www.youtube.com/watch?v=GYZzkid7nno
	 * @param excel
	 * @return whether the Excel file was successfully read into primary memory
	 */
	private boolean readDataFromExcelFile(File excel) {
		boolean datasetReadSuccessfully = false;
		try {
			fis = new FileInputStream(excel);
			wb  = new HSSFWorkbook(fis);
			ws = wb.getSheetAt(0);
			rowNum = ws.getLastRowNum() + 1;
			colNum = ws.getRow(0).getLastCellNum();
			data = new String[rowNum][colNum];
			dataTypes = new String[colNum];
			datasetName = wb.getSheetName(0).toUpperCase().replace(" ", "_");
			for (int i = 0; i < rowNum; i++) {
				HSSFRow row = ws.getRow(i);
				for (int j = 0; j < colNum; j++) {
					HSSFCell cell = row.getCell(j);
					int type = cell.getCellType();
					switch (type) {
						case 0:
							dataTypes[j] = "numeric(10, 2)";
							break;
						case 1:
							dataTypes[j] = "varchar(30)";
							break;
						default:
							dataTypes[j] = "varchar(30)";
					}
					String value = cellToString(cell);
					data[i][j] = value;
				}
			}
			datasetReadSuccessfully = true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to read the .xls file.", 
	    			"Error: Dataset Not Loaded Into Database", JOptionPane.ERROR_MESSAGE);
		}
		return datasetReadSuccessfully;
	}
	
	/**
	 * Method that inserts the data that was read from
	 * the .xls dataset into the application's database.
	 * @return whether the dataset was successfully inserted into the application's database
	 */
	private boolean insertDataIntoDatabase() {
		boolean dataSuccessfullyInsertedIntoDatabase = false;
		try {
			//establish connection with the database
			Connection con = DriverManager.getConnection("jdbc:derby:datasetsDB;create=true");
			Statement stmt = con.createStatement();
			
			/*
			 * creates a new table in the database and inserts its schema;
			 * the new table is named after the excel worksheet that contains the dataset;
			 * empty spaces in the fields of the schema are replaced by underscores
			 */
			String schema = "create table " + datasetName + "(";
			for (int i = 0; i < colNum - 1; i++) {
				schema += data[0][i].replace(' ', '_').replace('-', '_') + " " + dataTypes[i] + ", ";
			}
			schema += data[0][colNum-1].replace(' ', '_').replace('-', '_') + " " + dataTypes[colNum-1] + ")";
			stmt.executeUpdate(schema);
			
			/*
			 * inserts the individual data records that were taken
			 * from the .xls dataset into the newly created table;
			 * the "'" symbol is used to escape an "'" symbol
			*/
			for (int i = 1; i < rowNum; i++) {
				String rowValues = "insert into " + datasetName + " values(";
				for (int j = 0; j < colNum - 1; j++) {
					if (dataTypes[j].equals("varchar(30)")) {
						rowValues += "'" + data[i][j].replace("'", "''") + "', ";
					} else {
						rowValues += data[i][j].replace("'", "''") + ", ";
					}
					
				}
				if (dataTypes[colNum-1].equals("varchar(30)")) {
					rowValues += "'" + data[i][colNum-1].replace("'", "''") + "', ";
				} else {
					rowValues += data[i][colNum-1].replace("'", "''") + ")";
				}
				stmt.executeUpdate(rowValues);
			}
			dataSuccessfullyInsertedIntoDatabase = true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, 
	    			"Something went wrong while attempting to insert the dataset into the database.", 
	    			"Error: Dataset Not Loaded Into Database", JOptionPane.ERROR_MESSAGE);
		}
		return dataSuccessfullyInsertedIntoDatabase;
	}
	
	/**
	 * Method which gets a cell, reads its content, and
	 * returns a String representation of the cell's content.
	 * @param cell
	 * @return a String representation of the content of the cell
	 */
	private String cellToString(HSSFCell cell) {
		int type; //cell type
		Object cellContent; //object for storing the content of the cell
		
		type = cell.getCellType(); //checks the cell type and assigns it to type
		
		switch (type) {
			case 0: //cell has numeric value
				cellContent = cell.getNumericCellValue(); //assigns the cell's value to cellContent
				break;
			case 1: //cell has nominal value
				cellContent = cell.getStringCellValue(); //assigns the cell's value to cellContent
				break;
			default: //cell's value is not numeric or nominal
				cellContent = ""; //assigns an empty String to cellContent
				//throw new RuntimeException("Cell type not supported!");
		}
		return cellContent.toString(); //return a String representation of the cell's content
	}
	
}
