/**
 * 
 */
package com.git.gdsbuilder.type.validate.option;

import java.util.List;

import com.git.gdsbuilder.type.validate.option.specific.AttributeMiss;
import com.git.gdsbuilder.type.validate.option.specific.CloseMiss;
import com.git.gdsbuilder.type.validate.option.specific.GraphicMiss;
import com.git.gdsbuilder.type.validate.option.standard.LayerFixMiss;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className QaOptionList.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 14. 오전 11:21:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAOption {

	String name; // typeName
	List<LayerFixMiss> layerMissOptions;
	List<AttributeMiss> attributeMissOptions;
	List<GraphicMiss> graphicMissOptions;
	List<CloseMiss> closeMissOptions;

}
