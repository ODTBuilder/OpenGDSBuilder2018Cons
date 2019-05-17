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

import java.util.ArrayList;

/**
 * DTLayerCollectionList 정보를 저장하는 클래스.
 * <p>
 * 다수의{@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}을 List 형태로
 * 저장
 * 
 * @author DY.Oh
 */
public class DTLayerCollectionList extends ArrayList<DTLayerCollection> {

	/**
	 * DTLayerCollectionList에 저장된
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList} 중
	 * collectionName에 해당하는
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}를 반환
	 * 
	 * @param collectionName 반환하고자 하는
	 *                       {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 *                       이름
	 * @return DTLayerCollection collectionName에 해당하는
	 *         {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 * @author DY.Oh
	 */
	public DTLayerCollection getLayerCollection(String collectionName) {

		DTLayerCollection layerCollection = null;
		for (DTLayerCollection collection : this) {
			if (collection.getCollectionName().equals(collectionName)) {
				layerCollection = collection;
				break;
			}
		}
		return layerCollection;
	}

	/**
	 * DTLayerCollectionList에 저장된
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList} 중 인접 도엽
	 * 정보인 {@link com.git.gdsbuilder.type.dt.collection.MapSystemRule}에 해당하는 상, 하,
	 * 좌, 우 각각의 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}를
	 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList}에 담아 반환
	 * 
	 * @param mapSystemRule
	 * @return DTLayerCollectionList
	 * 
	 * @author DY.Oh
	 */
	public DTLayerCollectionList getCloseLayerCollections(MapSystemRule mapSystemRule) {

		DTLayerCollectionList closeList = new DTLayerCollectionList();

		if (mapSystemRule == null) {
			return null;
		}

		Integer top = mapSystemRule.getTop();
		Integer bottom = mapSystemRule.getBottom();
		Integer right = mapSystemRule.getRight();
		Integer left = mapSystemRule.getLeft();

		for (DTLayerCollection collection : this) {
			if (top != null && collection.getCollectionName().equals(String.valueOf(top))) {
				closeList.add(collection);
			}
			if (bottom != null && collection.getCollectionName().equals(String.valueOf(bottom))) {
				closeList.add(collection);
			}
			if (right != null && collection.getCollectionName().equals(String.valueOf(right))) {
				closeList.add(collection);
			}
			if (left != null && collection.getCollectionName().equals(String.valueOf(left))) {
				closeList.add(collection);
			}
		}
		if (closeList.size() > 0) {
			return closeList;
		} else {
			return null;
		}
	}
}
