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

define([ "Core/jObj", "./TransducerDataFlow" ],
    function (jObj, transducerDataFlow) {
        "use strict";

        function TransducerUpStreamDataFlow(component, transducer) {
            jObj.bless(this, transducerDataFlow(transducer));
            this.component = component;
        }

        TransducerUpStreamDataFlow.init = jObj.constructor([jObj.types.Any, "TransducerComponent", "DataTransducer"],
            function (component, transducer) {
                return new TransducerUpStreamDataFlow(component, transducer);
            });

        TransducerUpStreamDataFlow.prototype.getDataFlow = jObj.method([], "DataFlow",
            function () {
                if (this.component.getDestinationComponentLink() !== undefined) {
                    throw jObj.exception("L.source.not.yet.connected");
                } else {
                    return this.component.getDestinationComponentLink().getUpStreamDataFlow();
                }
            });

        return TransducerUpStreamDataFlow.init;
    });
