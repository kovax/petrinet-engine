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
import groovy.util.logging.Slf4j


/**
 * @author kovax
 *
 */
@Slf4j
@CompileStatic
class PNMLUtility {
    
    
    /**
     * Imports files which were created PNML (http://pnml.lip6.fr/index.html) compliant editors
     * 
     * @param the name of the PNML (xml) file
     * @return the generated PetriNet object
     */
    public static PetriNet pnmlImport(String file) {
        return null
    }

    
    /**
     * Export the PetriNet to a PNML (http://pnml.lip6.fr/index.html) file
     * 
     * @param pn the PetriNet to be exported
     * @return the generated PNML string (xml)
     */
    public static String pnmlIExport(PetriNet pn) {
        return ""
    }

}
