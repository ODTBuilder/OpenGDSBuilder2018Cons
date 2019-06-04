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
 * 특정 레이어의 속성 컬럼 구조, 고정 속성값 검수 항목을 정의하는 클래스
 * 
 * @author DY.Oh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedValue {

	/**
	 * 속성 컬럼명
	 */
	String key;
	/**
	 * 속성 타입(ex. String, Integer, Double..)
	 */
	String type;
	/**
	 * 속성값 Null 허용 여부
	 */
	boolean isnull;
	/**
	 * 속성값 길이
	 */
	Long length;
	/**
	 * 고정 속성값 List (해당 속성값만 가질 수 있음)
	 */
	List<Object> values;

}
