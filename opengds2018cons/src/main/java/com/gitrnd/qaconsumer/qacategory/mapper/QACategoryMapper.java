package com.gitrnd.qaconsumer.qacategory.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.qacategory.domain.QACategory;

/**
 * QACategoryMapper.xml에 접근하는 클래스.
 * 
 * @author IJ.S
 *
 */
@Mapper
public interface QACategoryMapper {

	/**
	 * idx에 해당하는 {@link QACategory}를 DB에서 조회하여 반환.
	 * 
	 * @param idx idx
	 * @return DB에서 조회된 {@link QACategory}
	 * 
	 * @author IJ.S
	 */
	public QACategory retrieveQACategoryByIdx(int idx);

}
