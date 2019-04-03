package com.git.gdsbuilder.validator.basic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.CRS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.git.gdsbuilder.parser.qa.QATypeParser;
import com.git.gdsbuilder.quadtree.Node;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.layer.QALayerTypeList;

public class TEST {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Throwable {

		// parse option
		JSONParser parser = new JSONParser();
		Object layerObj = parser.parse(new FileReader("D:\\새 폴더\\옵션_수정중\\국가기본도_레이어.json"));
		Object optionObj = parser.parse(new FileReader("D:\\새 폴더\\song2\\국가기본도_경계침범_option.json"));

		JSONArray layerJson = (JSONArray) layerObj;
		JSONObject optionJson = (JSONObject) optionObj;

		JSONArray typeValidate = (JSONArray) optionJson.get("definition");
		int layerJsonSize = layerJson.size();
		int typeValidateSize = typeValidate.size();

		for (int j = 0; j < layerJsonSize; j++) {
			JSONObject lyrItem = (JSONObject) layerJson.get(j);
			String lyrItemName = (String) lyrItem.get("name");
			JSONArray iyrItemArray = (JSONArray) lyrItem.get("layers");
			Boolean isExist = false;
			for (int i = 0; i < typeValidateSize; i++) {
				JSONObject optItem = (JSONObject) typeValidate.get(i);
				String typeName = (String) optItem.get("name");
				if (typeName.equals(lyrItemName)) {
					optItem.put("layers", iyrItemArray);
					isExist = true;
				}
			}
			if (!isExist) {
				JSONObject obj = new JSONObject();
				obj.put("name", lyrItemName);
				obj.put("layers", iyrItemArray);
				typeValidate.add(obj);
			}
		}

		QATypeParser qaTypeParser = new QATypeParser(typeValidate);
		QALayerTypeList qaTypeList = qaTypeParser.getValidateLayerTypeList();

		// unzip file
//		String baseDir = "D:\\국가기본도 테스트파일\\temp";
//		String zipFilePath = "D:\\국가기본도 테스트파일\\data\\data.zip";
//		String crs = "EPSG:4326";
//		File zipFile = new File(zipFilePath);
//		String unZipPath = decompress(zipFile, baseDir);

