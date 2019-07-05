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

import java.util.ArrayList;

import org.apache.commons.cli.*;

import static org.junit.Assert.*;

/** <p>This class is a typical use of the org.apache.commons.cli package.</p>
 *  <p>This class has No other function - other than to parse the commandline arguments and handle user's input errors.</p>
 *  <p>For making it easy to have simple code generate debugging-output, added a toString() method to this class.</p>
 *  <p>Typical use of this class is: </p>
 *<pre>
 public static void main(String[] args) {
 cmdLineArgs = new CmdLineArgsIntf(args);
 .. ..
 *</pre>
 *
 *  <p>See full details of how to use this, in {@link org.ASUX.yaml.Cmd} as well as the <a href="https://github.com/org-asux/org.ASUX.cmdline">org.ASUX.cmdline</a> GitHub.com project.</p>
 * @see org.ASUX.yaml.Cmd
 */
public abstract class CmdLineArgsCommon implements java.io.Serializable {

    private static final long serialVersionUID = 141L;

    public static final String CLASSNAME = CmdLineArgsCommon.class.getName();

    protected static final String INPUTFILE = "inputfile";
    protected static final String OUTPUTFILE = "outputfile";

    public static final String NOQUOTE = "no-quote";
    public static final String SINGLEQUOTE = "single-quote";
    public static final String DOUBLEQUOTE = "double-quote";

    //------------------------------------
    protected final ArrayList<String> args = new ArrayList<>();
    public boolean verbose = false;
    public boolean showStats = false;

    public String inputFilePath = "/tmp/undefined";
    public String outputFilePath = "/tmp/undefined";

    public Enums.ScalarStyle quoteType = Enums.ScalarStyle.UNDEFINED;

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  The command line arguments as-is
     *  @return instead of String[], you get an ArrayList (which is guaranteed to be NOT-Null)
     */
    public final ArrayList<String> getArgs() {
        @SuppressWarnings("unchecked")
        final ArrayList<String> ret = (ArrayList<String>) this.args.clone();
        return ret;
    }

    //------------------------------------
    /** For making it easy to have simple code generate debugging-output, added this toString() method to this class.
     */
    public String toString() {
        return
        " --verbose="+verbose+" --showStats="+showStats
        +" inpfile="+inputFilePath+" outputfile="+outputFilePath
        +" this.quoteType=["+this.quoteType+"] "
        ;
    }


    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     * Add cmd-line argument definitions (using apache.commons.cli.Options) for the instance-variables defined in this class
     * @param options a Non-Null instance
     */
    protected void defineCommonOptions( final Options options ) {
        Option opt;

        opt= new Option("v", "verbose", false, "Show debug output");
        opt.setRequired(false);
        options.addOption(opt);

        opt= new Option("vs", "showStats", false, "Show - at end output - a summary of how many matches happened, or entries were affected");
        opt.setRequired(false);
        options.addOption(opt);

        //----------------------------------
        opt = new Option("i", INPUTFILE, true, "input file path");
        opt.setRequired(true);
        options.addOption(opt);

        opt = new Option("o", OUTPUTFILE, true, "output file");
        opt.setRequired(true);
        options.addOption(opt);

        //----------------------------------
        OptionGroup grp2 = new OptionGroup();
        Option noQuoteOpt = new Option("nq", NOQUOTE, false, "do Not use Quotes in YAML output");
        Option singleQuoteOpt = new Option("sq", SINGLEQUOTE, false, "use ONLY Single-quote when generating YAML output");
        Option doubleQuoteOpt = new Option("dq", DOUBLEQUOTE, false, "se ONLY Double-quote when generating YAML output");
        grp2.addOption(noQuoteOpt);
        grp2.addOption(singleQuoteOpt);
        grp2.addOption(doubleQuoteOpt);
        grp2.setRequired(false);

        options.addOptionGroup(grp2);

    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================

    /**
     *  <p>This method is to be used by all Sub-classes for the common instance-variables defined in this claass</p>
     *  <p>fter {@link #defineCommonOptions(Options)}, this method allows to detect what exactly was entered by the user on the command line. </p>
     * @param cmd a Non-null instance
     */
    protected void parseCommonOptions( final org.apache.commons.cli.CommandLine cmd )
    {
        this.verbose = cmd.hasOption("verbose");
        this.showStats = cmd.hasOption("showStats");

        this.inputFilePath = cmd.getOptionValue(INPUTFILE);
        this.outputFilePath = cmd.getOptionValue(OUTPUTFILE);

        if ( cmd.hasOption( NOQUOTE ) ) this.quoteType = Enums.ScalarStyle.PLAIN;
        if ( cmd.hasOption( SINGLEQUOTE ) ) this.quoteType = Enums.ScalarStyle.SINGLE_QUOTED;
        if ( cmd.hasOption( DOUBLEQUOTE ) ) this.quoteType = Enums.ScalarStyle.DOUBLE_QUOTED;
        if ( this.verbose ) System.out.println("this.quoteType = "+this.quoteType.toString());
        // DO NOT do this --> assertTrue( this.quoteType != Enums.ScalarStyle.UNDEFINED );
        // We now __actually use__ UNDEFINED to represent the fact that the end-user did NOT provide anything on the commandline (whether no-quote, single or double)
    }

    //=================================================================================
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //=================================================================================


//     /** Constructor.
//      *  @param args command line argument array - as received as-is from main().
//      *  @throws Exception like ParseException while trying to parse the commandline arguments
//      */
//     public CmdLineArgsIntf(String[] args) throws Exception
//     {
//         this.args.addAll( java.util.Arrays.asList(args) );

//         //----------------------------------
//         Options options = new Options();
//         Option opt;

//         opt= new Option("v", "verbose", false, "Show debug output");
//         opt.setRequired(false);
//         options.addOption(opt);

//         org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
//         org.apache.commons.cli.HelpFormatter formatter = new HelpFormatter();
//         org.apache.commons.cli.CommandLine cmd;

//         try {

//             // if ( ???.verbose ) ..
//             // what if the parse() statement below has issues.. ?  We can't expect to use this.apacheCmd.hasOption("verbose") 
// // System.err.print( CLASSNAME +" parse(): _args = "+ args +"  >>>>>>>>>>>>> "); for( String s: args) System.out.print(s+"\t");  System.out.println();
// // System.err.println( CLASSNAME +" parse(): this = "+ this.toString() );
//             cmd = parser.parse( options, args, true ); //3rd param: boolean stopAtNonOption

//             this.verbose = cmd.hasOption("verbose");

//         } catch (ParseException e) {
//             e.printStackTrace(System.err); // Too Serious an Error.  We do NOT have the benefit of '--verbose',as this implies a FAILURE to parse command line.
//             formatter.printHelp( "\n\njava <jarL> "+CLASSNAME, options );
//             System.err.println( "\nERROR: "+ CLASSNAME +" constructor(): failed to parse the command-line: "+ options );
//             throw e;
//         }
//     }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    // For unit-testing purposes only
//    public static void main(String[] args) {
//        new CmdLineArgsIntf(args);
//    }

}
