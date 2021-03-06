package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class WaitFor extends GPNode { 
    public String toString() { return "WaitFor"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result = "\t\tvar e = bp.sync({ waitFor:[ ";

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += " ] });\n";

            rd.str = result;
        }
}




