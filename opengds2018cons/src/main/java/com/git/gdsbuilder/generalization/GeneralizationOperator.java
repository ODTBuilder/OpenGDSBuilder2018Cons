package com.git.gdsbuilder.generalization;

import java.util.List;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

import com.git.gdsbuilder.type.dt.layer.DTLayer;
import com.vividsolutions.jts.geom.Geometry;

public class GeneralizationOperator {

	DTLayer dtLayer;
	List<GeneralizationOption> genalOpts;

	int beforeFetureNum;
	int beforePointNum;

	int afterFetureNum;
	int afterPointNum;

	public GeneralizationOperator(DTLayer dtLayer, List<GeneralizationOption> genalOpts) {
		this.dtLayer = dtLayer;
		this.genalOpts = genalOpts;
	}

	public SimpleFeatureCollection generalizate() {

		SimpleFeatureCollection inputSfc = dtLayer.getSimpleFeatureCollection();
		this.beforeFetureNum = inputSfc.size();
		this.beforePointNum = getNumPoints(inputSfc);

		SimpleFeatureCollection sfc = new DefaultFeatureCollection();
		sfc = inputSfc;

		for (GeneralizationOption genalOpt : genalOpts) {
			String name = genalOpt.getName();
			double tolerance = genalOpt.getTolerance();
			if (name.equals(GeneralizationOption.SIMPLIFICATION)) {
				DTSimplifier simplifier = new DTSimplifier();
				sfc = simplifier.simplify(sfc, tolerance, genalOpt.getRepeat(), genalOpt.isMerge());
			}
			if (name.equals(GeneralizationOption.ELIMINATION)) {
				DTEliminater eliminater = new DTEliminater();
				sfc = eliminater.eliminate(sfc, tolerance);
			}
		}
		this.afterFetureNum = sfc.size();
		this.afterPointNum = getNumPoints(sfc);

		return sfc;
	}

	public int getNumPoints(SimpleFeatureCollection sfc) {

		int ptNum = 0;
		SimpleFeatureIterator sfIter = sfc.features();
		while (sfIter.hasNext()) {
			SimpleFeature feature = sfIter.next();
			Geometry geom = (Geometry) feature.getDefaultGeometry();
			ptNum += geom.getNumPoints();
		}
		return ptNum;
	}
}
