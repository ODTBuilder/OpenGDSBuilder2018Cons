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

package com.git.gdsbuilder.validator.collection;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;
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
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.layer.QALayerType;
import com.git.gdsbuilder.type.validate.layer.QALayerTypeList;
import com.git.gdsbuilder.type.validate.option.QAOption;
import com.git.gdsbuilder.type.validate.option.specific.AttributeMiss;
import com.git.gdsbuilder.type.validate.option.specific.GraphicMiss;
import com.git.gdsbuilder.type.validate.option.specific.OptionFigure;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionRelation;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.git.gdsbuilder.type.validate.option.standard.FixedValue;
import com.git.gdsbuilder.type.validate.option.standard.LayerFixMiss;
import com.git.gdsbuilder.validator.layer.LayerValidator;
import com.git.gdsbuilder.validator.layer.LayerValidatorImpl;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * ValidateLayerCollectionList를 검수하는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 4. 18. 오후 3:30:17
 */
public class QuadCollectionValidator {

	ErrorLayer errLayer;
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
		this.errLayer = new ErrorLayer();
		this.progress = new HashMap<String, Object>();
		this.maxFeatureCount = maxFeatureCount;

		DTLayerList dtLayers = collection.getLayers();
		DTQuadLayerList quadLayers = new DTQuadLayerList();
		for (DTLayer dtLayer : dtLayers) {
			SimpleFeatureCollection sfc = dtLayer.getSimpleFeatureCollection();
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
			this.errLayer = new ErrorLayer();
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
								SimpleFeature f = (SimpleFeature) items.get(i);
								if (isContains(envelope, f)) {
									dfc.add(f);
								}
							}
							DTLayer targetLayer = new DTLayer();
							targetLayer.setSimpleFeatureCollection(dfc);
							targetLayer.setLayerID(quadLayer.getLayerID());
							targetLayer.setFilter(quadLayer.getFilter());
							quadLayer.getLayerType();
							LayerValidatorImpl layerValidator = new LayerValidatorImpl(targetLayer);
							QAOption qaOption = qaType.getOption();
							// 1.개념일관성
							// 가.스키마 규칙 준수
							List<LayerFixMiss> layerFixMissList = qaOption.getLayerMissOptions();
							if (layerFixMissList != null) {
								for (LayerFixMiss layerFixMiss : layerFixMissList) {
									String code = layerFixMiss.getCode();
									String option = layerFixMiss.getOption();
									DTQuadLayer dtLayer = collection.getQuadLayer(code);
									if (dtLayer == null) {
										continue;
									}
									if (option.equals("LayerFixMiss")) { // tmp -> enum으로 대체
										String geometry = layerFixMiss.getGeometry();
										List<FixedValue> fixedValue = layerFixMiss.getFix();
										if (fixedValue == null) {
											continue;
										} else {
											if (fixedValue.size() > 0) {
												ErrorLayer typeErrorLayer = layerValidator
														.validateLayerFixMiss(geometry, fixedValue);
												if (typeErrorLayer != null) {
													errLayer.mergeErrorLayer(typeErrorLayer);
												}
											}
										}
									}
								}
							}
							// 2.도메인일관성
							// 가.코드리스트 규칙준수
							// 나.숫자오류
							// 다.범위불일치
							List<AttributeMiss> attributeMissList = qaOption.getAttributeMissOptions();
							if (attributeMissList != null) {
								for (AttributeMiss attributeMiss : attributeMissList) {
									// filter
									List<OptionFilter> filters = attributeMiss.getFilter();
									if (filters != null) {
										for (OptionFilter filter : filters) {
											String code = filter.getCode();
											if (code.equals(layerId)) {
												quadLayer.setFilter(filter);
											}
										}
									}
									// figure
									List<OptionFigure> figures = attributeMiss.getFigure();
									// option
									String option = attributeMiss.getOption();
									if (option.equals("NumericalValue")) {
										if (figures != null) {
											for (OptionFigure figure : figures) {
												String code = figure.getCode();
												ErrorLayer typeErrorLayer = new ErrorLayer();
												if (code == null) {
													typeErrorLayer = layerValidator.validateNumericalValue(figure);
												} else {
													if (code.equals(layerId)) {
														typeErrorLayer = layerValidator.validateNumericalValue(figure);
													}
												}
												if (typeErrorLayer != null) {
													this.errLayer.mergeErrorLayer(typeErrorLayer);
												}
											}
										}
									}
								}
							}
							// 3.포맷일관성
							// 4.위상일관성
							List<GraphicMiss> graphicMissList = qaOption.getGraphicMissOptions();
							if (graphicMissList != null) {
								for (GraphicMiss graphicMiss : graphicMissList) {
									// tolerance
									List<OptionTolerance> toleranceList = graphicMiss.getTolerance();
									OptionTolerance tolerance = null;
									if (toleranceList != null) {
										for (OptionTolerance tole : toleranceList) {
											String code = tole.getCode();
											if (code != null) {
												if (code.equals(layerId)) {
													tolerance = tole;
												}
											}
										}
									}
									String option = graphicMiss.getOption();
									// 가.도형형상
									if (option.equals("TwistedPolygon")) { // 꼬임선형 객체
										ErrorLayer typeErrorLayer = layerValidator.validateTwistedPolygon();
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									if (option.equals("SmallArea")) { // 미세면적 객체
										ErrorLayer typeErrorLayer = layerValidator.validateSmallArea(tolerance);
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									if (option.equals("NodeMiss")) { // 언더슛
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateNodeMiss(reLayerList, tolerance);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									if (option.equals("OverShoot")) { // 오버슛
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateOverShoot(reLayerList, tolerance);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									if (option.equals("PointDuplicated")) { // 정점중복선형 객체
										ErrorLayer typeErrorLayer = layerValidator.validatePointDuplicated();
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									if (option.equals("SmallArea")) { // 짧은선형 객체
										ErrorLayer typeErrorLayer = layerValidator.validateSmallLength(tolerance);
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									// 나.객체 중복
									if (option.equals("EntityDuplicated")) { // 중복객체
										ErrorLayer typeErrorLayer = layerValidator.validateEntityDuplicated();
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									// 다.등고선 오류
									if (option.equals("ConIntersected")) { // 등고선 교차
										ErrorLayer typeErrorLayer = layerValidator.validateConIntersected();
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									if (option.equals("ConOverDegree")) { // 등고선 꺾임
										ErrorLayer typeErrorLayer = layerValidator.validateConOverDegree(tolerance);
										if (typeErrorLayer != null) {
											this.errLayer.mergeErrorLayer(typeErrorLayer);
										}
									}
									if (option.equals("ConBreak")) { // 등고선 단락
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateConBreak(reLayerList, tolerance);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									if (option.equals("")) { // 등고선 미연결

									}
									// 라.중심선 누락
									if (option.equals("CenterLineMiss")) {
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateCenterLineMiss(reLayerList);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									// 마.중심선 속성 불일치
									// 바.경계 불일치
									if (option.equals("OutBoundary")) {
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateOutBoundary(reLayerList, tolerance);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									// 사. 경계 침범
									if (option.equals("SelfEntity")) {
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateSelfEntity(reLayerList, tolerance);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									// 아.홀(Hole) 폴리곤
									if (option.equals("EntityInHole")) {
										List<OptionRelation> relationList = graphicMiss.getRetaion();
										if (relationList != null) {
											DTQuadLayerList relationLayers = null;
											for (OptionRelation relation : relationList) {
												String relationName = relation.getName();
												List<OptionFilter> reFilters = relation.getFilters();
												if (reFilters != null) {
													for (OptionFilter filter : reFilters) {
														String filterCode = filter.getCode();
														DTQuadLayer relationLayer = types.getTypeQuadLayer(relationName,
																filterCode, collection);
														relationLayer.setFilter(filter);
														relationLayers.add(relationLayer);
													}
												} else {
													relationLayers = types.getTypeQuadLayers(relationName, collection);
												}
												DTLayerList reLayerList = new DTLayerList();
												for (DTQuadLayer relationLayer : relationLayers) {
													Quadtree reQuadtree = relationLayer.getQuadtree();
													List reItems = reQuadtree.query(envelope);
													DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
													int reSize = reItems.size();
													if (reSize > 0) {
														for (int i = 0; i < reSize; i++) {
															SimpleFeature sf = (SimpleFeature) reItems.get(i);
															if (isContains(envelope, sf)) {
																reDfc.add(sf);
															}
														}
														DTLayer reDtLayer = new DTLayer();
														reDtLayer.setLayerID(relationLayer.getLayerID());
														reDtLayer.setSimpleFeatureCollection(reDfc);
														reLayerList.add(reDtLayer);
														ErrorLayer typeErrorLayer = layerValidator
																.validateEntityInHole(reLayerList);
														if (typeErrorLayer != null) {
															this.errLayer.mergeErrorLayer(typeErrorLayer);
														}
													}
												}
											}
										}
									}
									// 자.NFID 무결성
								}
							}
							System.out.println(layerId + " : " + ns + " 중 " + n + " 개 완료");
							n++;
						}
					}
				}
			}
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

}
