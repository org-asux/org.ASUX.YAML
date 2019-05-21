/*
 BSD 3-Clause License
 
 Copyright (c) 2019, Udaybhaskar Sarma Seetamraju
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 
 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.
 
 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.
 
 * Neither the name of the copyright holder nor the names of its
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.ASUX.yaml;

import org.ASUX.common.Tuple;
import org.ASUX.common.Output;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 *  <p>This class is a bunch of tools to help make it easy to work with the java.util.Map objects that the YAML library creates.</p>
 *  <p>One example is the work around required when replacing the 'Key' - within the MACRO command Processor.</p>
 *  <p>If the key is already inside single or double-quotes.. then the replacement ends up as <code>'"newkeystring"'</code></p>
 */
public abstract class Tools implements java.io.Serializable, Cloneable {

    public static final String CLASSNAME = Tools.class.getName();

    /** <p>Whether you want deluge of debug-output onto System.out.</p><p>Set this via the constructor.</p>
     *  <p>It's read-only (final data-attribute).</p>
     */
    public final boolean verbose;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
      * <p>Utility class for use within the org.ASUX.yaml library only</p><p>one of 2 constructors - public/private/protected</p>
      * @param _verbose Whether you want deluge of debug-output onto System.out.
      */
    public Tools(boolean _verbose ) {
        this.verbose = _verbose;
    }


    /**
     * Allows this class to get the YAML-Library specific implementation details from the appropriate subclass of CmdInvoker
     * @param _cmdInvoker instance of org.ASUX.yaml.CmdInvoker - specifically its subclasses org.ASUX.yaml.CollectionsImpl.CmdInvoker / org.ASUX.yaml.NodeImpl.CmdInvoker
     */
    public abstract void setCmdInvoker( final CmdInvoker _cmdInvoker );

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** This function ensures the String form of JSON will work will all the YAML commands: read, list, replace, macro, ..
     *  I need such a function, as I learned the hard way that libraries do NOT work 100% well.  Only file-formats are the workaorund/ way out.
     *  I definitely "fgool-proof" method to ensure 'valid' YAML, for error-free processing by the entire org.ASUX.yaml library to work without any issues
     *  Note: Currently this function is identical to JSON2YAML()!
     *  @param _jsonStr java.lang.String object
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     * @throws com.fasterxml.jackson.core.JsonProcessingException if any error using Jackson library
     * @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     * @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public LinkedHashMap<String, Object>  JSONlintRemover( final String  _jsonStr )
                    throws com.fasterxml.jackson.core.JsonProcessingException,
                            java.io.IOException, Exception
    {
        return JSONString2YAML( _jsonStr );
    }

