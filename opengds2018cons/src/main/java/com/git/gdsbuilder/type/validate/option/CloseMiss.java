/**
 * 
 */
package com.git.gdsbuilder.type.validate.option;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 인접 영역에 존재하는 동일 ID의 레이어 객체 속성 값 검수 항목을 정의하는 클래스
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseMiss {

	/**
	 * 검수 항목 이름
	 */
	String option;
	/**
	 * {@link com.git.gdsbuilder.type.validate.option.OptionFilter}에 따라 해당 레이어의 특정
	 * 속성 값을 가진 객체만 검수 수행
	 */
	List<OptionFilter> filter;
	/**
	 * 검수 대상 레이어와 {@link com.git.gdsbuilder.type.validate.option.OptionRelation}에
	 * 저장된 타 레이어와의 위상관계 검수 대상 레이어의 검수 수행
	 */
	List<OptionRelation> retaion;
	/**
	 * {@link com.git.gdsbuilder.type.validate.option.OptionFigure}에 저장된 레이어의 속성
	 * key, value 정보에 따라 검수 대상 레이어의 검수 수행
	 */
	List<OptionFigure> figure;
	/**
	 * {@link com.git.gdsbuilder.type.validate.option.OptionTolerance}에 저장된 허용 오차범위,
	 * 수치 조건 등의 수치 정보에 따라 검수 대상 레이어의 검수 수행
	 */
	List<OptionTolerance> tolerance;

}
