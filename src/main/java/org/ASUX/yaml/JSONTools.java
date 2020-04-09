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

// import com.fasterxml.jackson.core.JsonParseException;
// import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.LinkedHashMap;

import static org.junit.Assert.fail;

/**
 *  <p>This class is a bunch of tools to help make it easy to work with the java.util.Map objects that the YAML library creates.</p>
 *  <p>One example is the work around required when replacing the 'Key' - within the MACRO command Processor.</p>
 *  <p>If the key is already inside single or double-quotes.. then the replacement ends up as <code>'"newkeystring"'</code></p>
 */
public abstract class JSONTools implements java.io.Serializable, Cloneable {

    private static final long serialVersionUID = 122L;

    public static final String CLASSNAME = JSONTools.class.getName();

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
    public JSONTools(boolean _verbose ) {
        this.verbose = _verbose;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     *  Takes any STRING-form JSON as input - it better be valid JSON - and reads it back as YAML/LinkedHashMap.
     *  I need such a function, as I learned the hard way that libraries do NOT work 100% well.  Only file-formats are the workaorund/ way out.
     *  I definitely "fgool-proof" method to ensure 'valid' YAML, for error-free processing by the entire org.ASUX.yaml library to work without any issues
     *  @param _verbose Whether you want deluge of debug-output onto System.out.
     *  @param _jsonString a java.lang.String object
     *  @return a java.util.LinkedHashMap&lt;String, Object&gt; object that's definitely "kosher" for the entire org.ASUX.yaml library to work without any issues
     *  @throws java.io.IOException if any error using java.io.StringReader and java.io.StringWriter
     *  @throws Exception any other run-time exception, while parsing large Strings, nullpointers, etc.. ..
     */
    public static LinkedHashMap<String, Object>  JSONString2Map(final boolean _verbose, final String  _jsonString )
                    throws java.io.IOException, Exception
    {
        // We're going to alter the contents of '_jsonString', even as we need the original '_jsonString' as-is value for debug-statements and error-messages.
        String wellFormedJSONString = _jsonString.trim();

        // If the JSON-string has any beginning and ending quote-characters.. remove them
        if ( wellFormedJSONString.matches( "^['\"]\\s*\\{.+" ) )       // trim() invoked above ensures no white-space before the quote character
            wellFormedJSONString = wellFormedJSONString.substring( 1 );
        if ( wellFormedJSONString.matches( ".+\\}\\s*['\"]$" ) )       // trim() invoked above ensures no white-space AFTER the quote character
            wellFormedJSONString = wellFormedJSONString.substring( 0, wellFormedJSONString.length() - 1 );

        if ( wellFormedJSONString.contains("=") && ! wellFormedJSONString.contains(":") ) {
            // WEll! it means the entire string in Key=Value format.   Not in proper Key:Value JSON format.
            wellFormedJSONString = wellFormedJSONString.replaceAll("=", ": "); // fingers crossed. I hope this works.
        } else {
            wellFormedJSONString = wellFormedJSONString.replaceAll(":", ": "); // Many libraries do NOT like  'key:value'.  They want a blank after colon like 'key: value'
        }

        if ( _verbose ) System.out.println(">>>>>>>>>>>>>>>>>>>> "+ CLASSNAME+": JSONString2Map(): "+ wellFormedJSONString);

        try {
            final java.io.Reader reader3 = new java.io.StringReader( wellFormedJSONString );
            // http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#read-map-from-json-string 
            // https://fasterxml.github.io/jackson-databind/javadoc/2.7/com/fasterxml/jackson/databind/ObjectMapper.html#readValue(java.io.Reader,%20java.lang.Class)
            com.fasterxml.jackson.databind.ObjectMapper objMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true );
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            /* com.fasterxml.jackson.databind.type.MapType type   = */
                objMapper.getTypeFactory().constructMapType( LinkedHashMap.class, String.class, Object.class );
            LinkedHashMap<String, Object> retMap2 = objMapper.readValue( reader3, new com.fasterxml.jackson.core.type.TypeReference< LinkedHashMap<String,Object> >(){}  );
            if ( _verbose ) System.out.println( CLASSNAME +" JSONString2Map("+ _jsonString +"): jsonMap loaded BY OBJECTMAPPER into a LinkedHashMao =" + retMap2 );
            // retMap2 = this.lintRemoverMap( retMap2 ); // this will 'clean/lint-remove'
            return retMap2;

        // FYI: JsonParseException & JsonMappingException are both Sub-classes of java.io.IOException
        // } catch ( JsonParseException | JsonMappingException e) {
        //     if (_verbose) e.printStackTrace(System.out);
        //     if (_verbose) System.out.println( CLASSNAME+": JSONString2Map(): Failed to parse ["+ _jsonString +"] after converting to ["+ wellFormedJSONString +"]" );
        //     throw e;

        } catch (java.io.IOException e) {
            if (_verbose) e.printStackTrace(System.out);
            if (_verbose) System.out.println( CLASSNAME+": JSONString2Map(): Failed to parse ["+ _jsonString +"] after converting to ["+ wellFormedJSONString +"]" );
            throw e;
        }
    } // function

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    /**
     * <p>Method to ingest JSON and convert it into an object of type {@link CmdLineArgsCommon} or it's subclasses.</p>
     * <p>In addition to the command-line UI for an user to process YAML, this offers a JS/Node.js interface that relies on JSON as input (instead of String).</p>
     * @param _verbose Whether you want deluge of debug-output onto System.out.
     * @param _jsonAsString NotNull String containing _VALID_ JSON.  See ASUX.org WIKI for full details.
     * @throws Exception If any errors parsing the contents of the JSON provided as argument
     */
    public static CmdLineArgsCommon toCmdLineArgs( final boolean _verbose, String _jsonAsString ) throws Exception
    {   final String HDR = CLASSNAME +": toCmdLineArgs(_verbose, _jsonAsString): ";
        if ( _verbose ) System.out.print( HDR + "_jsonAsString = ["+ _jsonAsString +"]" );

        // Step 1: Read the JSON String into a java.util.Map object.  Leverage other methods in this class
        // Step 2: Do some sanity checks on the JSON.  More importantly, figure out what the YAML command is.. so the appropriate Subclass of CmdLineArgsCommon can be created.
        // Step 3: Re-Read the JSON String into the appropriate subclass of CmdLineArgsCommon

        try {

            // Step 1
            // String testJson = "{\n" + "  \"user\": {\n" + "    \"0\": {\n" + "      \"firstName\": \"Monica\",\n" + "      \"lastName\": \"Belluci\"\n" + "    },\n" + "    \"1\": {\n" + "      \"firstName\": \"John\",\n" + "      \"lastName\": \"Smith\"\n" + "    },\n" + "    \"2\": {\n" + "      \"firstName\": \"Owen\",\n" + "      \"lastName\": \"Hargreaves\"\n" + "    }\n" + "  }\n" + "}";
            final LinkedHashMap<String, Object> jsonMap = JSONString2Map( _verbose, _jsonAsString );

            // Step 2
            final Object valObj = jsonMap.get( "cmdType" );
            String errMsg = "ERROR: Missing or invalid 'cmdType' within JSON provided!  Found ["+ valObj +"] within:\n" + _jsonAsString + "";

            if ( !( valObj instanceof String ) ) {
                System.err.println( errMsg );
                throw new Exception( errMsg );
            }
            final String cmdTypeStr = (String) jsonMap.get( "cmdType" ); // guaranteed to succeed because of above IF statement.
            Enums.CmdEnum cmdType = Enums.CmdEnum.fromString( cmdTypeStr );
            if ( cmdType == Enums.CmdEnum.UNKNOWN ) {
                System.err.println( errMsg );
                throw new Exception( errMsg );
            }

            // Step 3  (see comments at the top of this method)
            com.fasterxml.jackson.databind.ObjectMapper objMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true );
            objMapper.configure( com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            objMapper.enable( com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS );
            CmdLineArgsCommon cmd = null;
            switch( cmdType ) {
                case READ:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsReadCmd.class );
                    break;
                case LIST:
                case DELETE:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsRegExp.class );
                    break;
                case INSERT:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsInsertCmd.class );
                    break;
                case REPLACE:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsReplaceCmd.class );
                    break;
                case TABLE:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsTableCmd.class );
                    break;
                case MACRO:
                case MACROYAML:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsMacroCmd.class );
                    break;
                case BATCH:
                    cmd = objMapper.readValue( _jsonAsString, CmdLineArgsBatchCmd.class );
                    break;
                default: fail();
            }

            if ( _verbose ) System.out.print( HDR + "Cmd = ["+ cmd +"]" );
            return cmd;

        } catch (java.io.IOException e) {
            if (_verbose) e.printStackTrace(System.out);
            System.err.println( CLASSNAME+": JSONString2Map(): Failed to parse ["+ _jsonAsString +"]" );
            throw e;
        }
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // public static void main( String[] args) {
    //     // see JSONInterface.java
    // }

}
