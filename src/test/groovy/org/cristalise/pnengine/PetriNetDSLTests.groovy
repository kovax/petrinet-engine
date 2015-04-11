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

import org.junit.Test

import static org.junit.Assert.*

/**
 * @author kovax
 *
 */
class PetriNetDSLTests {

    @Test
    public void placeToTransition() {
        Arc a1;
        def pn = PetriNet.petrinet("placeToTransition") {
            def p1 = place "p1" withTokens 1
            def t1 = transition "t1"

            a1 = connect p1 to t1
        }
        
        pn.printJson()

        assert a1.name == "p1t1"

        assert pn.places.p1.tokens == 1
        assert pn.transitions.t1.canFire()

        pn.transitions.t1.fire()

        assert pn.places.p1.tokens == 0
        assert ! pn.transitions.t1.canFire()
    }
    
    @Test
    public void orSplit() {
        PetriNet.petrinet("orSplit") {
            def p1 = place "p1" withTokens 1
            def p2 = place "p2"
            def t1 = transition "t1"
            def t2 = transition "t2"

            connect p1 to t1
            connect p1 to t2
            connect t1 to p2
            connect t2 to p2
            
            transitions.t1.fire()

            assert ! t1.canFire()
            assert ! t2.canFire()
            
            assert p1.tokens == 0
            assert p2.tokens == 1
        }
    }

    @Test
    public void andSplit() {
        PetriNet.petrinet("andSplit") {
            assert false
        }
    }
}
