import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.GPTree;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import func.StringData;
import il.ac.bgu.cs.bp.bpjs.analysis.DfsBProgramVerifier;
import il.ac.bgu.cs.bp.bpjs.analysis.VerificationResult;
import il.ac.bgu.cs.bp.bpjs.analysis.listeners.PrintDfsVerifierListener;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.StringBProgram;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;

//import il.ac.bgu.cs.bp.statespacemapper.GenerateAllTracesInspection;

public class BpgpProblem extends GPProblem implements SimpleProblemForm {
    static final String bpRunLog = "bpRun.log";
    static final int numOfRandomRuns = 0;
    static final boolean useVerifier = true;
    static final boolean debug = false;

    private int bpRun(String generatedCode) {
        final BProgram bprog = getBProgram(generatedCode);

        BProgramRunner rnr = new BProgramRunner(bprog);
        BpgpListener listener = null;

        try {
            // TODO keep log from previous runs (?)
            PrintStream ps = new PrintStream(bpRunLog);
            listener = rnr.addListener( new BpgpListener(ps) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // go!
        rnr.run();

        return getRunFitness(listener.runResult);
    }

    private int bpVerify(String generatedCode) {
        final BProgram bprog = getBProgram(generatedCode);
//        GenerateAllTracesInspection inspector = new GenerateAllTracesInspection();
//        ExecutionTraceInspection inspection = new ExecutionTraceInspection();
        DfsBProgramVerifier vrf = new DfsBProgramVerifier();           // ... and a verifier
//        vrf.setProgressListener(new BriefPrintDfsVerifierListener());  // add a listener to print progress
        VerificationResult res = null;                  // this might take a while
        try {
            res = vrf.verify(bprog);
            PrintDfsVerifierListener listener = new PrintDfsVerifierListener();
            vrf.setProgressListener(listener);
            var event = vrf.trace
            System.out.println(listener);
//            return getRunFitness(listener.);
//            Collection<List<BEvent>> traces = inspector.getResult().traces;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;       //TODO!

    }



    private BProgram getBProgram(String generatedCode) {
        // This will load the program file from <Project>/src/main/resources/
        // TODO take file name from user (param file, or cli flag)
        String code = resourceToString("FourInARow.js");

        // TODO redirect these to some file, waiting for https://github.com/bThink-BGU/BPjs/issues/163
        code = "bp.log.setLevel(\"Warn\");\n" + code;

        //TODO use bprog.appendSource , like in ./src/test/java/il/ac/bgu/cs/bp/bpjs/examples/analysis/TicTacToe/TicTacToeVerificationMain.java

        code += "\n\n" + generatedCode;

        return new StringBProgram(code);
    }

    // Koza fitness: 0 is best, infinity is worst
    // TODO be flexible - currently it wants Yellow to win
    private int getRunFitness(BEvent runResult) {
        if (debug)
            System.out.println("result event: " + runResult);
        if (runResult.name.contains("Win") && runResult.name.contains("Yellow"))
            return 0;
        else if (runResult.name.contains("Win") && runResult.name.contains("Red"))
            return 2;
        if (runResult.name.contains("Draw"))
            return 1;
        else
            throw new RuntimeException();
    }

    private String resourceToString(String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        String result = null;
        try {
            result = IOUtils.toString(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void evaluate(final EvolutionState state,
                         final Individual ind,
                         final int subpopulation,
                         final int threadnum)
    {
        if (ind.evaluated) return;

        StringData input = (StringData)(this.input);

        if (!(ind instanceof GPIndividual))
            state.output.fatal("Whoa!  It's not a GPIndividual!!!",null);

        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        System.out.println("==============");
        System.out.println("Generation: " + state.generation);
        System.out.println("---------\n" + input.str + "---------");

        String niceTree = treeToString(((GPIndividual) ind).trees[0], state);
        System.out.println("grammar: " + niceTree + "---------");

        int totalRunResults = 0;
        for (int i=0; i < numOfRandomRuns; i++) {
            int runResult = bpRun(input.str);
            if (debug)
                System.out.println("runResult:" + runResult);
            totalRunResults += runResult;
        }

        if (useVerifier)
            totalRunResults += bpVerify(input.str);

        System.out.println("totalRunResults: " + totalRunResults);
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, totalRunResults);
        f.printFitnessForHumans(state, 0);
        ind.evaluated = true;
    }

    private String treeToString(GPTree tree, EvolutionState state) {
        StringWriter out    = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        tree.printTree(state, writer);
        writer.flush();
        return out.toString();
    }

    public void describe(
        final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum,
        final int log)
    {
        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        state.output.println("\n\nBest Individual's code\n======================", log);
        state.output.println(((StringData) input).str, log);
    }


}
