/**
 * This file is part of the CRISTAL-iSE kernel.
 * Copyright (c) 2001-2015 The CRISTAL Consortium. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * http://www.fsf.org/licensing/licenses/lgpl.html
 */
package org.cristalise.pnengine

import groovy.transform.CompileStatic
import groovy.transform.ToString

/**
 * Base class for all objects in the PetriNet
 */
@ToString(includePackage = false)
@CompileStatic
abstract class PNObject {

    public PetriNet parent = null

    /**
     * The unique ID of all PN Object within the PetriNet
     */
    int ID = -1

    /**
     * Index is a unique ID within the type of the PetriNet object. It follows the natural 
     * convention used in Algebra and drawings done by humans.
     */
    int index = -1

    /**
     * The optional name of the PN Object. It can be any string but it is set to the shortName if nothing was set
     */
    String name  = ""

    /**
     * The shortName of the PN Object implemented by each subclass
     * 
     * @return the shortName
     */
    public abstract String shortName();
}
