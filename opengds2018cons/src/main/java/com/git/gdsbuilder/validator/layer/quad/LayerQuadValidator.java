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

package com.git.gdsbuilder.validator.layer.quad;

import java.io.IOException;

import org.geotools.feature.SchemaException;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.option.OptionTolerance;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

/**
 * {@link DTLayer}의 기하학적 오류, 위상관계를 검수하는 클래스.
 * <p>
 * {@link DTLayer}내에 객체 수가 검수 성능에 영향을 끼칠만큼 많이 존재하거나, 등고선 등 객체의 vertex 개수가 매우 많은
 * {@link DTLayer} 검수 항목은 성능 향상을 위해 QuadTree 알고리즘을 적용하여 검수를 수행함.
 * <p>
 * {@link LayerValidatorImpl}의 검수 항목 중 일부 항목만 구현되어 있으며 항목별 파라미터 및 리턴 타입은 동일함.
 * 
 * @author DY.Oh
 *
 */
public interface LayerQuadValidator {

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
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneAcre(지류계오류, Mismatching farmland size
	 * (Total)) 검수 수행.
	 * 
	 * @param typeLayers 경지계 {@link DTLayerList}
	 * @return {@link ErrorLayer} 지류계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneAcre(DTLayerList typeLayers);

	/**
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 OneStage(경지계오류, Excluded farmland (Part))
	 * 검수 수행.
	 * 
	 * @param typeLayers 지류계 {@link DTLayerList}
	 * @return {@link ErrorLayer} 경지계오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateOneStage(DTLayerList typeLayers);

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
	 * {@link DTLayer}의 내에 존재하는 모든 객체에 대하여 HoleMisPlaceMent(홀(Hole)존재오류, Hole
	 * misplacement) 검수 수행.
	 * 
	 * @return {@link ErrorLayer} 경계누락오류가 발생한 모든 객체. 오류 객체가 없는 경우 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorLayer validateHoleMissplacement();

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
}
