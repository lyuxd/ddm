package cn.edu.tju;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Document;

import org.jdom2.input.SAXBuilder;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class DomParser implements XmlDocumentInterface {
	private final Logger log = Logger.getLogger(DomParser.class);
	SAXBuilder buider = new SAXBuilder();
	public Element currentClass = null;
	public String currentClassId = null;
	public String currentDataSetId = null;
	public Element currentDataSet = null;
	boolean ifModified = false;
	boolean newlinks = false;
	Document document = null;
	Element root = null;
	boolean ClassExist = false;
	boolean DatasetExist = false;
	String downloadRootPath = FilePath.getDOWNLOADROOT();
	String dataDir = FilePath.getDATADIR();

	@Override
	public int parserXml(String filename) throws Exception {
		// TODO Auto-generated method stub

		log.info("Start to parse...");
		try {
			File mdflinkfile = new File(FilePath.getMODIFIEDLINKFILE());
			log.info(mdflinkfile.getAbsolutePath());
			if (!mdflinkfile.exists() || !mdflinkfile.isFile()) {
				mdflinkfile.createNewFile();
			}

			Document document = buider.build(new File(filename));
			if (document != null) {
				Element root = document.getRootElement();
				List<Element> classList = root.getChildren();
				for (int i = 0; i < classList.size(); i++) {
					currentClass = classList.get(i);
					currentClassId = currentClass.getAttributeValue("id");

					// check if this class is existing. if not , create it.
					String classDirPath = dataDir + currentClassId + "/";

					File classDir = new File(classDirPath);
					if (!classDir.exists() || !classDir.isDirectory()) {
						classDir.mkdir();
					}
					List<Element> datasetList = currentClass.getChildren();
					for (int j = 0; j < datasetList.size(); j++) {
						currentDataSet = datasetList.get(j);
						currentDataSetId = currentDataSet
								.getAttributeValue("id");
						String dataSetDirPath = classDirPath + currentDataSetId
								+ "/";

						File datasetDir = new File(dataSetDirPath);
						if (!datasetDir.exists() || !datasetDir.isDirectory()) {
							datasetDir.mkdir();
						}
						// create new link file.
						String linkFilePath = dataSetDirPath + "link";
						File link = new File(linkFilePath);
						if (!link.exists() || !link.isFile()) {
							link.createNewFile();
							List<Element> locationList = currentDataSet
									.getChildren();
							if (locationList.size() > 0) {
								FileOutputStream linkfos = new FileOutputStream(
										linkFilePath, true);
								for (int k = 0; k < locationList.size(); k++) {
									linkfos.write((locationList.get(k)
											.getText() + "\n").getBytes());
								}
								linkfos.close();
								FileOutputStream mdflinkfilefos = new FileOutputStream(
										FilePath.getMODIFIEDLINKFILE(), true);
								mdflinkfilefos.write((linkFilePath + "\n")
										.getBytes());
								mdflinkfilefos.close();
							}

						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			log.error("ERROR: ", e);
			return 1;
		}
		log.info("parsing successfully.");
		return 0;

	}

	@Override
	public int insertXml(String filename, String _class, String name,
			String[] links) {
		init();
		log.info("Start to insert.");
		// try {
		// Create document. If file not exit, just new one.
		System.out.println(filename);
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Xml file does not exist. Failed to create xml"
						+ filename);
				return 1;
			}
		}

		try {
			document = buider.build(new FileInputStream(new File(filename)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("File not found: " + filename);
			return 1;
		}
		// check root
		// get root
		root = document.getRootElement();

		// root name should be "dataset_location"
		if (!root.getName().toString().equals("dataset_location")) {
			root.setName("dataset_location");
			ifModified = true;
		}
		// get current class. if not exit, create it.
		List<Element> classList = root.getChildren();
		// 在 xml文件中寻找当前分类，如果不存在，新建。
		for (Element elm : classList) {
			if (elm.getAttributeValue("id").toString().equals(_class)) {
				currentClass = elm;
				ClassExist = true;
				break;
			}
		}
		// class not exist, create one.
		if (!ClassExist) {
			currentClass = new Element("class").setAttribute("id", _class);
			root.addContent(currentClass);
			ifModified = true;
		}

		// 检查对应当前分类的文件夹是否存在，如果不存在，新建一个。
		currentClassId = currentClass.getAttributeValue("id").toString();
		String currentClassPath = FilePath.getDATADIR() + currentClassId + "/";
		File currentClassDir = new File(currentClassPath);
		if (!currentClassDir.exists() || !currentClassDir.isDirectory()) {
			currentClassDir.mkdir();
		}

		// 在currentClass下寻找是否存在当前数据库，如果不存在，新建。
		List<Element> datasetList = currentClass.getChildren();
		for (Element elm : datasetList) {
			if (elm.getAttributeValue("id").toString().equals(name)) {
				currentDataSet = elm;
				DatasetExist = true;
				break;
			}
		}
		if (!DatasetExist) {
			currentDataSet = new Element("dataset").setAttribute("id", name);
			currentClass.addContent(currentDataSet);
			ifModified = true;
		}
		currentDataSetId = currentDataSet.getAttributeValue("id");
		String currentDataPath = currentClassPath + currentDataSetId + "/";
		File currentDataSetDir = new File(currentDataPath);
		if (!currentDataSetDir.exists() || !currentDataSetDir.isDirectory()) {
			currentDataSetDir.mkdir();
		}
		// 如果link文件不存在 新建一个
		String linkPath = currentDataPath + "link";
		File currentLinkFile = new File(linkPath);
		if (!currentLinkFile.exists() || !currentLinkFile.isFile()) {
			try {
				currentLinkFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Failed to create file: " + linkPath);
				return 1;
			}
		}

		// 向link文件中写入链接
		if (currentClass != null && currentDataSet != null) {
			FileOutputStream linkFOS = null;
			try {
				linkFOS = new FileOutputStream(linkPath, true);
			for (String link : links) {
				currentDataSet
						.addContent(new Element("location").setText(link));
				ifModified = true;
				// 有新link在当前数据库中添加
				newlinks = true;
				
					linkFOS.write((link + "\n").getBytes());
			}
			
				linkFOS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("Failed to write file: "+linkPath);
			}
			// link文件修改后，将link文件的地址放入 modifiedlinkfile中，供checker检查。
			if (newlinks) {
				FileOutputStream modifiedlinkfileFOS;
				try {
					modifiedlinkfileFOS = new FileOutputStream(
							FilePath.getMODIFIEDLINKFILE(), true);
					modifiedlinkfileFOS.write((linkPath + "\n").getBytes());
					modifiedlinkfileFOS.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("Failed to write in "
							+ FilePath.getMODIFIEDLINKFILE());
					return 1;
				}
			}
		}

		// 在xml文件中记录
		if (ifModified) {
			Format format = Format.getCompactFormat();
			format.setEncoding("gb2312");
			format.setIndent("  ");
			// format.setLineSeparator("\t\n");
			XMLOutputter outputter = new XMLOutputter(format);
			try {
				outputter.output(document, new FileOutputStream(filename));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("Failed to write in " + filename);
				return 1;
			}
			// parserXml(filename);
			log.info("Insert over.");
			return 0; // something is changed.
		} else {
			log.info("No record to insert.");
			return 2; // nothing is changed.
		}

	}

	public void init() {
		currentClass = null;
		currentClassId = null;
		currentDataSet = null;
		currentDataSetId = null;
		ifModified = false;
		newlinks = false;
		document = null;
		root = null;
		ClassExist = false;
		DatasetExist = false;
	}
}
