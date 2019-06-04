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
package com.git.gdsbuilder.validator.layer;

import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.SchemaException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.option.FixedValue;
import com.git.gdsbuilder.type.validate.option.OptionFigure;
import com.git.gdsbuilder.type.validate.option.OptionTolerance;
import com.git.gdsbuilder.validator.feature.FeatureAttributeValidator;
import com.git.gdsbuilder.validator.feature.FeatureCloseCollectionValidator;
import com.git.gdsbuilder.validator.feature.FeatureGraphicValidator;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * {@link DTLayer}의 기하학적 오류, 위상관계, 속성, 인접영역 검수하는 클래스.
 * <p>
 * {@link DTLayer}의 {@link SimpleFeatureCollection}를 검수하는 클래스로 각 검수 항목마다 요구되는
 * 형태로 {@link FeatureGraphicValidator}, {@link FeatureAttributeValidator},
 * {@link FeatureCloseCollectionValidator}를 요청함.
 * 
 * @author DY.Oh
 */
public interface LayerValidator {

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 ConBreak(등고선끊김오류, Contour line
	 * disconnections) 검수 수행.
	 * 
	 * @param relationLayers 등고선 {@link DTLayer}의 객체와 맞닿아 있는지 여부를 검사할
	 *                       {@link DTLayerList}.
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 등고선끊김오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateConBreak(DTLayerList relationLayers, OptionTolerance tolerance) throws SchemaException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 ConIntersected(등고선교차오류, Contour line
	 * intersections) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 등고선교차오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateConIntersected() throws SchemaException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 ConOverDegree(등고선꺾임오류, Unsmooth
	 * contour line curves) 검수 수행.
	 * 
	 * @param tolerance 허용오차 범위
	 * @return {@link ErrorLayer} 등고선꺾임오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateConOverDegree(OptionTolerance tolerance) throws SchemaException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 ZvalueAmbiguou(고도값오류, Wrong
	 * elevation) 검수 수행.
	 * 
	 * @param figure 높이값의 범위 및 유효성 검사 조건
	 * @return {@link ErrorLayer} 고도값오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateZValueAmbiguous(OptionFigure figure) throws SchemaException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 UselessPoint(직선화미처리오류, Useless points
	 * in contour line) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 직선화미처리오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @throws NoSuchAuthorityCodeException {@link NoSuchAuthorityCodeException}
	 * @throws SchemaException              {@link SchemaException}
	 * @throws FactoryException             {@link FactoryException}
	 * @throws TransformException           {@link TransformException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUselessPoint()
			throws NoSuchAuthorityCodeException, SchemaException, FactoryException, TransformException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SmallArea(허용범위이하면적오류, Areas between
	 * tolerance limit) 검수 수행.
	 * 
	 * @param tolerance 허용 면적(㎡)
	 * @return {@link ErrorLayer} 허용범위이하면적오류가 발생한 모든 등고선 객체. 오류 객체가 없는 경우
	 *         {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSmallArea(OptionTolerance tolerance) throws SchemaException;

	/**
	 * 등고선 {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SmallLength(허용범위이하길이오류, Segments
	 * between length tolerance limit) 검수 수행.
	 * 
	 * @param tolerance 허용 길이(m)
	 * @return {@link ErrorLayer} 허용범위이하길이오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSmallLength(OptionTolerance tolerance) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OverShoot(기준점초과오류, Feature crossing the
	 * sheet) 검수 수행.
	 * 
	 * @param relationLayers 검수 대상 {@link DTLayer}의 객체와 맞닿아 있는지 여부를 검사할
	 *                       {@link DTLayerList}.
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 기준점초과오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOverShoot(DTLayerList relationLayers, OptionTolerance tolerance) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SelfEntity(단독존재오류, Overlapping features)
	 * 검수 수행.
	 * 
	 * @param relationLayers 검수 대상 {@link DTLayer}의 객체와 겹쳐 있는지 여부를 검사할
	 *                       {@link DTLayerList}.
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 단독존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * @throws IOException     {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSelfEntity(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException, IOException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SelfEntity(단독존재오류, Overlapping features)
	 * 검수 수행.
	 * 
	 * @param relationLayer 검수 대상 {@link DTLayer}의 객체와 겹쳐 있는지 여부를 검사할
	 *                      {@link DTLayer}.
	 * @param tolerance     허용오차 범위
	 * @return {@link ErrorLayer} 단독존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * @throws IOException     {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSelfEntity(DTLayer relationLayer, OptionTolerance tolerance)
			throws SchemaException, IOException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OutBoundary(경계초과오류, Feature crossing the
	 * boundary) 검수 수행.
	 * 
	 * @param relationLayers 검수 대상 {@link DTLayer}의 경계 초과 여부를 검사할 {@link DTLayer}.
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 경계초과오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOutBoundary(DTLayerList relationLayers, OptionTolerance tolerance) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 EntityDuplicated(요소중복오류, Duplicated
	 * features) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 요소중복오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateEntityDuplicated() throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 EntityOpenMiss(객체폐합오류, Unclosed feature)
	 * 검수 수행.
	 * 
	 * @param relationLayers 검수 대상 {@link DTLayer} 객체와 객체폐합오류 검사를 수행할
	 *                       {@link DTLayerList}
	 * @param tole           허용오차 범위
	 * @return {@link ErrorLayer} 객체폐합오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateEntityOpenMiss(DTLayerList relationLayers, OptionTolerance tole) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 BridgeNameMiss(교량명오류, Wrong bridge name)
	 * 검수 수행.
	 * 
	 * @param figure         교량 {@link DTLayer}의 교량명 속성값 검사 조건
	 * @param relationLayers 검수 대상 {@link DTLayer} 객체와 교량명오류 검사를 수행할
	 *                       {@link DTLayerList}
	 * @param reFigures      하천 중심선 {@link DTLayer}의 교량명 속성값 검사 조건
	 * @return {@link ErrorLayer} 교량명오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateBridgeNameMiss(OptionFigure figure, DTLayerList relationLayers,
			List<OptionFigure> reFigures) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 AdminBoundaryMiss(행정명오류, Administrative
	 * boundary mismatch) 검수 수행.
	 * 
	 * @param figure 행정경계 {@link DTLayer}의 속성값 검사 조건
	 * @return {@link ErrorLayer} 행정명오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateAdminBoundaryMiss(OptionFigure figure) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 TwistedPolygon(폴리곤꼬임오류, Twisted polygons)
	 * 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 폴리곤꼬임오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateTwistedPolygon() throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 NodeMiss(선형노드오류, Missing node) 검수 수행.
	 * 
	 * @param relationLayers 검수 대상 {@link DTLayer}의 객체와 선형노드오류 여부를 검사할
	 *                       {@link DTLayerList}.
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 선형노드오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * @throws IOException     {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateNodeMiss(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException, IOException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 NodeMiss(선형노드오류, Missing node) 검수 수행.
	 * 
	 * @param relationLayer 검수 대상 {@link DTLayer}의 객체와 선형노드오류 여부를 검사할
	 *                      {@link DTLayer}.
	 * @param tolerance     허용오차 범위
	 * @return {@link ErrorLayer} 선형노드오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * @throws IOException     {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateNodeMiss(DTLayer relationLayer, OptionTolerance tolerance);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 PointDuplicated(중복점오류, Duplicated point)
	 * 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 중복점오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validatePointDuplicated();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneAcre(지류계오류, Mismatching farmland size
	 * (Total)) 검수 수행.
	 * 
	 * @param relationLayerList 경지계 {@link DTLayerList}
	 * @return {@link ErrorLayer} 지류계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneAcre(DTLayerList relationLayerList);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneAcre(지류계오류, Mismatching farmland size
	 * (Total)) 검수 수행.
	 * 
	 * @param relationLayer 경지계 {@link DTLayer}
	 * @return {@link ErrorLayer} 지류계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneAcre(DTLayer relationLayer);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneStage(경지계오류, Excluded farmland (Part))
	 * 검수 수행.
	 * 
	 * @param relationLayerList 지류계 {@link DTLayerList}
	 * @return {@link ErrorLayer} 경지계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneStage(DTLayerList relationLayerList);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneStage(경지계오류, Excluded farmland (Part))
	 * 검수 수행.
	 * 
	 * @param relationLayer 지류계 {@link DTLayer}
	 * @return {@link ErrorLayer} 경지계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneStage(DTLayer relationLayer);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 UFIDMiss (UFID오류, Missing UFID) 검수 수행.
	 * 
	 * @param collectionName 표준 조건 확인을 위한 도엽번호
	 * @param layerName      표준 조건 확인을 위한 레이어 ID
	 * @param figure         UFID 속성값 표준 조건
	 * @param relationLayers 중복 여부를 검사 {@link DTLayerList}
	 * @return {@link ErrorLayer} UFID오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUFIDMiss(String collectionName, String layerName, OptionFigure figure,
			DTLayerList relationLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 BuildingSiteMiss(건물부지오류, Land and
	 * facility mismatch) 검수 수행.
	 * 
	 * @param relationLayerList 건물 객체의 포함 여부를 검수할 {@link DTLayerList}
	 * @return {@link ErrorLayer} 건물부지오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateBuildingSiteMiss(DTLayerList relationLayerList);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 BoundaryMiss(경계누락오류, Missing boundary) 검수
	 * 수행.
	 * 
	 * @param relationLayers {@link LineString} 타입의 중심선 {@link DTLayerList}
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateBoundaryMiss(DTLayerList relationLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 BoundaryMiss(경계누락오류, Missing boundary) 검수
	 * 수행.
	 * 
	 * @param relationLayer {@link LineString} 타입의 중심선 {@link DTLayer}
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateBoundaryMiss(DTLayer relationLayer);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 CenterLineMiss(중심선누락오류, Missing center
	 * line) 검수 수행.
	 * 
	 * @param relationLayers {@link Polygon} 타입의 경계 {@link DTLayerList}
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateCenterLineMiss(DTLayerList relationLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 CenterLineMiss(중심선누락오류, Missing center
	 * line) 검수 수행.
	 * 
	 * @param relationLayer {@link Polygon} 타입의 경계 {@link DTLayer}
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateCenterLineMiss(DTLayer relationLayer);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 HoleMisPlaceMent(홀(Hole)존재오류, Hole
	 * misplacement) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateHoleMissplacement();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 EntityInHole(홀(Hole)중복오류, Hole with
	 * entity) 검수 수행.
	 * 
	 * @param relationLayers 홀(Hole)중복오류 검수 비교 {@link DTLayerList}
	 * @return {@link ErrorLayer} 홀(Hole)중복오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateEntityInHole(DTLayerList relationLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 LinearDisconnection(선형단락오류, Linear
	 * disconnection) 검수 수행.
	 * 
	 * @param relationLayers 포함관계 여부를 검수할 {@link DTLayerList}
	 * @param tolerance      허용오차 범위
	 * @return {@link ErrorLayer} 선형단락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer valildateLinearDisconnection(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 LinearDisconnection(선형단락오류, Linear
	 * disconnection) 검수 수행.
	 * 
	 * @param relationLayer 포함관계 여부를 검수할 {@link DTLayer}
	 * @param tolerance     허용오차 범위
	 * @return {@link ErrorLayer} 선형단락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer valildateLinearDisconnection(DTLayer relationLayer, OptionTolerance tolerance)
			throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 MultiPart(다중객체존재오류, election of wrong
	 * multiple parts) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 다중객체존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateMultiPart();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 FRefEntityNone(인접요소부재오류, Missing adjacent
	 * feature) 검수 수행. 임상도 검수 시 사용.
	 * 
	 * @param closeLayer    검수 대상 {@link DTLayer}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역 {@link Geometry}
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorLayer} 인접요소부재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateFRefEntityNone(DTLayer closeLayer, Geometry closeBoundary, OptionTolerance tolerance);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 DRefEntityNone(인접요소부재오류, Missing adjacent
	 * feature) 검수 수행. 수치지도 검수 시 사용.
	 * 
	 * @param closeLayer    검수 대상 {@link DTLayer}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역 {@link Geometry}
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorLayer} 인접요소부재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateDRefEntityNone(DTLayer closeLayer, Geometry closeBoundary, OptionTolerance tolerance);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 RefAttributeMiss(인접요소속성오류, Missing
	 * attribute of adjacent features) 검수 수행.
	 * 
	 * @param closeLayer    검수 대상 {@link DTLayer}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역 {@link Geometry}
	 * @param figure        속성 일치 조건
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorLayer} 인접요소속성오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateRefAttributeMiss(DTLayer closeLayer, Geometry closeBoundary, OptionFigure figure,
			OptionTolerance tolerance);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 RefZValueMiss(인접요소고도값오류, Wrong elevation
	 * of adjacent feature) 검수 수행.
	 * 
	 * @param closeLayer    검수 대상 {@link DTLayer}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역 {@link Geometry}
	 * @param figure        고도값 검수 조건
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorLayer} 인접요소고도값오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateRefZValueMiss(DTLayer closeLayer, Geometry closeBoundary, OptionFigure figure,
			OptionTolerance tolerance);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 UNodeMiss(시설물선형노드오류, Missing node on line
	 * (Underground)) 검수 항목 수행.
	 * 
	 * @param relationLayers 시설물 심볼 {@link DTLayerList}
	 * @return {@link ErrorLayer} 시설물선형노드오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUNodeMiss(DTLayerList relationLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 UAvrgDPH20(평균심도오류(구조화), Wrong mean
	 * depth(Attribute) (Underground)) 검수 수행.
	 * 
	 * @param figures        관로 속성값 검사 조건
	 * @param relationLayers 심도 레이어 {@link DTLayerList}
	 * @param reFigure       심도 속성값 검사 조건
	 * @return {@link ErrorLayer} 평균심도오류(구조화)오류가 발생한 모든 객체. 오류 객체가 없는 경우
	 *         {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUAvrgDPH20(List<OptionFigure> figures, DTLayerList relationLayers,
			List<OptionFigure> reFigure);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 ULeaderLine(지시선교차오류, Leader line
	 * overlapping (Underground)) 검수 항목 수행.
	 * 
	 * @param relationLayers 관로 이력 {@link DTLayerList}
	 * @return {@link ErrorLayer} 지시선교차 오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateULeaderline(DTLayerList relationLayers) throws SchemaException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 UAvrgDPH10(평균심도오류(정위치), Wrong mean
	 * depth(Graphic) (Underground)) 검수 수행.
	 * 
	 * @param relationLayers 평균심도 값을 속성으로 갖는 지시선 {@link DTLayerList}
	 * @param reTolerances   지시선 {@link DTLayerList} 검수 수치 조건
	 * @return {@link ErrorLayer} 평균심도(정위치) 오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUAvrgDPH10(DTLayerList relationLayers, List<OptionTolerance> reTolerances);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SymbolDirection(시설물심볼방향오류, Mismatching
	 * direction of symbol (Underground)) 검수 수행.
	 * 
	 * @param figures        관로 방향 조건
	 * @param relationLayers 관로 객체의 vertex들의 사이에 존재하는 시설물 {@link DTLayerList}
	 * @param reFigures      시설물 {@link DTLayerList} 검수 속성 조건
	 * @return {@link ErrorLayer} 시설물심볼방향오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateUSymbolDirection(List<OptionFigure> figures, DTLayerList relationLayers,
			List<OptionFigure> reFigures);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 USymbolOut(심볼단독존재오류, Symbol misplacement
	 * (Underground)) 검수 수행.
	 * 
	 * @param lines 관로 {@link DTLayerList}
	 * @return {@link ErrorLayer} 심볼단독존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author hochul.Kim
	 */
	public ErrorLayer validateUSymbolOut(DTLayerList lines);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SymbolInLine(선형내심볼미존재오류, Missing symbol
	 * on line (Underground)) 검수 수행.
	 * 
	 * @param points       모든 심볼 {@link DTLayerList}
	 * @param reTolerances 심볼 레이어의 검수 수치조건
	 * @return {@link ErrorLayer} 선형내심볼미존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * @author hochul.Kim
	 */
	public ErrorLayer validateSymbolInLine(DTLayerList points, List<OptionTolerance> reTolerances);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SymbolsDistance(심볼간격오류, Distance between
	 * symbols (Underground)) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 심볼간격오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author hochul.Kim
	 */
	public ErrorLayer validateSymbolsDistance();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 LineCross(관로상하월오류, Crossing pipes
	 * (Underground)) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 관로상하월오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author hochul.Kim
	 */
	public ErrorLayer validateLineCross();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 LayerFixMiss(Geometry타입오류, Feature with
	 * wrong geometry type) 또는 (필드구조오류, Feature with wrong attribute value) 검수 수행.
	 * 
	 * @param geometry   Geometry 타입
	 * @param fixedValue 속성 필드구조
	 * @return {@link ErrorLayer} Geometry타입오류 또는 필드구조오류가 발생한 모든 객체. 오류 객체가 없는 경우
	 *         {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateLayerFixMiss(String geometry, List<FixedValue> fixedValue);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 FcodeLogicalAttribute(FCode, Wrong F Code
	 * (Forest)) 검수 수행.
	 * 
	 * @param figures 임목 Fcode 속성 조건
	 * @return {@link ErrorLayer} FCode 오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateFcodeLogicalAttribute(List<OptionFigure> figures);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 FLabelLogicalAttribute(Label불일치, Wrong F
	 * Label (Forest)) 검수 수행.
	 * 
	 * @param figures 임목 Label 속성 조건
	 * @return {@link ErrorLayer} Label불일치 오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateFLabelLogicalAttribute(List<OptionFigure> figures);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 NumericalValue(수치값오류, Wrong numerical
	 * value) 검수 수행.
	 * 
	 * @param figure 수치값 범위 조건
	 * @return {@link ErrorLayer} 수치값 오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateNumericalValue(OptionFigure figure);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 EntityInHole(홀(Hole)중복오류, Hole with
	 * entity) 검수 수행(임상도 검수에 적용).
	 * 
	 * @return {@link ErrorLayer} 홀(Hole)중복오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null}
	 *         반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateFEntityInHole();

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 Dissolve(인접속성병합오류, Discord of adjacent
	 * attribute) 검수 수행.
	 * 
	 * @param figures 인접속성 조건
	 * @return {@link ErrorLayer} 인접속성병합오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author hochul.Kim
	 */
	public ErrorLayer validateDissolve(List<OptionFigure> figures);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SelfEntity(단독존재오류, Overlapping features)
	 * 검수 수행.
	 * 
	 * @param tolerance 허용오차 범위
	 * @return {@link ErrorLayer} 인접속성병합오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * @throws SchemaException {@link SchemaException}
	 * @throws IOException     {@link IOException}
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSelfEntity(OptionTolerance tolerance) throws SchemaException, IOException;

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 SymbolOut(심볼단독존재오류, Symbol missplacement)
	 * 검수 수행(수치지도 검수에 적용).
	 * 
	 * @param relationLayers 심볼 {@link DTLayer}와 포함관계를 검사할 {@link DTLayerList}
	 * @return {@link ErrorLayer} 심볼단독존재오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateSymbolOut(DTLayerList relationLayers);

}
