package com.gitrnd.qaconsumer.qareport.details.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.qareport.details.domain.QADetailReport;

@Mapper
public interface QADetailReportMapper {

	/**
	 * @param detail
	 * @return
	 */
	public Integer insertQADetailReport(QADetailReport detail);

}
