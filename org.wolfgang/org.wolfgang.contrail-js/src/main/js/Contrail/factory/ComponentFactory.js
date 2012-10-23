/*
 * Copyright (C)2012 D. Plaindoux.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; see the file COPYING.  If not, write to
 * the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*global define*/

define( [ "Utils/jStrict", "../component/Component", "../component/SourceComponent", "../component/DestinationComponent", "../component/pipeline/PipelineComponent" ] , 
function(jStrict, Component, SourceComponent, DestinationComponent, PipelineComponent) {
	
	var ComponentFactory = {};

	ComponentFactory.component = function () {
	    return Component.init();
	};
	
	ComponentFactory.sourceComponent = function () {
	    return SourceComponent.init();
	};

	ComponentFactory.destinationComponent = function () {
		return DestinationComponent.init();
	};
	
	ComponentFactory.pipelineComponent = function () {
		return PipelineComponent.init();
	};
	
	return ComponentFactory;
	
});