package com.git.gdsbuilder.validator.open;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.option.AttributeFigure;
import com.git.gdsbuilder.type.validate.option.AttributeFilter;
import com.git.gdsbuilder.type.validate.option.FixedValue;
import com.git.gdsbuilder.type.validate.option.OptionFigure;
import com.git.gdsbuilder.type.validate.option.OptionFilter;
import com.git.gdsbuilder.type.validate.option.OptionTolerance;
import com.git.gdsbuilder.validator.feature.FeatureFilter;
import com.git.gdsbuilder.validator.open.OpenQAOptions.LangType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.TopologyException;

public class OpenFeatureAttributeValidator {

	LangType langType;

	public OpenFeatureAttributeValidator(LangType langType) {
		this.langType = langType;
	}

	// 속성오류 (Attribute Fix)
	public ErrorFeature validateAttributeFixMiss(DTFeature feature, List<FixedValue> fixArry) {

		SimpleFeature sf = feature.getSimefeature();
		String comment = null;

		for (FixedValue fix : fixArry) {
			String key = fix.getKey();
			String type = fix.getType();
			String length = fix.getLength();
			Boolean flag = fix.isIsnull();
			List<Object> valueObjs = fix.getValues();
			SimpleFeatureType sft = sf.getFeatureType();
			AttributeDescriptor attrDesc = sft.getDescriptor(key);
			if (attrDesc == null) {
				// 테이블 컬럼 규칙 비준수
				comment = "ERR_CLUNM_NULL" + "(" + key + ")";
				break;
			}
			// 컬럼타입 불일치
			if (type != null) {
				AttributeType attrType = attrDesc.getType();
				String typeStr = attrType.getBinding().getSimpleName();
				String innerType = null;
				if (type.equals("VARCHAR")) {
					innerType = "String";
				}
				if (type.equals("NUMERIC")) {
					innerType = "Integer/Double";
				}
				if (type.equals("TIMESTAMP")) {
					innerType = "Date";
				}
				if (typeStr.equals("Long")) {
					typeStr = "Integer";
				}
				if (!innerType.contains(typeStr)) {
					comment = "ERR_CLUNM_TYPE" + "(" + key + ")";
					break;
				}
			}
			// 필수 컬럼 속성 누락
			Object attrObj = sf.getAttribute(key);
			boolean isNull = false;
			if (flag != null) {
				if (flag == false) {
					if (attrObj == null) {
						isNull = true;
						comment = "ERR_VALUE_NULL" + "(" + key + ")";
						break;
					} else {
						String attrStr = attrObj.toString();
						if (attrStr.equals("")) {
							isNull = true;
							comment = "ERR_VALUE_NULL" + "(" + key + ")";
							break;
						} else {
							if (length != null) {
								if (type.equals("NUMERIC")) {
									if (length.contains(",")) {
										String[] lengthArr = length.split(",");
										String p = lengthArr[0];
										String s = lengthArr[1];
										if (p != null) {
											int pLength = Integer.parseInt(p);
											int attrLength = attrStr.replace(".", "").length();
											if (pLength < attrLength || attrLength == 0) {
												comment = "ERR_CLUNM_TYPE" + "(" + key + ")";
												break;
											}
										}
										if (s != null) {
											int sLength = Integer.parseInt(s);
											int idx = attrStr.indexOf(".");
											String attrS = attrStr.substring(idx);
											int attrSLength = attrS.length();
											if (sLength < attrSLength || attrSLength == 0) {
												comment = "ERR_CLUNM_TYPE" + "(" + key + ")";
												break;
											}
										}
									} else {
										int lengthInt = Integer.parseInt(length);
										int attrLength = attrStr.length();
										if (lengthInt < attrLength || attrLength == 0) {
											comment = "ERR_CLUNM_TYPE" + "(" + key + ")";
											break;
										}
									}
								} else if (type.equals("VARCHAR")) {
									int lenghtInt = Integer.parseInt(length);
									int valueLength = attrStr.length();
									if (valueLength != lenghtInt) {
										comment = "ERR_CLUNM_TYPE" + "(" + key + ")";
										break;
									}
								}
							}
						}
					}
				}
				// 코트리스트 비준수
				if (valueObjs != null) {
					if (flag != null) {
						if (flag == false) {
							boolean isError = true;
							String attrStr = attrObj.toString();
							for (Object valueObj : valueObjs) {
								String valueStr = valueObj.toString();
								if (valueStr.equals(attrStr)) {
									isError = false;
								}
							}
							if (isError) {
								comment = "ERR_FIXED_VALUE" + "(" + key + ")";
								break;
							}
						}
					}
				}
			}
			// 숫자 타입 입력 오류
			if (!isNull) {
				if (type.equals("NUMERIC")) {
					String simpleName = attrObj.getClass().getSimpleName();
					if (simpleName.equals("Long") || simpleName.equals("Integer") || simpleName.equals("Double")) {
						continue;
					} else {
						comment = "ERR_NUMTYPE" + "(" + key + ")";
						break;
					}
				}
			}
		}
		if (comment != null) {
			// error
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			if (geom == null || geom.isEmpty()) {
				return null;
			}
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			String layerID = feature.getLayerID();
			ErrorFeature errFeature = new ErrorFeature();
			errFeature.setLayerID(layerID);
			errFeature.setErrCode(OpenQAOptions.QAType.ATTRIBUTEFIXMISS.getErrCode());
			errFeature.setErrType(OpenQAOptions.QAType.ATTRIBUTEFIXMISS.getErrType(langType));
			errFeature.setErrName(OpenQAOptions.QAType.ATTRIBUTEFIXMISS.getErrName(langType));
			errFeature.setComment(comment);
			errFeature.setErrPoint(errPt);
			return errFeature;
		} else {
			return null;
		}
	}

