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
package com.git.gdsbuilder.validator.feature.filter;

import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.option.specific.AttributeFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;

/**
 * {@link SimpleFeature}의 Attribute 중 {@link AttributeFilter}에 해당하는 속성이 존재하는지
 * 확인하는 클래스.
 * <p>
 * {@link AttributeFilter}가 {@link List} 형태인 경우 OR 조건으로 {@link SimpleFeature}의
 * Attribute가 최소 1개 이상의 {@link AttributeFilter} 조건과 일치하면 {@code true}를 반환함.
 * 
 * @author DY.Oh
 *
 */
public class FeatureFilter {

	/**
	 * {@link SimpleFeature}의 Attribute 중 {@link AttributeFilter}에 해당하는 속성이 존재하는지 확인
	 * 
	 * @param sf      속성 필터 대상 {@link SimpleFeature}
	 * @param filters 속성 필터 OR 조건
	 * @return boolean 대상 {@link SimpleFeature}의 Attribute가 최소 1개 이상의
	 *         {@link AttributeFilter} 조건과 일치하면 {@code true}를 반환
	 * 
	 * @author DY.Oh
	 */
	public static boolean filter(SimpleFeature sf, List<AttributeFilter> filters) {

		boolean isTrue = false;
		if (filters == null) {
			isTrue = true;
		} else {
			for (AttributeFilter filter : filters) {
				String key = filter.getKey();
				if (key == null) {
					continue;
				}
				// filter
				List<Object> values = filter.getValues();
				if (values != null) {
					Object attribute = sf.getAttribute(key);
					for (Object value : values) {
						if (attribute.toString().equals(value)) {
							isTrue = true;
						}
					}
				} else {
					isTrue = true;
				}
			}
		}
		return isTrue;
	}

	/**
	 * {@link SimpleFeature}의 Attribute 중 {@link AttributeFilter}에 해당하는 속성이 존재하는지 확인
	 * 
	 * @param sf     속성 필터 대상 {@link SimpleFeature}
	 * @param filter 속성 필터 조건
	 * @return boolean 대상 {@link SimpleFeature}의 Attribute가 {@link AttributeFilter}
	 *         조건과 일치하면 {@code true}를 반환
	 * 
	 * @author DY.Oh
	 */
	public static boolean filter(SimpleFeature sf, AttributeFilter filter) {

		boolean isTrue = false;
		if (filter == null) {
			isTrue = true;
		} else {
			String key = filter.getKey();
			if (key != null) {
				// filter
				List<Object> values = filter.getValues();
				if (values != null) {
					Object attribute = sf.getAttribute(key);
					for (Object value : values) {
						if (attribute.toString().equals(value)) {
							isTrue = true;
						}
					}
				} else {
					isTrue = true;
				}
			}
		}
		return isTrue;
	}

	/**
	 * {@link DTLayerList}의 레이어 객체 중 각 레이어의 {@link AttributeFilter} 조건을 만족한 객체만
	 * {@link DefaultFeatureCollection}에 추가하여 반환함.
	 * 
	 * @param relationLayers {@link AttributeFilter} 조건 검사 대상 {@link DTLayerList}
	 * @param reTolerances   {@link AttributeFilter} 조건 검사 대상 여부를 확인. reTolerances에
	 *                       저장되어 있는 {@link DTLayer}만 조건 검사를 수행함.
	 * @return DefaultFeatureCollection {@link AttributeFilter} 조건을 만족한 객체
	 *         Collection
	 * 
	 * @author DY.Oh
	 */
	public static DefaultFeatureCollection filter(DTLayerList relationLayers, List<OptionTolerance> reTolerances) {

		SimpleFeatureCollection tempSfc;
		SimpleFeatureIterator tempIterator;
		DefaultFeatureCollection relationSfc = new DefaultFeatureCollection();

		for (int i = 0; i < relationLayers.size(); i++) {
			DTLayer relationLayer = relationLayers.get(i);
			String reLayerCode = relationLayer.getLayerID();
			// tolerance
			if (reTolerances != null) {
				for (OptionTolerance reTolerance : reTolerances) {
					if (!reLayerCode.equals(reTolerance.getLayerID())) {
						continue;
					}
					// filter
					OptionFilter filters = relationLayer.getFilter();
					if (filters != null) {
						String code = filters.getLayerID();
						if (!reLayerCode.equals(code)) {
							continue;
						} else {
							List<AttributeFilter> attrFilters = filters.getFilter();
							tempSfc = relationLayer.getSimpleFeatureCollection();
							tempIterator = tempSfc.features();
							while (tempIterator.hasNext()) {
								SimpleFeature relationSf = tempIterator.next();
								if (attrFilters != null) {
									// filter
									if (FeatureFilter.filter(relationSf, attrFilters)) {
										relationSfc.add(relationSf);
									}
								}
							}
						}
					} else {
						tempSfc = relationLayer.getSimpleFeatureCollection();
						tempIterator = tempSfc.features();
						while (tempIterator.hasNext()) {
							SimpleFeature relationSf = tempIterator.next();
							relationSfc.add(relationSf);
						}
					}
				}
			} else {
				tempSfc = relationLayer.getSimpleFeatureCollection();
				tempIterator = tempSfc.features();
				while (tempIterator.hasNext()) {
					SimpleFeature relationSf = tempIterator.next();
					relationSfc.add(relationSf);
				}
			}
		}
		return relationSfc;
	}
}
