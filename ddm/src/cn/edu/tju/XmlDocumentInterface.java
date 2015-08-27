package cn.edu.tju;

import java.io.IOException;

public interface XmlDocumentInterface {
/**
 * @param filename 
 * @throws Exception 
 */
	public int parserXml(String filename) throws IOException, Exception;
	/**
	 * @param filename class name links
	 */
	public int insertXml(String filenameString, String _class, String name, String[] links );
}
