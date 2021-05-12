package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class ColumnOfWait extends GPNode {
    static public long MAX = 7;     //TODO have it more generic?

    public String toString() { return "ColumnOfWait"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result2 = "(e.data.col + ";

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result2 += rd.str;

            result2 += ")";

            rd.str = "(" + result2 + " > " + MAX + ") ? " + MAX + " : " + result2;

        }
}




