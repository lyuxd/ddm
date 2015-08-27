package cn.edu.tju;




import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class InitServlet
 */
@WebServlet(urlPatterns={"/init"},loadOnStartup=1)
public class InitServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(InitServlet.class);
	private static final long serialVersionUID = 1L;
    public InitServlet() {
    	try {
			new FilePath();
			DomParser parser = new DomParser();
			parser.parserXml(FilePath.getXMLFILE());
			Runtime.getRuntime().exec("chmod 777 "+FilePath.getCHECKER());
			Runtime.getRuntime().exec("chmod 777 "+FilePath.getMONITOR());
			try {
				Thread.sleep(3000);//sleep for 3 sec.
			} catch (InterruptedException e) {
				// TODO: handle exception
				logger.error(e);
				
			}
			String cmd = "nohup /bin/sh "+FilePath.getCHECKER()+" >> /dev/null &";
			Process process = Runtime.getRuntime().exec(cmd);
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String message;
			message = br.toString();
			if(message!=null&&!message.equals("")){
				logger.info("checker message:\n"+message);
				
			}
		} 
 catch (Exception e) {
	
			e.printStackTrace();
		}
    }

}
