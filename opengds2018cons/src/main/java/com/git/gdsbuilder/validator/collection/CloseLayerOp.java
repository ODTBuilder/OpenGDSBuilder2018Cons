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
package com.git.gdsbuilder.validator.collection;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.git.gdsbuilder.type.dt.collection.MapSystemRule.MapSystemRuleType;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.option.OptionTolerance;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 인접 영역 검수 시 검수 영역의 허용 오차 범위 내에 있는 검수 대상 레이어의 객체들과 인접 검수 영역 내에 레이어의 객체들을 계산하는
 * 클래스.
 * <p>
 * 인접 객체 계산 결과는 검수 영역과 인접한 검수 대상 레이어의 객체들을 모아 생성한 {@link DTLayer}와 검수 영역과 인접한 인접
 * 영역 레이어의 객체들을 모아 생성한 {@link DTLayer}임.
 * 
 * @author DY.Oh
 *
 */
public class CloseLayerOp {

	/**
	 * Geometry Column name
	 */
	protected static String geomColunm = "the_geom";
	/**
	 * 인접 검수 영역 방향 (BOTTOM or LEFT or RIGHT or TOP)
	 */
	String direction;
	/**
	 * 검수 대상 레이어
	 */
	DTLayer typeLayer;
	/**
	 * 인접 영역 내 검수 대상 레이어와 동일한 ID를 가진 레이어
	 */
	DTLayer closeLayer;
	/**
	 * 검수 영역
	 */
	Geometry closeBoundary;

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public DTLayer getTypeLayer() {
		return typeLayer;
	}

	public void setTypeLayer(DTLayer typeLayer) {
		this.typeLayer = typeLayer;
	}

	public DTLayer getCloseLayer() {
		return closeLayer;
	}

	public void setCloseLayer(DTLayer closeLayer) {
		this.closeLayer = closeLayer;
	}

	public Geometry getCloseBoundary() {
		return closeBoundary;
	}

	public void setCloseBoundary(Geometry closeBoundary) {
		this.closeBoundary = closeBoundary;
	}

