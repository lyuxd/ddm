package cn.edu.tju;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;



public  class FilePath {
	private final Logger logger = Logger.getLogger(FilePath.class);
	private static String RUNNINGTASK;
	private static String XMLFILE;
	private static String DOWNLOADROOT;
	private static String MODIFIEDLINKFILE;
	private static String DATADIR;
	private static String CHECKER;
	private static String MONITOR;
	
	public static String getMONITOR() {
		return MONITOR;
	}


	public void setMONITOR(String mONITOR) {
		MONITOR = mONITOR;
	}


	public static String getCHECKER() {
		return CHECKER;
	}


	public void setCHECKER(String cHECKER) {
		CHECKER = cHECKER;
	}


	public static String getDATADIR() {
		return DATADIR;
	}


	public void setDATADIR(String dATADIR) {
		DATADIR = dATADIR;
	}


	public static String getMODIFIEDLINKFILE() {
		return MODIFIEDLINKFILE;
	}


	public void setMODIFIEDLINKFILE(String mODIFIEDLINKFILE) {
		MODIFIEDLINKFILE = mODIFIEDLINKFILE;
	}


	public static String getDOWNLOADROOT() {
		return DOWNLOADROOT;
	}


	public  void setDOWNLOADROOT(String dOWNLOADROOT) {
		DOWNLOADROOT = dOWNLOADROOT;
	}


	public static String getXMLFILE() {
		return XMLFILE;
	}


	public void setXMLFILE(String xMLFILE) {
		XMLFILE = xMLFILE;
	}


	public static String getRUNNINGTASK() {
		return RUNNINGTASK;
	}


	public void setRUNNINGTASK(String rUNNINGTASK) {
		RUNNINGTASK = rUNNINGTASK;
	}

	public FilePath() throws IOException, JDOMException {
		String id = null;
		File tmp = new File("");
		String myFatherPath = tmp.getCanonicalPath();
		String confile = myFatherPath+"/../webapps/ddm/dlconf/conf.xml";
		//String confile = myFatherPath+"/conf.xml";
		logger.info("Get conf from : "+confile);
		SAXBuilder buider = new SAXBuilder();
		Document doc = buider.build(new File(confile));
		Element root = doc.getRootElement();
		List<Element> paths = root.getChildren();
		for(Element path : paths){
			id = path.getName();
			if(id.equals("RUNNINGTASK")){
				this.setRUNNINGTASK(path.getText());
			}else if (id.equals("XMLFILE")) {
				this.setXMLFILE(path.getText());
			}else if(id.equals("DOWNLOADROOT")){
				this.setDOWNLOADROOT(path.getText());
			}else if(id.equals("MODIFIEDLINKFILE")){
				this.setMODIFIEDLINKFILE(path.getText());
			}else if(id.equals("DATADIR")){
				this.setDATADIR(path.getText());
			}else if(id.equals("CHECKER")){
				this.setCHECKER(path.getText());
			}else if(id.equals("MONITOR")){
				this.setMONITOR(path.getText());
			}
			
		}
		// TODO Auto-generated constructor stub
	}

}
