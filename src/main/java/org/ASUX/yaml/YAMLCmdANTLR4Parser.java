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

import org.ASUX.language.antlr4.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

// import static org.junit.Assert.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import static org.junit.Assert.*; // OLD JUnit compatible.

//==============================================================================
// https://www.antlr.org/api/Java/org/antlr/v4/runtime/package-summary.html
// https://www.antlr.org/api/Java/org/antlr/v4/runtime/CommonTokenStream.html
// https://www.antlr.org/api/Java/org/antlr/v4/runtime/Token.html
// https://www.antlr.org/api/Java/org/antlr/v4/runtime/ParserRuleContext.html
// https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTree.html
// Known Implementing Classes: ParserRuleContext, RuleContext, RuleContextWithAltNum, TerminalNodeImpl, ErrorNodeImpl, InterpreterRuleContext
// All Known Subinterfaces: ErrorNode, RuleNode, TerminalNode

// https://github.com/antlr/antlr4/blob/master/doc/tree-matching.md

// import org.antlr.v4.runtime.misc.*; // https://www.antlr.org/api/Java/org/antlr/v4/runtime/UnbufferedCharStream.html
// import org.antlr.v4.runtime.tree.*; // https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/TerminalNode.html
// import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;          // https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/pattern/ParseTreePattern.html
// import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;   // https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/pattern/ParseTreePatternMatcher.html
// import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;            // https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/pattern/ParseTreeMatch.html
// import org.antlr.v4.runtime.tree.xpath.XPath; // https://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/xpath/XPath.html

//==============================================================================
//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//==============================================================================

public class YAMLCmdANTLR4Parser implements org.ASUX.language.antlr4.GenericCmdANTLR4Parser {

    private static final String HDR0 = YAMLCmdANTLR4Parser.class.getName();

