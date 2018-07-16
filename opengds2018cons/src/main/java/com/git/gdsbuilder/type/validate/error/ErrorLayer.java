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

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ErrorLayer 정보를 담고 있는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 3. 11. 오후 2:57:39
 */
@Data
@AllArgsConstructor
public class ErrorLayer {

	String layerName;
	String collectionName;
	String collectionType;
	List<ErrorFeature> errFeatureList;

	int layerCount;
	int featureCount;
	int errCount;
	int nomalCount;
	int exceptCount;

	String comment;

	/**
	 * ErrorLayer 생성자
	 */
	public ErrorLayer() {
		super();
		this.layerName = "";
		this.collectionName = "";
		this.errFeatureList = new ArrayList<ErrorFeature>();
		this.collectionType = "";
	}

	public void addErrorFeature(ErrorFeature errFeature) {
		this.errFeatureList.add(errFeature);
	}

	public void addErrorFeatureCollection(List<ErrorFeature> errFeatures) {
		this.errFeatureList.addAll(errFeatures);
	}

	public void mergeErrorLayer(ErrorLayer errLayer) {
		this.errFeatureList.addAll(errLayer.getErrFeatureList());
	}

}
