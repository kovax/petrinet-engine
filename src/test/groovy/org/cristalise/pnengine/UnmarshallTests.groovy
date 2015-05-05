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
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.junit.Before
import org.junit.Test


/**
 *
 */
//@CompileStatic
class UnmarshallTeststs {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void jsonUnmarshall() {
        def pn = new PetriNet("p1->t1")
        pn.connect("p1").to("t1")

        def json = new JsonBuilder(pn).toPrettyString()
        def pn1 = new PetriNet( new JsonSlurper().parseText(json) )
        
        pn1.printJson()

        assert pn1.transitions
        assert pn1.transitions.size() == 1
        assert pn1.places
        assert pn1.places.size() == 1
        assert pn1.arcs
        assert pn1.arcs.size() == 1
        
        assert pn1.transitions.t1
        assert pn1.places.p1
        assert pn1.arcs.p1t1
        
        assert ! pn1.transitions.t1.canFire()
        pn1.places.p1.tokens = 1
        assert pn1.transitions.t1.canFire()
    }

    public void xmlUnmarshall() {
        def pn = new PetriNet("p1->t1")
        pn.connect("p1").to("t1")

        fail("UNIMPLEMENTED")
    }
}