    public boolean verbose;

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    public YAMLCmdANTLR4Parser( final boolean _verbose ) {
        this.verbose = _verbose;
    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // public static class MyParser extends YAMLANTLR4Parser {
    //     private static final String HDR0 = YAMLCmdANTLR4Parser.class.getName();

    //     /**
    //      * Super class has no default constructor
    //      * @param input see {@link org.ASUX.language.YAMLANTLR4Parser}
    //     */
    //     public MyParser( TokenStream input ) {
    //         super(input);
    //     }

    //     /** Always called by generated parsers upon entry to a rule. */
    //     @Override
    //     public void enterRule( ParserRuleContext localctx, int state, int ruleIndex ) {
    //         final String HDR = HDR0 + ".enterRule(): ";
    //         super.enterRule( localctx, state, ruleIndex );
    //         System.out.println( HDR + "rule-index="+ ruleIndex + " localCtx="+ localctx );
    //     }
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    // public void parseCommandsInFile() throws Exception {
    //     final String HDR = HDR0 + ".parseCommandsInFile():\t";
    //
    //     // try {
    //     //     // read line from file
    //     //     // for each line call parseYamlCommandLine()
    //     // } catch (Exception e) {
    //     //     e.printStackTrace(System.err);
    //     //     System.err.println(HDR + e.getMessage());
    //     //     YAMLCmdANTLR4Parser.bNoTestFailedSoFar = false;
    //     //     throw e;
    //     // }
    // }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    //=================================================================
    private void setRegExp( final CmdLineArgs carg, final YAMLANTLR4Parser.RegularexpressionContext regExpCtx ) {
        final String HDR = HDR0 + ".setRegExp():\t";
        final YAMLANTLR4ParserUtils util = new YAMLANTLR4ParserUtils( this.verbose );

        // 'regExpCtx' is an object that is a subclass of ParserRuleContext
        final ArrayList<String> sss = util.toStrings(regExpCtx);
        String regExpStr = "";
        for ( String is: sss ) {
            regExpStr += is;
        }
        if ( this.verbose ) System.out.println(HDR + "YAML-Cmd's REGEXPstring(from Parser) =[" + regExpStr + "] ");

        carg.yamlRegExpStr = regExpStr;
    }

    //=================================================================
    private void setOptionals( final CmdLineArgsCommon carg, final List<YAMLANTLR4Parser.OptionalsContext> _optionalsCtxSet ) throws Exception {
        for( YAMLANTLR4Parser.OptionalsContext optionalsCtx: _optionalsCtxSet ) {
            setOptionals( carg, optionalsCtx );
        }
    }

    private void setOptionalsDelimiter( final CmdLineArgs carg, final List<YAMLANTLR4Parser.OptionalsContext> _optionalsCtxSet ) throws Exception {
        final String HDR = HDR0 + ".setOptionalsDelimiter():\t";
        final YAMLANTLR4ParserUtils util = new YAMLANTLR4ParserUtils(this.verbose);

        for( YAMLANTLR4Parser.OptionalsContext optionalsCtx: _optionalsCtxSet ) {
            try {
                final String ed = util.getEffectiveOption( optionalsCtx.delimiter );  // <--- can throw
                if ( this.verbose ) System.out.println( HDR + "YAML-Cmd's Effective Delimiter = ["+ ed +"]" );
                carg.yamlPatternDelimiter = ed;
            } catch( java.util.NoSuchElementException e ) {
                if ( this.verbose ) System.out.println( HDR + "YAML-Cmd has No Delimiter Not in this instance.  Perhaps another one in this optionalsCtxSet(SET)." );
                // otherwise no change to carg.yamlPatternDelimiter
            }
        }
    }

    //=================================================================
    private void setOptionals( final CmdLineArgsCommon carg, final YAMLANTLR4Parser.OptionalsContext optionalsCtx ) throws Exception {
        final String HDR = HDR0 + ".setOptionals():\t";
        final YAMLANTLR4ParserUtils util = new YAMLANTLR4ParserUtils( this.verbose );

        // dump for debugging purposes.
        if ( this.verbose ) {
            final ArrayList<String> sss22 = util.toStrings( optionalsCtx );
            System.out.print( HDR + "\tYAML-Cmd's OPTIONALs:- " );
            sss22.forEach(System.out::println);
        }

        //---------------------------
        // final java.util.List<YAMLANTLR4Parser.Any_quoted_textContext> delims = optionalsCtx.delimiter;
        // .getText() does NOT WORK for:->> delims.forEach( d -> System.out.println(d.getText()) );
        if ( this.verbose ) {
            System.out.print( HDR + "\tYAML-Cmd's Delimiters:- " );
            optionalsCtx.delimiter.forEach( delim -> { final ArrayList<String> sss66 = util.toStrings( delim ); sss66.forEach( System.err::println); } );
        }

        //---------------------------
        // final java.util.List<Token> yamlImps = optionalsCtx.yamlImplementation;
        if ( ! optionalsCtx.yamlImplementation.isEmpty() && this.verbose ) {
            System.out.print( HDR + "YAML-Cmd's yamlImplementation:- " );
            optionalsCtx.yamlImplementation.forEach( tk -> System.out.println(tk.getText()) );
        }

        try {
            final String effYamlImpl = util.getEffectiveTokenOption( optionalsCtx.yamlImplementation );  // <<--- can throw
            if ( this.verbose ) System.out.println( HDR + "YAML-Cmd's Effective-YAML-Implementation = ["+ effYamlImpl +"]" );
            carg.YAMLLibrary = YAML_Libraries.fromString( effYamlImpl );
            if ( this.verbose ) System.out.println( HDR + "YAML-Cmd's Effective-YAML-Implementation-ID = ["+ carg.YAMLLibrary +"]" );
        } catch ( java.util.NoSuchElementException e ) {
            if ( this.verbose ) System.out.println( HDR + "YAML-Cmd has No yamlImplementation Not in this instance.  Perhaps another one in this optionalsCtxSet(SET)." );
            // otherwise no change to carg.yamlPatternDelimiter
        } // catch

        //---------------------------
        String quoteChar = (optionalsCtx.yamlQuoteChar != null) ? optionalsCtx.yamlQuoteChar.getText() : null;
        if ( this.verbose ) System.out.println( HDR + "YAML-Cmd's QuotingChar = ["+ quoteChar +"]" );
        // see how to call getText() correctly @ https://github.com/antlr/antlr4/blob/master/doc/faq/parse-trees.md#how-do-i-get-the-input-text-for-a-parse-tree-subtree
        if ( quoteChar != null )
            carg.quoteType = Enums.ScalarStyle.fromString(quoteChar);

        //---------------------------
        final java.util.List<TerminalNode> showStatsOpt = optionalsCtx.SHOWSTATS();
        if ( this.verbose && showStatsOpt == null ) System.out.println( HDR + "YAML-Cmd's show-Statistics is NULL" );
        if ( this.verbose && showStatsOpt != null ) System.out.println( HDR + "YAML-Cmd's show-Statistics = ["+ ( showStatsOpt.size() > 0 ) +"]" );
        carg.showStats = carg.showStats || ( (showStatsOpt != null) && (showStatsOpt.size() > 0) );

        //---------------------------
        final java.util.List<TerminalNode> verboseOpt = optionalsCtx.VERBOSE();
        if ( this.verbose ) System.out.println( HDR + "YAML-Cmd's verbose-Count = ["+ verboseOpt.size() +"]" );
        carg.verbose = carg.verbose || ( (verboseOpt != null) && (verboseOpt.size() > 0 ) );

    }

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

    public ArrayList<org.ASUX.language.antlr4.CmdLineArgs> parseYamlCommandLine( final String _cmdLineStr ) throws Exception {
        final String HDR = HDR0 + ".parseYamlCommandLine():\t";
        final ArrayList<org.ASUX.language.antlr4.CmdLineArgs> returnArray = new ArrayList<>();

        try {
            // DEPRECATED: ANTLRInputStream inputStream = new ANTLRInputStream( _cmdLineStr );
            // final CharStream inputStream = CharStreams.fromFileName( TEST_INPUT_YAML_COMMANDS ); // https://www.antlr.org/api/Java/org/antlr/v4/runtime/CharStreams.html
            final CharStream inputStream = CharStreams.fromString( _cmdLineStr ); // https://www.antlr.org/api/Java/org/antlr/v4/runtime/CharStreams.html

            final MyYAMLANTLR4Lexer myLexer = new MyYAMLANTLR4Lexer( this.verbose, inputStream );
            final CommonTokenStream commonTokenStream = new CommonTokenStream( myLexer );

            // Configure parser and appropriate Listeners and ErrorListeners
            if ( this.verbose ) System.out.println( HDR + "init parser" );
            final YAMLANTLR4Parser defaultParser = new YAMLANTLR4Parser( commonTokenStream );

            //==============================================================================
            defaultParser.setErrorHandler( new org.ASUX.language.antlr4.BailErrorStrategy( this.verbose ) );
            // With BailErrorStrategy, at the first ___Lexical___ Error, both parser & lexer stop.

            // Error Listener
            final MyErrorListener errorListener = new MyErrorListener( this.verbose, null );
            // defaultParser.addErrorListener( errorListener );  // This is __MY OWN__  Java-class to listen to errors.
                // void	syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
                // Upon syntax error, notify any interested parties.

            //==============================================================================
            // Start parsing
            if ( this.verbose ) System.out.println( HDR + "about to parse" );
            YAMLANTLR4Parser.Yaml_commandsContext topmostCtx = defaultParser.yaml_commands();  // if the grammer/scenario restricted user-input to JUST 1 command ONLY.
            final java.util.List<YAMLANTLR4Parser.Yaml_commandContext> cmdsCtx = topmostCtx.yaml_command();

            if ( this.verbose ) System.out.println( "\n" ); // because My-OWN-Lexer will be dumping tokens separated by Tabs

            //==============================================================================
            final YAMLANTLR4ParserUtils util = new YAMLANTLR4ParserUtils( this.verbose );

            //==============================================================================
            //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            //==============================================================================

            for (YAMLANTLR4Parser.Yaml_commandContext eachCmdCtx: cmdsCtx ) {

                final YAMLANTLR4Parser.Yaml_command_readContext readCtx = eachCmdCtx.yaml_command_read();
                if ( readCtx != null ) {

                    if ( this.verbose ) System.out.println( HDR + " yaml READ command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "about to run LEXER's ASSERT-checks" );

                    final CmdLineArgsReadCmd carg = new CmdLineArgsReadCmd();

                    //---------------------------------
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.READ;

                    // final YAMLANTLR4Parser.RegularexpressionContext regExpCtx = readCtx.regularexpression();
                    setRegExp( carg, readCtx.regularexpression() );

                    setOptionals( carg, readCtx.optionals() );
                    setOptionalsDelimiter( carg, readCtx.optionals() );

                    //---------------------------------
                    carg.inputFilePath  = readCtx.inputSrc.getText();   // commonTokenStream.get( regExpPos + ?? ).getText();
                    carg.outputFilePath = readCtx.outputSink.getText(); // commonTokenStream.get( regExpPos + ?? ).getText();
                    if ( this.verbose ) System.out.println( HDR + "Read-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    if ( readCtx.projectionpath() == null ) {
                        if (this.verbose) System.out.println(HDR + "Read-YAML's ProjectionPath is missing on command-line ");
                    } else {
                        final ArrayList<String> sss77 = util.toStrings( readCtx.projectionpath() );
                        // assertEquals( sss77.size(), 1);
                        final String s7 = sss77.get(0);
                        if (this.verbose) System.out.println(HDR + "Read-YAML's ProjectionPath =[" + s7 + "] ");
                        carg.projectionPath = s7;
                    }

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_listContext listCtx = eachCmdCtx.yaml_command_list();
                if ( listCtx != null ) {

                    if ( this.verbose ) System.out.println( HDR + "yaml LIST command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "about to run LEXER's ASSERT-checks" );

                    final CmdLineArgs carg = new CmdLineArgs();

                    //---------------------------------
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.LIST;

                    setRegExp( carg, listCtx.regularexpression() );

                    setOptionals( carg, listCtx.optionals() );
                    setOptionalsDelimiter( carg, listCtx.optionals() );

                    //---------------------------------
                    carg.inputFilePath  = listCtx.inputSrc.getText();   // commonTokenStream.get( regExpPos + ?? ).getText();
                    carg.outputFilePath = listCtx.outputSink.getText(); // commonTokenStream.get( regExpPos + ?? ).getText();
                    if ( this.verbose ) System.out.println( HDR + "LIST-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_tableContext tblCtx = eachCmdCtx.yaml_command_table();
                if ( tblCtx != null ) {

                    if ( this.verbose ) System.out.println( HDR + "yaml TABLE command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "tblCtx ="+ tblCtx );

                    final CmdLineArgsTableCmd carg = new CmdLineArgsTableCmd();

                    //---------------------------------
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.TABLE;

                    setRegExp( carg, tblCtx.regularexpression() );

                    setOptionals( carg, tblCtx.optionals() );
                    setOptionalsDelimiter( carg, tblCtx.optionals() );

                    //---------------------------------
                    carg.inputFilePath  = tblCtx.inputSrc.getText();
                    carg.outputFilePath = tblCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "TABLE-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    // final ArrayList<String> sss77 = util.toStrings( tblCtx.columns() );
                    // assertEquals( sss77.size(), 1);
                    // final String s7 = sss77.get(0);
                    carg.tableColumns = tblCtx.columnslist().getText();;
                    if (this.verbose) System.out.println(HDR + "TABLE-YAML's columns =[" + carg.tableColumns + "] ");

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_deleteContext  deleteCtx = eachCmdCtx.yaml_command_delete();
                if ( deleteCtx != null ) {
                    if ( this.verbose ) System.out.println( HDR + " yaml DELETE command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "deleteCtx ="+ deleteCtx );

                    final CmdLineArgs carg = new CmdLineArgs();

                    //=================================================================
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.DELETE;

                    setRegExp( carg, deleteCtx.regularexpression() );

                    setOptionals( carg, deleteCtx.optionals() );
                    setOptionalsDelimiter( carg, deleteCtx.optionals() );

                    //=================================================================
                    carg.inputFilePath  = deleteCtx.inputSrc.getText();
                    carg.outputFilePath = deleteCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "DELETE-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    Cmd.go( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_insertContext  insertCtx = eachCmdCtx.yaml_command_insert();
                if ( insertCtx != null )  {
                    if ( this.verbose ) System.out.println( HDR + " yaml INSERT command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "insertCtx ="+ insertCtx );

                    final CmdLineArgsInsertCmd carg = new CmdLineArgsInsertCmd();

                    //=================================================================
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.INSERT;

                    setRegExp( carg, insertCtx.regularexpression() );

                    setOptionals( carg, insertCtx.optionals() );
                    setOptionalsDelimiter( carg, insertCtx.optionals() );

                    //=================================================================
                    carg.inputFilePath  = insertCtx.inputSrc.getText();
                    carg.outputFilePath = insertCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "INSERT-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    final ArrayList<String> sss77 = util.toStrings( insertCtx.newcontent() );
                    // assertEquals( sss77.size(), 1);
                    final String s7 = sss77.get(0);
                    if (this.verbose) System.out.println(HDR + "INSERT-YAML's columns =[" + s7 + "] ");
                    carg.insertFilePath = s7;

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_replaceContext replaceCtx = eachCmdCtx.yaml_command_replace();
                if ( replaceCtx != null ) {
                    if ( this.verbose ) System.out.println( HDR + " yaml REPLACE command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "replaceCtx ="+ replaceCtx );

                    final CmdLineArgsReplaceCmd carg = new CmdLineArgsReplaceCmd();

                    //=================================================================
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.REPLACE;

                    setRegExp( carg, replaceCtx.regularexpression() );

                    setOptionals( carg, replaceCtx.optionals() );
                    setOptionalsDelimiter( carg, replaceCtx.optionals() );

                    //=================================================================
                    carg.inputFilePath  = replaceCtx.inputSrc.getText();
                    carg.outputFilePath = replaceCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "REPLACE-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    final ArrayList<String> sss77 = util.toStrings( replaceCtx.newcontent() );
                    // assertEquals( sss77.size(), 1);
                    final String s7 = sss77.get(0);
                    if (this.verbose) System.out.println(HDR + "REPLACE-YAML's columns =[" + s7 + "] ");
                    carg.replaceFilePath = s7;

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_macroContext   macroCtx = eachCmdCtx.yaml_command_macro();
                if ( macroCtx != null )  {
                    if ( this.verbose ) System.out.println( HDR + " yaml MACRO command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "macroCtx ="+ macroCtx );

                    final CmdLineArgsMacroCmd carg = new CmdLineArgsMacroCmd();

                    //=================================================================
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.MACRO;

                    setOptionals( carg, macroCtx.optionals() );

                    //=================================================================
                    carg.inputFilePath  = macroCtx.inputSrc.getText();
                    carg.outputFilePath = macroCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "MACRO-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    if (this.verbose) System.out.println(HDR + "MACRO-YAML's properties-file =[" + macroCtx.macroProperties().getText() + "] ");
                    carg.propertiesFilePath = macroCtx.macroProperties().getText();

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================
                final YAMLANTLR4Parser.Yaml_command_batchContext  batchCtx = eachCmdCtx.yaml_command_batch();
                if ( batchCtx != null )  {
                    if ( this.verbose ) System.out.println( HDR + " yaml BATCH command detected!" );
                    if ( this.verbose ) System.out.println( HDR + "batchCtx ="+ batchCtx );

                    final CmdLineArgsBatchCmd carg = new CmdLineArgsBatchCmd();

                    //=================================================================
                    carg.cmdAsStr = _cmdLineStr;
                    carg.cmdType = Enums.CmdEnum.BATCH;

                    setOptionals( carg, batchCtx.optionals() );

                    //=================================================================
                    carg.inputFilePath  = batchCtx.inputSrc.getText();
                    carg.outputFilePath = batchCtx.outputSink.getText();
                    if ( this.verbose ) System.out.println( HDR + "BATCH-YAML's InputSOURCE =["+ carg.inputFilePath +"] OutputSink=["+ carg.outputFilePath +"]" );

                    //=================================================================
                    if (this.verbose) System.out.println(HDR + "BATCH-YAML's properties-file =[" + batchCtx.batchFilePath().getText() + "] ");
                    carg.batchFilePath = batchCtx.batchFilePath().getText();

                    // Cmd.go( carg );
                    returnArray.add( carg );
                    continue; // !!!!!!!!!!!!!!!!! VERY IMPORTANT !!!!!!!!!!!!!!!!  .. .. as we are UNABLE to rely on a SWITCH-statement.
                }

                //==============================================================================
                //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                //==============================================================================

                if ( this.verbose ) System.out.println( HDR + "!!!!!!!!!!!! oh! oh! oh! oh! __UNKNOWN__ command detected!" );

            } // for loop

        } catch( AssertionError | Exception e ) { // AssertionError includes sub-class org.opentest4j.AssertionFailedError
            e.printStackTrace( System.err );
            System.err.println( HDR + e.getMessage() );
            throw e;
        }

        return returnArray;
        // throw new Exception( "Command Not recognized: "+ _cmdLineStr );

    } // parseYamlCommandLine()

    //==============================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //==============================================================================

}

//EOF
