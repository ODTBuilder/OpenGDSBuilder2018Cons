/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className OptionFilgure.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 16. 오후 3:38:51
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionFigure {

	String name;
	String code;
	List<AttributeFigure> figure;

	public List<AttributeFigure> getFilterFigure(int fidx) {

		List<AttributeFigure> filterFigures = new ArrayList<>();
		for (AttributeFigure attrFigure : figure) {
			Long fidxL = attrFigure.getFIdx();
			if (fidxL != null) {
				if (fidx == fidxL.intValue()) {
					filterFigures.add(attrFigure);
				}
			}
		}
		if (filterFigures.size() > 0) {
			return filterFigures;
		} else {
			return null;
		}
	}

	public List<AttributeFigure> getFilterFigure() {

		List<AttributeFigure> filterFigures = new ArrayList<>();
		for (AttributeFigure attrFigure : figure) {
			Long fidxL = attrFigure.getFIdx();
			if (fidxL == null) {
				filterFigures.add(attrFigure);
			}
		}
		if (filterFigures.size() > 0) {
			return filterFigures;
		} else {
			return null;
		}
	}

}
