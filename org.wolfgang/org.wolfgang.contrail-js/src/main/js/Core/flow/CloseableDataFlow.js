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

        function CloseableDataFlow(dataFlow) {
            jObj.bless(this, require("Core/flow").core());
            this.closed = false;
            this.dataFlow = dataFlow;
        }

        CloseableDataFlow.init = jObj.constructor([jObj.types.Named("DataFlow")],
            function (dataFlow) {
                return new CloseableDataFlow(dataFlow);
            });

        CloseableDataFlow.prototype.handleData = jObj.procedure([jObj.types.Any],
            function (data) {
                if (this.closed) {
                    throw jObj.exception("L.data.flow.closed");
                } else {
                    this.dataFlow.handleData(data);
                }
            });

        CloseableDataFlow.prototype.handleClose = jObj.procedure([],
            function () {
                this.closed = true;
            });

        return CloseableDataFlow.init;
    });