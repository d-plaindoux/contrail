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

/*global define:true, require, module*/

if (typeof define !== "function") {
    var define = require("amdefine")(module);
}

define([ "require", "Core/object/jObj" ],
    function (require, jObj) {
        "use strict";

        function BufferedDataFlow() {
            jObj.bless(this, require("Core/flow/jFlow").core());
            this.data = [];
        }

        BufferedDataFlow.init = jObj.constructor([],
            function () {
                return new BufferedDataFlow();
            });

        BufferedDataFlow.prototype.handleData = jObj.procedure([jObj.types.Any],
            function (data) {
                this.data = this.data.concat(data);
            });

        BufferedDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                this.data = [];
            });

        BufferedDataFlow.prototype.getCumulated = jObj.method([], jObj.types.Array,
            function () {
                return this.data; // Can be modified -- TODO
            });

        return BufferedDataFlow.init;
    });