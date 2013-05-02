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

define("Core/io/jMarshaller", [ "Core/object/jObj" ],
    function (jObj) {
        "use strict";

        var jMarshaller = {};

        /**
         * Type object encoding
         *
         * @type {Object}
         */
        jMarshaller.types = {
            Array:0x1,
            Object:0x2,
            Character:0x3,
            String:0x4,
            BooleanTrue:0x5,
            BooleanFalse:0x6,
            Undefined:0x7,
            Null:0x8,

            ShortNumber:0x9,
            Number:0x10,
            Float:0x11      // TODO
        };

        /**
         * Define size for simple data types like numbers, chars, etc.
         * @type {Object}
         */
        jMarshaller.sizeOf = {
            Character:2,
            Number:4,
            ShortNumber:2,
            Boolean:1
        };

        // -------------------------------------------------------------------------------------------------------------
        // Decoding ...
        // -------------------------------------------------------------------------------------------------------------

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToNumberWithOffset = jObj.method([jObj.types.Array, jObj.types.Number], jObj.types.Number,
            function (bytes, offset) {
                if (bytes.length < offset + jMarshaller.sizeOf.Number) {
                    jObj.throwError(jObj.exception("L.array.out.of.bound"));
                }

                return bytes[offset] << 24 | bytes[offset + 1] << 16 | bytes[offset + 2] << 8 | (bytes[offset + 3] & 0xFF);
            });

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToNumber = jObj.method([jObj.types.Array], jObj.types.Number,
            function (bytes) {
                return jMarshaller.bytesToNumberWithOffset(bytes, 0);
            });


        /**
         * Convert an array of bytes to a float
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToFloatWithOffset = jObj.method([jObj.types.Array, jObj.types.Number], jObj.types.Number,
            function (bytes, offset) {
                var intValue = jMarshaller.bytesToNumberWithOffset(bytes,offset);

                // Compute float number here

                return intValue;

            });

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToFloat = jObj.method([jObj.types.Array], jObj.types.Number,
            function (bytes) {
                return jMarshaller.bytesToFloatWithOffset(bytes, 0);
            });

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToShortNumberWithOffset = jObj.method([jObj.types.Array, jObj.types.Number], jObj.types.Number,
            function (bytes, offset) {
                if (bytes.length < offset + jMarshaller.sizeOf.ShortNumber) {
                    jObj.throwError(jObj.exception("L.array.out.of.bound"));
                }

                return bytes[offset] << 8 | (bytes[offset + 1] & 0xFF);
            });

        /**
         * Convert an array of bytes to an integer
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToShortNumber = jObj.method([jObj.types.Array], jObj.types.Number,
            function (bytes) {
                return jMarshaller.bytesToShortNumberWithOffset(bytes, 0);
            });

        /**
         * Convert an array of bytes to a number
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToCharWithOffset = jObj.method([jObj.types.Array, jObj.types.Number], jObj.types.Number,
            function (bytes, offset) {
                if (bytes.length < offset + jMarshaller.sizeOf.Character) {
                    jObj.throwError(jObj.exception("L.array.out.of.bound"));
                }

                return bytes[offset] << 8 | (bytes[offset + 1] & 0xFF);
            });

        /**
         * Convert an array of bytes to a number
         *
         * @param bytes The source
         * @param offset The initial position
         * @return {*}
         */
        jMarshaller.bytesToChar = jObj.method([jObj.types.Array], jObj.types.Number,
            function (bytes) {
                return jMarshaller.bytesToCharWithOffset(bytes, 0);
            });

        /**
         * Convert a byte array to string with a given offset
         *
         * @param bytes
         * @return {String}
         */
        jMarshaller.bytesToStringWithOffset = jObj.method([jObj.types.Array, jObj.types.Number, jObj.types.Number], jObj.types.String,
            function (bytes, offset, length) {
                var str = "", i;

                if (bytes.length < offset + length * jMarshaller.sizeOf.Character) {
                    jObj.throwError(jObj.exception("L.array.out.of.bound"));
                }

                for (i = 0; i < length * jMarshaller.sizeOf.Character; i += 2) {
                    str += String.fromCharCode(jMarshaller.bytesToCharWithOffset(bytes, i + offset));
                }

                return str;
            });

        /**
         * Convert a byte array to string
         *
         * @param bytes
         * @return {String}
         */
        jMarshaller.bytesToString = jObj.method([jObj.types.Array], jObj.types.String,
            function (bytes) {
                return jMarshaller.bytesToStringWithOffset(bytes, 0, bytes.length / 2);
            });

        /**
         * Convert a byte array to string with a given offset
         *
         * @param bytes
         * @return {String}
         */
        jMarshaller.charsToStringWithOffset = jObj.method([jObj.types.Array, jObj.types.Number, jObj.types.Number], jObj.types.String,
            function (chars, offset, length) {
                var str = "", i;

                if (chars.length < offset + length) {
                    jObj.throwError(jObj.exception("L.array.out.of.bound"));
                }

                for (i = 0; i < length; i += 1) {
                    str += String.fromCharCode(chars[i + offset]);
                }

                return str;
            });

        /**
         * Convert a char like array to string
         *
         * @param bytes
         * @return {String}
         */
        jMarshaller.charsToString = jObj.method([jObj.types.Array], jObj.types.String,
            function (chars) {
                return jMarshaller.charsToStringWithOffset(chars, 0, chars.length);
            });

        // -------------------------------------------------------------------------------------------------------------
        // Encoding ...
        // -------------------------------------------------------------------------------------------------------------

        /**
         * Concert a number to a byte array
         *
         * @param i
         * @return {Array}
         */
        jMarshaller.numberToBytes = jObj.method([jObj.types.Number], jObj.types.Array,
            function (value) {
                return [value >>> 24 & 0xFF, value >>> 16 & 0xFF, value >>> 8 & 0xFF, value & 0xFF];
            });

        /**
         * Concert a float to a byte array
         *
         * @param i
         * @return {Array}
         */
        jMarshaller.floatToBytes = jObj.method([jObj.types.Number], jObj.types.Array,
            function (value) {
                return jMarshaller.numberToBytes(value);
            });

        /**
         * Concert a number to a byte array
         *
         * @param i
         * @return {Array}
         */
        jMarshaller.shortNumberToBytes = jObj.method([jObj.types.Number], jObj.types.Array,
            function (value) {
                return [value >>> 8 & 0xFF, value & 0xFF];
            });

        /**
         * Convert a char to a byte array
         *
         * @param i
         * @return {Array}
         */
        jMarshaller.charToBytes = jObj.method([jObj.types.Number], jObj.types.Array,
            function (value) {
                return [value >>> 8 & 0xFF, value & 0xFF];
            });

        /**
         * Convert a string to a byte array
         *
         * @param str
         * @return {Array}
         */
        jMarshaller.stringToBytes = jObj.method([jObj.types.String], jObj.types.Array,
            function (value) {
                var bytes = [], i;

                for (i = 0; i < value.length; i += 1) {
                    bytes = bytes.concat(jMarshaller.charToBytes(value.charCodeAt(i)));
                }

                return bytes;
            });

        /**
         * Convert a string to a byte array
         *
         * @param str
         * @return {Array}
         */
        jMarshaller.stringToChars = jObj.method([jObj.types.String], jObj.types.Array,
            function (value) {
                var chars = [], i;

                for (i = 0; i < value.length; i += 1) {
                    chars.push(value.charCodeAt(i));
                }

                return chars;
            });

        return jMarshaller;
    });
