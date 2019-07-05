///*
// *    OpenGDS/Builder
// *    http://git.co.kr
// *
// *    (C) 2014-2017, GeoSpatial Information Technology(GIT)
// *    
// *    This library is free software; you can redistribute it and/or
// *    modify it under the terms of the GNU Lesser General Public
// *    License as published by the Free Software Foundation;
// *    version 3 of the License.
// *
// *    This library is distributed in the hope that it will be useful,
// *    but WITHOUT ANY WARRANTY; without even the implied warranty of
// *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// *    Lesser General Public License for more details.
// */
//
//package com.gitrnd.qaconsumer.generalization;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLEncoder;
//import java.nio.charset.Charset;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import org.apache.commons.io.FilenameUtils;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.geotools.data.DefaultTransaction;
//import org.geotools.data.Transaction;
//import org.geotools.data.shapefile.ShapefileDataStore;
//import org.geotools.data.shapefile.ShapefileDataStoreFactory;
//import org.geotools.data.simple.SimpleFeatureCollection;
//import org.geotools.data.simple.SimpleFeatureSource;
//import org.geotools.data.simple.SimpleFeatureStore;
//import org.geotools.feature.SchemaException;
//import org.geotools.referencing.CRS;
//import org.json.simple.JSONObject;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.referencing.FactoryException;
//import org.opengis.referencing.NoSuchAuthorityCodeException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.git.gdsbuilder.generalization.GeneralizationOperator;
//import com.git.gdsbuilder.generalization.GeneralizationOption;
//import com.git.gdsbuilder.geoserver.converter.GeoserverDataConverter;
//import com.git.gdsbuilder.geoserver.converter.impl.GeoserverDataConverterImpl;
//import com.git.gdsbuilder.parser.file.shp.SHPFileLayerParser;
//import com.git.gdsbuilder.type.dt.layer.DTLayer;
//import com.gitrnd.qaconsumer.filestatus.domain.FileStatus;
//import com.gitrnd.qaconsumer.filestatus.service.FileStatusService;
//import com.gitrnd.qaconsumer.qacategory.service.QACategoryService;
//import com.gitrnd.qaconsumer.qaprogress.domain.QAProgress;
//import com.gitrnd.qaconsumer.qaprogress.service.QAProgressService;
//import com.gitrnd.qaconsumer.user.domain.User;
//import com.gitrnd.qaconsumer.user.service.UserService;
//
//@ComponentScan
//@Service("generalizationService")
//public class GeneralizationServiceImpl implements GeneralizationService {
//
//	@Value("${gitrnd.apache.basedir}")
//	private String baseDir;
//	@Value("${gitrnd.serverhost}")
//	private String serverhost;
//	@Value("${server.servlet.port}")
//	private String port;
//	@Value("${server.servlet.context-path}")
//	private String contextPath;
//
//	// file dir
//	protected static String OUTPUT_DIR;
//	protected static String FILE_DIR;
//	protected static String OUTPUT_NAME;
//	protected static String ZIP_DIR;
//
//	@Autowired
//	FileStatusService fileStatusService;
//	@Autowired
//	QAProgressService qapgService;
//	@Autowired
//	UserService userService;
//	@Autowired
//	QACategoryService qaCatService;
//
//	// qa progress
//	protected static int FILEUPLOAD = 1;
//	protected static int VALIDATEPROGRESING = 2;
//	protected static int VALIDATESUCCESS = 3;
//	protected static int VALIDATEFAIL = 4;
//
//	@Transactional(propagation = Propagation.REQUIRES_NEW)
//	public void excute(JSONObject param) throws Throwable {
//
//		String serverURL = (String) param.get("serverURL");
//		Long pid = (Long) param.get("pid");
//		int pidx = pid.intValue();
//		Long fileIdx = (Long) param.get("fid");
//		int fidx = fileIdx.intValue();
//		String epsg = (String) param.get("crs");
//
//		QAProgress progress = qapgService.retrieveQAProgressById(pidx);
//
//		// start
//		progress.setQaState(VALIDATEPROGRESING);
//		qapgService.updateQAState(progress);
//
//		int uIdx = progress.getUIdx();
//		User user = userService.retrieveUserByIdx(uIdx);
//		String uId = user.getUid();
//
//		FileStatus fileStatus = fileStatusService.retrieveFileStatusById(fidx);
//		String fname = fileStatus.getFname();
//
//		Timestamp cTime = fileStatus.getCtime();
//		String cTimeStr = new SimpleDateFormat("yyMMdd" + "_" + "HHmmss").format(cTime);
//
//		// create download path
//		String basePath = "c:" + File.separator + baseDir + File.separator + uId + File.separator + cTimeStr;
//		File baseDirFile = new File(basePath);
//		String unzipPath = basePath + File.separator + "unzipfiles";
//
//		// set generalization option
//		JSONObject preset = (JSONObject) param.get("preset");
//		List<GeneralizationOption> genalOpts = new ArrayList<>();
//		GeneralizationOption firOpt = new GeneralizationOption();
//		firOpt.createGeneraliationOpt(GeneralizationOption.FIR, (JSONObject) preset.get(GeneralizationOption.FIR));
//		GeneralizationOption secOpt = new GeneralizationOption();
//		secOpt.createGeneraliationOpt(GeneralizationOption.SEC, (JSONObject) preset.get(GeneralizationOption.SEC));
//		genalOpts.add(firOpt);
//		genalOpts.add(secOpt);
//
//		// geoserver layer download
//		JSONObject layers = (JSONObject) param.get("layers");
//		GeoserverDataConverter geoDataConverter = new GeoserverDataConverterImpl(serverURL, layers, unzipPath, epsg);
//		geoDataConverter.generalizationExport();
//
//		// set err directory
//		OUTPUT_DIR = basePath + File.separator + "generalization";
//		String entryName = progress.getOriginName(); // 호철씨가 준 파일 이름
//		OUTPUT_NAME = entryName + "_" + cTimeStr;
//		FILE_DIR = OUTPUT_DIR + File.separator + OUTPUT_NAME;
//		createFileDirectory(FILE_DIR);
//
//		// read downloaded layer
//		File geoPath = new File(unzipPath);
//		File[] wsDirs = geoPath.listFiles();
//
//		for (File wsDir : wsDirs) {
//			File[] layerDirs = wsDir.listFiles();
//			for (File layerDir : layerDirs) {
//				DTLayer dtLayer = null;
//				String fileName = layerDir.getName();
//				int Idx = fileName.lastIndexOf(".");
//				String layerName = fileName.substring(0, Idx);
//				String ext = FilenameUtils.getExtension(layerDir.getPath());
//				if (!ext.endsWith("shp")) {
//					continue;
//				}
//				try {
//					dtLayer = new SHPFileLayerParser().parseDTLayer(epsg, layerDir);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				if (dtLayer != null) {
//					// request generalization
//					GeneralizationOperator operator = new GeneralizationOperator(dtLayer, genalOpts);
//					SimpleFeatureCollection sfc = operator.generalizate();
//					try {
//						writeSHP(sfc, FILE_DIR + File.separator + layerName + "_gen_result.shp", epsg);
//					} catch (IOException | SchemaException | FactoryException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		progress.setQaState(VALIDATESUCCESS);
//		qapgService.updateQAState(progress);
//
//		boolean isTrue = zipFileDirectory();
//		if (isTrue) {
//			// result 파일이 있는 경우
//			String destination = "http://" + serverhost + ":" + port + contextPath + "/uploadGsError.do";
//			HttpPost post = new HttpPost(destination);
//			InputStream inputStream = new FileInputStream(FILE_DIR + ".zip");
//			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//			builder.addTextBody("user", uId);
//			builder.addTextBody("time", cTimeStr);
//			builder.addTextBody("file", OUTPUT_NAME);
//			builder.addTextBody("fid", Integer.toString(fidx));
//			builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"), OUTPUT_NAME + ".zip");
//			builder.setCharset(Charset.forName("UTF-8"));
//
//			HttpEntity entity = builder.build();
//			post.setEntity(entity);
//
//			CloseableHttpClient client = HttpClients.createDefault();
//			HttpResponse response = client.execute(post);
//			client.close();
//
//			String encodeName = URLEncoder.encode(OUTPUT_NAME, "UTF-8");
//			if (response.getStatusLine().getStatusCode() == 200) {
//				String errDir = "downloaderror.do?" + "time=" + cTimeStr + "&" + "file=" + encodeName + ".zip";
//				progress.setErrdirectory(errDir);
//				progress.setErrFileName(fname + ".zip");
//				qapgService.updateQAResponse(progress);
//			}
//		} else {
//			progress.setComment("일반화 결과 파일이 존재하지 않습니다");
//			qapgService.updateQAState(progress);
//			qapgService.updateQAResponse(progress);
//		}
//		deleteDirectory(baseDirFile);
//	}
//
//	private static void writeSHP(SimpleFeatureCollection sfc, String filePath, String epsg)
//			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {
//
//		ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
//		File file = new File(filePath);
//		Map map = Collections.singletonMap("url", file.toURI().toURL());
//		ShapefileDataStore myData = (ShapefileDataStore) factory.createNewDataStore(map);
//		myData.setCharset(Charset.forName("EUC-KR"));
//		SimpleFeatureType featureType = sfc.getSchema();
//		myData.createSchema(featureType);
//		Transaction transaction = new DefaultTransaction("create");
//		String typeName = myData.getTypeNames()[0];
//		myData.forceSchemaCRS(CRS.decode(epsg));
//
//		SimpleFeatureSource featureSource = myData.getFeatureSource(typeName);
//		if (featureSource instanceof SimpleFeatureStore) {
//			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
//			featureStore.setTransaction(transaction);
//			try {
//				featureStore.addFeatures(sfc);
//				transaction.commit();
//			} catch (Exception e) {
//				e.printStackTrace();
//				transaction.rollback();
//			} finally {
//				transaction.close();
//			}
//		}
//	}
//
//	private void createFileDirectory(String directory) {
//		File file = new File(directory);
//		if (!file.exists()) {
//			file.mkdirs();
//		}
//	}
//
//	private boolean zipFileDirectory() {
//
//		File directory = new File(FILE_DIR);
//		List<String> fileList = getFileList(directory);
//		if (fileList.size() > 0) {
//			try {
//				ZIP_DIR = FILE_DIR + ".zip";
//				FileOutputStream fos = new FileOutputStream(ZIP_DIR);
//				ZipOutputStream zos = new ZipOutputStream(fos);
//
//				for (String filePath : fileList) {
//					String name = filePath.substring(directory.getAbsolutePath().length() + 1, filePath.length());
//					ZipEntry zipEntry = new ZipEntry(name);
//					zos.putNextEntry(zipEntry);
//					FileInputStream fis = new FileInputStream(filePath);
//					byte[] buffer = new byte[1024];
//					int length;
//					while ((length = fis.read(buffer)) > 0) {
//						zos.write(buffer, 0, length);
//					}
//					zos.closeEntry();
//					fis.close();
//
//					// 압축 후 삭제
//					File file = new File(filePath);
//					file.delete();
//				}
//				zos.close();
//				fos.close();
//				directory.delete();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	private List<String> getFileList(File directory) {
//
//		List<String> fileList = new ArrayList<>();
//
//		File[] files = directory.listFiles();
//		if (files != null && files.length > 0) {
//			for (File file : files) {
//				if (file.isFile()) {
//					fileList.add(file.getAbsolutePath());
//				} else {
//					getFileList(file);
//				}
//			}
//		}
//		return fileList;
//	}
//
//	private void deleteDirectory(File dir) {
//
//		if (dir.exists()) {
//			File[] files = dir.listFiles();
//			if (files.length == 0) {
//				dir.delete();
//			}
//			for (File file : files) {
//				if (file.isDirectory()) {
//					deleteDirectory(file);
//				} else {
//					file.delete();
//				}
//			}
//		}
//		dir.delete();
//	}
//}
