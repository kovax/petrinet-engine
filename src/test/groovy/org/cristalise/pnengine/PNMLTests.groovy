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

package org.cristalise.pnengine;

import static org.junit.Assert.*
import groovy.transform.CompileStatic

import org.junit.Before
import org.junit.Test

/**
 * @author kovax
 *
 */
@CompileStatic
class PNMLTests {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }
    
    
    public void generalPNAsserts(PetriNet pn) {
        assert pn
        assert pn.transitions
        assert pn.places
        assert pn.arcs
        
        assert ! pn.inhibitors
    }


    @Test
    public void importANDSplitPNML() {
        def pn = PNMLUtility.pnmlImport("src/test/data/ANDSlit.pnml")
        generalPNAsserts(pn)
    }


    @Test
    public void importXORSplitPNML() {
        def pn = PNMLUtility.pnmlImport("src/test/data/XORSlit.pnml")
        generalPNAsserts(pn)
    }
}
