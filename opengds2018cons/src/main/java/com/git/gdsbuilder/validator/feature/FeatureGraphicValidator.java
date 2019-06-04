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
 *    Lesser General Public License for more details.
 */

package com.git.gdsbuilder.validator.feature;

import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.option.OptionTolerance;

/**
 * {@link DTFeature}의 기하학적 오류 및 {@link DTFeature}간의 위상관계를 검수하는 클래스.
 * <p>
 * 기본적으로 {@link Geometry} 위상관계(equals, intersects, contains 등)를 활용하였며 국내 검수 지침에
 * 따라 각각의 검수 항목을 세부적으로 정의함.
 * <p>
 * 검수 결과는 오류 객체의 위치점({@link Point})이 포함된 {@link ErrorFeature}로 반환되며 다수의 오류점이 있는
 * 경우 List 형태로 반환함.
 * 
 * @author DY.Oh
 *
 */
public interface FeatureGraphicValidator {

	/**
	 * ConBreak(등고선끊김오류, Contour line disconnections) 검수 수행.
	 * <p>
	 * 등고선은 동일한 고도값을 가진 vertex를 이어 1개의 {@link LineString}으로 생성된 객체로 검수 영역 레이어
	 * {@link DTLayer}에 양 끝점이 닿아있거나 그렇지 않은 경우 {@link LineString}이 폐합되지 않으면 오류 객체로
	 * 반환.
	 * 
	 * @param feature         등고선 {@link DTFeature} 객체
	 * @param neatLine        검수 영역 {@link DTLayer}
	 * @param optionTolerance 허용오차 범위
	 * @return 폐합되지 않은 등고선의 양 끝점을 각각 {@link ErrorFeature}로 생성하여 {@link List} 형태로 반환.
	 *         조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateConBreak(DTFeature feature, DTLayer neatLine, OptionTolerance optionTolerance);

	/**
	 * ConIntersected(등고선교차오류, Contour line intersections) 검수 수행.
	 * <p>
	 * 등고선 객체가 스스로 꼬여있는 지 검사하는 항목.
	 * 
	 * @param feature 등고선 {@link DTFeature} 객체
	 * @return 교차되어있는 등고선의 모든 교차점을 각각 {@link ErrorFeature}로 생성하여 {@link List} 형태로
	 *         반환. 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateConIntersected(DTFeature feature);

	/**
	 * ConIntersected(등고선교차오류, Contour line intersections) 검수 수행.
	 * <p>
	 * 등고선 객체가 다른 등고선 객체와 겹처있는지 검사하는 항목.
	 * 
	 * @param feature   등고선 {@link DTFeature} 객체
	 * @param reFeature 동일한 레이어 내의 다른 등고선 {@link DTFeature} 객체
	 * @return 교차되어있는 등고선의 모든 교차점을 각각 {@link ErrorFeature}로 생성하여 {@link List} 형태로
	 *         반환. 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateConIntersected(DTFeature feature, DTFeature reFeature);

	/**
	 * ConOverDegree(등고선꺾임오류, Unsmooth contour line curves) 검수 수행.
	 * <p>
	 * 등고선 객체의 연속되는 3개의 vertex의 각도가 허용오차 범위 이하인 경우 오류 객체로 반환.
	 * 
	 * @param feature         등고선 {@link DTFeature} 객체
	 * @param optionTolerance 각도가 허용오차 범위
	 * @return 조건을 만족시키지 않은 모든 Vertex를 각각 {@link ErrorFeature}로 생성하여 {@link List}
	 *         형태로 반환. 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateConOverDegree(DTFeature feature, OptionTolerance optionTolerance);

	/**
	 * UselessPoint(직선화미처리오류, Useless points in contour line) 검수 수행.
	 * <p>
	 * 등고선 객체의 연속되는 3개의 vertex의 각도가 6도 이하이고 길이의 합이 3m 이하인 경우 오류 객체로 반환.
	 * 
	 * @param feature 등고선 {@link DTFeature} 객체
	 * @return 조건을 만족시키지 않은 모든 Vertex를 각각 {@link ErrorFeature}로 생성하여 {@link List}
	 *         형태로 반환. {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateUselessPoint(DTFeature feature);

	/**
	 * SmallArea(허용범위이하면적오류, Areas between tolerance limit) 검수 수행.
	 * <p>
	 * Polygon 타입의 객체가 일정한 면적(㎡) 이하인 경우 오류 객체로 반환.
	 * 
	 * @param feature         Polygon 타입의 {@link DTFeature} 객체
	 * @param optionTolerance 허용 면적(㎡)
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateSmallArea(DTFeature feature, OptionTolerance optionTolerance);

	/**
	 * SmallLength(허용범위이하길이오류, Segments between length tolerance limit) 검수 수행.
	 * <p>
	 * LineString 타입의 객체가 일정한 길이(m) 이하인 경우 오류 객체로 반환.
	 * 
	 * @param feature         Polygon 타입의 {@link DTFeature} 객체
	 * @param optionTolerance 허용 길이(m)
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateSmallLength(DTFeature feature, OptionTolerance optionTolerance);

	/**
	 * OverShoot(기준점초과오류, Feature crossing the sheet) 검수 수행.
	 * <p>
	 * 객체가 검수 영역 외부에 존재하거나 검수 영역과 교차되어 있는 경우 오류 객체로 반환.
	 * 
	 * @param feature         기준점초과오류 검수 대상 {@link DTFeature} 객체
	 * @param neatLine        검수 영역 {@link DTLayer}
	 * @param optionTolerance 허용오차 범위
	 * @return 검수 영역 외부에 존재하거니 검수 영역과 교차되어 있는 {@link DTFeature}의 모든 vertex를 각각
	 *         {@link ErrorFeature}로 생성하여 {@link List} 형태로 반환. {@link ErrorFeature}
	 *         검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateOverShoot(DTFeature feature, DTLayer neatLine, OptionTolerance optionTolerance);

	/**
	 * SelfEntity(단독존재오류, Overlapping features) 검수 수행.
	 * <p>
	 * 검수 대상 객체가 비교 객체와 겹쳐있는 경우 오류 객체로 반환.
	 * 
	 * @param feature         단독존재오류 검수 대상 {@link DTFeature} 객체
	 * @param reFeature       비교 {@link DTFeature} 객체
	 * @param optionTolerance 허용오차 범위
	 * @return 검수 대상 {@link DTFeature} 객체와 비교 {@link DTFeature} 객체가 겹쳐있는 모든 vertex를
	 *         각각 {@link ErrorFeature}로 생성하여 {@link List} 형태로 반환.
	 *         {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateSelfEntity(DTFeature feature, DTFeature reFeature, OptionTolerance optionTolerance);

	/**
	 * OutBoundary(경계초과오류, Feature crossing the boundary) 검수 수행.
	 * <p>
	 * {@link Polygon} 또는 {@link LineString} 타입의 객체가 다른 객체와의 포함관계(Within)가 맞지 않은 경우.
	 * 예를 들어, 도로 경계에 교량이 완전히 포함되지 않거나 교량이 단독으로 존재하는 경우 교량을 오류 객체로 반환.
	 * <p>
	 * {@link DTFeature}는 {@link DTLayer}의 객체들 중 1개의 객체와도 포함관계가 맞지 않으면 오류.
	 * 
	 * @param feature         경계초과오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayer         대상 {@link DTFeature} 객체가 완전히 포함되어야 하는 {@link DTLayer}
	 *                        객체
	 * @param optionTolerance 허용오차 범위
	 * @return {@link DTLayer}에 포함되지 않은 {@link DTFeature}. {@link ErrorFeature} 검사
	 *         조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateOutBoundary(DTFeature feature, DTLayer reLayer, OptionTolerance optionTolerance);

	/**
	 * EntityDuplicated(요소중복오류, Duplicated features) 검수 수행.
	 * <p>
	 * 두개의 {@link DTFeature} 객체의 모든 좌표값과 모든 속성이 완전히 동일할 경우 검수 대상 {@link DTFeature}
	 * 객체를 오류 객체로 반환.
	 * 
	 * @param feature   요소중복오류 검수 대상 {@link DTFeature} 객체
	 * @param reFeature 비교 {@link DTFeature} 객체
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateEntityDuplicated(DTFeature feature, DTFeature reFeature);

	/**
	 * EntityOpenMiss(객체폐합오류, Unclosed feature) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 {@link DTFeature} 객체의 양 끝점이 {@link DTLayer}의 객체와 닿아있지
	 * 않거나 폐합되어 있지 않은 경우 오류 객체로 반환.
	 * <p>
	 * {@link DTFeature} 객체가 폐합되어 있는 경우 {@link DTLayer}와의 검사는 수행하지 않음.
	 * 
	 * @param feature         {@link LineString} 타입의 검수 대상 {@link DTFeature} 객체
	 * @param reLayer         검수 대상 {@link DTFeature} 객체와 검사를 수행할 {@link DTLayer}
	 * @param optionTolerance 허용오차 범위
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateEntityOpenMiss(DTFeature feature, DTLayer reLayer, OptionTolerance optionTolerance);

	/**
	 * LayerFixMiss(Geometry타입오류, Feature with wrong geometry type) 검수 수행.
	 * <p>
	 * {@link SimpleFeature}의 Geometry 타입이 geomType(Point, LineString, Polygon,
	 * MultiPoint, MultiLineString, MultiPolygon)와 일치하지 않는 경우 오류 객체로 반환.
	 * 
	 * @param feature  Geometry 타입오류 검수 대상 {@link SimpleFeature} 객체
	 * @param geomType Geometry 타입 문자열
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateLayerFixMiss(SimpleFeature feature, String geomType);

	/**
	 * TwistedPolygon(폴리곤꼬임오류, Twisted polygons) 검수 수행.
	 * <p>
	 * {@link Polygon} 타입의 {@link DTFeature} 객체의 Boundary가 스스로 꼬여있는 경우 오류 객체로 반환.
	 * <p>
	 * OGC SFS 표준에 따라 유효하지 않은 {@link Polygon}인 경우
	 * 
	 * @param feature 폴리곤꼬임오류 검수 대상 {@link DTFeature} 객체
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateTwistedPolygon(DTFeature feature);

	/**
	 * NodeMiss(선형노드오류, Missing node) 검수 수행.
	 * <p>
	 * {@link LineString} 타입 객체의 양 끝점이 포함관계에 있는 {@link Polygon} 타입 객체의 경계와 맞닿아 있지 않은
	 * 경우 오류 객체로 반환.
	 * <p>
	 * {@link Polygon} 객체 경계와 맞닿아 있지 않더라도 {@link LineString}과 동일한 레이어 내에 양 끝점이 맞닿아
	 * 있는 다른 {@link LineString}가 존재한다면 오류가 아닌 것으로 판단하여 {@code null} 반환.
	 * 
	 * @param feature   선형노드오류 검수 대상 {@link DTFeature} 객체
	 * @param sfc       {@link LineString}가 포함된 {@link SimpleFeatureCollection}
	 * @param reLayer   {@link Polygon} 타입 {@link DTLayer}
	 * @param tolerance 허용오차 범위
	 * @return {@link Polygon} 또는 {@link LineString}와 맞닿아 있지 않은 양 끝점을 각각
	 *         {@link ErrorFeature}로 생성하여 {@link List} 형태로 반환. 조건을 충족시키면
	 *         {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateNodeMiss(DTFeature feature, SimpleFeatureCollection sfc, DTLayer reLayer,
			OptionTolerance tolerance);

	/**
	 * PointDuplicated(중복점오류, Duplicated point) 검수 수행.
	 * <p>
	 * 1개의 {@link Geometry}의 같은 위치에 두개 이상의 Point가 있는 경우 오류 객체로 반환.
	 * 
	 * @param feature 중복점오류 검수 대상 {@link DTFeature} 객체
	 * @return 중복된 모든 점들을 각각 {@link ErrorFeature} 생성하여 {@link List} 형태로 반환. 검수 조건을
	 *         충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validatePointDuplicated(DTFeature feature);

	/**
	 * OneAcre(지류계오류, Mismatching farmland size (Total)) 검수 수행.
	 * <p>
	 * 경지계 객체와 지류계 객체의 포함관계가 맞지 않은 경우 오류 객체로 반환.
	 * <p>
	 * 지류계는 2개 이상의 경지계 객체와 겹쳐져 있어야 하며 경계가 일치해야 함. 또한 경지계 객체들의 넓이의 합이 지류계 1개의 객체의 넓이와
	 * 동일해야 함.
	 * 
	 * @param feature 지류계오류 검수 대상 {@link DTFeature} 객체
	 * @param reSfc   경지계 {@link SimpleFeatureCollection}
	 * @return 검수 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateOneAcre(DTFeature feature, SimpleFeatureCollection reSfc);

	/**
	 * OneStage(경지계오류, Excluded farmland (Part)) 검수 수행.
	 * <p>
	 * 경지계 객체가 지류계 객체와 겹쳐있지 않는 경우 오류 객체로 반환.
	 * <p>
	 * 경지계는 지류계와 겹쳐있어야 하며 단독으로 존재할 수 없음.
	 * 
	 * @param feature 경지계오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayer 지류계 {@link DTLayer}
	 * @return 검수 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateOneStage(DTFeature feature, DTLayer reLayer);

	/**
	 * BuildingSiteMiss(건물부지오류, Land and facility mismatch) 검수 수행.
	 * <p>
	 * 특정 속성을 가진 건물이 해당하는 건물 부지에 포함되어 있지 않은 경우 오류 객체로 반환.
	 * <p>
	 * 건물 객체 중 "용도" 속성 값이 "위험물저장및처리시설"인 경우 "주유소" 부지 안에 포함되어 있어야 하며 "관광 휴게시설"인 경우
	 * "휴게소" 부지 안에 포함되어 있어야 함.
	 * 
	 * @param feature  건물부지오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayers 건물 객체의 포함 여부를 검수할 {@link DTLayer} List
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateBuildingSiteMiss(DTFeature feature, DTLayerList reLayers);

	/**
	 * BoundaryMiss(경계누락오류, Missing boundary) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 중심선 객체를 포함하는 {@link Polygon} 타입의 객체가 존재하지 않은 경우 오류 객체로
	 * 반환.
	 * <p>
	 * {@link LineString} 타입의 도로 중심선 객체는 도로 경계 {@link Polygon} 객체에 포함(Contains)되어야
	 * 함.
	 * <p>
	 * 하천 중심선, 철도 중심선의 경우도 각각의 경계 {@link Polygon} 객체에 포함되어야 함.
	 * 
	 * @param feature 경계누락오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayer {@link LineString} 타입의 중심선 {@link DTLayer}
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateBoundaryMiss(DTFeature feature, DTLayer reLayer);

	/**
	 * CenterLineMiss(중심선누락오류, Missing center line) 검수 수행.
	 * <p>
	 * {@link Polygon} 타입 객체 내에 {@link LineString} 타입의 중심선 객체가 존재하지 않은 경우 오류 객체로 반환.
	 * <p>
	 * {@link Polygon} 타입의 도로 경계 객체는 도로 중심선 {@link LineString} 객체를 포함(Within)해야 함.
	 * <p>
	 * 하천 경계, 철도 경계의 경우도 각각의 중심선 {@link LineString} 객체를 포함해야 함.
	 * 
	 * @param feature 중심선누락오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayer {@link Polygon} 타입의 경계 {@link DTLayer}
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateCenterLineMiss(DTFeature feature, DTLayer reLayer);

	/**
	 * HoleMisPlaceMent(홀(Hole)존재오류, Hole misplacement) 검수 수행.
	 * <p>
	 * {@link Polygon} 타입의 객체에 홀(Hole)이 존재하는 경우 오류 객체로 반환.
	 * <p>
	 * 하천 경계, 철도 경계 등 홀(Hole)이 존재하면 안되는 객체 검수 수행.
	 * 
	 * @param feature 홀(Hole)존재오류 검수 대상 {@link DTFeature} 객체
	 * @return 객체내에 존재하는 모든 홀(Hole)을 각각의 {@link ErrorFeature}로 생성하여 List 형태로 반환. 검수
	 *         조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateHoleMissplacement(DTFeature feature);

	/**
	 * EntityInHole(홀(Hole)중복오류, Hole with entity) 검수 수행.
	 * <p>
	 * {@link Polygon} 타입의 객체에 존재하는 홀(Hole)과 동일한 Geometry인 객체가 {@link DTLayer}에 존재하는
	 * 경우 오류 객체로 반환.
	 * 
	 * @param feature  홀(Hole)중복오류 검수 대상 {@link DTFeature} 객체
	 * @param layer    홀(Hole)중복오류 검수 비교 {@link DTLayer}
	 * @param isEquals 비교 {@link DTLayer}가 검수 대상 {@link DTFeature} 객체가 포함된
	 *                 {@link DTLayer}인 경우 {@code true}
	 * @return 객체 내에 중복된 모든 홀(Hole)을 각각의 {@link ErrorFeature}로 생성하여 List 형태로 반환. 검수
	 *         조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateEntityInHole(DTFeature feature, DTLayer layer, boolean isEquals);

	/**
	 * LinearDisconnection(선형단락오류, Linear disconnection) 검수 수행.
	 * <p>
	 * {@link LineString} 타입 객체의 양 끝점이 포함관계에 있지 않은 {@link Polygon} 타입 객체 내부에 존재하는 경우
	 * 오류 객체로 반환.
	 * 
	 * @param feature         선형단락오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayer         포함관계 여부를 검수할 {@link DTLayer}
	 * @param optionTolerance 허용오차 범위
	 * @return 포함관계에 있지 않은 {@link LineString}의 모든 vertex를 각각의 {@link ErrorFeature}로
	 *         생성하여 List 형태로 반환. 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateLinearDisconnection(DTFeature feature, DTLayer reLayer, OptionTolerance optionTolerance);

	/**
	 * MultiPart(다중객체존재오류, election of wrong multiple parts) 검수 수행.
	 * <p>
	 * 같은 속성을 가진 2개 이상의 객체가 하나의 객체로 존재하는 경우 오류 객체로 반환.
	 * <p>
	 * Geometry 타입이 Multi인 경우 1개의 {@link Geometry} 객체 내에 n개의 {@link Geometry}가 존재할 수
	 * 있음. 이 경우는 각 레이어의 특성에 따라 오류 객체로 간주하는 경우가 있음.
	 * 
	 * @param feature 다중객체존재오류 검수 대상 {@link DTFeature} 객체
	 * @return 1개의 {@link Geometry} 객체 내에 존재하는 n개의 모든 {@link Geometry} 객체를 각각의
	 *         {@link ErrorFeature}로 생성하여 List 형태로 반환. 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateMultiPart(DTFeature feature);

	/**
	 * UNodeMiss(시설물선형노드오류, Missing node on line (Underground)) 검수 항목 수행.
	 * <p>
	 * {@link LineString} 타입의 관로 객체가 {@link Point} 타입의 시설물 심볼과 겹쳐있는 경우 해당 vertex가 관로
	 * 객체의 양끝점이 아닌 경우 오류 객체로 반환.
	 * <p>
	 * 관로 객체는 여러개의 점들로 연결된 선형 객체이나 시설물 심볼 객체가 관로 객체가 존재할 때에 노드를 생성하고 또다른
	 * {@link LineString} 객체를 생성하여 관로를 연결해야 함.
	 * 
	 * @param feature   시설물선형노드오류 검수 대상 {@link DTFeature} 객체
	 * @param reFeature 시설물 심볼 {@link DTFeature} 객체
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateUNodeMiss(DTFeature feature, DTFeature reFeature);

	/**
	 * ULeaderLine(지시선교차오류, Leader line overlapping (Underground)) 검수 항목 수행.
	 * <p>
	 * {@link LineString} 타입의 지시선 객체가 {@link Point} 타입의 관로 이력 객체와 겹쳐있는 경우 오류 객체로 반환.
	 * 
	 * @param feature   지시선 객체
	 * @param reFeature 관로 이력 객체
	 * @return 지시선과 겹쳐있는 모든 관료이력 객체를 각각의 {@link ErrorFeature}로 생성하여 List 형태로 반환. 검수
	 *         조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateULeaderLine(DTFeature feature, DTFeature reFeature);

	/**
	 * USymbolOut(심볼단독존재오류, Symbol misplacement (Underground)) 검수 수행.
	 * <p>
	 * {@link Point} 타입의 심볼 객체가 {@link LineString} 타입의 관로 객체 위에 존재하지 않는 경우 오류 객체로
	 * 반환.
	 * 
	 * @param feature 심볼 객체
	 * @param reLayer 관로 {@link DTLayer}
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author hochul.Kim
	 */
	ErrorFeature validateUSymbolOut(DTFeature feature, DTLayer reLayer);

