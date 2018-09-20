package com.git.gdsbuilder.validator.fileReader.shp.parser;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;

import com.git.gdsbuilder.type.dt.layer.DTLayer;

public class SHPFileLayerParser {

	public DTLayer parseDTLayer(String epsg, File file) throws Exception {

		String fileName = file.getName();
		int Idx = fileName.lastIndexOf(".");
		String layerName = fileName.substring(0, Idx);
		SimpleFeatureCollection collection = getShpObject(epsg, file, layerName);
		if (collection != null) {
			DTLayer layer = new DTLayer();
			SimpleFeatureType featureType = collection.getSchema();
			GeometryType geometryType = featureType.getGeometryDescriptor().getType();
			String geomType = geometryType.getBinding().getSimpleName().toString();
			layer.setLayerID(layerName);
			layer.setLayerType(geomType);
			layer.setSimpleFeatureCollection(collection);
			return layer;
		} else {
			return null;
		}
	}

	public SimpleFeatureCollection getShpObject(String epsg, File file, String shpName) {

		ShapefileDataStore beforeStore = null;
		try {
			Map<String, Object> beforeMap = new HashMap<String, Object>();
			beforeMap.put("url", file.toURI().toURL());
			beforeStore = (ShapefileDataStore) DataStoreFinder.getDataStore(beforeMap);
			Charset euckr = Charset.forName("EUC-KR");
			beforeStore.setCharset(euckr);
			String typeName = beforeStore.getTypeNames()[0];
			SimpleFeatureSource source = beforeStore.getFeatureSource(typeName);
			Filter filter = Filter.INCLUDE;
			SimpleFeatureCollection collection = source.getFeatures(filter);
			return collection;
		} catch (Exception e) {
			return null;
		}
	}

	public DTLayer parseDTLayer(String epsg, String filePath, String shpName) throws Exception {

		SimpleFeatureCollection collection = getShpObject(epsg, filePath, shpName);

		if (collection != null) {
			DTLayer layer = new DTLayer();
			SimpleFeatureType featureType = collection.getSchema();
			GeometryType geometryType = featureType.getGeometryDescriptor().getType();
			String geomType = geometryType.getBinding().getSimpleName().toString();
			String layerName = shpName;
			layer.setLayerID(layerName);
			layer.setLayerType(geomType);
			layer.setSimpleFeatureCollection(collection);
			return layer;
		} else {
			return null;
		}
	}

	public SimpleFeatureCollection getShpObject(String epsg, String filePath, String shpName) {

		ShapefileDataStore beforeStore = null;
		try {
			// before
			File beforeFile = new File(filePath);
			if (beforeFile.isDirectory()) {
				beforeFile = new File(filePath, shpName + ".shp");
			}
			Map<String, Object> beforeMap = new HashMap<String, Object>();
			beforeMap.put("url", beforeFile.toURI().toURL());
			beforeStore = (ShapefileDataStore) DataStoreFinder.getDataStore(beforeMap);
			Charset euckr = Charset.forName("EUC-KR");
			beforeStore.setCharset(euckr);
			String typeName = beforeStore.getTypeNames()[0];
			SimpleFeatureSource source = beforeStore.getFeatureSource(typeName);
			Filter filter = Filter.INCLUDE;
			SimpleFeatureCollection collection = source.getFeatures(filter);
			return collection;
		} catch (Exception e) {
			return null;
		}
	}
}