		// read shp files, set DTLayerCollection
		File unZipFolder = new File("D:\\새 폴더\\song2\\2차_1기가_원본");
		File[] listOfFiles = unZipFolder.listFiles();
		DTLayerList list = new DTLayerList();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				String fileName = file.getName();
				if (fileName.endsWith(".shp")) {
					DTLayer dtLayer = new DTLayer();
					int Idx = fileName.lastIndexOf(".");
					String layerName = fileName.substring(0, Idx);
					SimpleFeatureCollection sfc = getShpObject("EGPS:5179", file, layerName);
					if (sfc != null) {
//						add geopack entr 보류
//						DTFeatureEntry fe = createFeatureEntry(sfc.getSchema(), sfc.getBounds());
//						geopkg.add(fe, sfc);

						SimpleFeatureType featureType = sfc.getSchema();
						GeometryType geometryType = featureType.getGeometryDescriptor().getType();
						String geomType = geometryType.getBinding().getSimpleName().toString();
						dtLayer.setLayerType(geomType);
						int size = list.size();
						if (size > 0) {
							if (layerName.endsWith("_E")) {
								boolean added = false;
								String replaceStr = layerName.replace("_E", "");
								for (int i = 0; i < size; i++) {
									DTLayer tmp = list.get(i);
									String tmpId = tmp.getLayerID();
									if (replaceStr.equals(tmpId)) {
										DefaultFeatureCollection dfc = new DefaultFeatureCollection();
										SimpleFeatureCollection tmpSfc = tmp.getSimpleFeatureCollection();
										dfc.addAll(tmpSfc);
										dfc.addAll(sfc);
										tmp.setSimpleFeatureCollection(dfc);
										list.set(i, tmp);
										added = true;
									}
								}
								if (!added) {
									dtLayer.setSimpleFeatureCollection(sfc);
									dtLayer.setLayerID(replaceStr);
									list.add(dtLayer);
								}
							} else {
								dtLayer.setSimpleFeatureCollection(sfc);
								dtLayer.setLayerID(layerName);
								list.add(dtLayer);
							}
						} else {
							String replaceStr = layerName.replace("_E", "");
							dtLayer.setSimpleFeatureCollection(sfc);
							dtLayer.setLayerID(replaceStr);
							list.add(dtLayer);
						}
					}
				}
			}
		}
		DTLayerCollection collection = new DTLayerCollection();
		collection.setLayers(list);
		QuadCollectionValidator validator = new QuadCollectionValidator(collection, qaTypeList, 2000);
		validator.validate();
	}

	public static List<Object> getNodeEnvelopeList(Node[] subNodes, List<Object> envelopeList) {

		int length = subNodes.length;
		for (int i = 0; i < length; i++) {
			Node subNode = subNodes[i];
			if (subNode != null) {
				if (subNode.getLevel() == -9) {
					envelopeList.add(subNode.getEnvelope());
				}
				getNodeEnvelopeList(subNode.getSubnode(), envelopeList);
			}
		}
		return envelopeList;
	}

	public static SimpleFeatureCollection getShpObject(String epsg, File file, String shpName) {

		ShapefileDataStore beforeStore = null;
		SimpleFeatureCollection collection = null;
		try {
			Map<String, Object> beforeMap = new HashMap<String, Object>();
			beforeMap.put("url", file.toURI().toURL());
			beforeStore = (ShapefileDataStore) DataStoreFinder.getDataStore(beforeMap);
			Charset euckr = Charset.forName("EUC-KR");
			beforeStore.setCharset(euckr);
			String typeName = beforeStore.getTypeNames()[0];
			SimpleFeatureSource source = beforeStore.getFeatureSource(typeName);
			Filter filter = Filter.INCLUDE;
			collection = source.getFeatures(filter);
			beforeStore.dispose();
			beforeStore = null;
			source = null;
		} catch (Exception e) {
			return null;
		}
		return collection;
	}

	public static void writeSHP(String epsg, SimpleFeatureCollection simpleFeatureCollection, String filePath)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		File file = new File(filePath);

		Map<String, Serializable> params = new HashMap<>();
		params.put("url", file.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();
		ShapefileDataStore myData = (ShapefileDataStore) factory.createNewDataStore(params);
		SimpleFeatureType featureType = simpleFeatureCollection.getSchema();
		myData.forceSchemaCRS(CRS.decode(epsg));
		myData.setCharset(Charset.forName("EUC-KR"));
		myData.createSchema(featureType);

		Transaction transaction = new DefaultTransaction("create");
		String typeName = myData.getTypeNames()[0];
		SimpleFeatureSource featureSource = myData.getFeatureSource(typeName);
		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(simpleFeatureCollection);
				transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
		}
	}

	public static String decompress(File zipFile, String upzipPath) throws Throwable {

		int BUFFER_SIZE = 4096;
		FileInputStream fis = null;
		ZipInputStream zis = null;
		ZipEntry zipentry = null;
		String path = null;
		try {
			// 디렉토리생성
			String zipFileName = zipFile.getName(); // 파일 이름.확장자
			int comma = zipFileName.lastIndexOf(".");
			String entryName = zipFileName.substring(0, comma); // 파일 이름
			path = upzipPath + File.separator + entryName;
			FileUtils.forceMkdir(new File(path));
			// 파일 스트림
			fis = new FileInputStream(zipFile);
			// Zip 파일 스트림
			zis = new ZipInputStream(new BufferedInputStream(fis, BUFFER_SIZE), Charset.forName("EUC-KR"));
			// Fentry가 없을때까지 뽑기
			while ((zipentry = zis.getNextEntry()) != null) {
				String zipentryName = zipentry.getName();
				File file = new File(path, zipentryName);
				// entiry가 폴더면 폴더 생성
				if (zipentry.isDirectory()) {
					file.mkdirs();
				} else {
					// 파일이면 파일 만들기
					createFile(file, zis);
				}
			}
		} catch (Throwable e) {
			// LOGGER.info(e.getMessage());
		} finally {
			if (zis != null)
				zis.close();
			if (fis != null)
				fis.close();
		}
		return path;
	}

	private static boolean createFile(File file, ZipInputStream zis) throws Throwable {

		// 디렉토리 확인
		File parentDir = new File(file.getParent());
		// 디렉토리가 없으면 생성하자
		if (!parentDir.exists()) {
			parentDir.mkdirs();
		}
		// 파일 스트림 선언
		boolean isTrue = false;
		try (FileOutputStream fos = new FileOutputStream(file)) {
			byte[] buffer = new byte[256];
			int size = 0;
			// Zip스트림으로부터 byte뽑아내기
			while ((size = zis.read(buffer)) > 0) {
				// byte로 파일 만들기
				fos.write(buffer, 0, size);
			}
			isTrue = true;
		} catch (Exception e) {
			isTrue = false;
		}
		return isTrue;
	}
}
