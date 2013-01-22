# WolfGang

The WolfGang project goals are  multiple covering low level java tools
but also framework dedicated to components cooperation.

## Contrail Network

In this approach  we call network a set  of connected components which
cooperate in  order to solve a  given problem.  Such problem  can be a
simple one  like data  transformation, interceptors, ciphered  data or
complex like service based layer..

For his  purpose each  component provides an  upstream/downstream data
management. From  this simple  design component patterns  are provided
like:

* initial producer providing a data injection mechanism,
* terminal  consumer providing a data reception mechanism,
* pipeline providing a component to component communication,
* single source to multiple destination connection,
* a multiple source to a single destination connection and 
* router providing a baisc mechanism for routed packets.

### Supported Languages

The library is available in:

* Java and 
* JavaScript

### License

Copyright (C)2012 D. Plaindoux.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 2, or (at your option) any
later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; see the file COPYING.  If not, write to
the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
