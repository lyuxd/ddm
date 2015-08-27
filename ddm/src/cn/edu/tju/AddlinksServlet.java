package cn.edu.tju;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class addlinks
 */
@WebServlet("/addlinks")
public class AddlinksServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddlinksServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (request.getSession().getAttribute("account") != null) {
			if(request.getParameter("rToken")!=null && 
					request.getParameter("rToken").toString().equals(request.getSession().getAttribute("sToken").toString())){
				
				String sToken = request.getSession().getAttribute("sToken").toString();
				if(sToken.equals("1")){
					request.getSession().setAttribute("sToken","0");
					}else {
						request.getSession().setAttribute("sToken","1");
				}
			String content = request.getParameter("links");
			String j_name = request.getParameter("name");
			String j_class = request.getParameter("_class");
			//clear the request.attribute.

			if (content == null || j_name == null || content == ""
					|| j_name == "") {
				request.setAttribute("state",
						"Attention: class,name and links should have not empty values.");
				request.setAttribute("statecode", "1");
				
				request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
						.forward(request, response);
			}else if(j_name.contains(" ")){
				request.setAttribute("state",
						"Attention: \"name\" should not contains blank or tab character(s).");
				request.setAttribute("statecode", "1");
				
				request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
						.forward(request, response);
			} else {
				XmlDocumentInterface parser = new DomParser();
				// parser.parserXml(filename);
				//
				String[] urls = content.split("\\s+");
				int insertresult=parser.insertXml(FilePath.getXMLFILE(), j_class, j_name, urls);
				switch (insertresult) {
				case 0:
					request.setAttribute("state", "Adding successfully.");
					request.setAttribute("statecode", "0");
					
					
					request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
							.forward(request, response);
					break;
				case 1:
					request.setAttribute("state", "Failed.");
					request.setAttribute("statecode", "1");
					request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
							.forward(request, response);
					break;
				case 2:
					request.setAttribute("state", "Not add any url.");
					request.setAttribute("statecode", "0");
					request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
							.forward(request, response);
					break;
				default:
					break;
					
				}
				
			}
		}else{
			request.getRequestDispatcher("WEB-INF/jsp/management.jsp")
			.forward(request, response);
		}
			}else {
			//clear the request.attribute.
			request.getRequestDispatcher("WEB-INF/jsp/login.jsp")
			.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
