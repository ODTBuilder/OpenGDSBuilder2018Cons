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
package com.git.gdsbuilder.type.validate.option;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 검수 대상 레이어와 타 레이어의 위상관계, 속성관계 검수 필요 시 타 레이어의 정보를 저장하는 클래스
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionRelation {

	/**
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayerList}의 분류 명
	 */
	String name;
	/**
	 * {@link com.git.gdsbuilder.type.validate.option.OptionFilter}에 따라 해당 레이어의 특정
	 * 속성 값을 가진 객체만 검수 수행
	 */
	List<OptionFilter> filters;
	/**
	 * {@link com.git.gdsbuilder.type.validate.option.AttributeMiss} 검수 시 필요한 검수 대상
	 * 객체의 속성 key, value 정보를 저장
	 */
	List<OptionFigure> figures;
	/**
	 * 
	 */
	List<OptionTolerance> tolerances;

	/**
	 * code에 해당하는 {@link OptionFilter} 반환.
	 * 
	 * @param code 레이어 ID
	 * @return code에 해당하는 {@link OptionFilter}
	 * 
	 * @author DY.Oh
	 */
	public OptionFilter getFilter(String code) {

		if (filters != null) {
			for (OptionFilter filter : filters) {
				if (filter.getLayerID().equals(code)) {
					return filter;
				}
			}
		}
		return null;
	}

	/**
	 * code에 해당하는 {@link OptionFigure} 반환.
	 * 
	 * @param code 레이어 ID
	 * @return code에 해당하는 {@link OptionFigure}
	 * 
	 * @author DY.Oh
	 */
	public OptionFigure getFigure(String code) {

		if (figures != null) {
			for (OptionFigure figure : figures) {
				if (figure.getLayerID().equals(code)) {
					return figure;
				}
			}
		}
		return null;
	}

	/**
	 * code에 해당하는 {@link OptionTolerance} 반환.
	 * 
	 * @param code 레이어 ID
	 * @return code에 해당하는 {@link OptionTolerance}
	 * 
	 * @author DY.Oh
	 */
	public OptionTolerance getTolerance(String code) {

		if (tolerances != null) {
			for (OptionTolerance tolerance : tolerances) {
				if (tolerance.getLayerID().equals(code)) {
					return tolerance;
				}
			}
		}
		return null;
	}
}
