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
import com.git.gdsbuilder.type.dt.layer.DTQuadLayer;
import com.git.gdsbuilder.type.dt.layer.DTQuadLayerList;

/**
 * DTLayerCollection 정보를 저장하는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 3. 11. 오전 11:45:40
 */
public class DTLayerCollection {

	String collectionName; // 도엽번호
	DTLayer neatLine; // 도곽
	DTLayerList layers; // 레이어
	DTQuadLayerList quadlyers; // 레이어
	MapSystemRule mapRule; // 인접도엽 정보

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

	public DTQuadLayerList getQuadlyers() {
		return quadlyers;
	}

	public void setQuadlyers(DTQuadLayerList quadlyers) {
		this.quadlyers = quadlyers;
	}

	/**
	 * @author DY.Oh
	 * @Date 2018. 1. 30. 오후 1:59:49
	 * @param layerName
	 * @return DTLayer
	 * @decription DTLayerCollection에서 layerName에 해당하는 DTLayer 객체를 반환
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

	public DTQuadLayer getQuadLayer(String layerName) {

		DTQuadLayer layer = null;
		for (int i = 0; i < quadlyers.size(); i++) {
			DTQuadLayer tmp = quadlyers.get(i);
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
