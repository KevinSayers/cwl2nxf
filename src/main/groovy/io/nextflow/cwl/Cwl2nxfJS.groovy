package io.nextflow.cwl
/*
 * Copyright (c) 2013-2017, Centre for Genomic Regulation (CRG).
 * Copyright (c) 2013-2017, Paolo Di Tommaso and the respective authors.
 *
 *   This file is part of 'Nextflow'.
 *
 *   Nextflow is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Nextflow is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Nextflow.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @author Kevin Sayers <sayerskt@gmail.com>
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager
import javax.script.*

class Cwl2nxfJS {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("nashorn");

    SimpleBindings bindings = new SimpleBindings(['runtime':['coresMin': 1,'coresMax': 1,'ramMin': 1024,
                                                             'ramMax': 1024,'tmpdirMin': 1024,'tmpdirMax': 1024,
                                                             'outdirMin': 1024,'outdirMax': 1024,'outdir':'./',
                                                             'cores':1, 'ram':1]])
    Cwl2nxfJS(){

    }
    def evaluateJS(String jsString){
        return this.engine.eval(jsString, this.bindings)
    }
    def setJS(Map map){
        this.bindings.putAll(map)
    }
    def checkForJSPattern(String jsString){
        def jsRegex = (jsString =~ /^\$\((.*?)\)$/)
        return jsRegex.matches()
    }
    def evaluateJSExpression(String jsString){
        def jsRegex = (jsString =~ /^\$\((.*?)\)$/)
        jsRegex.matches()
        return evaluateJS(jsRegex.group(1))

    }
    static void main(String[] args) {
        def test = new Cwl2nxfJS()
        Map testmap = [:]

        test.setJS(['runtime':['coresMin': 3]])
        String testreg = 'sentinel_runtime=cores,$(runtime[\'cores\']),ram,$(runtime[\'ram\'])'
        def jsRegex = (testreg =~ /\$\((.*?)\)/).findAll()
        testmap[jsRegex[0][0]] = jsRegex[0][1]
        testmap[jsRegex[1][0]] = jsRegex[1][1]
        testmap.keySet().each{
            String newstr = testmap[it].replace("['",'.').replace("']",'')
            println(newstr)
        }
        println(test.evaluateJS('runtime.outdir'))
/*
        println(test.evaluateJSExpression(testreg))
*/
/*        def jsRegex = (testreg =~ /^\$\((.*?)\)$/)
        println jsRegex.matches()
        println jsRegex.group(1)*/




    }
}
