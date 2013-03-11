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

/*
 * Package optimizer for nodejs:
 *    node ~/OpenProjects/node_modules/requirejs/bin/r.js  -o src/main/nodejs/app.build.js
 */

({
    appDir:"../js",
    baseUrl:".",
    dir:"../../../target/node_modules",
    modules:[
        {
            name:"Contrail/jContrail",
            exclude:["Network/jNetwork", "Actor/jActor", "Core/object/jObj", "Core/flow/jFlow", "Core/io/jMarshaller", "Core/utils/jUtils"]
        },
        {
            name:"Network/jNetwork",
            exclude:["Contrail/jContrail", "Actor/jActor", "Core/object/jObj", "Core/flow/jFlow", "Core/io/jMarshaller", "Core/utils/jUtils"]
        },
        {
            name:"Actor/jActor",
            exclude:["Contrail/jContrail", "Network/jNetwork", "Core/object/jObj", "Core/flow/jFlow", "Core/io/jMarshaller", "Core/utils/jUtils"]
        },
        {
            name:"Core/jCore",
            include:["Core/object/jObj", "Core/flow/jFlow", "Core/io/jMarshaller", "Core/utils/jUtils"]
        }
    ]
})