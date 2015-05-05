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

import org.la4j.Matrix
import org.la4j.Vector
import org.la4j.matrix.dense.Basic2DMatrix
import org.la4j.vector.dense.BasicVector


/**
 * Class to create, manage and execute Petri Nets based on matrix algebra
 * 
 * Implementation is INCOMPLETE, canFire() does not work for individual Transition (check tests).
 */
@Slf4j
@CompileStatic
class PNMatrix {
    
    /**
     * <pre>
     * Transition input matrix a.k.a. Precondition matrix
     * 
     * It is an m x n (m rows, n columns) matrix, where 
     *  - m is the number of Transitions and
     *  - n is the number of Places in the PetriNet
     *
     * For each position [i,j]
     *  - place 1 if Transition i has input from Place j or 
     *  - place 0 if Transition i does not have input from Place j.
     * </pre>
     */
    private Matrix Dplus = null
    
    /**
     * <pre>
     * Transition output matrix a.k.a. Postcondition matrix
     * 
     * It is an m x n (m rows, n columns) matrix, where 
     *  - m is the number of Transitions and
     *  - n is the number of Places in the PetriNet. 
     * 
     * For each position [i,j] 
     *  - place 1 if Transition i has output from Place j or 
     *  - place 0 if Transition i does not have output from Place j. 
     * </pre>
     */
    private Matrix Dminus = null

    /**
     * Composite change matrix. 
     * 
     * It is computed by subtracting Dminus from Dplus ->D = (D+) - (D-).
     */
    private Matrix D = null

    /**
     * n size Vector holding the current number of tokens of each Place
     */
    private Vector currentMarking = null

    /**
     * Converts groovy array literal type to type required by la4j
     * 
     * @param markings
     */
    public void setDplus(List<List<Integer>> markings) {
        Dplus = new Basic2DMatrix((double[][])markings.toArray())
    }

    /**
     * Converts groovy array literal type to type required by la4j
     * 
     * @param markings
     */
    public void setDminus(List<List<Integer>> markings) {
        Dminus = new Basic2DMatrix((double[][])markings.toArray())
    }

    /**
     * Converts groovy array literal type to type required by la4j
     * 
     * @param markings
     */
    public void setCurrentMarking(List<Integer> markings) {
        currentMarking = new BasicVector((double[])markings.toArray())
    }

    /**
     * Converts la4j Vector compatible groovy array/List type
     * 
     * @param markings
     */
    public List<Integer> getCurrentMarking() {
        return (List<Integer>)currentMarking.toList()
    }

    /**
     * Converts la4j Vector compatible groovy array/List type
     *
     * @param markings
     */
    public List<List<Integer>> getD() {
        return (List<List<Integer>>)D.toList()
    }

    /**
     * Computes the Composite matrix
     */
    public void computeComposite() {
        assert Dplus && Dminus

        D = Dplus.subtract(Dminus)
    }

    /**
     * Computes the new marking using this formula: [transitions]*[D] + [currentMarking] 
     * 
     * @param trans the list of transitions to be fired
     * @return the new marking
     */
    private Vector computeMarking(Vector trans) {
        if(!D) computeComposite()

        Vector m = trans.multiply(D).add(currentMarking)

        log.debug("Computed marking: "+(List<Integer>)m.toList())

        return m
    }

    /**
     * Fires an individual transition
     * 
     * @param i the index of the transition to be fires
     * @return if the transition was fired or not
     */
    public boolean fire(int i) {
        if(!D) computeComposite()
        
        if(i >= 0 && D.rows() >= i) {
            def trans = new int[D.rows()]
            trans[i] = 1
            return fire(trans)
        }
        else {
            throw new RuntimeException("Transition index out of bound $i")
        }
    }

    /**
     * Groovy list literal type compliant version of fire() method
     * 
     * @param trans 
     * @return if the transition was fired or not
     */
    public boolean fire(List<Integer> trans) {
        fire((int[])trans.toArray())
    }

    /**
     * Fires the given list of transitions
     * 
     * @param trans the list of transition to be fired
     * @return true if fire has created a new marking
     */
    public boolean fire(int[] trans) {
        if(!D) computeComposite()
        
        log.debug("fire() - Transition vector: $trans")

        Vector newMarking = computeMarking(new BasicVector(trans))

        if (newMarking.equals(currentMarking, 0.000)) {
            log.warn("$trans did not fire")
            return false
        }
        else {
            currentMarking = newMarking
            return true
        }
    }

    /**
     * Check if an individual transition can fire or not
     * 
     * @param i the index of the Transition in the rows of D
     * @return if the transition can fire or not
     */
    public boolean canFire(int i) {
        if(!D) computeComposite()

        if(i >= 0 && D.rows() >= i) {
            def trans = new int[D.rows()]
            trans[i] = 1
            return canFire(trans)
        }
        else {
            throw new RuntimeException("Transition index out of bound $i")
        }
    }


    /**
     * Groovy list literal type compliant version of fire() method
     * 
     * @param trans
     * @return
     */
    public boolean canFire(List<Integer> trans) {
        canFire((int[])trans.toArray())
    }


    /**
     * Check if the given list of transitions can fire or not
     * 
     * @param trans the list of indices of the Transitions in the rows of D
     * @return
     */
    public boolean canFire(int[] trans) {
        log.debug("canFire() - Transition vector: $trans")
        return ! computeMarking(new BasicVector(trans)).equals(currentMarking, 0.0)
    }
}
