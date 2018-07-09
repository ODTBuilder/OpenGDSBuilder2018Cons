/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className OptionCondition.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오후 1:40:46
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionFilter {

	String name;
	String code;
	List<AttributeFilter> filter;
}
