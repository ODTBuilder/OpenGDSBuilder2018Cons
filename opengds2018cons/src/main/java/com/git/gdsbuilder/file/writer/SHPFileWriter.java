package com.git.gdsbuilder.file.writer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
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
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.vividsolutions.jts.geom.Geometry;

public class SHPFileWriter {

	public static void writeSHP(String epsg, SimpleFeatureCollection simpleFeatureCollection, String filePath)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();

		File file = new File(filePath);
		Map map = Collections.singletonMap("url", file.toURI().toURL());
		ShapefileDataStore myData = (ShapefileDataStore) factory.createNewDataStore(map);
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

	public static void writeSHP(String epsg, ErrorLayer errLayer, String filePath)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		List<ErrorFeature> errList = errLayer.getErrFeatureList();

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

				// String featureIdx = "f_" + i;
				String geomType = errPoint.getGeometryType();
				String comment = err.getComment();

//				CoordinateReferenceSystem worldCRS = CRS.decode("EPSG:32652");
//				CoordinateReferenceSystem dataCRS = CRS.decode(epsg);
//				MathTransform transform = CRS.findMathTransform(worldCRS, dataCRS);
//				Geometry afterGeom = null;
//				try {
//					afterGeom = JTS.transform(errPoint, transform);
//				} catch (MismatchedDimensionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (TransformException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				SimpleFeatureType sfType = DataUtilities.createType(featureIdx,
						"layer:String,feature:String,refLayer:String,refFeature:String,errCode:String,errType:String,errName:String,comment:String,the_geom:"
								+ geomType);
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
