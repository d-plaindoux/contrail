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

define("Actor/jActor", [ "Core/object/jObj", "Actor/jEvent", "./core/Coordinator", "./component/CoordinatorComponent", "./core/ActorException" ],
    function (jObj, event, coordinator, component, exception) {
        "use strict";

        var Actor = {};

        Actor.component = component;
        Actor.coordinator = coordinator;
        Actor.event = event;
        Actor.exception = exception;

        return Actor;
    })
;
