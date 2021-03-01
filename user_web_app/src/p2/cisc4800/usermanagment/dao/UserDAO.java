package p2.cisc4800.usermanagment.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.mysql.cj.protocol.Resultset;

import p2.cisc4800.usermanagment.model.User;

//data access object, provides CRUD database operations 
public class UserDAO {
		
		private String url1 = "jdbc:mysql://localhost:3306/test1";
		private String user = "root";
		private String password = "root";
	
			/*//other connection type 
			 * String url2 =
			 * "jdbc:mysql://localhost:3306/test1?user=root&password=root"; conn2 =
			 * DriverManager.getConnection(url2); if (conn2 != null) {
			 * System.out.println("Connected to the database test1-try2"); }
			 */
		//pre created sql statements to be used by the functions as prepared statements: 
		private static final String INSERT_USERS_SQL = "INSERT INTO users" + " (name, email, number, country) VALUES " + " (?, ?, ?, ?);";
		private static final String SELECT_USERS_BY_ID = "SELECT id, name, email, number, country FROM users where id =?";
		private static final String SELECT_ALL_USERS = "SELECT * FROM users";
		private static final String DELETE_USER_SQL = "DELETE from users where id = ?;";
		private static final String UPDATE_USER_SQL = "UPDATE users SET name = ?, email= ?, number=?, country =? WHERE id =?;";
		private static final String INSERT_USER_DELTBL_SQL= "INSERT INTO deleted" + " (id, name, email, number, country) VALUES " + " (?, ?, ?, ?, ?);";
		
		//Database connection function, gets called for each database operation. 
	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver"); //need the proper driver class to connect to ddb
			connection = DriverManager.getConnection(url1, user, password); //
		
		if (connection != null) { //check to make sure connection to database was successful. 
          System.out.println("Connected to the database test1");
		}
		
		}catch (SQLException | ClassNotFoundException ex) {
	        System.out.println("An error occurred. Maybe user/password is invalid");
	        ex.printStackTrace();
		}
		return connection; //returns the connected database object.  
	}
	
	//create or insert a user 
	public void insertUser(User user) { //Receives the data from the form and attaches it to the sql statement to insert the user to the DB. 
		try(Connection connection = getConnection();
				PreparedStatement preparedstatement = connection.prepareStatement(INSERT_USERS_SQL) ){
			preparedstatement.setString(1, user.getName());
			preparedstatement.setString(2, user.getEmail());
			preparedstatement.setString(3, user.getNumber());
			preparedstatement.setString(4, user.getCountry());
			preparedstatement.executeUpdate();
			
			
	} catch (SQLException e) {
		System.out.println("An error occurred with inserting user");
		e.printStackTrace(); 
		}
	}
	
	//update user //Receives the data from the form with the user ID and attaches it to the update sql statement to update the user in the DB.
	public boolean updateUser(User user) { 
		boolean rowUpdated = false;
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_USER_SQL) ){
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getNumber());
			statement.setString(4, user.getCountry());
			statement.setInt(5, user.getId());
	
			rowUpdated = statement.executeUpdate() > 0;
		}
		catch (SQLException e) {
			System.out.println("An error occurred with updating user");
		e.printStackTrace();
		}
	return rowUpdated;
}
	
	//select user by id
	public User selectUserByid(int id) {
		User user = null;
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(SELECT_USERS_BY_ID) ){
					statement.setInt(1, id);
					System.out.println(statement);
					//execute the qry
					ResultSet rs = statement.executeQuery();
					//process the rs object
					while(rs.next()) {
						String name = rs.getString("name");
						String email = rs.getString("email");
						String number = rs.getString("number");
						String country = rs.getString("country");
						user = new User(id, name, email, number, country);
					}
				} catch (SQLException e) {
					System.out.println("An error occurred with selecting user by id");
					e.printStackTrace();
				}
		return user;
	}
	//select user //selects all users in the DB to be shown in the table view.
	public List<User> selectAllUsers() {
		List<User> users = new ArrayList<>();
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS) ){
			System.out.println(statement);
			ResultSet rs = statement.executeQuery();
			//process the rs object
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String number = rs.getString("number");
				String country = rs.getString("country");
				users.add(new User(id, name, email, number, country));
			}
		} catch (SQLException e) {
			System.out.println("An error occurred with selecting all users");
			e.printStackTrace();
		}
		return users;
	}
	//delete user //deletes a user from the database. it receives the id of the users to delete and executes the prepared sql statement 
	public boolean deleteUser(int id) throws SQLException {
		boolean rowDeleted;
		try(Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_USER_SQL) ){
			moveUserToDel(id);
			statement.setInt(1, id);
	
			rowDeleted= statement.executeUpdate() > 0;
		}
		
	return rowDeleted;
	}
	
	public void moveUserToDel(int id) throws SQLException {
		User delUser = selectUserByid(id); // get the user getting deleted 
		try(Connection connection = getConnection(); 
				PreparedStatement statement = connection.prepareStatement(INSERT_USER_DELTBL_SQL) ){
				statement.setInt(1, delUser.getId());
				statement.setString(2, delUser.getName());
				statement.setString(3, delUser.getEmail());
				statement.setString(4, delUser.getNumber());
				statement.setString(5, delUser.getCountry());
				statement.executeUpdate();
				System.out.println("Moved deleted user");
		}
	}
	
}