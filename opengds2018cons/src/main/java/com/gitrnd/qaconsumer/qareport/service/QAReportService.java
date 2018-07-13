package com.gitrnd.qaconsumer.qareport.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitrnd.qaconsumer.qareport.domain.QAReport;

@Service
public class QAReportService {

	@Autowired
	private QAReportService reportService;

	public Integer insertQAReport(QAReport report) {
		return reportService.insertQAReport(report);
	}
}
