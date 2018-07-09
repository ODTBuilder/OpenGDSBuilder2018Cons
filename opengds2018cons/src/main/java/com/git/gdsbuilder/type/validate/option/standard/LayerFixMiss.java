/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.standard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className LayerFix.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오전 11:17:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LayerFixMiss {

	String option;
	String code;
	String geometry;
	List<FixedValue> fix;

}