	/**
	 * 수치지도 인접 영역 검수 시 검수 영역의 허용 오차 범위 내에 있는 검수 대상 레이어의 객체들과 인접 검수 영역 내에 레이어의 객체들을
	 * 계산함.
	 * <p>
	 * 검수 영역 레이어의 Geometry 타입이 {@link LineString}이며 검수 영역이 한개의 {@link LineString}인
	 * 경우
	 * 
	 * @param type            인접 검수 영역 방향 (BOTTOM or LEFT or RIGHT or TOP)
	 * @param neatLineLayer   검수 영역 {@link DTLayer}
	 * @param typeLayer       검수 대상 {@link DTLayer}
	 * @param closeLayer      검수 영역과 인접한 인접 영역 내 {@link DTLayer}
	 * @param optionTolerance 검수 영역 허용 오차
	 * 
	 * @author DY.Oh
	 */
	public void closeLayerOp(MapSystemRuleType type, DTLayer neatLineLayer, DTLayer typeLayer, DTLayer closeLayer,
			OptionTolerance optionTolerance) {

		double tolerance = optionTolerance.getValue();

		SimpleFeatureCollection neatLineCollection = neatLineLayer.getSimpleFeatureCollection();

		Coordinate firstPoint = null;
		Coordinate secondPoint = null;
		Coordinate thirdPoint = null;
		Coordinate fourthPoint = null;

		int i = 0;
		SimpleFeatureIterator featureIterator = neatLineCollection.features();
		while (featureIterator.hasNext()) {
			if (i == 0) {
				SimpleFeature feature = featureIterator.next();
				Geometry geometry = (Geometry) feature.getDefaultGeometry();

				Coordinate[] coordinateArray = this.getSort5Coordinate(geometry.getCoordinates());

				firstPoint = coordinateArray[0];
				secondPoint = coordinateArray[1];
				thirdPoint = coordinateArray[2];
				fourthPoint = coordinateArray[3];
				i++;
			}
		}
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Coordinate[] boundaryCoors = null;
		LineString boundary = null;

		String direction = type.getTypeName();

		if (direction.equals(MapSystemRuleType.TOP.getTypeName())) {
			boundaryCoors = new Coordinate[] { firstPoint, secondPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.BOTTOM.getTypeName())) {
			boundaryCoors = new Coordinate[] { thirdPoint, fourthPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.LEFT.getTypeName())) {
			boundaryCoors = new Coordinate[] { firstPoint, fourthPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.RIGHT.getTypeName())) {
			boundaryCoors = new Coordinate[] { secondPoint, thirdPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}

		Polygon bufferPolygon = (Polygon) boundary.buffer(tolerance);
		Filter filter = ff.intersects(ff.property(geomColunm), ff.literal(bufferPolygon));

		SimpleFeatureSource typeSource = DataUtilities.source(typeLayer.getSimpleFeatureCollection());
		SimpleFeatureSource closeSource = DataUtilities.source(closeLayer.getSimpleFeatureCollection());

		SimpleFeatureCollection typeFtCollection = null;
		SimpleFeatureCollection closeFtCollection = null;
		try {
			// type
			typeFtCollection = typeSource.getFeatures(filter);
			// close
			closeFtCollection = closeSource.getFeatures(filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.direction = direction;
		this.typeLayer = new DTLayer(typeLayer.getLayerID(), typeLayer.getLayerType(), typeFtCollection, null);
		this.closeLayer = new DTLayer(closeLayer.getLayerID(), closeLayer.getLayerType(), closeFtCollection, null);
		this.closeBoundary = boundary;

	}

	/**
	 * 임상도 인접 영역 검수 시 검수 영역의 허용 오차 범위 내에 있는 검수 대상 레이어의 객체들과 인접 검수 영역 내에 레이어의 객체들을
	 * 계산함.
	 * <p>
	 * 검수 영역 레이어의 Geometry 타입이 {@link Polygon}이며 검수 영역이 여러개의 {@link Polygon}으로 구성되어
	 * 있는 경우
	 * 
	 * @param type            인접 검수 영역 방향 (BOTTOM or LEFT or RIGHT or TOP)
	 * @param neatLineLayer   검수 영역 {@link DTLayer}
	 * @param typeLayer       검수 대상 {@link DTLayer}
	 * @param closeLayer      검수 영역과 인접한 인접 영역 내 {@link DTLayer}
	 * @param optionTolerance 검수 영역 허용 오차
	 * 
	 * @author DY.Oh
	 */
	public void closeLayerOpF(MapSystemRuleType type, DTLayer neatLineLayer, DTLayer typeLayer, DTLayer closeLayer,
			OptionTolerance optionTolerance) {

		double tolerance = optionTolerance.getValue();

		// 임상도
		SimpleFeatureCollection neatLineCollection = neatLineLayer.getSimpleFeatureCollection();
		List<Geometry> geometries = new ArrayList<>();
		SimpleFeatureIterator featureIterator = neatLineCollection.features();
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			Geometry geometry = (Geometry) feature.getDefaultGeometry();
			geometries.add(geometry);
		}
		Geometry polygonBoundary = null;
		if (geometries.size() > 1) {
			GeometryCollection geometryCollection = (GeometryCollection) new GeometryFactory()
					.buildGeometry(geometries);
			Geometry union = geometryCollection.union();
			polygonBoundary = union.getBoundary();
		} else {
			polygonBoundary = geometries.get(0).getBoundary();
		}
		Coordinate[] coordinateArray = this.getSort5Coordinate(polygonBoundary.getCoordinates());
		Coordinate firstPoint = coordinateArray[0];
		Coordinate secondPoint = coordinateArray[1];
		Coordinate thirdPoint = coordinateArray[2];
		Coordinate fourthPoint = coordinateArray[3];

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Coordinate[] boundaryCoors = null;
		LineString boundary = null;
		String direction = type.getTypeName();
		if (direction.equals(MapSystemRuleType.TOP.getTypeName())) {
			boundaryCoors = new Coordinate[] { firstPoint, secondPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.BOTTOM.getTypeName())) {
			boundaryCoors = new Coordinate[] { thirdPoint, fourthPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.LEFT.getTypeName())) {
			boundaryCoors = new Coordinate[] { firstPoint, fourthPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}
		if (direction.equals(MapSystemRuleType.RIGHT.getTypeName())) {
			boundaryCoors = new Coordinate[] { secondPoint, thirdPoint };
			boundary = new GeometryFactory().createLineString(boundaryCoors);
		}

		Polygon bufferPolygon = (Polygon) boundary.buffer(tolerance);
		Filter filter = ff.intersects(ff.property(geomColunm), ff.literal(bufferPolygon));

		SimpleFeatureSource typeSource = DataUtilities.source(typeLayer.getSimpleFeatureCollection());
		SimpleFeatureSource closeSource = DataUtilities.source(closeLayer.getSimpleFeatureCollection());

		SimpleFeatureCollection typeFtCollection = null;
		SimpleFeatureCollection closeFtCollection = null;
		try {
			// type
			typeFtCollection = typeSource.getFeatures(filter);
			// close
			closeFtCollection = closeSource.getFeatures(filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.direction = direction;
		this.typeLayer = new DTLayer(typeLayer.getLayerID(), typeLayer.getLayerType(), typeFtCollection, null);
		this.closeLayer = new DTLayer(closeLayer.getLayerID(), closeLayer.getLayerType(), closeFtCollection, null);
		this.closeBoundary = boundary;

	}

	private Coordinate[] getSort5Coordinate(Coordinate[] coordinates) {
		Coordinate[] returncoordinate = coordinates;
		if (coordinates.length == 5) {
			double fPointY = 0.0;
			double sPointY = 0.0;

			for (int a = 0; a < returncoordinate.length - 2; a++) {
				for (int j = 0; j < returncoordinate.length - 2; j++) {
					fPointY = returncoordinate[j].y;
					sPointY = returncoordinate[j + 1].y;

					Coordinate jCoordinate = returncoordinate[j];
					Coordinate kCoordinate = returncoordinate[j + 1];

					if (fPointY < sPointY) {
						returncoordinate[j + 1] = jCoordinate;
						returncoordinate[j] = kCoordinate;
					}
				}
			}
			Coordinate firstPoint = returncoordinate[0];
			Coordinate secondPoint = returncoordinate[1];
			Coordinate thirdPoint = returncoordinate[2];
			Coordinate fourthPoint = returncoordinate[3];

			if (firstPoint.x > secondPoint.x) {
				returncoordinate[0] = secondPoint;
				returncoordinate[1] = firstPoint;
			}

			if (thirdPoint.x < fourthPoint.x) {
				returncoordinate[2] = fourthPoint;
				returncoordinate[3] = thirdPoint;
			}

			returncoordinate[4] = returncoordinate[0];
		} else
			return null;

		return returncoordinate;
	}
}
