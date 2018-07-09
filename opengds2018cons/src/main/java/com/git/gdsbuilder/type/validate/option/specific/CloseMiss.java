/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className CloseMiss.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 22. 오전 9:53:10
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseMiss {

	String option;
	List<OptionFilter> filter;
	List<OptionRelation> retaion;
	List<OptionFigure> figure;
	List<OptionTolerance> tolerance;

}
