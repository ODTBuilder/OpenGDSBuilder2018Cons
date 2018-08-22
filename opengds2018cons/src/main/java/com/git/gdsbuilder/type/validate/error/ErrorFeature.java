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

package com.git.gdsbuilder.type.validate.error;

import com.vividsolutions.jts.geom.Geometry;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ErrorFeature 정보를 담고 있는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 3. 11. 오후 2:57:39
 */

@Data
@AllArgsConstructor
public class ErrorFeature {

	String layerID;
	String featureID;

	String refLayerId;
	String refFeatureId;

	String errCode;
	String errType;
	String errName;
	String comment;
	Geometry errPoint;

	public ErrorFeature(String featureID, String errCode, String errType, String errName, String comment,
			Geometry errPoint) {
		super();
		this.layerID = "";
		this.featureID = featureID;
		this.errCode = errCode;
		this.errType = errType;
		this.errName = errName;
		this.comment = comment;
		this.errPoint = errPoint;
	}

	public ErrorFeature(String featureID, String refLayerId, String refFeatureId, String errCode, String errType,
			String errName, String comment, Geometry errPoint) {
		super();
		this.layerID = "";
		this.featureID = featureID;
		this.refLayerId = refLayerId;
		this.refFeatureId = refFeatureId;
		this.errCode = errCode;
		this.errType = errType;
		this.errName = errName;
		this.comment = comment;
		this.errPoint = errPoint;
	}

	public ErrorFeature(String featureID, String refFeatureId, String errCode, String errType, String errName,
			String comment, Geometry errPoint) {
		super();
		this.layerID = "";
		this.featureID = featureID;
		this.refFeatureId = refFeatureId;
		this.errCode = errCode;
		this.errType = errType;
		this.errName = errName;
		this.comment = comment;
		this.errPoint = errPoint;
	}
}
