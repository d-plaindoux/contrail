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

define( [ "require", "../core/jObj", "../utils/Strict" ] , 
function(require, jObj, Strict) {

	function DestinationComponent() {
		jObj.bless(this, require("../factory/Factory").component());
		
		this.sourceLink = null;
	}
	
	DestinationComponent.prototype.acceptSource = function(componentId) {
		return (this.sourceLink === null);
	};
	
	DestinationComponent.prototype.connectSource = function(sourceLink) {
        Strict.assertType(sourceLink, "SourceLink");

		this.sourceLink = sourceLink;
		return require("../factory/Factory").componentLink(this.sourceLink.getSource(), this);
	};	
	
	DestinationComponent.prototype.closeDownStream = function() {
	    if (this.sourceLink !== null) {
	        this.sourceLink.getSource().closeDownStream();
	        this.sourceLink = null;
	    }
	};
	
	return DestinationComponent;
});