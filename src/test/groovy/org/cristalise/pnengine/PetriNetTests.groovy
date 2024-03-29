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

import groovy.transform.CompileStatic


/**
 *
 */
@CompileStatic
class PetriNetTests {
    @Test
    public void originalTest() {
        def pn = new PetriNet(name: "Test");

        def t1 = pn.transition("t1");
        def t2 = pn.transition("t2");

        def p1 = pn.place("p1", 2);
        def p2 = pn.place("p2");
        def p3 = pn.place("p3");

        pn.connect(p1, t1);
        pn.connect(t1, p2);
        pn.connect(p2, t2);
        pn.connect(t2, p3);

        pn.arcs.t1p2.weight = 2;
        
        pn.printJson()

        assertTrue(p1.hasEnoughTokens(1));
        assertFalse(p1.maxTokensReached(1));
        assertFalse(p2.hasEnoughTokens(1));
        assertFalse(p1.maxTokensReached(1));

        assertTrue(t1.canFire());
        assertFalse(t2.canFire());

        t1.fire();

        assertEquals(1, p1.tokens);
        assertEquals(2, p2.tokens);

        assertTrue(t1.canFire());
        assertTrue(t2.canFire());

        t1.fire();

        assertEquals(0, p1.tokens);
        assertEquals(4, p2.tokens);

        assertFalse(t1.canFire());
        assertTrue(t2.canFire());

        t2.fire();

        assertEquals(0, p1.tokens);
        assertEquals(3, p2.tokens);

        assertFalse(t1.canFire());
        assertTrue(t2.canFire());

        t2.fire();

        assertEquals(0, p1.tokens);
        assertEquals(2, p2.tokens);
        
        pn.arcs.p2t2.weight = 2;
        t2.fire();
        
        assertEquals(0, p1.tokens);
        assertEquals(0, p2.tokens);

        assertFalse(t1.canFire());
        assertFalse(t2.canFire());
    }
}
