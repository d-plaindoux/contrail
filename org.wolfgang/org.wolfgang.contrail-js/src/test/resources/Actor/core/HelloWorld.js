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

define("Hello/World", [ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        function HelloWorld(hello) {
            jObj.bless(this);

            this.hello = hello;
        }

        HelloWorld.init = jObj.constructor([jObj.types.String],
            function (hello) {
                return new HelloWorld(hello);
            });

        HelloWorld.prototype.sayHello = jObj.method([], jObj.types.String,
            function () {
                return this.hello;
            });

        return HelloWorld.init;
    });