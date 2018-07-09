/**
 * 
 */
package com.git.gdsbuilder.type.dt.feature;

import java.util.List;

import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.validate.option.specific.AttributeFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className DTFeature.java
 * @description
 * @author DY.Oh
 * @date 2018. 3. 15. 오후 6:25:07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTFeature {

	String layerID;
	SimpleFeature simefeature;
	List<AttributeFilter> filter;

}
