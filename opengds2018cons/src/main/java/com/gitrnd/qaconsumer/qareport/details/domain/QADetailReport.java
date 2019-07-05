/**
 * 
 */
package com.gitrnd.qaconsumer.qareport.details.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 세부 검수 결과 Report 정보.
 * 
 * @author DY.Oh
 *
 */
@Data
@AllArgsConstructor
public class QADetailReport {

	Integer rd_idx;
	String refLayerId;
	String featureId;
	String refFeatureId;
	String errType;
	String errName;
	String errPoint;
	String comment;
	Integer rIdx;

	public QADetailReport(String refLayerId, String featureId, String reffeatureId, String errType, String errName,
			String errPoint, String comment, Integer rIdx) {
		super();
		this.refLayerId = refLayerId;
		this.featureId = featureId;
		this.refFeatureId = reffeatureId;
		this.errType = errType;
		this.errName = errName;
		this.errPoint = errPoint;
		this.comment = comment;
		this.rIdx = rIdx;
	}

}
