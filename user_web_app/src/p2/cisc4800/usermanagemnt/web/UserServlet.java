package p2.cisc4800.usermanagemnt.web;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.annotation.WebServlet; 
import java.util.List;

import p2.cisc4800.usermanagment.dao.UserDAO;
import p2.cisc4800.usermanagment.model.User;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/") //this is the servlet class that does all the server work
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private UserDAO userDAO;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
    	this.userDAO = new UserDAO();
  
    }
    /**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);

	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	//Get function checks the url path to call the function for each call. 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getServletPath();
		
		switch(action) {
		case "/new":
			showNewForm(request, response);
			break;
		case "/insert":
			try {
				insertUser(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/delete":
			try {
				deleteUser(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;	
		case "/edit":
			try {
				showEditForm(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "/update":
			updateUser(request, response);
			break;
//		case "/deleted":
//			try {
//				deletedUsersList(request, response);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		default:
			try {
				listUser(request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
//		//not complete function
//		private void deletedUsersList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//			List<User> listUser;
//			
//			
//		}
	
		private void listUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			List<User> listUser = userDAO.selectAllUsers();
			request.setAttribute("listUser", listUser);
			RequestDispatcher dispatcher = request.getRequestDispatcher("user-list.jsp");
			dispatcher.forward(request, response);
		}
		
	
		private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
			dispatcher.forward(request, response);
		}
		//gets the data from the form and sends it to the insertUser() function in the DAO class
		private void insertUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String number = request.getParameter("number");
			String country = request.getParameter("country");
			User newUser = new User(name, email, number, country);
			userDAO.insertUser(newUser);
			response.sendRedirect("list");
		}
		 // gets the id of the user being deleted and passes it to the DAO class delete function. 
		private void deleteUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
			
			int id = Integer.parseInt(request.getParameter("id"));
			userDAO.deleteUser(id);
			response.sendRedirect("list");
		}
		
		//gets the users data and displays it on the user form to be edited. 
		public void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			int id = Integer.parseInt(request.getParameter("id"));
			User existingUser = userDAO.selectUserByid(id);
			RequestDispatcher dispatcher = request.getRequestDispatcher("user-form.jsp");
			request.setAttribute("user", existingUser);
			dispatcher.forward(request, response);
			
		}
		// gets the changed data  from the form and creates a new user object that gets passed to the updateUser() function in the DAO class. 
		public void updateUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
			int id = Integer.parseInt(request.getParameter("id"));
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String number = request.getParameter("number");
			String country = request.getParameter("country");
			User user = new User(id, name, email, number, country);
			userDAO.updateUser(user);
			response.sendRedirect("list");
		}
	

}
