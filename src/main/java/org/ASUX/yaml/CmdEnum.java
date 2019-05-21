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


/** Class that captures the ennumeration of the various commands supported by the org.ASUX.YAML implementation
 */
public enum CmdEnum {

    READ("read"), LIST("list"), DELETE("delete"), INSERT("insert"), REPLACE("replace"), TABLE("table"), MACRO("macro"), BATCH("batch"), UNKNOWN("unknown");

    private final String internalValue;
    public static final String CLASSNAME =CmdEnum.class.getName();

    //=================================
    /** <p>This constructor is private.  the right way to create new objects of this enum are via the {@link #fromString}.</p>
     * <p>For Enums based on strings, you need a constructor like this.  Only reason: To save the parameter as an internal variable.</p>
     * <p>Per Java language spec, this constructor is private (as I understand it)</p>
     * @param _i
     */
    private CmdEnum(final String _i) {
        this.internalValue = _i;
    }

    //=================================
    /** @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return this.internalValue;
    }

    //=================================
    /**
     * Given a string, this method will help convert the string into the standard ENUM values of this class.  If, invalid input.. an exception is thrown.
     * @param type a string value that should be one of com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml
     * @return a valid ENUM value of this class
     * @throws Exception if string parameter is invalid
     */
    public static CmdEnum fromString(String type) throws Exception {
        for (CmdEnum typeitem : CmdEnum.values()) {
            if (typeitem.toString().equals(type)) {
                return typeitem;
            }
        }
        throw new Exception ( CLASSNAME + ": fromString("+ type +"): should be one of the values: "+ list("\t") );
        // return YAML_Libraries.SNAKEYAML_Library; // Default.. or you can throw exception
    }

    //=================================
    /**
     * Use this method to define your REGEXPRESSIONS by providing a '|' as delimiter.
     * @param _delim any string you want
     * @return the valid values (com.esotericsoftware.yamlbeans org.yaml.snakeyaml.Yaml org.ASUX.yaml) separated by the delimiter
     */
    public static String list( final String _delim ) {
        // return ""+ ESOTERICSOFTWARE_Library +_delim+ SNAKEYAML_Library +_delim+ ASUXYAML_Library;
        String retval = "";
        for (CmdEnum typeitem : CmdEnum.values()) {
            retval += typeitem.toString() + _delim;
        }
        return retval;
    }
}

