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
import groovy.util.logging.Slf4j

/**
 * Transition represents an even which may accour in a system.
 */
@Slf4j
@ToString(includeNames = true, includePackage = false, includeSuper = true)
@CompileStatic
class Transition  extends PNObject {

    /**
     * List of shortNames of incoming arcs
     */
    List<String> incoming = [];

    /**
     * List of shortNames of outgoing arcs
     */
    List<String> outgoing = [];

    /**
     * Returns the shortName of the Transition e.g. t1 ("t"+index)
     * 
     * @return the shortName of the Transition
     */
    public String shortName() {
        return "t"+index
    }
    
    /**
     * Calculates if the Transition can fire or not by triggering all its incoming and outgoing Arcs
     * 
     * @return if the Transition can fire or not
     */
    public boolean canFire() {
        boolean canFire = true;

        log.trace "canFire() - $this"
        
        if(incoming.isEmpty() && outgoing.isEmpty()) return false

        for (String arcShortName : incoming) { canFire = canFire & parent.arcs[arcShortName].canFire(); }

        if(!canFire) return canFire

        for (String arcShortName : outgoing) { canFire = canFire & parent.arcs[arcShortName].canFire(); }

        return canFire;
    }

    /**
     * Fires the Transition by triggering all its incoming and outgoing Arcs
     */
    public void fire() {
        log.trace "fire() - $this"
        
        if(!canFire()) throw new RuntimeException("")

        for (String arcShortName : incoming) { parent.arcs[arcShortName].fire(); }
        for (String arcShortName : outgoing) { parent.arcs[arcShortName].fire(); }
    }
    
    /**
     * Adds the shortName of the Arc to its incoming list
     * 
     * @param arc the incoming Arc
     */
    public void addIncoming(Arc arc) {
        this.incoming.add(arc.shortName());
    }

    /**
     * Adds the shortName of the Arc to its outgoing list
     * 
     * @param arc the outgoing Arc
     */
    public void addOutgoing(Arc arc) {
        this.outgoing.add(arc.shortName());
    }
}
