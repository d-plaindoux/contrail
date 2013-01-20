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

$(function () {
    "use strict";

    require([ "Contrail/jContrail", "Core/object/jObj", "Core/utils/jTransducer", "Core/dom/jDom" ],
        function (Factory, jObj, jTransducer, jDom) {
            try {
                var key, classes, id, name, changeToType, changeToObject;

                changeToType = function (id, object) {
                    return function () {
                        $(id + " > pre").replaceWith(jDom("pre", { display_type:"type" }, jTransducer.toString(jObj.toType(object))));
                        $(id + " > pre").addClass("boxedArea");
                    };
                };

                changeToObject = function (id, object) {
                    return function () {
                        $(id + " > pre").replaceWith(jDom("pre", { display_object:"object" }, jTransducer.toString(object)));
                        $(id + " > pre").addClass("boxedArea");
                    };
                };

                classes = {
                    Component:Factory.component.core.component(),
                    SourceComponent:Factory.component.core.source(),
                    DestinationComponent:Factory.component.core.destination(),
                    PipelineComponent:Factory.component.pipeline()
                };

                for (key in classes) {
                    if (classes.hasOwnProperty(key)) {
                        name = jObj.getClass(classes[key]);
                        id = "#main > #" + name;
                        $("#main").append(jDom("div", { id:name }));
                        $(id).hide();
                        $(id).append(jDom("h3", {}, " " + name + " " + jDom("input", { value:"Object" }) + jDom("input", { value:"Type" })));
                        $(id + " > h3 > input[value='Object']").button().click(changeToObject(id, classes[key]));
                        $(id + " > h3 > input[value='Type']").button().click(changeToType(id, classes[key]));
                        $(id).append(jDom("pre", { display_classe:"type" }, jObj.toString(classes[key])));
                        $(id + " > pre").addClass("boxedArea");
                        $(id + " > h3").addClass("ui-widget-header");
                        $(id).css("position", "fixed");
                        $(id).show("slice");
                        $(id).draggable({ opacity:0.7, stack:"#main div" });
                    }
                }
            } catch (e) {
                $("#error").prepend(e.toString());
            }
        });
});
