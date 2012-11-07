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

define("IO/jMarshaller", [ "Core/jObj" ],
    function (jObj) {
        "use strict";

        var jMarshaller = {};

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToInt = jObj.method([jObj.types.Array, jObj.types.Number], jObj.types.Number, function (bytes, offset) {
            var i = jObj.value(offset, 0);

            if (bytes.length < i + 4) {
                throw jObj.exception("L.array.out.of.bound");
            }

            return bytes[i] << 24 | bytes[i + 1] << 16 | bytes[i + 2] << 8 | bytes[i + 3];
        });

        /**
         * Concert an integer to a byte array
         *
         * @param i
         * @return {Array}
         */
        jMarshaller.intToBytes = jObj.method([jObj.types.Number], jObj.types.Array, function (i) {
            return [i >>> 24 & 0xFF, i >>> 16 & 0xFF, i >>> 8 & 0xFF, i & 0xFF];
        });

        /**
         * Convert a byte array to string
         *
         * @param bytes
         * @return {String}
         */
        jMarshaller.byteToString = jObj.method([jObj.types.Array], jObj.types.String, function (bytes) {
            var str = "", i;
            for (i = 0; i < bytes.length; i += 2) {
                str += String.fromCharCode(jObj.readInt(i, bytes));
            }

            return str;
        });

        /**
         * Convert a string to a byte array
         *
         * @param str
         * @return {Array}
         */
        jMarshaller.stringToBytes = jObj.method([jObj.types.String], jObj.types.Array, function (str) {
            var bytes = [], char, i;
            for (i = 0; i < str.length; i += 1) {
                char = str.charCodeAt(i);
                bytes.push(char >>> 8);
                bytes.push(char & 0xFF);
            }

            return bytes;
        });

        return jMarshaller;
    });
