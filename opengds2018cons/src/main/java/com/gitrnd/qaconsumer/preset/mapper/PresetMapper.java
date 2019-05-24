package com.gitrnd.qaconsumer.preset.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gitrnd.qaconsumer.preset.domain.Preset;

/**
 * PresetMapper.xml 접근 클래스.
 * 
 * @author DY.Oh
 *
 */
@Mapper
public interface PresetMapper {

	/**
	 * cat에 해당하는 {@link Preset}를 DB에서 조회.
	 * 
	 * @param cat Preset Cat(1~5)
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrieveBasePreset(int cat);

	/**
	 * pid에 해당하는 {@link Preset}를 DB에서 조회.
	 * 
	 * @param pid tb_preset pid
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrievePresetById(int pid);

	/**
	 * DB에 저장되어있는 Preset 테이블 수정.
	 * 
	 * @param preset 수정하고자 하는 {@link Preset} 정보
	 * @return 수정 성공 수
	 * 
	 * @author IJ.S
	 */
	public int updatePreset(Preset preset);

	/**
	 * DB에 저장되어있는 Preset 테이블의 행 삭제.
	 * <p>
	 * 요청에 따라 다수의 테이블의 행 삭제 지원.
	 * 
	 * @param prList 수정하고자 하는 List 형태의 {@link Preset} 정보
	 * @return 삭제 성공 수
	 * 
	 * @author IJ.S
	 */
	public int deletePresets(ArrayList<Preset> prList);

	/**
	 * uidx에 해당하는 모든 {@link Preset}를 List 형태로 DB에서 조회.
	 * 
	 * @param uidx tb_user uidx
	 * @return List 형태의 {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public List<Preset> retrievePresetByUidx(int uidx);

	/**
	 * uidx에 해당하는 모든 {@link Preset}를 List 형태로 DB에서 조회.
	 * 
	 * @param uidx tb_user uidx
	 * @return List 형태의 {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public List<Preset> retrievePresetNamesByUidx(int uidx);

	/**
	 * DB에 {@link Preset}를 삽입.
	 * 
	 * @param preset 삽입하고자 하는 {@link Preset} 정보
	 * 
	 * @author IJ.S
	 */
	public void createPreset(Preset preset);

	/**
	 * id 및 uidx에 해당하는 모든 {@link Preset}를 DB에서 조회.
	 * 
	 * @param preset id 및 uidx 정보를 저장하고 있는 {@link Preset}
	 * @return {@link Preset}
	 * 
	 * @author IJ.S
	 */
	public Preset retrievePresetByIdAndUidx(Preset preset);
}
