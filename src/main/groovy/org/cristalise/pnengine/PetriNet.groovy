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
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j

import org.cristalise.pnengine.Arc.Direction


/**
 * The main class to create, manage and execute Petri Nets.
 */
@Slf4j
@CompileStatic
@TupleConstructor(includeFields=true)
class PetriNet {

    /**
     * Name of the PetriNet (optional)
     */
    String name

    int lastID         = 0
    int lastPlaceIndex = 1
    int lastTransIndex = 1
    int lastArcIndex   = 1

    /**
     * Map of Places based on their shortName (e.g. p1)
     */
    Map<String, Place> places = [:]
    
    /**
     * Map of Transitions based on their shortName (e.g. t1)
     */
    Map<String, Transition> transitions = [:]

    /**
     * Map of Arcs based on their shortName (e.g. p1t1)
     */
    Map<String, Arc> arcs = [:]

    /**
     * Temporary holder of PNObjects used by the connect() and to() methods implementing fluent API
     */
    PNObject cache = null


    public String printJson() { println new JsonBuilder(this).toPrettyString() }

    /**
     * Adds the PNObject to the correct list (places, transitions, arcs) using their short name
     * RuntimeException is thrown if the same PNObject was already added to the net with the same shortName.
     * 
     * @param pno the given PNObject
     */
    private void add(PNObject pno) {
        log.debug "add() $pno"

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
     * Computes the list of Transitions which can be fired
     * 
     * @return the list of Transitions which can be fired
     */
    public List<Transition> listOfTransitionsAbleToFire() {
        return (List<Transition>)transitions.values().findAll { it.canFire() };
    }

    /**
     * Factory method of Transition, it also adds the Transition to the PetriNet. 
     * RuntimeException is thrown if the Transition was already added to the net with the same name
     * 
     * @param name
     * @return the new Transition
     */
    public Transition transition(String name) {
        //This check is a small trick to make sure that calling transition('t1') twice will fail
        if(transitions.containsKey(name)) throw new RuntimeException("Transition '$name' already exists")

        Transition t = new Transition(parent: this, name: name, index: lastTransIndex++, ID: lastID++);
        add(t);
        return t;
    }

    /**
     * Factory method of Place, it also adds the Place to the PetriNet.
     * RuntimeException is thrown if the Place was already added to the net with the same name
     * 
     * @param name
     * @return the new Place
     */
    public Place place(String name, int initial = 0) {
        //This check is a small trick to make sure that calling place('t1') twice will fail
        if(places.containsKey(name)) throw new RuntimeException("Place '$name' already exists")

        Place p = new Place(parent: this, name: name, tokens: initial, index: lastPlaceIndex++, ID: lastID++);
        add(p);
        return p;
    }


    /**
     * Factory method of Arc, it also adds the Arc to the PetriNet. This should only be called connect() and to() methods.
     * 
     * @param name the name of the Arc
     * @param pIndex the index of the Place
     * @param tIndex the index of the Transition
     * @param weight the weight of the Arc (how many tokens it consumes)
     * @param direction the Arc has direction : Place2Trans or Trans2Place
     * @return the newly created Arc
     */
    private Arc arc(String name, int pIndex, int tIndex, int weight, Direction dir) {
        Arc a = new Arc(parent: this, name: name, placeIndex: pIndex, transIndex: tIndex, weight: weight, 
                        direction: dir, index: lastArcIndex++, ID: lastID++);
        add(a);
        return a;
    }

    /**
     * Connects the given Place to the given Transition using the given weight. It adds the new
     * Arc to the PetriNet. The Arc shortName will be the concatenated shortNames of Place and Transition.
     * Arc is added to the incoming list of Transition.
     * RuntimeException is thrown if the same Arc was already added to the net.
     * 
     * @param p Place from 
     * @param t Transition to
     * @param w weight of the Arc (defaults to 1)
     * @return the new Arc
     */
    public Arc connect(Place p, Transition t, int weight = 1) {
        Arc a = arc(p.name+t.name, p.index, t.index, weight, Direction.Place2Trans);
        t.addIncoming(a);
        return a;
    }

    /**
     * Connects the given Transition to the given Place using the given weight. It adds the new
     * Arc to the PetriNet. The Arc shortName will be the concatenated shortNames of Transition and Place.
     * RuntimeException is thrown if the same Arc was already added to the net.
     * Arc is added to the outgoing list of Transition.
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
     * DSL method: Sets up a closure to be executed in the context of the PetriNet
     * 
     * @param cl the closure containing the instructions
     */
    public void exec(Closure cl) {
        cl.delegate = this
        cl()
    }
    
    /**
     * DSL method: Builder method to create the PetriNet from a CVS like structure.
     * 
     * @param name the name of the PN
     * @param pnList the list containing the PN description
     * @param cl the closure containing the instructions
     * @return the new PetriNet
     */
    public static PetriNet builder(String name, List pnList, Closure cl = null) {
        PetriNet pn = new PetriNet(name: name)

        pnList.each {
            if(it instanceof List<Map<String,String>>) {
                def row = (List<Map<String,String>>)it
                pn.connect(row[0]).to(row[1])
            }
            else if(it instanceof Map<String,String>) {
                def row = (Map<String,String>)it
                def d = Direction.valueOf(row.direction)
                switch(d) {
                    case Direction.Trans2Place:
                    case Direction.T2P:
                        pn.connect(transition: row.transition).to(place: row.place)
                        break
                    case Direction.Place2Trans:
                    case Direction.P2T:
                        pn.connect(place: row.place).to(transition: row.transition)
                        break
                }
            }
            else {
                throw new RuntimeException("${it} is unknow")
            }
        }

        if(cl) {
            cl.delegate = pn
            cl()
        }

        return pn
    }

    /**
     * DSL method: Sets up the Closure to create and fire the new PetriNet
     * 
     * @param name the name of the PN
     * @param cl the closure containing the instructions
     * @return the new PetriNet
     */
    public static PetriNet builder(String name, Closure cl) {
        def pn = new PetriNet(name: name)

        if(cl) {
            cl.delegate = pn
            cl()
        }

        return pn
    }

    /**
     * DSL method: Adds a Place/Transition to the PetriNet based on the naming convention of shortNames.
     * If the Place/Transition was already exist, it only adds to an internal cache, so the subsequent to()
     * method call will be able to use it.
     * 
     * @param shortName the shortName of the PNObject it must start with 't' or 'p'
     * @return the PetriNet (fluent API)
     */
    public PetriNet connect(String shortName) {
        if     (shortName.startsWith('p')) return connect(place:      shortName)
        else if(shortName.startsWith('t')) return connect(transition: shortName)
        else                               throw new RuntimeException("Incorrect shortName '$shortName'! It must start with 't' or 'p'");
    }

    /**
     * DSL method: Adds a Place/Transition to the PetriNet based on groovy's map notation.
     * If the Place/Transition was already exist, it only adds to an internal cache, so the subsequent to()
     * method call will be able to use it.
     * 
     * @param pno
     * @return the PetriNet (fluent API)
     */
    public PetriNet connect(Map<String, String> pno) {
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
     * DSL method: Based on an existing Place object. It only adds it to an internal cache, so the subsequent to()
     * method call will be able to use it.
     * 
     * @param p
     * @return the PetriNet (fluent API)
     */
    public PetriNet connect(Place p) {
        log.trace "Adding to cache - $p"
        cache = p
        return this
    }

    /**
     * DSL method: Based on an existing Transition object. It only adds it to an internal cache, so the subsequent to()
     * method call will be able to use it.
     * 
     * @param t
     * @return the PetriNet (fluent API)
     */
    public PetriNet connect(Transition t) {
        log.trace "Adding to cache - $t"
        cache = t
        return this
    }

    /**
     * DSL method: should be used after connect() 
     * 
     * @param shortName the shortName of the PNObject it must start with 't' or 'p'
     * @return the Arc (fluent API)
     */
    public Arc to(String shortName) {
        if     (shortName.startsWith('p')) return to(place: shortName)
        else if(shortName.startsWith('t')) return to(transition: shortName)
        else                               throw new RuntimeException("ShortName '$shortName' must start with 't' or 'p'");
    }

    /**
     * DSL method: should be used after connect() 
     * 
     * @param p
     * @return the Arc (fluent API)
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
     * @return the Arc (fluent API)
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
     * @return the Arc (fluent API)
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
