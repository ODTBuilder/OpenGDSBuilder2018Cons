/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className OptionTolerance.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오후 1:41:08
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionTolerance {

	String name;
	String code;
	Double value;
	String condition;
	Double interval;

}
