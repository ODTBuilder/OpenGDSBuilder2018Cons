/*
 *    OpenGDS/Builder
 *    http://git.co.kr
 *
 *    (C) 2014-2017, GeoSpatial Information Technology(GIT)
 *    
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package com.git.gdsbuilder.validator.basic;

import java.util.ArrayList;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.feature.DTFeature;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.validate.option.specific.AttributeFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionFigure;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.git.gdsbuilder.type.validate.option.standard.FixedValue;

public class BasicLayerValidator {

	DTLayer validatorLayer;
	String typeName;
	BasicFeatureAttributeValidator attrValidator;
	BasicFeatureGraphicValidator grapValidator;

	public BasicLayerValidator(DTLayer validatorLayer) {

		this.validatorLayer = validatorLayer;
		this.typeName = validatorLayer.getTypeName();
		this.attrValidator = new BasicFeatureAttributeValidator();
		this.grapValidator = new BasicFeatureGraphicValidator();

	}

	public BasicErrorLayer validateLayerFixMiss(String geometry, List<FixedValue> fixedValue) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, null);
			// geom
			BasicErrorFeature graphicErrFeature = grapValidator.validateLayerFixMiss(feature, geometry);
			if (graphicErrFeature != null) {
				graphicErrFeature.setLayerID(layerID);
				errorLayer.addErrorFeature(graphicErrFeature);
			} else {
				// attr
				if (fixedValue != null) {
					BasicErrorFeature attrErrFeature = attrValidator.validateLayerFixMiss(feature, fixedValue);
					if (attrErrFeature != null) {
						attrErrFeature.setLayerID(layerID);
						errorLayer.addErrorFeature(attrErrFeature);
					}
				}
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateUnDefinedLayer() {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, null);
			BasicErrorFeature attrErrFeature = attrValidator.validateUnDefinedLayer(feature);
			if (attrErrFeature != null) {
				attrErrFeature.setLayerID(layerID);
				errorLayer.addErrorFeature(attrErrFeature);
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateNumericalValues(OptionFilter filter, OptionFigure figure) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			List<BasicErrorFeature> errorFeatures = attrValidator.validateNumericalValues(feature, figure);
			if (errorFeatures != null) {
				for (BasicErrorFeature errorFeature : errorFeatures) {
					errorFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errorFeature);
				}
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateFixValues(OptionFilter filter, OptionFigure figure) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			List<BasicErrorFeature> errorFeatures = attrValidator.validateFixValues(feature, figure);
			if (errorFeatures != null) {
				for (BasicErrorFeature errorFeature : errorFeatures) {
					errorFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errorFeature);
				}
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateEntityTwisted(OptionFilter filter) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			List<BasicErrorFeature> errorFeatures = grapValidator.validateEntityTwisted(feature);
			if (errorFeatures != null) {
				for (BasicErrorFeature errorFeature : errorFeatures) {
					errorFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errorFeature);
				}
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateSmallArea(OptionFilter filter, OptionTolerance tolerance) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			BasicErrorFeature errorFeature = grapValidator.validateSmallArea(feature, tolerance);
			if (errorFeature != null) {
				errorFeature.setLayerID(layerID);
				errorLayer.addErrorFeature(errorFeature);
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateSmallLength(OptionFilter filter, OptionTolerance tolerance) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			BasicErrorFeature errorFeature = grapValidator.validateSmallLength(feature, tolerance);
			if (errorFeature != null) {
				errorFeature.setLayerID(layerID);
				errorLayer.addErrorFeature(errorFeature);
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateEntityDuplicated(OptionFilter filter) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		List<DTFeature> tmpsSimpleFeatures = new ArrayList<DTFeature>();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			tmpsSimpleFeatures.add(feature);
		}
		simpleFeatureIterator.close();
		int tmpSize = tmpsSimpleFeatures.size();
		for (int i = 0; i < tmpSize - 1; i++) {
			DTFeature tmpSimpleFeatureI = tmpsSimpleFeatures.get(i);
			for (int j = i + 1; j < tmpSize; j++) {
				DTFeature tmpSimpleFeatureJ = tmpsSimpleFeatures.get(j);
				BasicErrorFeature errFeature = grapValidator.validateEntityDuplicated(tmpSimpleFeatureI,
						tmpSimpleFeatureJ);
				if (errFeature != null) {
					errFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errFeature);
				} else {
					continue;
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validatePointDuplicated(OptionFilter filter) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();

		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
			List<BasicErrorFeature> errorFeatures = grapValidator.validatePointDuplicated(feature);
			if (errorFeatures != null) {
				for (BasicErrorFeature errorFeature : errorFeatures) {
					errorFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errorFeature);
				}
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateConIntersected(OptionFilter filter) {

		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}
		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();
		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		List<SimpleFeature> tmpSfs = new ArrayList<SimpleFeature>();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature sf = simpleFeatureIterator.next();
			tmpSfs.add(sf);
		}
		simpleFeatureIterator.close();
		int tmpsSimpleFeaturesSize = tmpSfs.size();
		for (int i = 0; i < tmpsSimpleFeaturesSize - 1; i++) {
			SimpleFeature sf = tmpSfs.get(i);
			DTFeature feature = new DTFeature(layerID, sf, attrConditions);
			for (int j = i + 1; j < tmpsSimpleFeaturesSize; j++) {
				SimpleFeature reSf = tmpSfs.get(j);
				DTFeature reFeature = new DTFeature(layerID, reSf, attrConditions);
				List<BasicErrorFeature> errFeatures = grapValidator.validateConIntersected(feature, reFeature);
				if (errFeatures != null) {
					for (BasicErrorFeature errFeature : errFeatures) {
						errFeature.setLayerID(layerID);
						errorLayer.addErrorFeature(errFeature);
					}
				} else {
					continue;
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateSelfEntity(OptionFilter filter, OptionTolerance tolerance, DTLayer reLayer) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		OptionFilter optionFilter = validatorLayer.getFilter();
		List<AttributeFilter> attrConditions = null;
		if (optionFilter != null) {
			attrConditions = optionFilter.getFilter();
		}
		String layerID = validatorLayer.getLayerID();
		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureCollection reSfc = reLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		List<DTFeature> tmpsSimpleFeatures = new ArrayList<DTFeature>();
		if (reLayer != null) {
			String relayerID = reLayer.getLayerID();
			OptionFilter reFilter = reLayer.getFilter();
			List<AttributeFilter> reAttrConditions = null;
			if (reFilter != null) {
				reAttrConditions = optionFilter.getFilter();
			}
			if (relayerID.equals(layerID)) {
				while (simpleFeatureIterator.hasNext()) {
					SimpleFeature simpleFeature = simpleFeatureIterator.next();
					DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
					tmpsSimpleFeatures.add(feature);
				}
				simpleFeatureIterator.close();
				int tmpSize = tmpsSimpleFeatures.size();
				for (int i = 0; i < tmpSize - 1; i++) {
					DTFeature feature = tmpsSimpleFeatures.get(i);
					// self
					for (int j = i + 1; j < tmpSize; j++) {
						DTFeature tmpSimpleFeatureJ = tmpsSimpleFeatures.get(j);
						List<BasicErrorFeature> errFeatures = grapValidator.validateSelfEntity(feature,
								tmpSimpleFeatureJ, tolerance);
						if (errFeatures != null) {
							for (BasicErrorFeature errFeature : errFeatures) {
								errFeature.setLayerID(layerID);
								errorLayer.addErrorFeature(errFeature);
							}
						}
					}
				}
			} else {
				String retypeName = reLayer.getTypeName();
				while (simpleFeatureIterator.hasNext()) {
					SimpleFeature simpleFeature = simpleFeatureIterator.next();
					DTFeature feature = new DTFeature(typeName, layerID, simpleFeature, attrConditions);
					SimpleFeatureIterator reSfcIter = reSfc.features();
					while (reSfcIter.hasNext()) {
						SimpleFeature reSf = reSfcIter.next();
						DTFeature reFeature = new DTFeature(retypeName, relayerID, reSf, reAttrConditions);
						List<BasicErrorFeature> errFeatures = grapValidator.validateSelfEntity(feature, reFeature,
								tolerance);
						if (errFeatures != null) {
							for (BasicErrorFeature errFeature : errFeatures) {
								errFeature.setLayerID(layerID);
								errorLayer.addErrorFeature(errFeature);
							}
						}
					}
					reSfcIter.close();
				}
				simpleFeatureIterator.close();
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateConOverDegree(OptionTolerance tolerance) {

		OptionFilter filter = validatorLayer.getFilter();
		List<AttributeFilter> attrConditions = null;

		if (filter != null) {
			attrConditions = filter.getFilter();
		}

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator simpleFeatureIterator = sfc.features();
		while (simpleFeatureIterator.hasNext()) {
			SimpleFeature simpleFeature = simpleFeatureIterator.next();
			DTFeature feature = new DTFeature(layerID, simpleFeature, attrConditions);
			List<BasicErrorFeature> errFeatures = grapValidator.validateConOverDegree(feature, tolerance);
			if (errFeatures != null) {
				for (BasicErrorFeature errFeature : errFeatures) {
					errFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errFeature);
				}
			} else {
				continue;
			}
		}
		simpleFeatureIterator.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	public BasicErrorLayer validateNodeMiss(OptionFilter filter, OptionTolerance tolerance, DTLayer reLayer) {

		BasicErrorLayer errorLayer = new BasicErrorLayer();
		String layerID = validatorLayer.getLayerID();
		List<AttributeFilter> attrConditions = null;
		if (filter != null) {
			attrConditions = filter.getFilter();
		}
		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		SimpleFeatureIterator sfIter = sfc.features();
		while (sfIter.hasNext()) {
			SimpleFeature sf = sfIter.next();
			DTFeature feature = new DTFeature(typeName, layerID, sf, attrConditions);
			List<BasicErrorFeature> errFeatures = grapValidator.validateNodeMiss(feature, sfc, reLayer, tolerance);
			if (errFeatures != null) {
				for (BasicErrorFeature errFeature : errFeatures) {
					errFeature.setLayerID(layerID);
					errorLayer.addErrorFeature(errFeature);
				}
			} else {
				continue;
			}
		}
		sfIter.close();
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerName(validatorLayer.getLayerID());
			return errorLayer;
		} else {

			return null;
		}
	}
}
