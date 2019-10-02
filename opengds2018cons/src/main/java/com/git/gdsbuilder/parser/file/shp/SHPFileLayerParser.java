package com.git.gdsbuilder.parser.file.shp;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.transform.Definition;
import org.geotools.data.transform.TransformFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.ecql.ECQL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.git.gdsbuilder.type.dt.layer.DTLayer;

/**
 * 1개의 SHP 파일을 {@link DTLayer} 객체로 변환하는 클래스.
 * 
 * @author DY.Oh
 *
 */
public class SHPFileLayerParser {

	/**
	 * 1개의 SHP {@link File} 객체를 {@link DTLayer} 객체로 변환하는 클래스
	 * 
	 * @param epsg 좌표계 (ex EPSG:4326)
	 * @param file shp 파일 객체
	 * @return {@link DTLayer}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
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

	private SimpleFeatureCollection getShpObject(String epsg, File file, String shpName) {

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
		} catch (Exception e) {
			return null;
		}
		return collection;
	}

	/**
	 * 특정 경로의 1개 SHP 파일을 {@link DTLayer} 객체로 변환하는 클래스.
	 * 
	 * @param epsg     좌표계 (ex EPSG:4326)
	 * @param filePath SHP 파일 경로
	 * @param shpName  SHP 파일명
	 * @return {@link DTLayer}
	 * @throws Exception {@link Exception}
	 * 
	 * @author DY.Oh
	 */
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

	private SimpleFeatureCollection getShpObject(String epsg, String filePath, String shpName) {

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
			beforeStore.dispose();
			beforeStore = null;
			source = null;
			return collection;
		} catch (Exception e) {
			return null;
		}
	}

	public SimpleFeatureCollection getShpObject(String string, File file, String fileName, JSONArray attrFilterArry,
			JSONArray stateFilterArry) {

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
			SimpleFeatureType sft = source.getSchema();

			// set filter
			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
			Filter attrFilter = null;
			Filter stateFilter = null;
			// attr filter
			if (attrFilterArry != null) {
				List<Filter> attrFilterList = new ArrayList<>();
				for (int i = 0; i < attrFilterArry.size(); i++) {
					JSONObject filterAttr = (JSONObject) attrFilterArry.get(i);
					String key = (String) filterAttr.get("key");
					if (sft.getDescriptor(key) != null) {
						List<Object> values = (List<Object>) filterAttr.get("values");
						for (Object value : values) {
							Filter filterUp = ff.equals(ff.property(key), ff.literal(value));
							attrFilterList.add(filterUp);
						}
						Filter filterNull = ff.equals(ff.property(key), ff.literal(""));
						attrFilterList.add(filterNull);
					}
				}
				if (attrFilterList.size() > 0) {
					attrFilter = ff.or(attrFilterList);
				}
			}
			// state filter
			if (stateFilterArry != null) {
				List<Filter> stateFilterList = new ArrayList<>();
				for (int i = 0; i < stateFilterArry.size(); i++) {
					JSONObject filterAttr = (JSONObject) stateFilterArry.get(i);
					String key = (String) filterAttr.get("key");
					if (sft.getDescriptor(key) != null) {
						List<Object> values = (List<Object>) filterAttr.get("values");
						for (Object value : values) {
							Filter filter = ff.equals(ff.property(key), ff.literal(value));
							stateFilterList.add(filter);
						}
						Filter filterNull = ff.equals(ff.property(key), ff.literal(""));
						stateFilterList.add(filterNull);
					}
				}
				if (stateFilterList.size() > 0) {
					stateFilter = ff.or(stateFilterList);
				}
			}
			// attribute name toUpper
			List<Definition> definitions = new ArrayList<Definition>();
			boolean state = false;
			List<AttributeDescriptor> attrDescs = sft.getAttributeDescriptors();
			for (AttributeDescriptor attrDesc : attrDescs) {
				String attrName = attrDesc.getName().toString();
				if (attrName.equalsIgnoreCase("the_geom")) {
					definitions.add(new Definition(attrName, ECQL.toExpression(attrName)));
				} else {
					definitions.add(new Definition(attrName.toUpperCase(), ECQL.toExpression(attrName)));
				}
				if (attrName.equals("state")) {
					state = true;
				}
			}
			SimpleFeatureSource transformed = TransformFactory.transform(source, source.getName(), definitions);

			// filter
			List<Filter> filterList = new ArrayList<>();
			filterList.add(Filter.INCLUDE);
			filterList.add(ff.notEqual(ff.property("the_geom"), ff.literal(null)));
			if (attrFilter != null) {
				filterList.add(attrFilter);
			}
			if (state && stateFilter != null) {
				filterList.add(stateFilter);
			}
			Filter commonFilter = ff.and(filterList);
			collection = transformed.getFeatures(commonFilter);
			beforeStore.dispose();
			beforeStore = null;
		} catch (Exception e) {
			return null;
		}
		return collection;
	}
}
