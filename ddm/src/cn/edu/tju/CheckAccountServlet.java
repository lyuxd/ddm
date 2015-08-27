package cn.edu.tju;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


/**
 * Servlet implementation class CheckAccount
 */
@WebServlet("/login")
public class CheckAccountServlet extends HttpServlet {
	private final Logger log = Logger.getLogger(CheckAccountServlet.class);
	private static final long serialVersionUID = 1L;
	
    /**
     * Default constructor. 
     */
    public CheckAccountServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession();
		Account account = new Account();
		account.setName(request.getParameter("username"));
		account.setPassword(request.getParameter("password"));
		if(( account.getName() != null ) && (account.getName().trim().equals("lxd"))&&(account.getPassword() != null)&&(account.getPassword().trim().equals("25B520"))) {
			int hash = (account.getName()+account.getPassword()).hashCode();
			session.setAttribute("account", hash);
			session.setAttribute("runningtask", FilePath.getRUNNINGTASK());
			session.setAttribute("xmlfile", FilePath.getXMLFILE());
			session.setAttribute("sToken", "0");
			request.getRequestDispatcher("WEB-INF/jsp/management.jsp").forward(request, response);
			//response.sendRedirect("manage.jsp"); //not safe.
		}else{
			session.setAttribute("msg", "Please check your username and password.");
			request.getRequestDispatcher("WEB-INF/jsp/login.jsp")
			.forward(request, response);
			
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);		
	}
}
