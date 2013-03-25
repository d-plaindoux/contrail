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

/*global define, navigator*/

define([ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function Camera() {
            jObj.bless(this);

            this.stream = null;

            var failure = function (error) {
                jObj.throwError(jObj.exception("An error occurred: [CODE " + error.code + "]", null));
            };

            if (navigator.getUserMedia) { // For OPERA 12
                navigator.getUserMedia("video", this.setStream, failure);
            } else if (navigator.webkitGetUserMedia) { // For CHROME 12
                navigator.webkitGetUserMedia("video", this.setStream, failure);
            } else { // What else ?
                jObj.throwError(jObj.exception("Native web camera streaming is not supported in this browser!", null));
            }
        }

        Camera.init = jObj.constructor([],
            function () {
                return new Camera();
            });

        Camera.prototype.setStream = jObj.procedure([ jObj.types.Object ],
            function (stream) {
                this.stream = stream;
            });

        Camera.prototype.getStream = jObj.method([], jObj.types.Object,
            function () {
                return this.stream;
            });

        return Camera.init;
    });
