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

define([ "require", "Core/jObj" ],
    function (require, jObj) {
        "use strict";

        function GuardedDataFlow(dataFlow, predicate) {
            jObj.bless(this, require("Flow/Factory").core());
            this.dataFlow = dataFlow;
            this.predicate = predicate;
        }

        GuardedDataFlow.init = jObj.constructor([jObj.types.Named("DataFlow"), jObj.types.Function],
            function (dataFlow, predicate) {
                return new GuardedDataFlow(dataFlow, predicate);
            });

        GuardedDataFlow.prototype.handleData = jObj.procedure([jObj.types.Any],
            function (data) {
                if (this.predicate(data)) {
                    this.dataFlow.handleData(data);
                }
            });

        GuardedDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                // Ignore
            });

        return GuardedDataFlow.init;
    });