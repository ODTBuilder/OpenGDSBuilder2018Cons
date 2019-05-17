package com.git.gdsbuilder.type.dt.layer;

import com.git.gdsbuilder.quadtree.Quadtree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link com.git.gdsbuilder.type.dt.layer.DTQuadLayer}정보를 저장하는 클래스.
 * <p>
 * 객체수가 많은 레이어 검수 시 {@link com.git.gdsbuilder.type.dt.layer.DTLayer}보다 메모리 관련
 * Exception이 발생할 확률이 적으며 검수 시간이 짧아 효율적임.
 * 
 * @author DY.Oh
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTQuadLayer extends DTLayer {

	/**
	 * 검수 레이어 1개에 대한 Quadtree 객체
	 */
	Quadtree quadtree;

}
