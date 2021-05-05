package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

//TODO there must be smarter, and more scalable, way to do this
public class YellowCoinEs extends GPNode { // implements EvalPrint {
    public String toString() { return "YellowCoinEs"; }

    public int expectedChildren() { return 0; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            StringData rd = (StringData)input;
            rd.str = "yellowCoinEs";
        }
}




