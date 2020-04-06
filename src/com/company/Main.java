package com.company;

import java.sql.*;
import java.util.Date;
import java.util.Timer;

// Aufgabe JDBC Chat Mathias Angerer

public class Main {

    public static void main(String[] args) {

        // Variables
        Timer timer = new Timer();
        Send send = new Send();
        timer.schedule(send, 0, 3000);
        Connection connection = null;


        try {
            String url = "jdbc:mysql://localhost:3306/chat?user=root";
            connection = DriverManager.getConnection(url);
            String emptyTable = "DELETE FROM message_center";
            PreparedStatement preparedStatementEmpty = connection.prepareStatement(emptyTable);
            preparedStatementEmpty.executeUpdate();

            int rowCountBefore = 0;
            // Checking user input for exit chat
            while (!send.exitChat()) {

                // If new message written, database gets updated by adding new message
                if (send.newMessage()) {
                    writeInDB(connection, send.getCurrentMessage());
                }
                // Counting messages stored in database, if message count increased by any chat user,
                // new added message will be printed
                String query = "select count(*) from message_center";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                int rowCount = 0;
                while (resultSet.next()) {
                    rowCount = Integer.parseInt(resultSet.getString("count(*)"));
                }
                if (rowCount > rowCountBefore) {
                    readOutDB(connection);
                    rowCountBefore = rowCount;
                }
            }
            timer.cancel();
            System.out.println("chat end");
        } catch (SQLException e) {
            throw new Error("connection problem", e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Methods

    // Writing into table message_center in chat database, date and message_id added by database
    public static void writeInDB(Connection connection, String message) {
        final String SQL_INSERT = "INSERT INTO message_center (message_text, message_sent_from)" +
                "Values (?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, message);
            preparedStatement.setString(2, "user1");
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Printing latest entry from table message_center from database chat
    public static void readOutDB(Connection connection) {
        String query = "SELECT * FROM message_center ORDER BY message_id DESC LIMIT 1";
        try (Statement statementRead = connection.createStatement()) {
            ResultSet resultSet = statementRead.executeQuery(query);
            while (resultSet.next()) {
                String messageText = resultSet.getString("message_text");
                String messageFrom = resultSet.getString("message_sent_from");
                Date date = resultSet.getDate("message_sent_at");
                Time time = resultSet.getTime("message_sent_at");
                System.out.println("\n" + messageText + "\t\t\t(" + messageFrom + " " + date + " " + time + ")");
            }
        } catch (SQLException e) {
            System.out.println("read problem");
        }
    }
}
