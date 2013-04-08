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

define([ "require", "Core/object/jObj", "Core/io/jMarshaller" ],
    function (require, jObj, jMarshaller) {
        "use strict";

        function PayloadEncoder() {
            jObj.bless(this, require("Contrail/codec/jCodec").core.encoder());
        }

        PayloadEncoder.init = jObj.constructor([],
            function () {
                return new PayloadEncoder();
            });

        PayloadEncoder.prototype.transform = jObj.method([jObj.types.Array], jObj.types.Array,
            function (bytes) {
                return [ jMarshaller.numberToBytes(bytes.length).concat(bytes) ];
            });

        return PayloadEncoder.init;
    });