	// 필수속성오류 (Attribute)
	public ErrorFeature validateAttributeMiss(DTFeature feature, List<FixedValue> fixArry) {

		SimpleFeature sf = feature.getSimefeature();
		String comment = null;

		for (FixedValue fix : fixArry) {
			String key = fix.getKey();
			List<Object> valueObjs = fix.getValues();
			// 필수 컬럼 속성 누락
			Object attrObj = sf.getAttribute(key);
			if (attrObj == null) {
				comment = "ERR_VALUE_NULL" + "(" + key + ")";
				break;
			} else {
				String attrStr = attrObj.toString();
				if (attrStr.equals("")) {
					comment = "ERR_VALUE_NULL" + "(" + key + ")";
					break;
				}
			}
			// 코트리스트 비준수
			if (valueObjs != null) {
				boolean isError = true;
				String attrStr = attrObj.toString();
				for (Object valueObj : valueObjs) {
					String valueStr = valueObj.toString();
					if (valueStr.equals(attrStr)) {
						isError = false;
					}
				}
				if (isError) {
					comment = "ERR_VALUE_NULL" + "(" + key + ")";
					break;
				}
			}
		}
		if (comment != null) {
			// error
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			if (geom == null || geom.isEmpty()) {
				return null;
			}
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			String layerID = feature.getLayerID();
			ErrorFeature errFeature = new ErrorFeature();
			errFeature.setLayerID(layerID);
			errFeature.setErrCode(OpenQAOptions.QAType.ATTRIBUTEMISS.getErrCode());
			errFeature.setErrType(OpenQAOptions.QAType.ATTRIBUTEMISS.getErrType(langType));
			errFeature.setErrName(OpenQAOptions.QAType.ATTRIBUTEMISS.getErrName(langType));
			errFeature.setComment(comment);
			errFeature.setErrPoint(errPt);
			return errFeature;
		} else {
			return null;
		}
	}

