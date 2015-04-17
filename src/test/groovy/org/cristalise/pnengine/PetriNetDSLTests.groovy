/**
q * This file is part of the CRISTAL-iSE kernel.
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

import org.junit.Test

/**
 *
 */
class PetriNetDSLTests {

    @Test
    public void placeToTransition() {
        def pn = PetriNet.builder("p1->t1") {
            def p1 = place "p1" withTokens 1
            def t1 = transition "t1"

            connect p1 to t1
            assert arcs.p1t1.name == "p1t1"
            
            assert places.p1.tokens == 1
            assert transitions.t1.canFire()
        }

        assert pn.places.p1.tokens == 1
        assert pn.transitions.t1.canFire()

        pn.transitions.t1.fire()

        assert pn.places.p1.tokens == 0
        assert ! pn.transitions.t1.canFire()
    }
    

    @Test
    public void SameNameThrowsException() {
        def pn = PetriNet.builder("p1->t1") {
            try {
                place "p1" withTokens 1
                place "p1"
                fail()
            }
            catch(RuntimeException e) {
                assert e.message == "Place 'p1' already exists"
            }

            try {
                transition "t1"
                transition "t1"
                fail()
            }
            catch(RuntimeException e) {
                assert e.message == "Transition 't1' already exists"
            }

            try {
                connect transition: "t1" to place: "p1" 
                connect transition: "t1" to place: "p1" 
                fail()
            }
            catch(RuntimeException e) {
                assert e.message == "Arc 't1p1' already exists"
            }
        }
    }


    @Test
    public void xorSplit() {
        PetriNet.builder("xorSplit") {
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
        PetriNet.builder("andSplit") {
            connect place:      "p1" to transition: "t1"
            connect transition: "t1" to place:      "p2"
            connect transition: "t1" to place:      "p3"
            connect place:      "p2" to transition: "t2"
            connect place:      "p3" to transition: "t3"
            connect transition: "t2" to place:      "p4"
            connect transition: "t3" to place:      "p5"
            connect place:      "p4" to transition: "t4"
            connect place:      "p5" to transition: "t4"
            
            assert ! transitions.t1.canFire()
            assert ! transitions.t2.canFire()
            assert ! transitions.t3.canFire()
            assert ! transitions.t4.canFire()

            places.p1.tokens = 1

            assert   transitions.t1.canFire()
            assert ! transitions.t2.canFire()
            assert ! transitions.t3.canFire()
            assert ! transitions.t4.canFire()

            transitions.t1.fire()

            assert places.p1.tokens == 0
            assert places.p2.tokens == 1
            assert places.p3.tokens == 1
            assert places.p4.tokens == 0
            assert places.p5.tokens == 0
            
            assert ! transitions.t1.canFire()
            assert   transitions.t2.canFire()
            assert   transitions.t3.canFire()
            assert ! transitions.t4.canFire()
            
            transitions.t2.fire()

            assert places.p1.tokens == 0
            assert places.p2.tokens == 0
            assert places.p3.tokens == 1
            assert places.p4.tokens == 1
            assert places.p5.tokens == 0

            assert ! transitions.t1.canFire()
            assert ! transitions.t2.canFire()
            assert   transitions.t3.canFire()
            assert ! transitions.t4.canFire()

            transitions.t3.fire()

            assert places.p1.tokens == 0
            assert places.p2.tokens == 0
            assert places.p3.tokens == 0
            assert places.p4.tokens == 1
            assert places.p5.tokens == 1
            
            assert ! transitions.t1.canFire()
            assert ! transitions.t2.canFire()
            assert ! transitions.t3.canFire()
            assert   transitions.t4.canFire()
            
            transitions.t4.fire()
            
            assert ! transitions.t1.canFire()
            assert ! transitions.t2.canFire()
            assert ! transitions.t3.canFire()
            assert ! transitions.t4.canFire()
            
            assert places.p1.tokens == 0
            assert places.p2.tokens == 0
            assert places.p3.tokens == 0
            assert places.p4.tokens == 0
            assert places.p5.tokens == 0
        }
    }
}
