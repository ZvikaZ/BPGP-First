package func;

import ec.gp.*;
import ec.util.*;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;


import java.io.*;

public class ColumnERC extends ERC {
    public long value;
    static public long MAX = 7;     //TODO have it more generic?

    public String name() {
        return "ColumnERC";
    }

    public String toStringForHumans() {
        return "" + value;
    }

    public String encode() {
        return Code.encode(value);
    }

    public boolean decode(DecodeReturn dret) {
        int pos = dret.pos;
        String data = dret.data;
        Code.decode(dret);
        if (dret.type != DecodeReturn.T_LONG) {
            // uh oh! Restore and signal error.
            dret.data = data;
            dret.pos = pos;
            return false;
        }
        value = dret.l;
        return true;
    }

    public boolean nodeEquals(GPNode node) {
        return (node.getClass() == this.getClass() && ((ColumnERC)node).value == value);
    }

    public void readNode(EvolutionState state, DataInput dataInput) throws IOException {
        value = dataInput.readLong();
    }

    public void writeNode(EvolutionState state, DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(value);
    }

    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextLong(this.MAX);
    }

    public void mutateNode(EvolutionState state, int thread) {
        throw new RuntimeException();
    }

    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack,
                     GPIndividual individual, Problem Problem) {
        StringData rd = ((StringData)(input));
        rd.str = "" + value;
    }
}

