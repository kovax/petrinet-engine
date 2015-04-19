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

import org.junit.Test


/**
 *
 */
@CompileStatic
class PNMLTests {

    public void generalPNAsserts(PetriNet pn, Map sizes) {
        assert pn
        assert pn.transitions
        assert pn.transitions.size() == sizes.transitions
        assert pn.places
        assert pn.places.size() == sizes.places
        assert pn.arcs
        assert pn.arcs.size() == sizes.arcs
    }


    @Test
    public void importANDSplitPNML() {
        def pn = new PNMLUtility().pnmlImport("src/test/data/ANDSplit.pnml")
        generalPNAsserts(pn, [transitions:4, places:6, arcs:10])
        
        assert ! pn.transitions["t1"].canFire()

        pn.places.p1.tokens = 1
//        pn.printJson()
        assert pn.transitions.t1.canFire()
    }


    @Test
    public void importXORSplitPNML() {
        def pn = new PNMLUtility().pnmlImport("src/test/data/XORSplit.pnml")
        generalPNAsserts(pn, [transitions:4, places:4, arcs:8])
    }
}
