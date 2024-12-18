package backend;

import backend.mips.MipsModule;
import backend.mips.assembly.MipsAssembly;
import backend.mips.assembly.MipsLsu;

import java.util.ArrayList;
import java.util.HashSet;

public class PeepHole {
    private final MipsModule mipsModule;
    private final ArrayList<MipsAssembly> textSegement;

    public PeepHole() {
        this.mipsModule = BackEnd.GetMipsModule();
        this.textSegement = this.mipsModule.GetTextSegment();
    }

    public void Peep() {
        boolean finished = false;
        while (!finished) {
            finished = true;
            finished &= this.PeepContinueSW();
        }
    }

    private boolean PeepContinueSW() {
        boolean finished = true;
        HashSet<MipsAssembly> removeSet = new HashSet<>();
        for (int i = 0; i < this.textSegement.size(); i++) {
            MipsAssembly nowInstr = this.textSegement.get(i);
            if (nowInstr instanceof MipsLsu nowLsu && nowLsu.IsStoreType()) {
                if (i == 0) {
                    continue;
                }
                MipsAssembly beforeInstr = this.textSegement.get(i - 1);
                // 对同一地址连续写
                if (beforeInstr instanceof MipsLsu beforeLsu && beforeLsu.IsStoreType()) {
                    if (nowLsu.GetTarget().equals(beforeLsu.GetTarget())) {
                        removeSet.add(beforeInstr);
                        finished = false;
                    }
                }
            }
        }

        for (MipsAssembly removeInstr : removeSet) {
            this.textSegement.remove(removeInstr);
        }

        return finished;
    }
}
