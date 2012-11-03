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

define( [ "require",  "Core/jObj" ] , 
function(require, jObj) {

	function SourceComponent() {
		jObj.bless(this, require("Contrail/Factory").component.basic());

		this.destinationLink = null;
	}

	SourceComponent.init = jObj.constructor([], function () {
		return new SourceComponent();
	});

	SourceComponent.prototype.acceptDestination = jObj.method([jObj.types.String], jObj.types.Boolean, function(componentId) {
		return this.destinationLink === null;
	});

	SourceComponent.prototype.connectDestination = jObj.method(["DestinationLink"], "ComponentLink", function(destinationLink) {
		this.destinationLink = destinationLink;
		return require("Contrail/Factory").link.components(this, this.destinationLink.getDestination());
	});

	SourceComponent.prototype.getDownStreamDataFlow = jObj.method([], "DataFlow");

	SourceComponent.prototype.closeUpStream = jObj.procedure([], function() {
		if (this.destinationLink !== null) {
			this.destinationLink.getSource().closeUpStream();
			this.destinationLink = null;
		} else {
			throw jObj.exception("L.destination.not.connected");
		}
	});
	
	return SourceComponent;
});
