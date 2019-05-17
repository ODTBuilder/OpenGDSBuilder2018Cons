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
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link com.git.gdsbuilder.type.validate.option.specific.AttributeMiss} 또는
 * {@link com.git.gdsbuilder.type.validate.option.specific.GraphicMiss} 수행 시
 * 사용되는 속성 필터
 * <p>
 * 속성 중 특정 key 값을 가지며, 특정 value를 가진 1개의 객체 검수
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFilter {

	/**
	 * 속성 컬럼 key
	 */
	String key;
	/**
	 * 속성 value list
	 */
	List<Object> values;

}