	/**
	 * SymbolInLine(선형내심볼미존재오류, Missing symbol on line (Underground)) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 특정 관로 객체의 vertex에 {@link Point} 타입의 심볼 객체가 존재하지 않는 경우
	 * 오류 객체로 반환.
	 * 
	 * @param feature      관로 객체
	 * @param reLayers     모든 심볼 레이어 List {@link DTLayerList}
	 * @param reTolerances 심볼 레이어 List 검수 수치조건
	 * @return 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author hochul.Kim
	 */
	List<ErrorFeature> validateSymbolInLine(DTFeature feature, DTLayerList reLayers,
			List<OptionTolerance> reTolerances);

	/**
	 * LineCross(관로상하월오류, Crossing pipes (Underground)) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 관로 객체가 관로 레이어의 서로 다른 관로 객체들이 교차하였을 때 교차 지점에 Vertex가
	 * 존재하지 않을 시 오류 객체로 반환.
	 * 
	 * @param feature        관로 객체
	 * @param validatorLayer 관로 레이어 List {@link DTLayer}
	 * @return 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author hochul.Kim
	 */
	List<ErrorFeature> validateLineCross(DTFeature feature, DTLayer validatorLayer);

	/**
	 * SymbolsDistance(심볼간격오류, Distance between symbols (Underground)) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 특정 관로 객체를 이루는 vertex들 간의 거리가 20m이상인 경우 오류 객체로 반환.
	 * 
	 * @param feature 관로 객체
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author hochul.Kim
	 */
	List<ErrorFeature> validateSymbolsDistance(DTFeature feature);