    //-----------------------------------------------------------------------------------------
    /** String output variant of JSONlintRemover(String).
     *  @param _jsonString java.lang.String object
     *  @return a String object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     * @throws com.fasterxml.jackson.core.JsonProcessingException if any error using Jackson library
     * @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     * @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public String  JSONlintRemoverString( final String  _jsonString )
                    throws com.fasterxml.jackson.core.JsonProcessingException,
                            java.io.IOException, Exception
    {
        final LinkedHashMap<String, Object> map = JSONString2YAML( _jsonString );
        return this.Map2JSONString(map);
    }

    //-----------------------------------------------------------------------------------------
    /** Convert an in-memory YAML into a JSON-compatible string.
     *  @param _map a java.util.LinkedHashMap&lt;String, Object&gt; object, as generated by Jackson http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-map-from-json-string 
     *  @return a String object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     * @throws com.fasterxml.jackson.core.JsonProcessingException if any error using Jackson library
     * @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     * @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public String Map2JSONString( final LinkedHashMap<String, Object>  _map )
                    throws com.fasterxml.jackson.core.JsonProcessingException,
                            java.io.IOException, Exception
    {
        com.fasterxml.jackson.databind.ObjectMapper objMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        // final java.io.StringWriter strwrtr4 = new java.io.StringWriter();
        // mapper.writeValue( strwrtr4, wellFormedJSONString );
        // strwrtr4.close();
        String s = objMapper.writeValueAsString(_map);
        s = s.replaceAll(":", ": ");
        return s;
    }


    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /** Takes any JSON input - as a LinkedHashmap obtained from any Library - and exports it as YAML (to java.util.String), and then reads it back as YAML.
     *  I need such a function, as I learned the hard way that libraries do NOT work 100% well.  Only file-formats are the workaorund/ way out.
     *  I definitely "fgool-proof" method to ensure 'valid' YAML, for error-free processing by the entire org.ASUX.yaml library to work without any issues
     *  @param _map a java.util.LinkedHashMap&lt;String, Object&gt; object, as generated by Jackson http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-map-from-json-string 
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     *  @throws Exception Any issue whatsoever when dealing with convering YAML/JSON content into Strings and back (as part of lintremoval)
     */
    public LinkedHashMap<String, Object> lintRemover( final LinkedHashMap<String, Object> _map ) throws Exception 
    {
        // First write it to java.lang.String object... then, read it back into YAML, using the YamlReder class

        // final java.io.StringWriter strwrtr7 = new java.io.StringWriter();
        try {
            // YamlWriter writer3 = new YamlWriter( strwrtr7 );
            // writer3.write( _json );
            // writer3.close();
            // if ( this.verbose ) System.out.println( CLASSNAME + ": JSON2YAML(): created new YAML-String\n" + strwrtr7.toString() +"\n" );

            final String s = Map2YAMLString( _map );
            return YAMLString2YAML( s, false ); // 2nd parameter is 'bWrapScalar' === false;.  's' cannot be a scalar at this point.  If it is, I want things to fail with null-pointer.

        } catch (java.io.IOException e) {
            if ( this.verbose ) e.printStackTrace(System.err);
            if ( this.verbose ) System.err.println( CLASSNAME + ": JSON2YAML(): Failure to read/write the contents of the String '" + _map.toString() +"'.");
            throw e;
        } catch (Exception e) {
            if ( this.verbose ) e.printStackTrace(System.err);
            if ( this.verbose ) System.err.println( CLASSNAME + ": JSON2YAML(): Unknown Internal error:.");
            throw e;
        }
        // return null;
    } // function

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     *  Takes any STRING-form JSON as input - it better be valid JSON - and reads it back as YAML/LinkedHashMap.
     *  I need such a function, as I learned the hard way that libraries do NOT work 100% well.  Only file-formats are the workaorund/ way out.
     *  I definitely "fgool-proof" method to ensure 'valid' YAML, for error-free processing by the entire org.ASUX.yaml library to work without any issues
     *  @param _jsonString a java.lang.String object
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     * @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     * @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public LinkedHashMap<String, Object>  JSONString2YAML( final String  _jsonString )
                    throws java.io.IOException, Exception
    {
        String wellFormedJSONString = _jsonString;

        if ( _jsonString.contains("=") && ! _jsonString.contains(":") ) {
            // WEll! it means the entire string in Key=Value format.   Not in proper Key:Value JSON format.
            wellFormedJSONString = _jsonString.replaceAll("=", ": "); // fingers crossed. I hope this works.
        } else {
            wellFormedJSONString = _jsonString.replaceAll(":", ": "); // Many libraries do NOT like  'key:value'.  They want a blank after colon like 'key: value'
        }
        if ( this.verbose ) System.out.println(">>>>>>>>>>>>>>>>>>>> "+ CLASSNAME+": JSONString2YAML(): "+ wellFormedJSONString);

        try {
            final java.io.Reader reader3 = new java.io.StringReader( wellFormedJSONString );
            // http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-map-from-json-string 
            // https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#readValue(java.io.Reader,%20java.lang.Class)
            com.fasterxml.jackson.databind.ObjectMapper objMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true );
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            com.fasterxml.jackson.databind.type.MapType type = objMapper.getTypeFactory().constructMapType( LinkedHashMap.class, String.class, Object.class );
            LinkedHashMap<String, Object> retMap2 = objMapper.readValue( reader3, new com.fasterxml.jackson.core.type.TypeReference< LinkedHashMap<String,Object> >(){}  );
            if ( this.verbose ) System.out.println( CLASSNAME +" JSONString2YAML("+ _jsonString +"): jsonMap loaded BY OBJECTMAPPER into a LinkedHashMao =" + retMap2 );
            retMap2 = this.lintRemover( retMap2 ); // this will 'clean/lint-remove'
            return retMap2;

        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            if (this.verbose) e.printStackTrace(System.err);
            if (this.verbose) System.err.println( CLASSNAME+": JSONString2YAML(): Failed to parse ["+ _jsonString +"] after converting to ["+ wellFormedJSONString +"]" );
            throw e;
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            if (this.verbose) e.printStackTrace(System.err);
            if (this.verbose) System.err.println( CLASSNAME+": JSONString2YAML(): Failed to parse ["+ _jsonString +"] after converting to ["+ wellFormedJSONString +"]" );
            throw e;
        } catch (java.io.IOException e) {
            if (this.verbose) e.printStackTrace(System.err);
            if (this.verbose) System.err.println( CLASSNAME+": JSONString2YAML(): Failed to parse ["+ _jsonString +"] after converting to ["+ wellFormedJSONString +"]" );
            throw e;
        }
    } // function

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================
    //-----------------------------------------------------------------------------------------
    /** Takes any YAML input - as a LinkedHashmap - and exports it as YAML-String (to java.util.String)
     *  @param _yaml a java.util.LinkedHashMap&lt;String, Object&gt; object, as generated by Jackson http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-map-from-json-string 
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     *  @throws Exception Any issue whatsoever when dealing with convering YAML/JSON content into Strings and back (as part of lintremoval)
     */
    public abstract String Map2YAMLString( final LinkedHashMap<String, Object> _yaml ) throws Exception;

    /**
     *  Takes any STRING-form JSON as input - it better be valid JSON - and reads it back as YAML/LinkedHashMap.
     *  I need such a function, as I learned the hard way that libraries do NOT work 100% well.  Only file-formats are the workaorund/ way out.
     *  I definitely "fgool-proof" method to ensure 'valid' YAML, for error-free processing by the entire org.ASUX.yaml library to work without any issues
     *  @param _yamlString a java.lang.String object
     *  @param _bWrapScalar true or false.  If the returne value is going to be a SCALAR, do you want it wrapped into a LinkedHashMap or throw instead?
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     * @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     * @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public abstract LinkedHashMap<String, Object>  YAMLString2YAML( final String  _yamlString, final boolean _bWrapScalar )
                    throws java.io.IOException, Exception;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

}
