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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 */
public class PetriNetJavaTests {
    @Test
    public void originalTest() {
        PetriNet pn = new PetriNet();

        Transition t1 = pn.transition("t1");
        Transition t2 = pn.transition("t2");

        Place p1 = pn.place("p1", 2);
        Place p2 = pn.place("p2");
        Place p3 = pn.place("p3");

        pn.connect(p1, t1);
        pn.connect(t1, p2);
        pn.connect(p2, t2);
        pn.connect(t2, p3);

        pn.getArcs().get("t1p2").setWeight(2);
        
        assertTrue(p1.hasEnoughTokens(1));
        assertFalse(p1.maxTokensReached(1));
        assertFalse(p2.hasEnoughTokens(1));
        assertFalse(p1.maxTokensReached(1));

        assertTrue(t1.canFire());
        assertFalse(t2.canFire());

        t1.fire();

        assertEquals(1, p1.getTokens());
        assertEquals(2, p2.getTokens());

        assertTrue(t1.canFire());
        assertTrue(t2.canFire());

        t1.fire();

        assertEquals(0, p1.getTokens());
        assertEquals(4, p2.getTokens());

        assertFalse(t1.canFire());
        assertTrue(t2.canFire());

        t2.fire();

        assertEquals(0, p1.getTokens());
        assertEquals(3, p2.getTokens());

        assertFalse(t1.canFire());
        assertTrue(t2.canFire());

        t2.fire();

        assertEquals(0, p1.getTokens());
        assertEquals(2, p2.getTokens());
        
        pn.getArcs().get("p2t2").setWeight(2);
        t2.fire();
        
        assertEquals(0, p1.getTokens());
        assertEquals(0, p2.getTokens());

        assertFalse(t1.canFire());
        assertFalse(t2.canFire());
    }
    
    @Test
    public void andSplit() {
        PetriNet pn = new PetriNet();

        pn.connect("p1").to("t1");
        pn.connect("t1").to("p2");
        pn.connect("t1").to("p3");
        pn.connect("p2").to("t2");
        pn.connect("p3").to("t3");
        pn.connect("t2").to("p4");
        pn.connect("t3").to("p5");
        pn.connect("p4").to("t4");
        pn.connect("p5").to("t4");

        assertTrue( ! pn.getTransitions().get("t1").canFire());
        assertTrue( ! pn.getTransitions().get("t2").canFire());
        assertTrue( ! pn.getTransitions().get("t3").canFire());
        assertTrue( ! pn.getTransitions().get("t4").canFire());

        pn.getPlaces().get("p1").setTokens(1);
        
        assertTrue(   pn.getTransitions().get("t1").canFire());
        assertTrue( ! pn.getTransitions().get("t2").canFire());
        assertTrue( ! pn.getTransitions().get("t3").canFire());
        assertTrue( ! pn.getTransitions().get("t4").canFire());

        pn.getTransitions().get("t1").fire();

        assertTrue( ! pn.getTransitions().get("t1").canFire());
        assertTrue(   pn.getTransitions().get("t2").canFire());
        assertTrue(   pn.getTransitions().get("t3").canFire());
        assertTrue( ! pn.getTransitions().get("t4").canFire());

        assertEquals(0, pn.getPlaces().get("p1").getTokens());
        assertEquals(1, pn.getPlaces().get("p2").getTokens());
        assertEquals(1, pn.getPlaces().get("p3").getTokens());
        assertEquals(0, pn.getPlaces().get("p4").getTokens());
        assertEquals(0, pn.getPlaces().get("p5").getTokens());

        pn.getTransitions().get("t2").fire();

        assertTrue( ! pn.getTransitions().get("t1").canFire());
        assertTrue( ! pn.getTransitions().get("t2").canFire());
        assertTrue(   pn.getTransitions().get("t3").canFire());
        assertTrue( ! pn.getTransitions().get("t4").canFire());

        assertEquals(0, pn.getPlaces().get("p1").getTokens());
        assertEquals(0, pn.getPlaces().get("p2").getTokens());
        assertEquals(1, pn.getPlaces().get("p3").getTokens());
        assertEquals(1, pn.getPlaces().get("p4").getTokens());
        assertEquals(0, pn.getPlaces().get("p5").getTokens());

        pn.getTransitions().get("t3").fire();

        assertTrue( ! pn.getTransitions().get("t1").canFire());
        assertTrue( ! pn.getTransitions().get("t2").canFire());
        assertTrue( ! pn.getTransitions().get("t3").canFire());
        assertTrue(   pn.getTransitions().get("t4").canFire());

        assertEquals(0, pn.getPlaces().get("p1").getTokens());
        assertEquals(0, pn.getPlaces().get("p2").getTokens());
        assertEquals(0, pn.getPlaces().get("p3").getTokens());
        assertEquals(1, pn.getPlaces().get("p4").getTokens());
        assertEquals(1, pn.getPlaces().get("p5").getTokens());

        pn.getTransitions().get("t4").fire();

        assertTrue( ! pn.getTransitions().get("t1").canFire());
        assertTrue( ! pn.getTransitions().get("t2").canFire());
        assertTrue( ! pn.getTransitions().get("t3").canFire());
        assertTrue( ! pn.getTransitions().get("t4").canFire());

        assertEquals(0, pn.getPlaces().get("p1").getTokens());
        assertEquals(0, pn.getPlaces().get("p2").getTokens());
        assertEquals(0, pn.getPlaces().get("p3").getTokens());
        assertEquals(0, pn.getPlaces().get("p4").getTokens());
        assertEquals(0, pn.getPlaces().get("p5").getTokens());
    }
}