	// 고도값오류 (Z-Value Abmiguous)
	public ErrorFeature validateZvalueAmbiguous(DTFeature feature, OptionFigure figure) {

		SimpleFeature sf = feature.getSimefeature();
		boolean isTrue = false;
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		} else {
			isTrue = true;
		}
		boolean isError = false;
		Geometry geometry = (Geometry) sf.getDefaultGeometry();
		if (isTrue) {
			List<AttributeFigure> attrFigures = figure.getFigure();
			for (AttributeFigure attrFigure : attrFigures) {
				String key = attrFigure.getKey();
				Object attributeValue = sf.getAttribute(key);
				if (attributeValue != null) {
					Double number = attrFigure.getNumber();
					String condition = attrFigure.getCondition();
					Double interval = attrFigure.getInterval();
					String valueStr = attributeValue.toString();
					Double valueD = Double.valueOf(valueStr);
					if (condition != null) {
						if (condition.equals("equal")) {
							if (!attributeValue.toString().equals(number.toString())
									|| !(valueStr + ".0").equals(number.toString())) {
								isError = true;
							}
						}
						if (condition.equals("over")) {
							if (valueD < number) {
								isError = true;
							}
						}
						if (condition.equals("under")) {
							if (valueD > number) {
								isError = true;
							}
						}
					}
					if (interval != null) {
						Double result = valueD % interval;
						if (!(result == 0.0)) {
							isError = true;
						}
					}
				}
			}
		}
		if (isError) {
			Geometry errPt = null;
			try {
				errPt = geometry.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geometry.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			String layerID = feature.getLayerID();
			ErrorFeature errFeature = new ErrorFeature();
			errFeature.setLayerID(layerID);
			errFeature.setErrCode(OpenQAOptions.QAType.ZVALUEAMBIGUOUS.getErrCode());
			errFeature.setErrType(OpenQAOptions.QAType.ZVALUEAMBIGUOUS.getErrType(langType));
			errFeature.setErrName(OpenQAOptions.QAType.ZVALUEAMBIGUOUS.getErrName(langType));
			errFeature.setErrPoint(errPt);
			return errFeature;
		} else {
			return null;
		}
	}

