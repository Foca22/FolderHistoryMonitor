package com.fhm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class SQLiteDB {
    private static Logger logger = LogManager.getLogger(FolderProcess.class);

    Connection connection = null;
    Statement stmt = null;
    private static SQLiteDB instance;


    public static SQLiteDB getInstance() {
        if (instance == null) {
            instance = new SQLiteDB();
        }
        return instance;
    }


    private SQLiteDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:FolderHistoryDB.db");
            logger.info("Connected to the database!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            logger.fatal("Not able to connect to the database!");
        }
        createTable();
    }

    public void createTable() {
        String createSqlTable = "CREATE TABLE IF NOT EXISTS FolderHistory (\n" +
                "File_Name VARCHAR PRIMARY KEY,\n" +
                "File_Extension VARCHAR,\n" +
                "File_Dimension DOUBLE,\n" +
                "File_Modification_Date DOUBLE);";
        try {
            this.stmt = connection.createStatement();
            if (!stmt.isClosed()) {
                stmt.executeUpdate(createSqlTable);
                logger.info("Database table created.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Not able to create a database table!");
        }
    }

    public void addFilesFromFolder(List<FileDetails> fileDetails) {
        refreshTable();
        try {
            this.stmt = connection.createStatement();
            PreparedStatement ps = connection.prepareStatement("INSERT INTO FolderHistory (File_Name, File_Extension, File_Dimension, File_Modification_Date)\n" +
                    "\tVALUES(?, ?, ?, ?)");
            for (FileDetails file : fileDetails) {
                ps.setString(1, file.getFileName());
                ps.setString(2, file.getFileExtension());
                ps.setLong(3, file.getFileSize());
                ps.setLong(4, file.getFileModificationDate());
                ps.addBatch();
            }
            ps.executeBatch();
            listFiles();
            logger.info("Added current files to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Not able to add current files from the folder in the database!");
        }
    }

    public void listFiles() {
        try {
            this.stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM FolderHistory");

            while (rs.next()) {
                String fileName = rs.getString("File_Name");
                String fileExtension = rs.getString("File_Extension");
                double fileDimension = rs.getInt("File_Dimension");
                double fileModificationDate = rs.getInt("File_Modification_Date");

                System.out.println(fileName + " " + fileExtension + " " + fileDimension + " " + fileModificationDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Not able to list the files in the database!");
        }
    }

    public void refreshTable() {
        String refreshTable = "DELETE FROM FolderHistory;";
        try {
            this.stmt = connection.createStatement();
            stmt.executeUpdate(refreshTable);
            logger.info("Database table refreshed.");
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Not able to refresh the database table!");
        }
    }
}
