/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className AttributeFigure.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 16. 오후 5:01:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFigure {

	String key;
	List<Object> values;
	Double number;
	String condition;
	Double interval;
}
