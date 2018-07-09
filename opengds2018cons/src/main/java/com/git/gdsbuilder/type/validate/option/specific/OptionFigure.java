/**
 * 
 */
package com.git.gdsbuilder.type.validate.option.specific;

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

}
