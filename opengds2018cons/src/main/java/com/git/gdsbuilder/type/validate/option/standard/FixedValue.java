/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.standard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className FixedValue.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오후 1:42:56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedValue {

	String name;
	String type;
	boolean isnull;
	Long length;
	List<Object> values;

}
