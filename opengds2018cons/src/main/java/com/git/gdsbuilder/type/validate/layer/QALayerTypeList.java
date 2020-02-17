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
package com.git.gdsbuilder.type.validate.layer;

import java.util.ArrayList;
import java.util.List;

import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;

/**
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의 검수 옵션
 * <p>
 * 다수의{@link com.git.gdsbuilder.type.validate.layer.QALayerType}을
 * {@link java.util.ArrayList} 형태로 저장
 * 
 * @author DY.Oh
 */
public class QALayerTypeList extends ArrayList<QALayerType> {

	/**
	 * 검수 종류 수치지도 1.0 : 1, 수치지도 2.0 : 2, 지하시설물 1.0 : 3, 지하시설물 2.0 : 4, 임상도 : 5
	 */
	int category;
	/**
	 * QALayerTypeList에 해당하는 LayerID List
	 */
	List<String> layerIDList = new ArrayList<String>();

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public List<String> getLayerIDList() {
		return layerIDList;
	}

	public void setLayerIDList(List<String> layerIDList) {
		this.layerIDList = layerIDList;
	}

	public void addAllLayerIdList(List<String> list) {
		this.layerIDList.addAll(list);
	}

	public void addLayerId(String layerID) {
		this.layerIDList.add(layerID);
	}

	/**
	 * 특정 Name에 해당하는 {@link com.git.gdsbuilder.type.validate.layer.QALayerType}의
	 * LayerID List와 동일한 ID를 가진 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}를
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}에서 모두 검색하여
	 * {@link com.git.gdsbuilder.type.dt.layer.DTLayerList} 형태로 반환
	 * 
	 * @param typeName        {@link com.git.gdsbuilder.type.validate.layer.QALayerType}
	 *                        Name
	 * @param layerCollection LayerID를 검색할
	 *                        {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 * @return DTLayerList
	 *         {@link com.git.gdsbuilder.type.validate.layer.QALayerType}의 LayerID
	 *         List와 동일한 ID를 가진 {@link com.git.gdsbuilder.type.dt.layer.DTLayerList}
	 * 
	 * @author DY.Oh
	 */
	public DTLayerList getTypeLayers(String typeName, DTLayerCollection layerCollection) {
		DTLayerList layers = new DTLayerList();
		for (int j = 0; j < this.size(); j++) {
			QALayerType type = this.get(j);
			if (type.getName().equals(typeName)) {
				List<String> names = type.getLayerIDList();
				for (int i = 0; i < names.size(); i++) {
					String name = names.get(i);
					DTLayer geoLayer = layerCollection.getLayer(name);
					if (geoLayer != null) {
						geoLayer.setTypeName(typeName);
						layers.add(geoLayer);
					} else {
						continue;
					}
				}
			}
		}
		return layers;
	}

	/**
	 * 특정 Name에 해당하는 {@link com.git.gdsbuilder.type.validate.layer.QALayerType}의
	 * LayerID와 동일한 ID를 가진 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}를
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}에서 검색하여 반환
	 * 
	 * @param typeName        {@link com.git.gdsbuilder.type.validate.layer.QALayerType}
	 *                        Name
	 * @param layerID         {@link com.git.gdsbuilder.type.dt.layer.DTLayer} ID
	 * @param layerCollection LayerID를 검색할
	 *                        {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 * @return DTLayer {@link com.git.gdsbuilder.type.validate.layer.QALayerType}의
	 *         LayerID List와 동일한 ID List를 가진
	 *         {@link com.git.gdsbuilder.type.dt.layer.DTLayerList}
	 * @author DY.Oh
	 */
	public DTLayer getTypeLayer(String typeName, String layerID, DTLayerCollection layerCollection) {

		DTLayer layer = null;
		for (int j = 0; j < this.size(); j++) {
			QALayerType type = this.get(j);
			if (type.getName().equals(typeName)) {
				List<String> names = type.getLayerIDList();
				for (int i = 0; i < names.size(); i++) {
					String name = names.get(i);
					if (name.equals(layerID)) {
						layer = layerCollection.getLayer(name);
					}
				}
			}
		}
		return layer;
	}

}
