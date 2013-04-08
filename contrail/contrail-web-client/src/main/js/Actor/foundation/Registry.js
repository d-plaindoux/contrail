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

/*global define*/

define("Actor/foundation/Registry", [ "Core/object/jObj", "Actor/jActor", "Core/client/jLoader", "Core/utils/jUtils" ],
    function(jObj, jActor, jLoader, jUtils) {
        "use strict";

        function Registry() {
            jObj.bless(this);
            this.registered = [];
        }

        Registry.init = jObj.constructor([],
            function() {
                return new Registry();
            });

        Registry.prototype.coordinator = undefined;

        Registry.prototype.registerNamedModel = jObj.procedure([ jObj.types.String, jObj.types.String, jObj.types.String, jObj.types.Array ],
            function(name,source,model,parameters) {
                var self = this;

                jLoader().
                    source(source).
                    onLoad(function() {
                        self.coordinator.actor(name).bindToModule(model,parameters);
                        self.registered.push(name);
                    });
            });

        Registry.prototype.registerModel = jObj.procedure([ jObj.types.String, jObj.types.String, jObj.types.Array ],
            function(source,model,parameters) {
                this.registerNamedModel(jUtils.uuid(), source, model, parameters);
            });

        Registry.prototype.registered = jObj.method([], jObj.types.ArrayOf(jObj.types.String),
            function() {
                return this.registered;
            });

        return Registry.init;
    });