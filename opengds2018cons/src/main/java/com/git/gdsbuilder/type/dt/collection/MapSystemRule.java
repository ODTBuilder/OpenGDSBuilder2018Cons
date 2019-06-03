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

/**
 * 검수 대상 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의
 * 인접도엽(상, 하, 좌, 우)
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollectionList}의 ID를 담고
 * 있는 클래스
 * <p>
 * 인접도엽 ID는 검수 대상
 * {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의 ID가 숫자인 경우
 * 해당 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}의 ID를 기준으로
 * 기준으로 상(ID-10), 하(ID+10), 좌(ID-1), 우(ID+1) ID 값을 가짐.
 * 
 * @author DY.Oh
 * @since 2018. 1. 30. 오후 2:02:11
 */
public class MapSystemRule {

	private Integer bottom = 0;
	private Integer top = 0;
	private Integer left = 0;
	private Integer right = 0;

	public enum MapSystemRuleType {
		BOTTOM("BOTTOM"), LEFT("LEFT"), RIGHT("RIGHT"), TOP("TOP"), UNKNOWN(null);
		private String typeName;

		private MapSystemRuleType(String typeName) {
			this.typeName = typeName;
		}

		public static MapSystemRuleType get(String typeName) {
			for (MapSystemRuleType type : values()) {
				if (type == UNKNOWN)
					continue;
				if (type.typeName.equals(typeName))
					return type;
			}
			return UNKNOWN;
		}

		public String getTypeName() {
			return this.typeName;
		}
	};

	public Integer getBottom() {
		return bottom;
	}

	public void setBottom(Integer bottom) {
		this.bottom = bottom;
	}

	public Integer getTop() {
		return top;
	}

	public void setTop(Integer top) {
		this.top = top;
	}

	public Integer getLeft() {
		return left;
	}

	public void setLeft(Integer left) {
		this.left = left;
	}

	public Integer getRight() {
		return right;
	}

	public void setRight(Integer right) {
		this.right = right;
	}

	/**
	 * 검수 대상 {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 * collectionName에 해당하는 인접도엽(상, 하, 좌, 우)ID를 계산하여 left, right, top, bottom값을 설정
	 * 
	 * @param collectionName 검수 대상
	 *                       {@link com.git.gdsbuilder.type.dt.collection.DTLayerCollection}
	 *                       collectionName
	 * @return MapSystemRule
	 * 
	 * @author DY.Oh
	 */
	public MapSystemRule setMapSystemRule(String collectionName) {

		if (collectionName.matches("^[0-9]*$")) {
			Integer center = Integer.parseInt(collectionName);
			if (collectionName.endsWith("1")) {
				if (collectionName.endsWith("01")) {
					this.left = null;
					this.right = center + 1;
					this.top = null;
					this.bottom = center + 10;
				} else if (collectionName.endsWith("91")) {
					this.left = null;
					this.right = center + 1;
					this.top = center - 10;
					this.bottom = null;
				} else {
					this.left = null;
					this.right = center + 1;
					this.top = center - 10;
					this.bottom = center + 10;
				}
			} else if (collectionName.endsWith("0")) {
				if (collectionName.endsWith("10")) {
					this.left = center - 1;
					this.right = null;
					this.top = null;
					this.bottom = center + 10;
				} else if (collectionName.endsWith("100")) {
					this.left = center - 1;
					this.right = null;
					this.top = center - 10;
					this.bottom = null;
				} else {
					this.left = center - 1;
					this.right = null;
					this.top = center - 10;
					this.bottom = center + 10;
				}
			} else {
				this.left = center - 1;
				this.right = center + 1;
				this.top = center - 10;
				this.bottom = center + 10;

			}
			return this;
		} else {
			return null;
		}
	}
}
