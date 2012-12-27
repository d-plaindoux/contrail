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

        function FilteredDataFlow(dataFlow, predicate) {
            jObj.bless(this, require("Core/flow/jFlow").core());
            this.dataFlow = dataFlow;
            this.filter = predicate;
        }

        FilteredDataFlow.init = jObj.constructor([jObj.types.Named("DataFlow"), jObj.types.Function/*A -> null|A*/],
            function (dataFlow, predicate) {
                return new FilteredDataFlow(dataFlow, predicate);
            });

        FilteredDataFlow.prototype.handleData = jObj.procedure([jObj.types.Any],
            function (data) {
                var value = this.filter(data);

                if (value) {
                    this.dataFlow.handleData(value);
                }
            });

        FilteredDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                // Ignore
            });

        return FilteredDataFlow.init;
    });