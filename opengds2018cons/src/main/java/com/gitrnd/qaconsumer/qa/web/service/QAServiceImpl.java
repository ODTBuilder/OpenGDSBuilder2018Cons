/*
 *    OpenGDS/Builder
 *    http://git.co.kr
 *
 *    (C) 2014-2017, GeoSpatial Information Technology(GIT)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.gitrnd.qaconsumer.qa.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.git.gdsbuilder.file.writer.SHPFileWriter;
import com.git.gdsbuilder.parser.file.QAFileParser;
import com.git.gdsbuilder.parser.qa.QATypeParser;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList;
import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.layer.QALayerTypeList;
import com.git.gdsbuilder.validator.collection.CollectionValidator;
import com.gitrnd.qaconsumer.filestatus.service.FileStatusService;
import com.gitrnd.qaconsumer.preset.domain.Preset;
import com.gitrnd.qaconsumer.preset.service.PresetService;
import com.gitrnd.qaconsumer.qacategory.service.QACategoryService;
import com.gitrnd.qaconsumer.qaprogress.domain.QAProgress;
import com.gitrnd.qaconsumer.qaprogress.service.QAProgressService;
import com.gitrnd.qaconsumer.qareport.details.domain.QADetailReport;
import com.gitrnd.qaconsumer.qareport.details.service.QADetailReportService;
import com.gitrnd.qaconsumer.qareport.domain.QAReport;
import com.gitrnd.qaconsumer.qareport.service.QAReportService;
import com.gitrnd.qaconsumer.user.domain.User;
import com.gitrnd.qaconsumer.user.service.UserService;

@ComponentScan
@Service("fileService")
public class QAServiceImpl implements QAService {

	@Value("${gitrnd.apache.basedir}")
	private String baseDir;
	@Value("${gitrnd.serverhost}")
	private String serverhost;
	@Value("${server.port}")
	private String port;
	@Value("${server.context-path}")
	private String contextPath;

	// file dir
	protected static String ERR_OUTPUT_DIR;
	protected static String ERR_FILE_DIR;
	protected static String ERR_OUTPUT_NAME;
	protected static String ERR_ZIP_DIR;

	// qa progress
	protected static int fileUpload = 1;
	protected static int validateProgresing = 2;
	protected static int validateSuccess = 3;
	protected static int validateFail = 4;

	@Autowired
	FileStatusService fileStatusService;
	@Autowired
	QAProgressService qapgService;
	@Autowired
	UserService userService;
	@Autowired
	QACategoryService qaCatService;
	@Autowired
	PresetService presetService;
	@Autowired
	QAReportService reportService;
	@Autowired
	QADetailReportService detailService;

	@Value("${gitrnd.serverhost}")
	private String producerAddr;

	@Value("${server.port}")
	private String portNum;

	@Value("${server.context-path}")
	private String context;

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public boolean validate(JSONObject param) throws Throwable {

		boolean isSuccess = false;

		String qaVer = (String) param.get("qaVer");
		String qaType = (String) param.get("qaType");
		String prid = (String) param.get("prid");
		String fileformat = (String) param.get("fileformat");

		Long catetoryIdx = (Long) param.get("category");
		int cIdx = catetoryIdx.intValue();

		Long pid = (Long) param.get("pid");
		int pIdx = pid.intValue();

		String epsg = (String) param.get("crs");

		QAProgress progress = qapgService.retrieveQAProgressById(pIdx);
		String type = progress.getFileType();

		// start
		progress.setQaState(validateProgresing);
		qapgService.updateQAState(progress);

		Preset prst = null;
		// preset
		if (prid.equals("nonset")) {
			if (qaVer.equals("qa1")) {
				switch (qaType) {
				case "nm1":
					break;
				case "nm5":
					prst = presetService.retrieveBasePreset(1);
					break;
				case "nm25":
					break;
				case "ug1":
					break;
				case "ug5":
					prst = presetService.retrieveBasePreset(3);
					break;
				case "ug25":
					break;
				case "fr1":
					break;
				case "fr5":
					prst = presetService.retrieveBasePreset(5);
					break;
				case "fr25":
					break;
				default:
					break;
				}
			} else if (qaVer.equals("qa2")) {
				switch (qaType) {
				case "nm1":
					break;
				case "nm5":
					prst = presetService.retrieveBasePreset(2);
					break;
				case "nm25":
					break;
				case "ug1":
					break;
				case "ug5":
					prst = presetService.retrieveBasePreset(4);
					break;
				case "ug25":
					break;
				case "fr1":
					break;
				case "fr5":
					prst = presetService.retrieveBasePreset(5);
					break;
				case "fr25":
					break;
				default:
					break;
				}
			}
		} else {
			prst = presetService.retrievePresetById(Integer.parseInt(prid));
		}
		// 옵션또는 파일이 제대로 넘어오지 않았을때 강제로 예외발생
		if (qaVer == null || qaType == null || prid == null) {
			progress.setComment("재로그인 후 다시 요청해주세요.");
			progress.setQaState(validateFail);
			qapgService.updateQAState(progress);
			return isSuccess;
		} else if (prst == null) {
			progress.setComment("옵션을 재설정 해주세요.");
			progress.setQaState(validateFail);
			qapgService.updateQAState(progress);
			return isSuccess;
		} else if (fileformat == null) {
			progress.setComment("파일포맷을 설정해주세요.");
			progress.setQaState(validateFail);
			qapgService.updateQAState(progress);
			return isSuccess;
		} else {
			int uIdx = progress.getUIdx();
			User user = userService.retrieveUserByIdx(uIdx);
			String uId = user.getUid();

			Timestamp cTime = progress.getStart_time();
			String cTimeStr = new SimpleDateFormat("yyMMdd" + "_" + "HHmmss").format(cTime);

			String basePath = "c:" + File.separator + baseDir + File.separator + uId + File.separator + cTimeStr
					+ File.separator;

			String geoLayersPath = basePath + File.separator + "geoLayers"; // geoserver layer file download 경로
			String comment = "";

			// option parsing
			JSONParser jsonP = new JSONParser();
			JSONObject option = (JSONObject) jsonP.parse(prst.getOptionDef());
			JSONArray layers = (JSONArray) jsonP.parse(prst.getLayerDef());

			Object neatLine = option.get("border");
			String neatLineCode = null;
			if (neatLine != null) {
				JSONObject neatLineObj = (JSONObject) neatLine;
				neatLineCode = (String) neatLineObj.get("code");
			}

			// files
			File baseDirFile = new File(geoLayersPath);
			QAFileParser parser = new QAFileParser(epsg, cIdx, type, baseDirFile, neatLineCode);
			boolean parseTrue = parser.isTrue();
			if (!parseTrue) {
				comment += parser.getStatus();
				if (!comment.equals("")) {
					progress.setComment(comment);
				}
				progress.setQaState(validateFail);
				qapgService.updateQAState(progress);
				deleteDirectory(baseDirFile);
				return isSuccess;
			}
			DTLayerCollectionList collectionList = parser.getCollectionList();
			if (collectionList == null) {
				// 파일 다 에러
				comment += parser.getStatus();
				if (!comment.equals("")) {
					progress.setComment(comment);
				}
				progress.setQaState(validateFail);
				qapgService.updateQAState(progress);
				deleteDirectory(baseDirFile);
				return isSuccess;
			} else {
				// 몇개만 에러
				comment += parser.getStatus();
				if (!comment.equals("")) {
					progress.setComment(comment);
				}
				qapgService.updateQAState(progress);
			}
			JSONArray typeValidate = (JSONArray) option.get("definition");
			for (int j = 0; j < layers.size(); j++) {
				JSONObject lyrItem = (JSONObject) layers.get(j);
				Boolean isExist = false;
				for (int i = 0; i < typeValidate.size(); i++) {
					JSONObject optItem = (JSONObject) typeValidate.get(i);
					String typeName = (String) optItem.get("name");
					if (typeName.equals((String) lyrItem.get("name"))) {
						optItem.put("layers", (JSONArray) lyrItem.get("layers"));
						isExist = true;
					}
				}
				if (!isExist) {
					JSONObject obj = new JSONObject();
					obj.put("name", (String) lyrItem.get("name"));
					obj.put("layers", (JSONArray) lyrItem.get("layers"));
					typeValidate.add(obj);
				}
			}

			// options
			QATypeParser validateTypeParser = new QATypeParser(typeValidate);
			QALayerTypeList validateLayerTypeList = validateTypeParser.getValidateLayerTypeList();
			if (validateLayerTypeList == null) {
				comment += validateTypeParser.getComment();
				if (!comment.equals("")) {
					progress.setComment(comment);
				}
				progress.setQaState(validateFail);
				qapgService.updateQAState(progress);
				deleteDirectory(baseDirFile);
				return isSuccess;
			}
			validateLayerTypeList.setCategory(cIdx);

			// set err directory
			ERR_OUTPUT_DIR = basePath + "error";

			String entryName = progress.getOriginName(); // 호철씨가 준 파일 이름
			ERR_OUTPUT_NAME = entryName + "_" + cTimeStr;

			progress.setErrFileName(ERR_OUTPUT_NAME + ".zip");

			ERR_FILE_DIR = ERR_OUTPUT_DIR + File.separator + ERR_OUTPUT_NAME;
			createFileDirectory(ERR_FILE_DIR);

			// excute validation
			isSuccess = executorValidate(collectionList, validateLayerTypeList, epsg, ERR_OUTPUT_NAME, pIdx);
			if (isSuccess) {
				// insert validate state
				progress.setQaState(validateSuccess);
				qapgService.updateQAState(progress);

				// zip err shp directory
				zipFileDirectory();
				String destination = "http://" + producerAddr + ":" + portNum + context + "/uploaderror.do";
				HttpPost post = new HttpPost(destination);
				InputStream inputStream = new FileInputStream(ERR_FILE_DIR + ".zip");
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				builder.addTextBody("user", uId);
				builder.addTextBody("time", cTimeStr);
				builder.addTextBody("file", ERR_OUTPUT_NAME);
				// builder.addTextBody("fid", Integer.toString(fIdx));
				builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"),
						ERR_OUTPUT_NAME + ".zip");
				builder.setCharset(Charset.forName("UTF-8"));

				HttpEntity entity = builder.build();
				post.setEntity(entity);

				CloseableHttpClient client = HttpClients.createDefault();
				HttpResponse response = client.execute(post);
				client.close();

				String encodeName = URLEncoder.encode(ERR_OUTPUT_NAME, "UTF-8");
				if (response.getStatusLine().getStatusCode() == 200) {
					String errDir = "http://" + serverhost + ":" + port + contextPath + "/downloaderror.do?" + "time="
							+ cTimeStr + "&" + "file=" + encodeName + ".zip";
					progress.setErrdirectory(errDir);
					qapgService.updateQAResponse(progress);
				}
			} else {
				// insert validate state
				progress.setQaState(validateFail);
				qapgService.updateQAState(progress);
			}
			deleteDirectory(baseDirFile);
			return isSuccess;
		}
	}

	private boolean executorValidate(DTLayerCollectionList collectionList, QALayerTypeList validateLayerTypeList,
			String epsg, String errLayerName, int pIdx) {

		// 도엽별 검수 쓰레드 생성
		List<Future> futures = new ArrayList<>();
		ExecutorService execService = Executors.newFixedThreadPool(3);
		for (final DTLayerCollection collection : collectionList) {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					CollectionValidator validator = null;
					try {
						DTLayerCollectionList closeCollections = collectionList
								.getCloseLayerCollections(collection.getMapRule());
						validator = new CollectionValidator(collection, closeCollections, validateLayerTypeList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ErrorLayer errLayer = validator.getErrLayer();
					errLayer.setLayerName(errLayerName);
					int errSize = errLayer.getErrFeatureList().size();
					if (errSize > 0) {
						// write shp file
						writeErrShp(epsg, errLayer);
					}
					// insert qa report
					insertQAReport(errLayer, errSize, pIdx);
				}
			};
			Future future = execService.submit(runnable);
			futures.add(future);
		}
		int futureCount = 0;
		for (int i = 0; i < futures.size(); i++) {
			Future tmp = futures.get(i);
			try {
				tmp.get();
				futureCount++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		execService.shutdown();
		if (futureCount == collectionList.size()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param errLayer
	 * @param errCount
	 * @param pIdx
	 */
	protected void insertQAReport(ErrorLayer errLayer, int errCount, int pIdx) {

		String errLayerName = errLayer.getLayerName();
		int layerCount = errLayer.getLayerCount();
		int featureCount = errLayer.getFeatureCount();
		int normalCount = 0;
		if (errCount > 0) {
			normalCount = featureCount - errCount;
		}
		int exceptCount = errLayer.getExceptCount();
		String comment = errLayer.getComment();

		QAReport report = new QAReport(errLayerName, layerCount, featureCount, normalCount, errCount, exceptCount,
				comment, pIdx);
		Integer rIdx = reportService.insertQAReport(report);

		List<ErrorFeature> errList = errLayer.getErrFeatureList();
		for (ErrorFeature err : errList) {

			String refLayerId = err.getRefLayerId();
			String featureId = err.getFeatureID();
			String refFeatureId = err.getRefFeatureId();
			String errType = err.getErrType();
			String errName = err.getErrName();
			String errPoint = err.getErrPoint().toString();
			String detailComment = err.getComment();

			QADetailReport detail = new QADetailReport(refLayerId, featureId, refFeatureId, errType, errName, errPoint,
					detailComment, rIdx);
			detailService.insertQADetailReport(detail);
		}
	}

	private void zipFileDirectory() {

		File directory = new File(ERR_FILE_DIR);
		List<String> fileList = getFileList(directory);
		try {
			ERR_ZIP_DIR = ERR_FILE_DIR + ".zip";
			FileOutputStream fos = new FileOutputStream(ERR_ZIP_DIR);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String filePath : fileList) {
				String name = filePath.substring(directory.getAbsolutePath().length() + 1, filePath.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zos.putNextEntry(zipEntry);
				FileInputStream fis = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				fis.close();

				// 압축 후 삭제
				File file = new File(filePath);
				file.delete();
			}
			zos.close();
			fos.close();
			directory.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getFileList(File directory) {

		List<String> fileList = new ArrayList<>();

		File[] files = directory.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile()) {
					fileList.add(file.getAbsolutePath());
				} else {
					getFileList(file);
				}
			}
		}
		return fileList;
	}

	private void writeErrShp(String epsg, ErrorLayer errLayer) {
		try {
			// 오류레이어 발행
			SHPFileWriter.writeSHP(epsg, errLayer, ERR_FILE_DIR + "\\" + errLayer.getCollectionName() + "_err.shp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createFileDirectory(String directory) {
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	private void deleteDirectory(File dir) {

		if (dir.exists()) {
			File[] files = dir.listFiles();
			if (files.length == 0) {
				dir.delete();
			}
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		dir.delete();
	}

	public int COMPARETYPE_NAME = 0;
	public int COMPARETYPE_DATE = 1;

	public File[] sortFileList(File[] files, final int compareType) {

		Arrays.sort(files, new Comparator<Object>() {
			@Override
			public int compare(Object object1, Object object2) {

				String s1 = "";
				String s2 = "";

				if (compareType == COMPARETYPE_NAME) {
					s1 = ((File) object1).getName();
					s2 = ((File) object2).getName();
				} else if (compareType == COMPARETYPE_DATE) {
					s1 = ((File) object1).lastModified() + "";
					s2 = ((File) object2).lastModified() + "";
				}

				return s1.compareTo(s2);

			}
		});

		return files;
	}

	/**
	 * 폴더 내에 폴더가 있을시 하위 폴더 탐색
	 * 
	 * @author SG.Lee
	 * @Date 2018. 4. 18. 오전 9:09:33
	 * @param source void
	 */
	@SuppressWarnings("unused")
	private static void subDirList(String source) {
		File dir = new File(source);

		File[] fileList = dir.listFiles();
		List<File> indexFiles = new ArrayList<File>();

		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];

			if (file.isFile()) {
				String filePath = file.getPath();
				String fFullName = file.getName();

				int Idx = fFullName.lastIndexOf(".");
				String _fileName = fFullName.substring(0, Idx);

				String parentPath = file.getParent(); // 상위 폴더 경로

				if (_fileName.endsWith("index")) {
					indexFiles.add(fileList[i]);// 도곽파일 리스트 add(shp,shx...)
				} else {
					if (_fileName.contains(".")) {
						moveDirectory(_fileName.substring(0, _fileName.lastIndexOf(".")), fFullName, filePath,
								parentPath);
					} else {
						moveDirectory(_fileName, fFullName, filePath, parentPath);
					}
				}
			}
		}

		fileList = dir.listFiles();
		// 도엽별 폴더 생성후 도곽파일 이동복사
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				String message = "[디렉토리] ";
				message = fileList[i].getName();
				System.out.println(message);
				for (File iFile : indexFiles) {
					try {
						FileNio2Copy(iFile.getPath(), fileList[i].getPath() + File.separator + iFile.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.getMessage();
					}
				}
			}
		}

		// index파일 삭제
		for (File iFile : indexFiles) {
			iFile.delete();
		}

		// 파일 사용후 객체초기화
		fileList = null;
		indexFiles = null;
	}

	/**
	 * 임상도 폴더 재생성
	 * 
	 * @author SG.Lee
	 * @Date 2018. 4. 18. 오후 1:24:16
	 * @param unzipFolder void
	 */
	private static File[] createCollectionFolders(File unzipFolder) {
		boolean equalFlag = false; // 파일명이랑 압축파일명이랑 같을시 대비 flag값
		String unzipName = unzipFolder.getName();

		if (unzipFolder.exists() == false) {
			System.out.println("경로가 존재하지 않습니다");
		}

		File[] fileList = unzipFolder.listFiles();
		List<File> indexFiles = new ArrayList<File>();
		String parentPath = unzipFolder.getParent(); // 상위 폴더 경로

		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				/*
				 * String message = "[디렉토리] "; message = fileList[ i ].getName();
				 * System.out.println( message );
				 * 
				 * subDirList( fileList[ i ].getPath());//하위 폴더 탐색
				 */ } else {
				String filePath = fileList[i].getPath();
				String fFullName = fileList[i].getName();

				int Idx = fFullName.lastIndexOf(".");
				String _fileName = fFullName.substring(0, Idx);

				if (_fileName.equals(unzipName)) {
					equalFlag = true;
				}

				if (_fileName.endsWith("index")) {
					indexFiles.add(fileList[i]);// 도곽파일 리스트 add(shp,shx...)
				} else {
					if (_fileName.contains(".")) {
						moveDirectory(_fileName.substring(0, _fileName.lastIndexOf(".")), fFullName, filePath,
								parentPath);
					} else {
						moveDirectory(_fileName, fFullName, filePath, parentPath);
					}
				}
			}
		}

		fileList = unzipFolder.listFiles();

		// 도엽별 폴더 생성후 도곽파일 이동복사
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				for (File iFile : indexFiles) {
					try {
						FileNio2Copy(iFile.getPath(), fileList[i].getPath() + File.separator + iFile.getName());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println(e.getMessage());
					}
				}
			}
		}

		// index파일 삭제
		for (File iFile : indexFiles) {
			iFile.delete();
		}

		// 원래 폴더 삭제
		if (!equalFlag) {
			unzipFolder.delete();
		}

		// 파일 사용후 객체초기화
		fileList = null;
		indexFiles = null;

		return new File(parentPath).listFiles();
	}

	/**
	 * 파일이동
	 * 
	 * @author SG.Lee
	 * @Date 2018. 4. 18. 오전 9:46:27
	 * @param folderName
	 * @param fileName
	 * @param beforeFilePath
	 * @param afterFilePath
	 * @return String
	 */
	private static String moveDirectory(String folderName, String fileName, String beforeFilePath,
			String afterFilePath) {
		String path = afterFilePath + "/" + folderName;
		String filePath = path + "/" + fileName;

		File dir = new File(path);

		if (!dir.exists()) { // 폴더 없으면 폴더 생성
			dir.mkdirs();
		}

		try {
			File file = new File(beforeFilePath);

			if (file.renameTo(new File(filePath))) { // 파일 이동
				return filePath; // 성공시 성공 파일 경로 return
			} else {
				return null;
			}
		} catch (Exception e) {
			e.getMessage();
			return null;
		}
	}

	/**
	 * 파일복사
	 * 
	 * @author SG.Lee
	 * @Date 2018. 4. 18. 오전 9:45:55
	 * @param source
	 * @param dest
	 * @throws IOException void
	 */
	private static void FileNio2Copy(String source, String dest) throws IOException {
		Files.copy(new File(source).toPath(), new File(dest).toPath());
	}
}
