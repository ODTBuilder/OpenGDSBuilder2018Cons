package com.git.gdsbuilder.type.dt.layer;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.collection.MapSystemRule;
import com.git.gdsbuilder.type.validate.option.specific.OptionFigure;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className DTLayer.java
 * @description DTLayer 정보를 저장하는 클래스
 * @author DY.Oh
 * @date 2018. 1. 30. 오후 2:03:42
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTLayer {

	String typeName;
	String layerID;
	String layerType;
	SimpleFeatureCollection simpleFeatureCollection;
	OptionFilter filter;
	OptionFigure figure;
	MapSystemRule mapRule; // 인접도엽 정보

	public DTLayer(String layerID, String layerType, SimpleFeatureCollection simpleFeatureCollection,
			OptionFilter filter, MapSystemRule mapRule) {
		this.layerID = layerID;
		this.layerType = layerType;
		this.simpleFeatureCollection = simpleFeatureCollection;
		this.filter = filter;
		this.mapRule = mapRule;
	}

	/**
	 * @author DY.Oh
	 * @Date 2018. 1. 30. 오후 2:03:58
	 * @param feature
	 * @decription simpleFeatureCollection에 feature를 더함
	 */
	public void addFeature(SimpleFeature feature) {
		((DefaultFeatureCollection) this.simpleFeatureCollection).add(feature);
	}

}
