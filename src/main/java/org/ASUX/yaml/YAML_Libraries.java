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

import static org.junit.Assert.*;

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
/**
 *  <p>This enum class is a bit extensive, only because the ENNUMERATED VALUEs are strings.</p>
 *  <p>For variations - see https://stackoverflow.com/questions/3978654/best-way-to-create-enum-of-strings</p>
 *  <p>Valid values are: </p>
 *  <ul>
    <li>ESOTERICSOFTWARE_Library ("com.esotericsoftware.yamlbeans")</li>
    <li>ORGSNAKEYAML_Library ("org.yaml.snakeyaml")</li>
    <li>SNAKEYAML_Library ("SnakeYAML")</li>
    <li>CollectionsImpl_Library ("CollectionsImpl")</li>
    <li>NodeImpl_Library ("NodeImpl")</li>
    <li>ASUXYAML_Library ("org.ASUX.yaml")</li>
 *  </ul>
 */
public enum YAML_Libraries
{
    ESOTERICSOFTWARE_Library ("com.esotericsoftware.yamlbeans"),
    SNAKEYAML_Library ("SnakeYAML"),
    ORGSNAKEYAML_Library ("org.yaml.snakeyaml"),
    CollectionsImpl_Library ("CollectionsImpl"),
    NodeImpl_Library ("NodeImpl"),
    ASUXYAML_Library ("org.ASUX.yaml"),
    UNDEFINED("undefined")
    ;

    //============================================================
    private static final String CLASSNAME = YAML_Libraries.class.getName();

    private final String internalValue;

    //============================================================
    /** <p>This constructor is private.  the right way to create new objects of this enum are via the {@link #fromString}.</p>
     * <p>For Enums based on strings, you need a constructor like this.  Only reason: To save the parameter as an internal variable.</p>
     * <p>Per Java language spec, this constructor is private (as I understand it)</p>
     * @param _i
     */
    private YAML_Libraries(final String _i) {
        this.internalValue = _i;
    }
    /* (non-Javadoc)
        * @see java.lang.Enum#toString()
        */
    @Override
    public String toString() {
        return this.internalValue;
    }

    //============================================================
    /**
     * Given a string, this method will help convert the string into the standard ENUM values of this class.  If, invalid input.. an exception is thrown.
     * @param type a string value that should be one of com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml
     * @return a valid ENUM value of this class
     * @throws Exception if string parameter is invalid
     */
    public static YAML_Libraries fromString(String type) throws Exception {
        for (YAML_Libraries typeitem : YAML_Libraries.values()) {
            if (typeitem.toString().equals(type)) {
                if ( typeitem == ESOTERICSOFTWARE_Library ) return CollectionsImpl_Library;
                if ( typeitem == SNAKEYAML_Library ) return NodeImpl_Library;
                if ( typeitem == ORGSNAKEYAML_Library ) return NodeImpl_Library;
                return typeitem;
            }
        }
        throw new Exception ( CLASSNAME + ": fromString("+ type +"): should be one of the values: "+ list("\t") );
        // return YAML_Libraries.SNAKEYAML_Library; // Default.. or you can throw exception
    }

    //============================================================
    /**
     * Whether the YAML-Library is an implementation based on java.util Collections.  If not, it's a SnakeYaml's Node-based implementtion.
     * @param _yl one the ENUM values of this class
     * @return ( _yl == ESOTERICSOFTWARE_Library || _yl == CollectionsImpl_Library )
     */
    public static boolean isCollectionsImpl( final YAML_Libraries _yl ) {
        return ( _yl == ESOTERICSOFTWARE_Library || _yl == CollectionsImpl_Library );
    }

    /**
     * Whether the YAML-Library is an implementation based on java.util Collections.  If not, it's a SnakeYaml's Node-based implementtion.
     * @param _ylstr a java.lang.String object whose value is one the ENUM values of this class
     * @return ( _yl == ESOTERICSOFTWARE_Library || _yl == CollectionsImpl_Library )
     * @throws Exception if the parameter _ylstr is invalid
     */
    public static boolean isCollectionsImpl( final String _ylstr ) throws Exception {
        final YAML_Libraries yl = fromString( _ylstr );
        return isCollectionsImpl(yl);
    }

    /**
     * Whether the YAML-Library is an implementation based on SnakeYaml's Node.  If not, it's a java.util Collections based implementtion.
     * @param _yl one the ENUM values of this class
     * @return ( _yl == SNAKEYAML_Library || _yl == NodeImpl_Library )
     */
    public static boolean isNodeImpl( final YAML_Libraries _yl ) {
        return ( _yl == ORGSNAKEYAML_Library || _yl == SNAKEYAML_Library || _yl == NodeImpl_Library );
    }

    /**
     * Whether the YAML-Library is an implementation based on SnakeYaml's Node.  If not, it's a java.util Collections based implementtion.
     * @param _ylstr a java.lang.String object whose value is one the ENUM values of this class
     * @return ( _yl == SNAKEYAML_Library || _yl == NodeImpl_Library )
     * @throws Exception if the parameter _ylstr is invalid
     */
    public static boolean isNodeImpl( final String _ylstr ) throws Exception {
        final YAML_Libraries yl = fromString( _ylstr );
        return isNodeImpl(yl);
    }

    //============================================================
    /**
     * Use this method to define your REGEXPRESSIONS by providing a '|' as delimiter.
     * @param _delim any string you want
     * @return the valid values (com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml) separated by the delimiter
     */
    public static String list( final String _delim ) {
        // return ""+ ESOTERICSOFTWARE_Library +_delim+ SNAKEYAML_Library +_delim+ ASUXYAML_Library;
        String retval = "";
        for (YAML_Libraries typeitem : YAML_Libraries.values()) {
            if ( typeitem == UNDEFINED )
                continue;
            retval += typeitem.toString() + _delim;
        }
        return retval;
    }

    //============================================================
    /**
     *  <p>Since {@link #NodeImpl_Library} is a synonym for {@link #SNAKEYAML_Library} and since {@link #ESOTERICSOFTWARE_Library} is a synonym for {@link #CollectionsImpl_Library}
     *      we will standardize on {@link #SNAKEYAML_Library} and  {@link #ESOTERICSOFTWARE_Library}.</p>
     *  <p>In addition, {@link #ASUXYAML_Library} is currently an alias for {@link #SNAKEYAML_Library}</p>
     *  @param _inp an enum of this class
     *  @return either {@link #SNAKEYAML_Library},  {@link #ESOTERICSOFTWARE_Library} or {@link #UNDEFINED}
     */
    public static YAML_Libraries normalize( final YAML_Libraries _inp ) {
        switch ( _inp ) {
        case CollectionsImpl_Library:   return ESOTERICSOFTWARE_Library;
        case NodeImpl_Library:          return SNAKEYAML_Library;
        case ASUXYAML_Library:          return SNAKEYAML_Library;
        case ORGSNAKEYAML_Library:      return SNAKEYAML_Library;
        case SNAKEYAML_Library:         return _inp;
        case ESOTERICSOFTWARE_Library:  return _inp;
        default:    return UNDEFINED; // only valid values left.
        } // switch
    }

    //============================================================
};
