/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className GraphicMiss.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 19. 오전 10:34:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphicMiss {

	String option;
	List<OptionFilter> filter;
	List<OptionRelation> retaion;
	List<OptionTolerance> tolerance;

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

	public OptionTolerance getLayerTolerance(String layerID) {

		if (tolerance != null) {
			for (OptionTolerance layerTolerance : tolerance) {
				String code = layerTolerance.getCode();
				if (code == null || layerID.equals(code)) {
					return layerTolerance;
				}
			}
		}
		return null;
	}

}
