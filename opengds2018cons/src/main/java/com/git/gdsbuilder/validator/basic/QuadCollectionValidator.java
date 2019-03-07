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

/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    OpenGDS_2018
 *    Lesser General Public License for more details.
 */

package com.git.gdsbuilder.validator.basic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
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
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.git.gdsbuilder.quadtree.OptimalEnvelopsOp;
import com.git.gdsbuilder.quadtree.Quadtree;
import com.git.gdsbuilder.quadtree.Root;
import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.dt.layer.DTQuadLayer;
import com.git.gdsbuilder.type.dt.layer.DTQuadLayerList;
import com.git.gdsbuilder.type.validate.layer.QALayerType;
import com.git.gdsbuilder.type.validate.layer.QALayerTypeList;
import com.git.gdsbuilder.type.validate.option.QAOption;
import com.git.gdsbuilder.type.validate.option.specific.GraphicMiss;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionRelation;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.TopologyException;

/**
 * ValidateLayerCollectionList를 검수하는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 4. 18. 오후 3:30:17
 */
public class QuadCollectionValidator {

	BasicErrorLayer errLayer;
	DTLayerCollection collection;
	QALayerTypeList types;
	Map<String, Object> progress;
	String collectionType;
	int maxFeatureCount;

	/**
	 * CollectionValidator 생성자
	 * 
	 * @param closeCollections
	 * 
	 * @param validateLayerCollectionList
	 * @throws NoSuchAuthorityCodeException
	 * @throws SchemaException
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws IOException
	 */
	public QuadCollectionValidator(DTLayerCollection collection, QALayerTypeList types, int maxFeatureCount) {

		this.types = types;
		this.errLayer = new BasicErrorLayer();
		this.progress = new HashMap<String, Object>();
		this.maxFeatureCount = maxFeatureCount;

		DTLayerList dtLayers = collection.getLayers();
		DTQuadLayerList quadLayers = new DTQuadLayerList();
		for (DTLayer dtLayer : dtLayers) {
			SimpleFeatureCollection sfc = dtLayer.getSimpleFeatureCollection();
			System.out.println(sfc.size());
			Quadtree quadtree = new Quadtree();
			try {
				sfc.accepts(new FeatureVisitor() {
					@Override
					public void visit(Feature feature) {
						SimpleFeature simpleFeature = (SimpleFeature) feature;
						Geometry geom = (Geometry) simpleFeature.getDefaultGeometry();
						// Just in case: check for null or empty geometry
						if (geom != null) {
							Envelope env = geom.getEnvelopeInternal();
							if (!env.isNull()) {
								quadtree.insert(env, simpleFeature);
							}
						}
					}
				}, new NullProgressListener());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DTQuadLayer quadLayer = new DTQuadLayer();
			quadLayer.setLayerID(dtLayer.getLayerID());
			quadLayer.setLayerType(dtLayer.getLayerType());
			quadLayer.setFilter(dtLayer.getFilter());
			quadLayer.setSimpleFeatureCollection(sfc);
			quadLayer.setQuadtree(quadtree);
			quadLayers.add(quadLayer);
		}
		collection.setQuadlyers(quadLayers);
		collection.setLayers(null);
		this.collection = collection;
	}

	public void validate() {

		DTLayerCollection collection = this.collection;
		QALayerTypeList qaTypes = this.types;

		try {
			this.errLayer = new BasicErrorLayer();
			errLayer.setCollectionName(collection.getCollectionName());
			errLayer.setCollectionType(this.collectionType);

//			int layerCount = collection.getLayers().size();
//			int featureCount = getTotalFeatureCount(collection);
//
//			errLayer.setLayerCount(layerCount);
//			errLayer.setFeatureCount(featureCount);

			DTQuadLayerList quadLayers = collection.getQuadlyers();
			for (DTQuadLayer quadLayer : quadLayers) {
				String layerId = quadLayer.getLayerID();
				for (QALayerType qaType : qaTypes) {
					List<String> layerList = qaType.getLayerIDList();
					boolean isTrue = false;
					for (String layer : layerList) {
						if (layerId.equals(layer)) {
							isTrue = true;
						}
					}
					String typeName = qaType.getName();
					if (isTrue) {
						Quadtree quadtree = quadLayer.getQuadtree();
						Root root = quadtree.getRoot();
						int maxLevel = root.maxLevel();
						OptimalEnvelopsOp op = new OptimalEnvelopsOp(quadtree, maxLevel, maxFeatureCount);
						List<Object> results = op.getOptimalEnvelops(maxLevel);

						int n = 1;
						int ns = results.size();
						for (Object result : results) {
							Envelope envelope = (Envelope) result;
							List items = quadtree.query(envelope);
							DefaultFeatureCollection dfc = new DefaultFeatureCollection();
							int size = items.size();
							for (int i = 0; i < size; i++) {
								SimpleFeature sf = getIntersection(envelope, (SimpleFeature) items.get(i));
								if (sf != null) {
									dfc.add(sf);
								}
//								if (isContains(envelope, f)) {
//									dfc.add(f);
//								}
							}
							DTLayer targetLayer = new DTLayer();
							targetLayer.setSimpleFeatureCollection(dfc);
							targetLayer.setLayerID(quadLayer.getLayerID());
							targetLayer.setFilter(quadLayer.getFilter());
							targetLayer.setTypeName(typeName);
							quadLayer.getLayerType();
							BasicLayerValidator layerValidator = new BasicLayerValidator(targetLayer);
							QAOption qaOption = qaType.getOption();
							// 1.개념일관성
							// 가.스키마 규칙 준수
//							List<LayerFixMiss> layerFixMissArr = qaOption.getLayerMissOptions();
//							if (layerFixMissArr != null) {
//								for (LayerFixMiss fix : layerFixMissArr) {
//									String geometry = fix.getGeometry();
//									BasicErrorLayer typeErr = layerValidator.validateLayerFixMiss(geometry,
//											fix.getFix());
//									if (typeErr != null) {
//										errLayer.mergeErrorLayer(typeErr);
//									}
//								}
//							}
//							List<AttributeMiss> attrMissArr = qaOption.getAttributeMissOptions();
//							if (attrMissArr != null) {
//								for (AttributeMiss attr : attrMissArr) {
//									String optionName = attr.getOption();
//									OptionFilter filter = attr.getLayerFilter(layerId);
//									OptionFigure figure = attr.getLayerFigure(layerId);
//									if (optionName.equals("NumericalValues")) {
//										BasicErrorLayer typeErr = layerValidator.validateNumericalValues(filter,
//												figure);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("FixValues")) {
//										BasicErrorLayer typeErr = layerValidator.validateFixValues(filter, figure);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//								}
//							}
							List<GraphicMiss> grapMissArr = qaOption.getGraphicMissOptions();
							if (grapMissArr != null) {
								for (GraphicMiss grapMiss : grapMissArr) {
									String optionName = grapMiss.getOption();
									OptionFilter filter = grapMiss.getLayerFilter(layerId);
									OptionTolerance tolerance = grapMiss.getLayerTolerance(layerId);
									List<OptionRelation> relations = grapMiss.getRetaion();
//									if (optionName.equals("EntityTwisted")) {
//										BasicErrorLayer typeErr = layerValidator.validateEntityTwisted(filter);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("SmallArea")) {
//										BasicErrorLayer typeErr = layerValidator.validateSmallArea(filter, tolerance);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("SmallLength")) {
//										BasicErrorLayer typeErr = layerValidator.validateSmallLength(filter, tolerance);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("EntityDuplicated")) {
//										BasicErrorLayer typeErr = layerValidator.validateEntityDuplicated(filter);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("PointDuplicated")) {
//										BasicErrorLayer typeErr = layerValidator.validatePointDuplicated(filter);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("ConIntersected")) {
//										BasicErrorLayer typeErr = layerValidator.validateConIntersected(filter);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
//									if (optionName.equals("ConOverDegree")) {
//										BasicErrorLayer typeErr = layerValidator.validateConOverDegree(tolerance);
//										if (typeErr != null) {
//											errLayer.mergeErrorLayer(typeErr);
//										}
//									}
									if (optionName.equals("SelfEntity")) {
										if (relations != null) {
											DTQuadLayerList reQuadLayerList = null;
											for (OptionRelation relation : relations) {
												String reName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter refilter : reFilters) {
														String refilterCode = refilter.getCode();
														DTQuadLayer reLayer = this.collection
																.getQuadLayer(refilterCode);
														reQuadLayerList.add(reLayer);
													}
												} else {
													reQuadLayerList = types.getTypeQuadLayers(reName, this.collection);
												}
												for (DTQuadLayer reQuadLayer : reQuadLayerList) {
													Quadtree reQuadtree = reQuadLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
//															SimpleFeature sf = (SimpleFeature) reItems.get(i);
//															if (isContains(envelope, sf)) {
//																reDfc.add(sf);
//															}
															SimpleFeature sf = getIntersection(envelope,
																	(SimpleFeature) reItems.get(i));
															if (sf != null) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(reQuadLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reDtLayer.setTypeName(reName);
														reDtLayer.setFilter(filter);
														BasicErrorLayer typeErr = layerValidator
																.validateSelfEntity(filter, tolerance, reDtLayer);
														if (typeErr != null) {
															errLayer.mergeErrorLayer(typeErr);
														}
													}
												}
											}
										}
									}
//									if (optionName.equals("NodeMiss")) {
//										if (relations != null) {
//											DTQuadLayerList reQuadLayerList = null;
//											for (OptionRelation relation : relations) {
//												String reName = relation.getName();
//												List<OptionFilter> reFilters = relation.getFilters();
//												if (reFilters != null) {
//													for (OptionFilter refilter : reFilters) {
//														String refilterCode = refilter.getCode();
//														DTQuadLayer reLayer = this.collection
//																.getQuadLayer(refilterCode);
//														reQuadLayerList.add(reLayer);
//													}
//												} else {
//													reQuadLayerList = types.getTypeQuadLayers(reName, this.collection);
//												}
//												DTLayerList reLayerList = new DTLayerList();
//												for (DTQuadLayer reQuadLayer : reQuadLayerList) {
//													Quadtree reQuadtree = reQuadLayer.getQuadtree();
//													List reItems = reQuadtree.query(envelope);
//													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
//													int reSize = reItems.size();
//													if (reSize > 0) {
//														for (int i = 0; i < reSize; i++) {
//															SimpleFeature sf = (SimpleFeature) reItems.get(i);
//															if (isContains(envelope, sf)) {
//																reDfc.add(sf);
//															}
//														}
//														DTLayer reDtLayer = new DTLayer();
//														reDtLayer.setLayerID(reQuadLayer.getLayerID());
//														reDtLayer.setSimpleFeatureCollection(reDfc);
//														reDtLayer.setTypeName(reName);
//														reDtLayer.setFilter(filter);
//														reLayerList.add(reDtLayer);
//														BasicErrorLayer typeErr = layerValidator
//																.validateNodeMiss(filter, tolerance, reQuadLayer);
//														if (typeErr != null) {
//															errLayer.mergeErrorLayer(typeErr);
//														}
//													}
//												}
//											}
//										}
//									}
								}
							}
							System.out.println(layerId + " : " + ns + " 중 " + n + " 개 완료");
							n++;
						}
					}
				}
			}
			writeSHP("EPSG:5179", errLayer, "D:\\새 폴더\\song2\\err\\test2.shp");
			progress.put(collection.getCollectionName(), 2);
		} catch (Exception e) {
			e.printStackTrace();
			progress.put(collection.getCollectionName(), 3);
		}

	}

	private int getTotalFeatureCount(DTLayerCollection collection) {

		int totalCount = 0;
		DTLayerList layers = collection.getLayers();
		for (DTLayer layer : layers) {
			totalCount += layer.getSimpleFeatureCollection().size();
		}
		return totalCount;
	}

	private boolean isContains(Envelope envelope, SimpleFeature sf) {

		GeometryFactory f = new GeometryFactory();
		Geometry envelpoeGeom = f.toGeometry(envelope);
		Geometry sfGeom = (Geometry) sf.getDefaultGeometry();

		return envelpoeGeom.intersects(sfGeom);
	}

	private SimpleFeature getIntersection(Envelope envelope, SimpleFeature sf) {

		GeometryFactory f = new GeometryFactory();
		Geometry envelpoeGeom = f.toGeometry(envelope);
		Geometry sfGeom = (Geometry) sf.getDefaultGeometry();
		Geometry interGeom = null;
		try {
			interGeom = envelpoeGeom.intersection(sfGeom);
		} catch (TopologyException e) {
			// TODO: handle exception
		}
		if (interGeom != null) {
			sf.setDefaultGeometry(interGeom);
			return sf;
		} else {
			return null;
		}
	}

	public static void writeSHP(String epsg, BasicErrorLayer errLayer, String filePath)
			throws IOException, SchemaException, NoSuchAuthorityCodeException, FactoryException {

		DefaultFeatureCollection collection = new DefaultFeatureCollection();
		List<BasicErrorFeature> errList = errLayer.getErrFeatureList();

		SimpleFeatureType sfType = DataUtilities.createType("err",
				"validation:String,type:String,item:String,name:String,layerID:String,refLayerId:String,geomStr:String,the_geom:Point");

		if (errList.size() > 0) {
			for (int i = 0; i < errList.size(); i++) {
				BasicErrorFeature err = errList.get(i);
				String featureId = "f" + String.valueOf(i);
				Geometry errPoint = err.getErrPoint();
				SimpleFeature sf = SimpleFeatureBuilder.build(sfType,
						new Object[] { err.getValidation(), err.getType(), err.getItem(), err.getName(),
								err.getLayerID(), err.getRefLayerId(), err.getGeomStr(), errPoint },
						featureId);
				collection.add(sf);
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
