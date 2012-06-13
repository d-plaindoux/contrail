/* Copyright (C)2012 D. Plaindoux.
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

ct.lang = {
	Object : function(fields, behaviors) {
		var o = behaviors;
		
		for(var name in fields) {
			var value = fields[name];
			var nName = name[0].toUpperCase() + name.substring(1);
			var set = "set" + nName;
			var get = "get" + nName;
			o[set] = function(value) {
				this[name] = value;
			};
			o[get] = function() {
				return this[name];
			};
			if(value != null) {
				o[set](value);
			}
		}
		
		return ct.lang.Extensible(o);
	},
	Extensible : function(o) {
		o.With = function(behavior) {
			if( typeof behavior == "object") {
				for(var attr in behavior) {
					o[attr] = behavior[attr];
				}
				return o;
			} else {
				throw "behavior not an object";
			}
		}
		
		return o;
	}
}