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

/*global $, require, setTimeout */

$(function() {
	require([ "Contrail/factory/Factory", "Contrail/core/jObj", "Contrail/core/jDom" ], function(Factory, jObj, jDom) {
	try {
		var key, classes, id, name, showHide, changeRepresentation;
		
		changeRepresentation = function (id, object) {
			return function(e) {
				if ($(id + " > pre").attr("display_toogle") === "type") {
					$(id + " > pre").replaceWith(jDom("pre", { display_toogle : "object" }, jObj.toString(object)).build());
					$(id + " > pre").addClass("boxedArea");
				} else {
					$(id + " > pre").replaceWith(jDom("pre", { display_toogle : "type" }, jObj.toString(jObj.toType(object))).build());
					$(id + " > pre").addClass("boxedArea");
				}
			};
		};
		
		classes = {
			Component : Factory.component(),
			SourceComponent : Factory.sourceComponent(),
			DestinationComponent : Factory.destinationComponent(),
			PipelineComponent : Factory.pipelineComponent()
		};

		for(key in classes) {
			name = jObj.getClass(classes[key]);
			id = "#main > #" + name;
			$("#main").append(jDom("div", { id : name }).build());
			$(id).hide();   
			$(id).append(jDom("h3",{}, " " + name + " " + jDom("button", {}, "View").build()).build());
			$(id + " > h3 > button").button().click(changeRepresentation(id, classes[key]));
			$(id).append(jDom("pre", { display_toogle : "object" }, jObj.toString(classes[key])).build());
			$(id + " > pre").addClass("boxedArea");
			$(id + " > h3").addClass("ui-widget-header");
			$(id).css("position","fixed");
			$(id).show("slice");
			$(id).draggable({ opacity: 0.7, stack: "#main div" });
		}
	} catch (e) {
		$("#error").prepend(e.toString());
	}
	});
});