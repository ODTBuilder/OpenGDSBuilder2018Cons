package com.git.gdsbuilder.generalization;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;

public class GenerTEST {

	public static void main(String[] args) {

		File file = new File("D:\\일반화\\origin\\gis_osm_waterways.shp");
		SimpleFeatureCollection collection = getShpObject(file);

		DefaultFeatureCollection dfc = new DefaultFeatureCollection();
		SimpleFeatureIterator iter = collection.features();
		while (iter.hasNext()) {
			dfc.add(iter.next());
		}
		
		
		SimpleFeatureIterator newIter  = dfc.features();
		
		
		
		
		
		
		
		
		

//		File file = new File("D:\\일반화\\origin\\gis_osm_waterways.shp");
//		SimpleFeatureCollection collection = getShpObject(file);
//		DTLayer dtLayer = new DTLayer();
//		dtLayer.setSimpleFeatureCollection(collection);
//
//		JSONParser p = new JSONParser();
//		Object obj = null;
//		try {
//			obj = p.parse(new FileReader("D:\\일반화\\option.json"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		JSONObject param = (JSONObject) obj;
//		JSONObject preset = (JSONObject) param.get("preset");
//
//		List<GeneralizationOption> genalOpts = new ArrayList<>();
//		GeneralizationOption firOpt = new GeneralizationOption();
//		firOpt.createGeneraliationOpt(GeneralizationOption.FIR, (JSONObject) preset.get(GeneralizationOption.FIR));
//		GeneralizationOption secOpt = new GeneralizationOption();
//		secOpt.createGeneraliationOpt(GeneralizationOption.SEC, (JSONObject) preset.get(GeneralizationOption.SEC));
//		genalOpts.add(firOpt);
//		genalOpts.add(secOpt);
//
//		GeneralizationOperator operator = new GeneralizationOperator(dtLayer, genalOpts);
//		SimpleFeatureCollection sfc = operator.generalizate();
//
//		try {
//			writeSHP(sfc, "D:\\일반화\\result\\waterway\\test_gis_osm_waterways.shp", "EPSG:4326");
//		} catch (IOException | SchemaException | FactoryException e) {
//			e.printStackTrace();
//		}

	}

	private static SimpleFeatureCollection getShpObject(File file) {

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

	private static void writeSHP(SimpleFeatureCollection sfc, String filePath, String epsg)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		ShapefileDataStoreFactory factory = new ShapefileDataStoreFactory();
		File file = new File(filePath);
		Map map = Collections.singletonMap("url", file.toURI().toURL());
		ShapefileDataStore myData = (ShapefileDataStore) factory.createNewDataStore(map);
		myData.setCharset(Charset.forName("EUC-KR"));
		SimpleFeatureType featureType = sfc.getSchema();
		myData.createSchema(featureType);
		Transaction transaction = new DefaultTransaction("create");
		String typeName = myData.getTypeNames()[0];
		myData.forceSchemaCRS(CRS.decode(epsg));

		SimpleFeatureSource featureSource = myData.getFeatureSource(typeName);
		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(sfc);
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
