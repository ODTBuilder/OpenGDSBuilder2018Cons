package com.git.gdsbuilder.validator.basic;

import java.util.ArrayList;
import java.util.List;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.validate.option.specific.AttributeFigure;
import com.git.gdsbuilder.type.validate.option.specific.AttributeFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionFigure;
import com.git.gdsbuilder.type.validate.option.standard.FixedValue;
import com.git.gdsbuilder.validator.feature.filter.FeatureFilter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.TopologyException;

import lombok.Data;

@Data
public class BasicFeatureAttributeValidator {

	public BasicErrorFeature validateLayerFixMiss(DTFeature feature, List<FixedValue> fixArry) {

		SimpleFeature sf = feature.getSimefeature();
		BMQAOptions.Type errType = null;

		for (FixedValue fix : fixArry) {
			String name = fix.getName();
			String type = fix.getType();
			// Long length = fix.getLength();
			Boolean flag = fix.isIsnull();
			List<Object> valueObjs = fix.getValues();

			SimpleFeatureType sft = sf.getFeatureType();
			AttributeDescriptor attrDesc = sft.getDescriptor(name);
			if (attrDesc == null) {
				// 테이블 컬럼 규칙 비준수
				errType = BMQAOptions.Type.ERR_CLUNM;
				break;
			}

			// 컬럼타입 불일치
			AttributeType attrType = attrDesc.getType();
			String typeStr = attrType.getBinding().getSimpleName();
			if (!typeStr.equals(type)) {
				errType = BMQAOptions.Type.ERR_CLUNMTYPE;
				break;
			}

			// 필수 컬럼 속성 누락
			Object attrObj = sf.getAttribute(name);
			boolean isNull = false;
			if (flag != null) {
				if (flag == false) {
					if (attrObj == null) {
						isNull = true;
						errType = BMQAOptions.Type.ERR_ATTRVALUE;
						break;
					} else if (attrObj.toString().equals("")) {
						isNull = true;
						errType = BMQAOptions.Type.ERR_ATTRVALUE;
						break;
					}
				}
			}
			// 숫자 타입 입력 오류
			if (!isNull) {
				if (type.equals("Double") || type.equals("Integer")) {
					String simpleName = attrObj.getClass().getSimpleName();
					if (!type.equals(simpleName)) {
						errType = BMQAOptions.Type.ERR_NUMTYPE;
						break;
					}
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
					errType = BMQAOptions.Type.ERR_CODE;
					break;
				}
			}
		}
		if (errType != null) {
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
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			err.setLayerID(feature.getLayerID());
			err.setValidation(errType.getValidation());
			err.setType(errType.getType());
			err.setItem(errType.getItem());
			err.setName(errType.getName());
			return err;
		} else {
			return null;
		}
	}

	public BasicErrorFeature validateUnDefinedLayer(DTFeature feature) {

		SimpleFeature sf = feature.getSimefeature();
		Geometry geom = (Geometry) sf.getDefaultGeometry();

		Geometry errPt = null;
		try {
			errPt = geom.getInteriorPoint();
		} catch (TopologyException e) {
			Coordinate[] coors = geom.getCoordinates();
			errPt = new GeometryFactory().createPoint(coors[0]);
		}

		BasicErrorFeature err = new BasicErrorFeature();
		err.setErrPoint(errPt);
		err.setGeomStr(geom.toString());
		err.setLayerID(feature.getLayerID());
		err.setValidation(BMQAOptions.Type.ERR_LAYERTYPE.getValidation());
		err.setType(BMQAOptions.Type.ERR_LAYERTYPE.getType());
		err.setItem(BMQAOptions.Type.ERR_LAYERTYPE.getItem());
		err.setName(BMQAOptions.Type.ERR_LAYERTYPE.getName());

		return err;
	}

