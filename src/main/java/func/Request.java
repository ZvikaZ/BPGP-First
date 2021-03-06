package func;

import ec.*;
import ec.gp.*;

public class Request extends GPNode { 
    public String toString() { return "Request"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result = "\t\tbp.sync({ request:[ ";

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += " ] });\n";

            rd.str = result;
        }
}




