package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className GeometryMiss.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오전 11:17:42
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeMiss {

	String option;
	List<OptionFilter> filter;
	List<OptionRelation> retaion;
	List<OptionFigure> figure;

	public OptionFilter getLayerFilter(String layerID) {

		if (filter != null) {
			for (OptionFilter layerFilter : filter) {
				String code = layerFilter.getCode();
				if (layerID.equals(code)) {
					return layerFilter;
				}
			}
		}
		return null;
	}

	public OptionFigure getLayerFigure(String layerID) {

		if (figure != null) {
			for (OptionFigure layerFigure : figure) {
				String code = layerFigure.getCode();
				if (layerID.equals(code)) {
					return layerFigure;
				}
			}
		}
		return null;
	}

}
