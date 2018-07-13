package com.gitrnd.qaconsumer.qareport.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.qareport.domain.QAReport;

@Mapper
public interface QAReportMapper {

	public Integer insertQAReport(QAReport report);

}
