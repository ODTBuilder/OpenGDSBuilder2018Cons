package com.gitrnd.qaconsumer.preset.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gitrnd.qaconsumer.preset.domain.Preset;
import com.gitrnd.qaconsumer.preset.mapper.PresetMapper;

/**
 * tb_preset Repository 클래스.
 * 
 * @author DY.Oh
 *
 */
@Repository
public class PresetRepository {

	@Autowired
	private PresetMapper presetMapper;

	/**
	 * cat에 해당하는 {@link Preset}를 DB에서 조회.
	 * 
	 * @param cat Preset Cat(1~5)
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrieveBasePreset(int cat) {
		return presetMapper.retrieveBasePreset(cat);
	}

	/**
	 * pid에 해당하는 {@link Preset}를 DB에서 조회.
	 * 
	 * @param pid tb_preset pid
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrievePresetById(int pid) {
		return presetMapper.retrievePresetById(pid);
	}

	/**
	 * uidx에 해당하는 모든 {@link Preset}를 List 형태로 DB에서 조회.
	 * 
	 * @param uidx tb_user uidx
	 * @return List 형태의 {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public List<Preset> retrievePresetByUidx(int uidx) {
		return presetMapper.retrievePresetByUidx(uidx);
	}

	/**
	 * uidx에 해당하는 모든 {@link Preset}를 List 형태로 DB에서 조회.
	 * 
	 * @param uidx uidx tb_user uidx
	 * @return List 형태의 {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public List<Preset> retrievePresetNamesByUidx(int uidx) {
		return presetMapper.retrievePresetNamesByUidx(uidx);
	}

	/**
	 * DB에 {@link Preset}를 삽입.
	 * 
	 * @param preset 삽입하고자 하는 {@link Preset} 정보
	 * 
	 * @author IJ.S
	 */
	public void createPreset(Preset preset) {
		presetMapper.createPreset(preset);
	}

	/**
	 * DB에 저장되어있는 Preset 테이블 수정.
	 * 
	 * @param preset 수정하고자 하는 {@link Preset} 정보
	 * @return {@code true} : 수정 성공
	 *         <p>
	 *         {@code false} : 수정 실패
	 * 
	 * @author IJ.S
	 */
	public boolean updatePreset(Preset preset) {
		boolean flag = false;
		int num = presetMapper.updatePreset(preset);
		if (num > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * DB에 저장되어있는 Preset 테이블의 행 삭제.
	 * <p>
	 * 요청에 따라 다수의 테이블의 행 삭제 지원.
	 * 
	 * @param prList 수정하고자 하는 List 형태의 {@link Preset} 정보
	 * @return {@code true} : 수정 성공
	 *         <p>
	 *         {@code false} : 수정 실패
	 * 
	 * @author IJ.S
	 */
	public boolean deletePresets(ArrayList<Preset> prList) {
		boolean flag = false;
		int num = presetMapper.deletePresets(prList);
		if (num > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * id 및 uidx에 해당하는 모든 {@link Preset}를 DB에서 조회.
	 * 
	 * @param preset id 및 uidx 정보를 저장하고 있는 {@link Preset}
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrievePresetByIdAndUidx(Preset preset) {
		return presetMapper.retrievePresetByIdAndUidx(preset);
	}

}