	public List<ErrorFeature> validateRefAttributeMiss(DTFeature feature, OptionFigure figure,
			OptionTolerance tolerance, SimpleFeatureCollection sfc, DTLayer retargetLayer) {

		SimpleFeature sf = feature.getSimefeature();

		boolean isTrue = true;
		List<AttributeFilter> filters = feature.getFilter();
		if (filters != null) {
			isTrue = FeatureFilter.filter(sf, filters);
		}
		List<ErrorFeature> errFeatures = new ArrayList<>();
		if (isTrue) {
			OptionFilter relationFilter = retargetLayer.getFilter();
			List<AttributeFilter> relationConditions = null;
			if (relationFilter != null) {
				relationConditions = relationFilter.getFilter();
			}
			Double value = tolerance.getValue();

			Geometry geom = (Geometry) sf.getDefaultGeometry();
			String layerID = feature.getLayerID();
			String refLayerID = retargetLayer.getLayerID();

			Coordinate[] tCoors = geom.getCoordinates();

			int coorLength = tCoors.length;

			Coordinate tFirCoor = tCoors[0];
			Coordinate tLasCoor = tCoors[coorLength - 1];

			GeometryFactory factory = new GeometryFactory();
			Geometry firPt = factory.createPoint(tFirCoor);
			Geometry lasPt = factory.createPoint(tLasCoor);

			Geometry firPtBf = firPt.buffer(value);
			Geometry lasPtBf = lasPt.buffer(value);

			boolean reInter = false;

			SimpleFeatureCollection relationSfc = retargetLayer.getSimpleFeatureCollection();
			SimpleFeatureIterator rIterator = relationSfc.features();
			while (rIterator.hasNext()) {
				SimpleFeature reSf = rIterator.next();
				if (FeatureFilter.filter(reSf, relationConditions)) {
					Geometry rGeom = (Geometry) reSf.getDefaultGeometry();
					if (rGeom == null) {
						continue;
					}
					String rGeomType = rGeom.getGeometryType();
					if (!rGeomType.equals("Polygon") && !rGeomType.equals("MultiPolygon")) {
						continue;
					}
					Geometry boundary = rGeom.getBoundary();
					if (boundary.intersects(firPtBf) || boundary.intersects(lasPtBf)) {
						reInter = true;
						break;
					}
				}
			}
			rIterator.close();
			if (reInter) {
				return null;
			} else {
				List<SimpleFeature> firIterSfs = new ArrayList<>();
				List<SimpleFeature> lasIterSfs = new ArrayList<>();
				String featureID = sf.getID();
				SimpleFeatureIterator sfIter = sfc.features();
				while (sfIter.hasNext()) {
					SimpleFeature tmpSf = sfIter.next();
					boolean selfTrue = true;
					if (filters != null) {
						selfTrue = FeatureFilter.filter(tmpSf, filters);
					}
					if (selfTrue) {
						if (featureID.equals(tmpSf.getID())) {
							continue;
						}
						Geometry selfGeom = (Geometry) tmpSf.getDefaultGeometry();
						if (selfGeom.intersects(firPtBf)) {
							firIterSfs.add(tmpSf);
						}
						if (selfGeom.intersects(lasPtBf)) {
							lasIterSfs.add(tmpSf);
						}
					}
				}
				sfIter.close();

				boolean firErr = false;
				boolean lasErr = false;
				List<AttributeFigure> attrFigures = figure.getFigure();
				for (AttributeFigure attrFigure : attrFigures) {
					String key = attrFigure.getKey();
					Object attrObj = sf.getAttribute(key);
					int firInterSize = firIterSfs.size();
					if (firInterSize == 1) {
						SimpleFeature firInterSf = firIterSfs.get(0);
						Object reAttrObj = firInterSf.getAttribute(key);
						if (attrObj == null) {
							if (reAttrObj != null) {
								firErr = true;
								break;
							}
						} else {
							if (reAttrObj == null) {
								firErr = true;
								break;
							} else if (!attrObj.toString().equals(reAttrObj.toString())) {
								firErr = true;
								break;
							}
						}
					}

					int lasInterSize = lasIterSfs.size();
					if (lasInterSize == 1) {
						SimpleFeature lasInterSf = lasIterSfs.get(0);
						Object reAttrObj = lasInterSf.getAttribute(key);
						if (attrObj == null) {
							if (reAttrObj != null) {
								lasErr = true;
								break;
							}
						} else {
							if (reAttrObj == null) {
								lasErr = true;
								break;
							} else if (!attrObj.toString().equals(reAttrObj.toString())) {
								lasErr = true;
								break;
							}
						}
					}
				}
				if (firErr) {
					ErrorFeature errFeature = new ErrorFeature();
					errFeature.setLayerID(layerID);
					errFeature.setRefLayerId(refLayerID);
					errFeature.setErrCode(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrCode());
					errFeature.setErrType(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrType(langType));
					errFeature.setErrName(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrName(langType));
					errFeature.setErrPoint(firPt);
					errFeatures.add(errFeature);
				}
				if (lasErr) {
					ErrorFeature errFeature = new ErrorFeature();
					errFeature.setLayerID(layerID);
					errFeature.setRefLayerId(refLayerID);
					errFeature.setErrCode(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrCode());
					errFeature.setErrType(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrType(langType));
					errFeature.setErrName(OpenQAOptions.QAType.REFATTRIBUTEMISS.getErrName(langType));
					errFeature.setErrPoint(lasPt);
					errFeatures.add(errFeature);
				}
				if (errFeatures.size() > 0) {
					return errFeatures;
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
