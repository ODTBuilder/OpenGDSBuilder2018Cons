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
import lombok.NoArgsConstructor;

/**
 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer} 정보를 담고 있는 클래스
 * <p>
 * 다수의 {@link com.git.gdsbuilder.type.validate.error.ErrorFeature}을
 * {@link java.util.List} 형태로 저장
 * <p>
 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer}는 단일
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection} 또는
 * {@link com.git.gdsbuilder.type.dt.layer.DTLayer} 검수 결과로 생성됨.
 * 
 * @author DY.Oh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorLayer {

	/**
	 * 오류 객체가 포함된 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}의 ID
	 */
	String layerID;
	/**
	 * 오류 객체가 포함된 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의
	 * ID
	 */
	String collectionName;
	/**
	 * 오류 객체가 포함된 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의
	 * Type
	 */
	String collectionType;
	/**
	 * 오류 객체 리스트
	 */
	List<ErrorFeature> errFeatureList = new ArrayList<>();
	/**
	 * 검수를 수행한 모든 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}의 수
	 */
	int layerCount;
	/**
	 * 검수를 수행한 모든 {@link com.git.gdsbuilder.type.dt.feature.DTFeature}의 수
	 */
	int featureCount;
	/**
	 * 검수를 수행한 {@link com.git.gdsbuilder.type.dt.feature.DTFeature} 중 오류 객체의 수
	 */
	int errCount;
	/**
	 * 검수를 수행한 {@link com.git.gdsbuilder.type.dt.feature.DTFeature} 중 오류가 아닌 객체의 수
	 */
	int nomalCount;
	/**
	 * 검수를 수행한 {@link com.git.gdsbuilder.type.dt.feature.DTFeature} 중 예외 객체의 수
	 */
	int exceptCount;
	/**
	 * 오류 세부 사항
	 */
	String comment;

	/**
	 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer}에 오류 객체에 대한 검수 결과인
	 * {@link com.git.gdsbuilder.type.validate.error.ErrorFeature} 추가
	 * 
	 * @param errFeature 오류 객체에 대한 검수 결과
	 *                   {@link com.git.gdsbuilder.type.validate.error.ErrorFeature}
	 * 
	 * @author DY.Oh
	 */
	public void addErrorFeature(ErrorFeature errFeature) {
		this.errFeatureList.add(errFeature);
	}

	/**
	 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer}에 다수 오류 객체에 대한 검수
	 * 결과인 {@link com.git.gdsbuilder.type.validate.error.ErrorFeature} List 추가
	 * 
	 * @param errFeatures 다수 오류 객체에 대한 검수 결과인
	 *                    {@link com.git.gdsbuilder.type.validate.error.ErrorFeature}
	 *                    List
	 * 
	 * @author DY.Oh
	 */
	public void addErrorFeatureList(List<ErrorFeature> errFeatures) {
		this.errFeatureList.addAll(errFeatures);
	}

	/**
	 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer}에 또 다른
	 * {@link com.git.gdsbuilder.type.validate.error.ErrorLayer}를 병합.
	 * 
	 * @param errLayer 단일
	 *                 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 *                 또는 {@link com.git.gdsbuilder.type.dt.layer.DTLayer} 검수 결과
	 * 
	 * @author DY.Oh
	 */
	public void mergeErrorLayer(ErrorLayer errLayer) {
		this.errFeatureList.addAll(errLayer.getErrFeatureList());
	}

}
