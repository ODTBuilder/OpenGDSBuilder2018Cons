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

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link com.git.gdsbuilder.type.validate.error.ErrorFeature} 정보를 담고 있는 클래스
 * <p>
 * 검수 후 오류가 있는 객체에 대해 ID, 오류 종류, 세부 정보, 오류 위치 등을 저장함.
 * 
 * @author DY.Oh
 */

@Data
@NoArgsConstructor
public class ErrorFeature {

	/**
	 * 오류 객체가 포함된 레이어 ID
	 */
	String layerID;
	/**
	 * 오류 객체 ID
	 */
	String featureID;
	/**
	 * 관계 레이어 ID
	 */
	String refLayerId;
	/**
	 * 관계 객체 ID
	 */
	String refFeatureId;
	/**
	 * 오류 코드
	 */
	String errCode;
	/**
	 * 오류 타입(LayerFixMiss or GraphicMiss or AttributeMiss)
	 */
	String errType;
	/**
	 * 오류명
	 */
	String errName;
	/**
	 * 오류 세부 정보
	 */
	String comment;
	/**
	 * 오류 위치점
	 */
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
