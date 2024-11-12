package murach.sql;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

public class SqlGatewayServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sqlStatement = request.getParameter("sqlStatement");
        String sqlResult = "";

        try {
            // Load the driver
            Class.forName("com.mysql.jdbc.Driver");

            // Get a connection
            String dbURL = "jdbc:mysql://mysql-3173170a-student-42c3.d.aivencloud.com:16902/murach";
            String username = "avnadmin";
            String password = "AVNS_C8UpMCoGMf24yWCOax-";
            Connection connection = DriverManager.getConnection(dbURL, username, password);

            // Create a statement
            Statement statement = connection.createStatement();

            // Parse the SQL string
            sqlStatement = sqlStatement.trim();
            if (sqlStatement.length() >= 6) {
                String sqlType = sqlStatement.substring(0, 6);

                // Check if it's a SELECT statement
                if (sqlType.equalsIgnoreCase("select")) {
                    // Execute query and create the HTML for the result set
                    ResultSet resultSet = statement.executeQuery(sqlStatement);
                    sqlResult = SqlUtil.getHtmlTable(resultSet);
                    resultSet.close();
                } else {
                    // Execute update for INSERT, UPDATE, DELETE, or DDL statements
                    int i = statement.executeUpdate(sqlStatement);

                    if (i == 0) {
                        // A DDL statement (e.g., CREATE, DROP, etc.)
                        sqlResult = "<p>The statement executed successfully.</p>";
                    } else {
                        // An INSERT, UPDATE, or DELETE statement
                        sqlResult = "<p>The statement executed successfully.<br>" +
                                i + " row(s) affected.</p>";
                    }
                }
            }

            // Close the statement and connection
            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            sqlResult = "<p>Error: The MySQL driver was not found.</p>";
            e.printStackTrace();
        } catch (SQLException e) {
            sqlResult = "<p>Error executing the SQL statement: " + e.getMessage() + "</p>";
            e.printStackTrace();
        }

        // Store the result and the SQL statement in the request attributes
        request.setAttribute("sqlResult", sqlResult);
        request.setAttribute("sqlStatement", sqlStatement);

        // Forward the request and response to the JSP page
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
