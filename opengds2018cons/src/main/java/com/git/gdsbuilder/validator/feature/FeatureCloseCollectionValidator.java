package com.git.gdsbuilder.validator.feature;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.error.ErrorFeature;
import com.git.gdsbuilder.type.validate.option.specific.OptionFigure;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.vividsolutions.jts.geom.Geometry;

/**
 * 검수 영역과 인접한 두 {@link DTFeature} 객체를 검수하는 클래스.
 * <p>
 * 검수 영역의 허용오차 범위 내에 있는 객체는 인접 영역 내에 동일한 속성을 가진 객체가 존재해야함. 객체가 존재하지 않거나 속성이 일치하지
 * 않는 경우 오류 객체를 반환함.
 * 
 * @author DY.Oh
 *
 */
public interface FeatureCloseCollectionValidator {

	/**
	 * FRefEntityNone(인접요소부재오류, Missing adjacent feature) 검수 수행. 임상도 검수 시 사용.
	 * <p>
	 * 검수 영역 허용오차 범위 내에 검수 대상 {@link DTFeature}는 존재하나 인접영역 내의 객체가 존재하지 않는 경우 오류 객체
	 * 반환.
	 * 
	 * @param feature       검수 영역 허용오차 범위 내에 있는 검수 대상 {@link DTFeature}
	 * @param closeLayer    검수 대상 {@link DTFeature}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorFeature validateFRefEntityNone(DTFeature feature, DTLayer closeLayer, Geometry closeBoundary,
			OptionTolerance tolerance);

	/**
	 * DRefEntityNone(인접요소부재오류, Missing adjacent feature) 검수 수행. 수치지도 검수 시 사용.
	 * <p>
	 * 검수 영역 허용오차 범위 내에 검수 대상 {@link DTFeature}는 존재하나 인접영역 내의 객체가 존재하지 않는 경우 오류 객체
	 * 반환.
	 * 
	 * @param feature       검수 영역 허용오차 범위 내에 있는 검수 대상 {@link DTFeature}
	 * @param closeLayer    검수 대상 {@link DTFeature}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역
	 * @param tolerance     검수 영역 허용오차 범위
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorFeature validateDRefEntityNone(DTFeature feature, DTLayer closeLayer, Geometry closeBoundary,
			OptionTolerance tolerance);

	/**
	 * RefAttributeMiss(인접요소속성오류, Missing attribute of adjacent features) 검수 수행.
	 * <p>
	 * 검수 영역 허용오차 범위 내에 존재하는 검수 대상 {@link DTFeature}와 인접영역 내의 존재하는 {@link DTFeature}
	 * 객체의 속성 일치 여부를 검사하는 항목.
	 * 
	 * @param feature       검수 영역 허용오차 범위 내에 있는 검수 대상 {@link DTFeature}
	 * @param closeLayer    검수 대상 {@link DTFeature}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역
	 * @param tolerance     검수 영역 허용오차 범위
	 * @param figure        속성 일치 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorFeature validateRefAttributeMiss(DTFeature feature, DTLayer closeLayer, Geometry closeBoundary,
			OptionTolerance tolerance, OptionFigure figure);

	/**
	 * RefZValueMiss(인접요소고도값오류, Wrong elevation of adjacent feature) 검수 수행.
	 * <p>
	 * 검수 영역 허용오차 범위 내에 존재하는 검수 대상 {@link DTFeature}와 인접영역 내의 존재하는 {@link DTFeature}
	 * 객체의 고도값오류를 검수 하는 항목.
	 * 
	 * @param feature       검수 영역 허용오차 범위 내에 있는 검수 대상 {@link DTFeature}
	 * @param closeLayer    검수 대상 {@link DTFeature}와 동일한 ID를 가진 인접영역 {@link DTLayer}
	 * @param closeBoundary 검수 영역
	 * @param tolerance     검수 영역 허용오차 범위
	 * @param figure        고도값 검수 조건
	 * @return {@link ErrorFeature} 검사 조건을 충족시키면 {@code null} 반환
	 * 
	 * @author DY.Oh
	 */
	public ErrorFeature validateRefZValueMiss(DTFeature feature, DTLayer closeLayer, Geometry closeBoundary,
			OptionTolerance tolerance, OptionFigure figure);
}
