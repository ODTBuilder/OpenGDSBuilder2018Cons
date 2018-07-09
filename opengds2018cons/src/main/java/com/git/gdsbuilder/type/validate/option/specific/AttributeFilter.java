/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className AttributeCondition.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오후 1:10:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeFilter {

	// 해당값들만 검수
	String key;
	List<Object> values;

}
