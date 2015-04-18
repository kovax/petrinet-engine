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

import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import org.cristalise.pnengine.Arc.Direction

/**
 * 
 */
@Slf4j
@CompileStatic
class PetriNet {

    String name

    int lastID = 0
    
    int lastPlaceIndex = 1
    int lastTransIndex = 1
    int lastArcIndex = 1

    Map<String, Place>      places      = [:]
    Map<String, Transition> transitions = [:]
    Map<String, Arc>        arcs        = [:]

    PNObject cache = null

    public String printJson() { println new JsonBuilder(this).toPrettyString() }

    /**
     * 
     * @param pno
     */
    private void add(PNObject pno) {
        log.debug "adding $pno"
        if(pno instanceof Place) {
            if(places.containsKey(pno.shortName())) throw new RuntimeException("Place '${pno.shortName()}' already exists")
            places[pno.shortName()] = (Place)pno
        }
        else if(pno instanceof Transition) {
            if(transitions.containsKey(pno.shortName())) throw new RuntimeException("Transition '${pno.shortName()}' already exists")
            transitions[pno.shortName()] = (Transition)pno
        }
        else if(pno instanceof Arc) {
            if(arcs.containsKey(pno.shortName())) throw new RuntimeException("Arc '${pno.shortName()}' already exists")
            arcs[pno.shortName()] = (Arc)pno
        }
    }

    /**
     * 
     * @return
     */
    public List<Transition> listOfTransitionsAbleToFire() {
        return transitions.values().collect { it.canFire() };
    }

    /**
     * Factory method of Transition
     * 
     * @param name
     * @return
     */
    public Transition transition(String name) {
        if(transitions.containsKey(name)) throw new RuntimeException("Transition '$name' already exists")

        Transition t = new Transition(parent: this, name: name, index: lastTransIndex++, ID: lastID++);
        add(t);
        return t;
    }

    /**
     * Factory method of Place
     * 
     * @param name
     * @return
     */
    public Place place(String name, int initial = 0) {
        if(places.containsKey(name)) throw new RuntimeException("Place '$name' already exists")

        Place p = new Place(parent: this, name: name, tokens: initial, index: lastPlaceIndex++, ID: lastID++);
        add(p);
        return p;
    }

    /**
     * Factory method of Arc
     * 
     * @param name
     * @param pIndex
     * @param tIndex
     * @param weight
     * @param direction
     * @return
     */
    public Arc arc(String name, int pIndex, int tIndex, int weight, Direction dir) {
        Arc a = new Arc(parent: this, name: name, placeIndex: pIndex, transIndex: tIndex, weight: weight, 
                        direction: dir, index: lastArcIndex++, ID: lastID++);
        add(a);
        return a;
    }

    /**
     * 
     * @param p Place from 
     * @param t Transition to
     * @param w weight of the Arc
     * @return the new Arc
     */
    public Arc connect(Place p, Transition t, int weight = 1) {
        Arc a = arc(p.name+t.name, p.index, t.index, weight, Direction.Place2Trans);
        t.addIncoming(a);
        return a;
    }

    /**
     * 
     * @param t Transition from
     * @param p Place to
     * @param w weight of the Arc
     * @return the new Arc
     */
    public Arc connect(Transition t, Place p, int weight = 1) {
        Arc a = arc(t.name+p.name, p.index, t.index, weight, Direction.Trans2Place);
        t.addOutgoing(a);
        return a;
    }
    
    /**
     * DSL method: 
     * 
     * @param cl
     */
    public void exec(Closure cl) {
        cl.delegate = this
        cl()
    }
    
    /**
     * DSL method: 
     * 
     * @param name
     * @param pnList
     * @return
     */
    public static PetriNet builder(String name, List pnList, Closure cl = null) {
        PetriNet pn = new PetriNet(name: name)
        
        pnList.each {
            if(it instanceof List<Map<String,String>>) {
                def row = (List<Map<String,String>>)it
                pn.connect row[0] to row[1]
            }
            else if(it instanceof Map<String,String>) {
                def row = (Map<String,String>)it
                def d = Direction.valueOf(row.direction)
                switch(d) {
                    case Direction.Trans2Place:
                    case Direction.T2P:
                        pn.connect transition: row.transition to place: row.place
                        break
                    case Direction.Place2Trans:
                    case Direction.P2T:
                        pn.connect place: row.place to transition: row.transition
                        break
                }
            }
        }

        if(cl) cl()

        return pn
    }

    /**
     * DSL method: Setup the Closure to create and fire a PN as 
     * 
     * @param name
     * @param cl
     * @return
     */
    public static PetriNet builder(String name, Closure cl) {
        def pn = new PetriNet(name: name)

        cl.delegate = pn
        cl()
        
        return pn
    }

    /**
     * DSL method: it should be used before to()
     * 
     * @param p
     * @return
     */
    public PetriNet connect(Map<String,String> pno) {
        if(pno.transition) {
            if(transitions.containsKey(pno.transition)) return connect(transitions[pno.transition])
            else                                        return connect(transition(pno.transition))
        }
        else if(pno.place) {
            if(places.containsKey(pno.place)) return connect(places[pno.place])
            else                              return connect(place(pno.place))
        }
        throw new RuntimeException("${pno} is unknow")
    }

    /**
     * DSL method: it should be used before to() 
     * 
     * @param p
     * @return
     */
    public PetriNet connect(Place p) {
        log.debug "Adding to cache - $p"
        cache = p
        return this
    }

    /**
     * DSL method: should be used before to() 
     * 
     * @param t
     * @return
     */
    public PetriNet connect(Transition t) {
        log.debug "Adding to cache - $t"
        cache = t
        return this
    }

    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Map<String,String> pno) {
        if(pno.transition) {
            if(transitions.containsKey(pno.transition)) return to(transitions[pno.transition])
            else                                        return to(transition(pno.transition))
        }
        else if(pno.place) {
            if(places.containsKey(pno.place)) return to(places[pno.place])
            else                              return to(place(pno.place))
        }
        throw new RuntimeException("${pno} is unknow")
    }

    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Transition t) {
        if(cache != null && cache instanceof Place) {
            Arc a = connect((Place)cache, t)
            cache = null
            return a
        }
        else {
            cache = null
            throw new RuntimeException("Call connect(Place) before caling to(Transition)")
        }
    }

    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return
     */
    public Arc to(Place p) {
        if(cache != null && cache instanceof Transition) {
            Arc a = connect((Transition)cache, p)
            cache = null
            return a
        }
        else {
            cache = null
            throw new RuntimeException("Call connect(Transition) before calling to(Place)")
        }
    }
}
