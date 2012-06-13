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

ct.require("lang");

ct.logger = {
	INFO : 4,
	WARNING : 2,
	ALERT : 1,
	NONE : 0,
	Object : function(level, display) {
		return ct.lang.Object({
			level : level,
			display : display
		}, {
			info : function(message) {
				if(this.getLevel() & ct.logger.INFO == ct.logger.INFO) {
					this.getDisplay().printMessage("[info] " + message)
				}
			},
			warning : function(message) {
				if(this.getLevel() & ct.logger.WARNING == ct.logger.WARNING) {
					this.getDisplay().printMessage("[warn] " + message)
				}
			},
			alert : function(message) {
				if(this.getLevel() & ct.logger.ALERT == ct.logger.ALERT) {
					this.getDisplay().printMessage("[alert] " + message)
				}
			}
		});
	}
}