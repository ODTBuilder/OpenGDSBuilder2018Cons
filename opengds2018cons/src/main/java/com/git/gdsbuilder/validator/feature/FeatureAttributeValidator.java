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

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.option.AttributeFigure;
import com.git.gdsbuilder.type.validate.option.FixedValue;
import com.git.gdsbuilder.type.validate.option.OptionFigure;

/**
 * {@link DTFeature}의 속성을 검수하는 클래스
 * 
 * @author DY.Oh
 *
 */
public interface FeatureAttributeValidator {

	/**
	 * ZvalueAmbiguou(고도값오류, Wrong elevation) 검수 수행.
	 * <p>
	 * 등고선 또는 건물 등과 같이 높이값을 속성으로 갖는 객체에 대하여 높이값의 범위 및 유효성을 검사하는 항목.
	 * 
	 * @param feature 등고선 또는 건물 등과 같이 높이값을 속성으로 갖는 {@link DTFeature} 객체
	 * @param figure  높이값의 범위 및 유효성 검사 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateZvalueAmbiguous(DTFeature feature, OptionFigure figure);

	/**
	 * BridgeNameMiss(교량명오류, Wrong bridge name) 검수 수행.
	 * <p>
	 * 하천을 위를 가로 지르는 교량의 교량명 속성값과 하천 중심선의 교량명 속성값의 일치 여부를
	 * 검사하file:///D:/consumer/opengds2018cons/doc/com/git/gdsbuilder/type/validate/error/ErrorFeature.html는
	 * 항목.
	 * 
	 * @param feature   교량 {@link DTFeature} 객체
	 * @param reFeature 하천 중심선 {@link DTFeature} 객체
	 * @param figure    교량 {@link DTFeature} 객체의 교량명 속성값 검사 조건
	 * @param reFigure  하천 중심선 {@link DTFeature} 객체의 교량명 속성값 검사 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateBridgeNameMiss(DTFeature feature, DTFeature reFeature, OptionFigure figure,
			OptionFigure reFigure);

	/**
	 * AdminBoundaryMiss(행정명오류, Administrative boundary mismatch) 검수 수행.
	 * <p>
	 * 행정경계 객체(시, 도, 읍/면/동)의 속성이 잘못 입력된 경우 오류 객체 반환.
	 * 
	 * @param feature 행정경계 (시, 도, 읍/면/동) {@link DTFeature} 객체
	 * @param figure  행정경계 {@link DTFeature} 객체의 속성값 검사 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateAdminBoundaryMiss(DTFeature feature, OptionFigure figure);

	/**
	 * NumericalValue(수치값오류, Wrong numerical value) 검수 수행.
	 * <p>
	 * 객체의 속성 중 특정 속성값이 일정한 수치값 범위에 속하지 않는 경우 오류 객체 반환.
	 * 
	 * @param feature 수치 속성값을 갖는 {@link DTFeature} 객체
	 * @param figure  {@link DTFeature} 객체의 수치값 범위 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateNumericalValue(DTFeature feature, OptionFigure figure);

	/**
	 * EntityDuplicated(요소중복오류, Duplicated features) 검수 수행.
	 * <p>
	 * 두개의 {@link DTFeature} 객체의 모든 속성이 동일할 경우 오류 객체 반환.
	 * 
	 * @param feature   중복오류를 검사할 대상 {@link DTFeature} 객체
	 * @param reFeature 대상 {@link DTFeature} 객체와 비교할 {@link DTFeature} 객체
	 * @return {@link ErrorFeature} 두 객체의 모든 속성이 동일할 경우 대상 {@link DTFeature} 객체를
	 *         {@link ErrorFeature}로 생성하여 반환.
	 *         <p>
	 *         검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateEntityDuplicated(DTFeature feature, DTFeature reFeature);

	/**
	 * UAvrgDPH20(평균심도오류(구조화), Wrong mean depth(Attribute) (Underground)) 검수 수행.
	 * <p>
	 * 관로 객체의 속성값 중 평균심도에 대한 값이 해당 관로 객체 위에 존재하는 모든 심도 레이어내 객체들의 심도값 평균과 일치하는지 검사하는
	 * 항목.
	 * 
	 * @param feature   평균심도를 속성값으로 가지는 관로 객체 {@link DTFeature}
	 * @param figures   관로 속성값 검사 조건
	 * @param reLayer   심도 레이어 {@link DTLayer}
	 * @param reFigures 심도 속성값 검사 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateUAvrgDPH20(DTFeature feature, List<AttributeFigure> figures, DTLayer reLayer,
			List<AttributeFigure> reFigures);

	/**
	 * LayerFixMiss(필드구조오류, Feature with wrong attribute value) 검수 수행.
	 * <p>
	 * {@link DTFeature} 객체의 속성 필드 구조(필수 컬럼 누락, 컬럼 타입, 필수 속성값 누락 등)가 조건에 일치하지 않는 경우
	 * 오류 객체 반환.
	 * 
	 * @param feature    속성 필드 구조 검수 대상 {@link DTFeature}
	 * @param fixedValue 속성 필드 구조(필수 컬럼 누락, 컬럼 타입, 필수 속성값 누락 등) 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateLayerFixMiss(DTFeature feature, List<FixedValue> fixedValue);

	/**
	 * SymbolDirection(시설물심볼방향오류, Mismatching direction of symbol (Underground)) 검수
	 * 수행.
	 * <p>
	 * 관로 객체의 특정 vertex와 다음 vertex의 방향과 해당 vertex들의 사이에 존재하는 시설물 객체의 방향값이 일치하는지 검수하는
	 * 항목.
	 * 
	 * @param feature   관로 객체 {@link DTFeature}
	 * @param figures   관로 객체 방향 조건
	 * @param reFeature 관로 객체의 vertex들의 사이에 존재하는 시설물 객체 {@link DTFeature}
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateUSymbolDirection(DTFeature feature, List<AttributeFigure> figures, DTFeature reFeature);

	/**
	 * FcodeLogicalAttribute(FCode불일치, Wrong F Code (Forest)) 검수 수행.
	 * <p>
	 * 임목 객체의 속성 중 Fcode 속성값이 논리적으로 올바르지 않은 경우 오류 객체 반환.
	 * 
	 * @param feature 임목 객체 {@link DTFeature}
	 * @param figure  임목 Fcode 속성 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateFcodeLogicalAttribute(DTFeature feature, OptionFigure figure);

	/**
	 * FLabelLogicalAttribute(Label불일치, Wrong F Label (Forest)) 검수 수행.
	 * <p>
	 * 임목 객체의 속성 중 Lable 속성값이 논리적으로 올바르지 않은 경우 오류 객체 반환.
	 * 
	 * @param feature 임목 객체 {@link DTFeature}
	 * @param figure  임목 Label 속성 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateFLabelLogicalAttribute(DTFeature feature, OptionFigure figure);

	/**
	 * UFIDMiss (UFID오류, Missing UFID) 검수 수행.
	 * <p>
	 * 객체의 속성값 중 Unique ID인 UFID 속성값이 표준 조건에 부합 여부 및 중복 여부를 검사하는 항목.
	 * 
	 * @param feature        UFID 오류 검수 대상 {@link DTFeature}
	 * @param collectionName 표준 조건 확인을 위한 도엽번호
	 * @param layerName      표준 조건 확인을 위한 레이어 ID
	 * @param figure         UFID 속성값 표준 조건
	 * @param reLayer        중복 여부를 검사 {@link DTLayer}
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	ErrorFeature validateUFIDMiss(DTFeature feature, String collectionName, String layerName, OptionFigure figure,
			DTLayer reLayer);

	/**
	 * Dissolve(인접속성병합오류, Discord of adjacent attribute) 검수 수행.
	 * <p>
	 * 속성 조건이 동일한 인접객체가 병합되지 않은 경우 오류 객체 반환
	 * 
	 * @param feature 인접속성병합오류 검수 대상 {@link DTFeature}
	 * @param sfc     인접속성병합오류 검수 대상 {@link DTFeature}이 포함된
	 *                {@link SimpleFeatureCollection}
	 * @param figure  속성 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author hochul.kim
	 */
	List<ErrorFeature> validateDissolve(DTFeature feature, SimpleFeatureCollection sfc, OptionFigure figure);
}
