package com.gitrnd.qaconsumer.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.domain.QAProgress;

@Mapper
public interface QAProgressMapper {

	public Integer insertQARequest(QAProgress progress);

	public void updateQAState(QAProgress progress);

	public void updateQAResponse(QAProgress progress);

	public List<HashMap<String, Object>> selectQAProgressList();

	/**
	 * @author DY.Oh
	 * @Date 2018. 3. 9. 오후 2:47:30
	 * @return String
	 * @decription
	 */
	public QAProgress selectQAStartTime();

	/**
	 * @author DY.Oh
	 * @Date 2018. 4. 11. 오후 3:32:19
	 * @param fid
	 * @return FileStatus
	 * @decription
	 */
	public QAProgress retrieveQAProgressById(int pIdx);

}
