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

package com.git.gdsbuilder.validator.basic;

import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ErrorFeature 정보를 담고 있는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 3. 11. 오후 2:57:39
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicErrorFeature {

	String validation;
	String type;
	String item;
	String name;

	String layerID;
	String refLayerId;

	String comment;

	String geomStr;
	Geometry errPoint;
}
