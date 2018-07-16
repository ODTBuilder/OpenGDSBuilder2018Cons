/**
 * 
 */
package com.gitrnd.qaconsumer.qareport.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className FileValidateProgress.java
 * @description
 * @author DY.Oh
 * @date 2018. 2. 7. 오전 10:55:35
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QAReport {

	Integer r_idx;
	String layerId;
	Integer layerCount;
	Integer featureCount;
	Integer normalCount;
	Integer errCount;
	Integer exceptCount;
	String comment;
	Integer pIdx;

	public QAReport(String layerId, int layerCount, int featureCount, int normalCount, int errCount, int exceptCount,
			String comment, int pIdx) {

		this.layerId = layerId;
		this.layerCount = new Integer(layerCount);
		this.featureCount = new Integer(featureCount);
		this.normalCount = new Integer(normalCount);
		this.errCount = new Integer(errCount);
		this.exceptCount = new Integer(exceptCount);
		this.pIdx = new Integer(pIdx);
	}
}
