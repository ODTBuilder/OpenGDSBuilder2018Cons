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
 * 검수 옵션 정의 클래스
 * <p>
 * 특정 Name에 해당하는 {@link com.git.gdsbuilder.type.dt.layer.DTLayerList}가 수행할 검수
 * 옵션에 대한 정보를 저장
 * <p>
 * 레이어 Geometry 타입, 속성 컬럼 구조 검수 항목인
 * {@link com.git.gdsbuilder.type.validate.option.LayerFixMiss}, 레이어 내 객체의 속성 값
 * 검수 항목인 {@link com.git.gdsbuilder.type.validate.option.AttributeMiss}, 레이어 내
 * 객체의 Geometry 검수 항목인
 * {@link com.git.gdsbuilder.type.validate.option.GraphicMiss}, 인접 검수 영역의 레이어 간
 * 검수 항목인 {@link com.git.gdsbuilder.type.validate.option.CloseMiss} 각각을 List 형태로
 * 저장
 * 
 * @author DY.Oh
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAOption {

	/**
	 * {@link com.git.gdsbuilder.type.validate.option.QAOption} Name
	 * <p>
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayerList}의 분류 명
	 */
	String name;
	/**
	 * 레이어 Geometry 타입, 속성 컬럼 구조 검수 항목 List
	 */
	List<LayerFixMiss> layerMissOptions;
	/**
	 * 레이어 내 객체의 속성 값 검수 항목 List
	 */
	List<AttributeMiss> attributeMissOptions;
	/**
	 * 레이어 내 객체의 Geometry 검수 항목 List
	 */
	List<GraphicMiss> graphicMissOptions;
	/**
	 * 인접 검수 영역의 레이어 간 검수 항목 List
	 */
	List<CloseMiss> closeMissOptions;

	public LayerFixMiss getLayerMissOption(String layerName) {

		if (layerMissOptions != null) {
			for (LayerFixMiss layfm : layerMissOptions) {
				if (layfm.getCode().equals(layerName)) {
					return layfm;
				}
			}
		}
		return null;
	}

}
