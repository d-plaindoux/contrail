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

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function DataFlow(handler) {
            jObj.bless(this);

            this.handler = handler;
        }

        DataFlow.init = jObj.constructor([jObj.types.Nullable(jObj.types.Function)],
            function (handler) {
                return new DataFlow(handler);
            });

        DataFlow.prototype.handleData = jObj.procedure([jObj.types.Any],
            function (data) {
                if (this.handler) {
                    this.handler(data);
                } else {
                    jObj.abstractDefinition.apply(this, arguments);
                }
            });

        DataFlow.prototype.handleClose = jObj.procedure([]);

        return DataFlow.init;
    });