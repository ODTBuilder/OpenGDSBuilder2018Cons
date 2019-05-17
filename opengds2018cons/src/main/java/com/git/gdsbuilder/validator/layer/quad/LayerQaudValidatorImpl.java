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

package com.git.gdsbuilder.validator.layer.quad;

import java.io.IOException;
import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.util.NullProgressListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureVisitor;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.quadtree.OptimalEnvelopsOp;
import com.git.gdsbuilder.quadtree.Quadtree;
import com.git.gdsbuilder.quadtree.Root;
import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.option.specific.OptionFilter;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;
import com.git.gdsbuilder.validator.layer.LayerValidatorImpl;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class LayerQaudValidatorImpl extends LayerValidatorImpl implements LayerQuadValidator {

	List<Object> envelops;
	Quadtree quadtree;

	public LayerQaudValidatorImpl(DTLayer validatorLayer, int maxFeatureCount) {
		super(validatorLayer);

		SimpleFeatureCollection sfc = validatorLayer.getSimpleFeatureCollection();
		Quadtree quadtree = new Quadtree();
		try {
			sfc.accepts(new FeatureVisitor() {
				@Override
				public void visit(Feature feature) {
					SimpleFeature simpleFeature = (SimpleFeature) feature;
					Geometry geom = (Geometry) simpleFeature.getDefaultGeometry();
					// Just in case: check for null or empty geometry
					if (geom != null) {
						Envelope env = geom.getEnvelopeInternal();
						if (!env.isNull()) {
							quadtree.insert(env, simpleFeature);
						}
					}
				}
			}, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.quadtree = quadtree;
		Root root = quadtree.getRoot();
		int maxLevel = root.maxLevel();
		OptimalEnvelopsOp op = new OptimalEnvelopsOp(quadtree, maxLevel, maxFeatureCount);
		this.envelops = op.getOptimalEnvelops(maxLevel);
	}

	@Override
	public ErrorLayer validateSelfEntity(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException, IOException {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateSelfEntity(reQuadLayer, tolerance);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			} else {
				ErrorLayer err = super.validateSelfEntity(tolerance);
				if (err != null) {
					errorLayer.mergeErrorLayer(err);
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}

	}

	@Override
	public ErrorLayer validateConIntersected() throws SchemaException {
		// TODO Auto-generated method stub
		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			ErrorLayer err = super.validateConIntersected();
			if (err != null) {
				errorLayer.mergeErrorLayer(err);
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateEntityDuplicated() throws SchemaException {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			ErrorLayer err = super.validateEntityDuplicated();
			if (err != null) {
				errorLayer.mergeErrorLayer(err);
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateNodeMiss(DTLayerList relationLayers, OptionTolerance tolerance) throws SchemaException {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateNodeMiss(reQuadLayer, tolerance);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateOneAcre(DTLayerList relationLayers, double tolerance) {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateOneAcre(reQuadLayer);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateOneStage(DTLayerList relationLayers) {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateOneStage(reQuadLayer);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateBoundaryMiss(DTLayerList relationLayers) {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateBoundaryMiss(reQuadLayer);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateCenterLineMiss(DTLayerList relationLayers) {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.validateCenterLineMiss(reQuadLayer);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer validateHoleMissplacement() {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			ErrorLayer err = super.validateHoleMissplacement();
			if (err != null) {
				errorLayer.mergeErrorLayer(err);
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	@Override
	public ErrorLayer valildateLinearDisconnection(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException {

		ErrorLayer errorLayer = new ErrorLayer();
		for (Object result : this.envelops) {
			Envelope envelope = (Envelope) result;
			List items = quadtree.query(envelope);
			DefaultFeatureCollection dfc = new DefaultFeatureCollection();
			int size = items.size();
			for (int i = 0; i < size; i++) {
				SimpleFeature f = (SimpleFeature) items.get(i);
				if (isContains(envelope, f)) {
					dfc.add(f);
				}
			}
			DTLayer layer = super.getValidatorLayer();
			layer.setSimpleFeatureCollection(dfc);
			super.setValidatorLayer(layer);
			if (relationLayers != null) {
				for (DTLayer relationLayer : relationLayers) {
					DTLayer reQuadLayer = getQuadRelationLayer(relationLayer, envelope);
					ErrorLayer err = super.valildateLinearDisconnection(reQuadLayer, tolerance);
					if (err != null) {
						errorLayer.mergeErrorLayer(err);
					}
				}
			}
		}
		if (errorLayer.getErrFeatureList().size() > 0) {
			errorLayer.setLayerID(super.getValidatorLayer().getLayerID());
			return errorLayer;
		} else {
			return null;
		}
	}

	private DTLayer getQuadRelationLayer(DTLayer relationLayer, Envelope envelope) {

		String reLayerId = relationLayer.getLayerID();
		String reLayerType = relationLayer.getLayerType();
		SimpleFeatureCollection reSfc = relationLayer.getSimpleFeatureCollection();
		OptionFilter reFilter = relationLayer.getFilter();
		Quadtree reQuadtree = new Quadtree();
		try {
			reSfc.accepts(new FeatureVisitor() {
				@Override
				public void visit(Feature feature) {
					SimpleFeature simpleFeature = (SimpleFeature) feature;
					Geometry geom = (Geometry) simpleFeature.getDefaultGeometry();
					// Just in case: check for null or empty geometry
					if (geom != null) {
						Envelope env = geom.getEnvelopeInternal();
						if (!env.isNull()) {
							reQuadtree.insert(env, simpleFeature);
						}
					}
				}
			}, new NullProgressListener());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List reItems = reQuadtree.query(envelope);
		DefaultFeatureCollection reDfc = new DefaultFeatureCollection();
		int reSize = reItems.size();
		if (reSize > 0) {
			for (int i = 0; i < reSize; i++) {
				SimpleFeature sf = (SimpleFeature) reItems.get(i);
				if (isContains(envelope, sf)) {
					reDfc.add(sf);
				}
			}
			DTLayer reDtLayer = new DTLayer();
			reDtLayer.setLayerID(reLayerId);
			reDtLayer.setLayerType(reLayerType);
			reDtLayer.setSimpleFeatureCollection(reDfc);
			reDtLayer.setFilter(reFilter);

			System.out.println(reDfc.size());

			return reDtLayer;
		} else {
			return null;
		}
	}

	private boolean isContains(Envelope envelope, SimpleFeature sf) {

		GeometryFactory f = new GeometryFactory();
		Geometry envelpoeGeom = f.toGeometry(envelope);
		Geometry sfGeom = (Geometry) sf.getDefaultGeometry();
		return envelpoeGeom.intersects(sfGeom);
	}

}
