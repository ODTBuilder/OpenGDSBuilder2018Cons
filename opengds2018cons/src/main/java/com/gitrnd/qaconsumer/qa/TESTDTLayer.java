package com.gitrnd.qaconsumer.qa;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.quadtree.Quadtree;
import com.git.gdsbuilder.type.dt.collection.MapSystemRule;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TESTDTLayer {

	String layerID;
	String layerType;
	SimpleFeatureCollection simpleFeatureCollection;
	OptionFilter filter;
	MapSystemRule mapRule; // 인접도엽 정보
	Quadtree quadtree;

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
