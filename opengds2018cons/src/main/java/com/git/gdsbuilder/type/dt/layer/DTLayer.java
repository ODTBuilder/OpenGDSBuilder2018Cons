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
package com.git.gdsbuilder.type.dt.layer;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.validate.option.OptionFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link com.git.gdsbuilder.type.dt.layer.DTLayer}정보를 저장하는 클래스.
 * <p>
 * 1개의 레이어를 검수 할 때 사용되며 검수 레이어의 정보(레이어 type 및 ID), 레이어 내의 공간 객체들의 Collection인
 * {@link org.geotools.data.simple.SimpleFeatureCollection}, 속성 컬럼 및 값을 지정하여
 * 해당하는 객체를 필터링하는 {@link com.git.gdsbuilder.type.validate.option.OptionFilter}를
 * 변수로 가짐.
 * 
 * @author DY.Oh
 */
@Data
public class DTLayer {
	/**
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayer}을 포함하는
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}명
	 */
	String typeName;
	/**
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayer}명, 검수 레이어명
	 */
	String layerID;
	/**
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayer} Geometry 타입 (ex Point,
	 * LineString, Polygon 등)
	 */
	String layerType;
	/**
	 * {@link org.geotools.data.simple.SimpleFeatureCollection}
	 */
	SimpleFeatureCollection simpleFeatureCollection;
	/**
	 * 객체를 필터링하기 위한 속성 컬럼 및 값을 저장
	 */
	OptionFilter filter;

	public DTLayer(String layerID, String layerType, SimpleFeatureCollection simpleFeatureCollection,
			OptionFilter filter) {
		this.layerID = layerID;
		this.layerType = layerType;
		this.simpleFeatureCollection = simpleFeatureCollection;
		this.filter = filter;
	}

	/**
	 * 해당 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}의
	 * {@link org.geotools.data.simple.SimpleFeatureCollection}에 feature를 추가함.
	 * <p>
	 * 추가하려는 feature는 {@link org.geotools.data.simple.SimpleFeatureCollection}와 동일한
	 * 구조를 가져야함.
	 * 
	 * @param feature 추가하려는 {@link org.geotools.data.simple.SimpleFeature}
	 * @author DY.Oh
	 */
	public void addFeature(SimpleFeature feature) {
		((DefaultFeatureCollection) this.simpleFeatureCollection).add(feature);
	}

	public DTLayer(String typeName, String layerID, String layerType, SimpleFeatureCollection simpleFeatureCollection,
			OptionFilter filter) {
		super();
		this.typeName = typeName;
		this.layerID = layerID;
		this.layerType = layerType;
		this.simpleFeatureCollection = simpleFeatureCollection;
		this.filter = filter;
	}

	public DTLayer() {
		super();
	}

}