	/**
	 * UAvrgDPH10(평균심도오류(정위치), Wrong mean depth(Graphic) (Underground)) 검수 수행.
	 * <p>
	 * {@link LineString} 타입의 관로이력 객체의 평균심도 값과 {@link LineString} 타입의 관로 객체 속성값 중
	 * 평균심도, 해당 관로 객체 위에 존재하는 {@link Point} 타입의 모든 심도 객체들의 심도 평균값, {@link Point} 타입의
	 * 심도 객체와 모두 일치하지 않은 경우 오류 객체로 반환.
	 * 
	 * @param feature     관로이력 객체
	 * @param leaderLayer 평균심도 값을 속성으로 갖는 지시선 {@link DTLayer}
	 * @param lineLayer   관로 {@link DTLayer}
	 * @param textLayer   심도 {@link DTLayer}
	 * @return {@link ErrorFeature} 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateUAvrgDPH10(DTFeature feature, DTLayer leaderLayer, DTLayer lineLayer, DTLayer textLayer);

	/**
	 * EntityInHole(홀(Hole)중복오류, Hole with entity) 검수 수행(임상도 검수에 적용).
	 * <p>
	 * {@link Polygon} 타입의 객체에 존재하는 홀(Hole)과 동일한 Geometry인 객체가 {@link DTLayer}에 존재하는
	 * 경우 오류 객체로 반환.
	 * 
	 * @param feature 홀(Hole)중복오류 검수 대상 {@link DTFeature} 객체
	 * @param layer   홀(Hole)중복오류 검수 비교 {@link DTLayer}
	 * @return 객체 내에 중복된 모든 홀(Hole)을 각각의 {@link ErrorFeature}로 생성하여 List 형태로 반환. 검수
	 *         조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	List<ErrorFeature> validateFEntityInHole(DTFeature feature, DTLayer layer);

	/**
	 * SymbolOut(심볼단독존재오류, Symbol misplacement) 검수 수행(수치지도 검수에 적용).
	 * <p>
	 * {@link Point} 타입의 심볼 객체가 다른 객체와의 포함관계가 맞지 않은 경우 오류 객체로 반환.
	 * 
	 * @param feature  심볼단독존재오류 검수 대상 {@link DTFeature} 객체
	 * @param reLayers 심볼 객체와 포함관계를 검사할 {@link DTLayerList}
	 * @return 심볼 객체가 {@link DTLayerList}의 중 1개의 객체 내에 포함되지 않으면 {@link ErrorFeature}
	 *         반환. 검수 조건을 충족시키면 {@code null} 반환.
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateSymbolOut(DTFeature feature, DTLayerList reLayers);
}
