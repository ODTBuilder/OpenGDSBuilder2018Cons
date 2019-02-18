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

import org.geotools.feature.SchemaException;

import com.git.gdsbuilder.type.dt.layer.DTLayerList;
import com.git.gdsbuilder.type.validate.error.ErrorLayer;
import com.git.gdsbuilder.type.validate.option.specific.OptionTolerance;

public interface LayerQuadValidator {

	public ErrorLayer validateSelfEntity(DTLayerList relationLayers, OptionTolerance tolerance)
			throws SchemaException, IOException;

	public ErrorLayer validateConIntersected() throws SchemaException;

	public ErrorLayer validateOutBoundary(DTLayerList relationLayers, OptionTolerance tole) throws SchemaException;

	public ErrorLayer validateEntityDuplicated() throws SchemaException;

	public ErrorLayer validateNodeMiss(DTLayerList relationLayers, OptionTolerance tole)
			throws SchemaException, IOException;

	public ErrorLayer validateOneAcre(DTLayerList typeLayers, double spatialAccuracyTolorence);

	public ErrorLayer validateOneStage(DTLayerList typeLayers);

	public ErrorLayer validateBoundaryMiss(DTLayerList relationLayers);

	public ErrorLayer validateCenterLineMiss(DTLayerList relationLayers);

	public ErrorLayer validateHoleMissplacement();

	public ErrorLayer valildateLinearDisconnection(DTLayerList relationLayers, OptionTolerance tole)
			throws SchemaException;
}
