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
package com.git.gdsbuilder.file.writer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 
 * 오류 레이어를 Point 타입의 SHP 파일로 생성하는 클래스.
 * 
 * @author DY.OH
 */
public class SHPFileWriter {

	/**
	 * 
	 * 오류 레이어를 Point 타입의 SHP 포맷 파일로 생성.
	 * 
	 * <p>
	 * <em>example use:</em>
	 * 
	 * <pre>
	 * <code>
	 * SHPFileWriter.writeSHP("EPSG:4326", errLayer, "C:\\data\\example.shp")
	 * </code>
	 * </pre>
	 * 
	 * @param epsg     SHP 파일 좌표계 ex) EPSG:4326
	 * @param errLayer 오류 레이어
	 * @param filePath 파일 경로
	 * @throws IOException
	 * @throws SchemaException
	 * @throws NoSuchAuthorityCodeException
	 * @throws FactoryException
	 * 
	 * @author DY.OH
	 */
	public static void writeSHP(String epsg, ErrorLayer errLayer, String filePath)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		List<ErrorFeature> errList = errLayer.getErrFeatureList();

		SimpleFeatureType sfType = DataUtilities.createType("ErrorLayer",
				"layer:String,feature:String,refLayer:String,refFeature:String,errCode:String,errType:String,errName:String,comment:String,the_geom:Point");

		if (errList.size() > 0) {
			for (int i = 0; i < errList.size(); i++) {
				ErrorFeature err = errList.get(i);
				String layerID = err.getLayerID();
				String featureID = err.getFeatureID();
				String refLayerId = err.getRefLayerId();
				String refFeatureId = err.getRefFeatureId();

				String errCode = err.getErrCode();
				String errType = err.getErrType();
				String errName = err.getErrName();
				Geometry errPoint = err.getErrPoint();

				String featureIdx = errCode + "_" + featureID;
				String comment = err.getComment();

				SimpleFeature newSimpleFeature = SimpleFeatureBuilder.build(sfType, new Object[] { layerID, featureID,
						refLayerId, refFeatureId, errCode, errType, errName, comment, errPoint }, featureIdx);

				collection.add(newSimpleFeature);
			}

			ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
			File file = new File(filePath);
			Map map = Collections.singletonMap("url", file.toURI().toURL());
			ShapefileDataStore myData = (ShapefileDataStore) factory.createNewDataStore(map);
			myData.setCharset(Charset.forName("EUC-KR"));
			SimpleFeatureType featureType = collection.getSchema();
			myData.createSchema(featureType);
			Transaction transaction = new DefaultTransaction("create");
			String typeName = myData.getTypeNames()[0];
			myData.forceSchemaCRS(CRS.decode(epsg));

			SimpleFeatureSource featureSource = myData.getFeatureSource(typeName);
			if (featureSource instanceof SimpleFeatureStore) {
				SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
				featureStore.setTransaction(transaction);
				try {
					featureStore.addFeatures(collection);
					transaction.commit();
				} catch (Exception e) {
					e.printStackTrace();
					transaction.rollback();
				} finally {
					transaction.close();
				}
			}
		}
	}
}
