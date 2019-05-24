/**
 * 
 */
package com.gitrnd.qaconsumer.qacategory.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.qacategory.domain.QACategory;
import com.gitrnd.qaconsumer.qacategory.mapper.QACategoryMapper;

/**
 * QACategoryRepository.
 * 
 * @author IJ.S
 *
 */
@Repository
public class QACategoryRepository {

	@Autowired
	private QACategoryMapper qaCategoryMapper;

	/**
	 * idx에 해당하는 {@link QACategory}를 DB에서 조회하여 반환.
	 * 
	 * @param idx idx
	 * @return DB에서 조회된 {@link QACategory}
	 * 
	 * @author IJ.S
	 */
	public QACategory retrieveQACategoryByIdx(int idx) {
		return qaCategoryMapper.retrieveQACategoryByIdx(idx);
	}
}
