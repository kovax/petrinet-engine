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
 * Test is based on this webpage:
 * http://www.techfak.uni-bielefeld.de/~mchen/BioPNML/Intro/MRPN.html
 */
@CompileStatic
class PNMatrixTests {
    PNMatrix pnM = new PNMatrix()

    /**
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

                     //p1,p2,p3,p4,p5
        pnM.Dminus = [[0, 0, 0, 0, 1], //t1
                      [1, 0, 0, 0, 0], //t2
                      [0, 1, 0, 0, 0], //t3
                      [0, 0, 1, 1, 0]] //t4

        pnM.Dplus = [[1,1,0,0,0],
                     [0,0,1,1,0],
                     [0,0,0,1,0],
                     [0,0,0,0,1]]

        pnM.currentMarking = [2,1,0,0,0]
    }

    @Test
    public void fullFiring() {
        assert pnM.fire([0,1,1,0])
        assert pnM.currentMarking == [1,0,1,2,0]

        assert pnM.fire([0,1,0,1])
        assert pnM.currentMarking == [0,0,1,2,1]

        assert pnM.fire([1,0,0,1])
        assert pnM.currentMarking == [1,1,0,1,1]

        assert pnM.fire([1,1,1,0])
        assert pnM.currentMarking == [1,1,1,3,0]
    }

    @Test
    public void test() {
        assert ! pnM.canFire(0)
        assert   pnM.canFire(1)
        assert   pnM.canFire(2)
        assert ! pnM.canFire(3)
    }
}
