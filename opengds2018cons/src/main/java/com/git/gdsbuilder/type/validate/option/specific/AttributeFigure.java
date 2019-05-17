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
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * 
 * @author DY.Oh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFigure {

	/**
	 * {@link com.git.gdsbuilder.type.validate.option.specific.AttributeFigure} 정의
	 * 순서에 따라 부여되는 index
	 */
	Long fIdx;
	/**
	 * 속성 key
	 */
	String key;
	/**
	 * 속성 value List
	 */
	List<Object> values;
	/**
	 * 속성 value (수치형 속성 value 검수할 때 사용)
	 */
	Double number;
	/**
	 * 속성 value 조건 (수치형 속성 value 검수할 때 사용)
	 */
	String condition;
	/**
	 * 속성 value 간격 (수치형 속성 value 검수할 때 사용)
	 */
	Double interval;

}
