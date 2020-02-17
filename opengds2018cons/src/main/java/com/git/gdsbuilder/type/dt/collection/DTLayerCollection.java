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
package com.git.gdsbuilder.type.dt.collection;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}정보를 저장하는 클래스.
 * <p>
 * 다수의 레이어를 동시에 검수 할 때 검수 레이어 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}
 * 또는 {@link com.git.gdsbuilder.quadtree.Quadtree.DTQuadLayer}를 List 형태로 저장 가능
 * <p>
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의
 * collectionName이 숫자인 경우 인접 검수영역의 정보(상,하,좌,우)
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의
 * collectionName을 저장
 * 
 * @author DY.Oh
 */
public class DTLayerCollection {

	/**
	 * Collection 이름
	 */
	String collectionName;
	/**
	 * 검수영역 DTLayer
	 */
	DTLayer neatLine;
	/**
	 * 검수 대상 레이어 리스트
	 */
	DTLayerList layers;
	/**
	 * 인접 검수영역 정보
	 */
	MapSystemRule mapRule;

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public DTLayer getNeatLine() {
		return neatLine;
	}

	public void setNeatLine(DTLayer neatLine) {
		this.neatLine = neatLine;
	}

	public DTLayerList getLayers() {
		return layers;
	}

	public void setLayers(DTLayerList layers) {
		this.layers = layers;
	}

	public MapSystemRule getMapRule() {
		return mapRule;
	}

	public void setMapRule(MapSystemRule mapRule) {
		this.mapRule = mapRule;
	}

	/**
	 * DTLayerCollection에 저장된 {@link com.git.gdsbuilder.type.dt.layer.DTLayerList} 중
	 * layerName에 해당하는 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}를 반환
	 * 
	 * @param layerName 반환하고자 하는 {@link com.git.gdsbuilder.type.dt.layer.DTLayer} 이름
	 * @return DTLayer layerName에 해당하는
	 *         {@link com.git.gdsbuilder.type.dt.layer.DTLayer}
	 * @author DY.Oh
	 */
	public DTLayer getLayer(String layerName) {

		DTLayer layer = null;
		for (int i = 0; i < layers.size(); i++) {
			DTLayer tmp = layers.get(i);
			if (tmp != null) {
				String validateLayerName = tmp.getLayerID();
				if (validateLayerName.equalsIgnoreCase(layerName)) {
					layer = tmp;
					break;
				} else {
					continue;
				}
			}
		}
		return layer;
	}

}
