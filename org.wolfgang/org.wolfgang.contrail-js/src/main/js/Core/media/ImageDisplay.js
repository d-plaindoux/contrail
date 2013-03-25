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

        function ImageDisplay(canvas, width, height) {
            jObj.bless(this);

            this.canvas = canvas;
            this.width = width;
            this.height = height;
        }

        ImageDisplay.init = jObj.constructor([ jObj.types.Object, jObj.types.Number, jObj.types.Number ], // TODO -- check types
            function (canvas, width, height) {
                return new ImageDisplay(canvas, width, height);
            });

        ImageDisplay.prototype.displayImage = jObj.procedure([ jObj.types.Object ], // TODO -- check types
            function (image) {
                var context = this.canvas.getContext("2d");
                context.drawImage(image, 0, 0, this.width, this.height);
            });

        ImageDisplay.prototype.getURL = jObj.method([], jObj.types.Any, // TODO -- check types
            function () {
                return this.canvas.toDataURL();
            });

        return ImageDisplay.init;
    });
