/*
 *    OpenGDS/Builder
 *    http://git.co.kr
 *
 *    (C) 2014-2017, GeoSpatial Information Technology(GIT)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.git.gdsbuilder.type.validate.layer;

import java.util.List;

import com.git.gdsbuilder.type.dt.collection.DTLayerCollection;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.option.QAOption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ValidateLayerType 정보를 담고 있는 클래스
 * 
 * @author DY.Oh
 * @Date 2017. 3. 11. 오후 3:02:56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QALayerType {

	String name;
	List<String> layerIDList;
	QAOption option;

	/**
	 * @author DY.Oh
	 * @Date 2018. 3. 30. 오후 2:22:05
	 * @param string
	 * @param colleciton
	 * @return DTLayer
	 * @decription
	 */
	public DTLayer getTypeLayer(String layerID, DTLayerCollection colleciton) {

		DTLayer dtLayer = null;
		for (int i = 0; i < this.layerIDList.size(); i++) {
			String id = this.layerIDList.get(i);
			if (id.equals(layerID)) {
				dtLayer = colleciton.getLayer(id);
			}
		}
		if (dtLayer != null) {
			return dtLayer;
		} else {
			return null;
		}
	}
}
