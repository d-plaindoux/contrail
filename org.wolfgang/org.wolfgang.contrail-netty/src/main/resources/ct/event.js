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

/**
 * Module <code>CTEvent</code> dedicated to event construction
 */
function CTEvent() {
	return {
		/**
		 * Event type which can be extended calling {@link #declareType(string,string)}.
		 */
		eventType : {
			NETWORK : "NetworkEvent"
		},

		/**
		 * Method called whether an event type must be declared
		 *
		 * @param type
		 *          The event type name
		 * @param value
		 *          The event type value
		 * @throws EventTypeDeclarationAlreadyExistException is the type is already defined
		 */
		setEventType : function(type, value) {
			if( typeof this.eventType[type] == "undefined") {
				this.eventType[type] = value;
			} else {
				throw {
					type : "EventTypeDeclarationAlreadyExistException",
					message : "Type [" + type + "] is already defined"
				};
			}
		},
		/**
		 * Method called whether an event type must be declared
		 *
		 * @param type
		 *          The event type name
		 * @param value
		 *          The event type value
		 * @throws EventTypeDeclarationNotFoundException is the type is not defined
		 */
		getEventType : function(type) {
			if( typeof this.eventType[type] == "undefined") {
				throw {
					type : "EventTypeDeclarationNotFoundException",
					message : "Type [" + type + "] is not defined"
				};
			} else {
				return this.eventType[type];
			}
		},
		/**
		 * Method called whether an event must be built
		 * @param type
		 * 			The event type
		 * @param content
		 * 			The event content
		 * @return a JSon object denoting a network event
		 */
		getEvent : function(type, content) {
			var event = this.basicEvent(this.getEventType(type));
			event.content = content;
			return event;
		},
		/**
		 * @param target
		 * 			The event target
		 * @param content
		 * 			The event content
		 * @return a JSon object denoting a network event
		 */
		basicEvent : function(type) {
			var event = new Object();
			event.type = type;
			return event;
		}
	};
}

CTEvent.Default = new CTEvent();