	public List<BasicErrorFeature> validateNumericalValues(DTFeature feature, OptionFigure figure) {

		SimpleFeature sf = feature.getSimefeature();
		List<BasicErrorFeature> errList = new ArrayList<>();
		List<AttributeFilter> filters = feature.getFilter();
		List<AttributeFigure> attrFigures = figure.getFigure();
		BasicErrorFeature err = null;
		BMQAOptions.Type type = null;
		for (AttributeFigure attrFigure : attrFigures) {
			Long fidx = attrFigure.getFIdx();
			String key = attrFigure.getKey();
			if (fidx == null) {
				err = isNumericalValues(sf, attrFigure);
			} else {
				if (FeatureFilter.filter(sf, filters.get(fidx.intValue()))) {
					err = isNumericalValues(sf, attrFigure);
				}
			}
			if (err != null) {
				String code = figure.getCode();
				if (code.equals("TN_RODWAY_CTLN") && key.equals("CARTRK_CO")) {
					type = BMQAOptions.Type.ERR_RANGE_CARTRK_CO;
				} else if (code.equals("TN_RODWAY_CTLN") && key.equals("ROAD_BT") && fidx == 0) {
					type = BMQAOptions.Type.ERR_RANGE_SE10_ROAD_BT;
				} else if (code.equals("TN_RODWAY_CTLN") && key.equals("ROAD_BT") && fidx == 1) {
					type = BMQAOptions.Type.ERR_RANGE_SE14_ROAD_BT;
				} else if (code.equals("TN_RODWAY_CTLN") && key.equals("ROAD_BT") && fidx == 2) {
					type = BMQAOptions.Type.ERR_RANGE_SE00_ROAD_BT;
				} else if (key.equals("ALPT_HG") || key.equals("CTRLN_HG")) {
					type = BMQAOptions.Type.ERR_RANGE_HG;
				} else if (key.equals("BFLR_CO")) {
					type = BMQAOptions.Type.ERR_RANGE_BFLR_CO;
				}
				if (type != null) {
					err.setLayerID(feature.getLayerID());
					err.setValidation(type.getValidation());
					err.setType(type.getType());
					err.setItem(type.getItem());
					err.setName(type.getName());
					errList.add(err);
					err = null;
					type = null;
				}
			}
		}
		if (errList.size() > 0) {
			return errList;
		} else {
			return null;
		}
	}

	private BasicErrorFeature isNumericalValues(SimpleFeature sf, AttributeFigure attrFigure) {

		boolean isError = false;

		String key = attrFigure.getKey();
		String condition = attrFigure.getCondition();
		double number = attrFigure.getNumber(); // 1.5
		Object attributeObj = sf.getAttribute(key);
		if (attributeObj != null) {
			String attributeStr = attributeObj.toString();
			if (attributeStr.equals("")) {
				isError = true;
			}
			double attributeDou = Double.parseDouble(attributeStr);
			if (condition.equals("over")) { // 초과
				if (attributeDou <= number) {
					isError = true;
				}
			} else if (condition.equals("andover")) { // 이상
				if (attributeDou < number) {
					isError = true;
				}
			} else if (condition.equals("under")) { // 미만
				if (attributeDou >= number) {
					isError = true;
				}
			} else if (condition.equals("andunder")) { // 이하
				if (attributeDou > number) {
					isError = true;
				}
			} else if (condition.equals("equal")) { // 같
				if (attributeDou != number) {
					isError = true;
				}
			}
		} else {
			isError = true;
		}
		if (isError) {
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			return err;
		} else {
			return null;
		}
	}

	public List<BasicErrorFeature> validateFixValues(DTFeature feature, OptionFigure figure) {

		SimpleFeature sf = feature.getSimefeature();
		List<BasicErrorFeature> errList = new ArrayList<>();
		List<AttributeFilter> filters = feature.getFilter();
		List<AttributeFigure> attrFigures = figure.getFigure();
		BasicErrorFeature err = null;
		BMQAOptions.Type type = null;
		for (AttributeFigure attrFigure : attrFigures) {
			Long fidx = attrFigure.getFIdx();
			String key = attrFigure.getKey();
			if (fidx == null) {
				err = isFixValues(sf, attrFigure);
			} else {
				if (FeatureFilter.filter(sf, filters.get(fidx.intValue()))) {
					err = isFixValues(sf, attrFigure);
				}
			}
			if (err != null) {
				String code = figure.getCode();
				if (code.equals("TN_BULD") && key.equals("BULD_NM") && fidx == 0) {
					type = BMQAOptions.Type.ERR_RANGE_BULD_NM;
				}
				if (type != null) {
					err.setLayerID(feature.getLayerID());
					err.setValidation(type.getValidation());
					err.setType(type.getType());
					err.setItem(type.getItem());
					err.setName(type.getName());
					errList.add(err);
					err = null;
					type = null;
				}
			}
		}
		if (errList.size() > 0) {
			return errList;
		} else {
			return null;
		}
	}

	private BasicErrorFeature isFixValues(SimpleFeature sf, AttributeFigure attrFigure) {

		boolean isError = true;

		String key = attrFigure.getKey();
		List<Object> values = attrFigure.getValues();
		Object attributeObj = sf.getAttribute(key);
		if (values != null) {
			for (Object value : values) {
				if (value.toString().equals(attributeObj)) {
					isError = false;
				}
			}
		} else {
			if (attributeObj == null) {
				isError = false;
			} else {
				if (attributeObj.toString().equals("")) {
					isError = false;
				}
			}
		}

		if (isError) {
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			Geometry errPt = null;
			try {
				errPt = geom.getInteriorPoint();
			} catch (TopologyException e) {
				Coordinate[] coors = geom.getCoordinates();
				errPt = new GeometryFactory().createPoint(coors[0]);
			}
			BasicErrorFeature err = new BasicErrorFeature();
			err.setErrPoint(errPt);
			err.setGeomStr(geom.toString());
			return err;
		} else {
			return null;
		}
	}

}
