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

package org.wolfgang.opala.lexing.impl;


import java.net.URL;

import org.wolfgang.opala.lexing.ILocation;

/**
 * This class denotes a location in a given file. The location is a tuple file name plus the character index and the
 * line number.
 */

public class Location implements ILocation {
    private static final long serialVersionUID = 1757116409848078234L;

    private URL url;
    private int charPos;
    private int linePos;

    public Location(URL url, int charPos, int linePos) {
        this.url = url;
        this.linePos = linePos;
        this.charPos = charPos;
    }

    public URL getURL() {
        return url;
    }

    public int getCharPos() {
        return charPos;
    }

    public int getLinePos() {
        return linePos;
    }

    public String toString() {
        return url + " (" + linePos + ":" + charPos + ")";
    }
}

