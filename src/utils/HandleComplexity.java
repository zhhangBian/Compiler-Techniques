package utils;

import backend.BackEnd;
import backend.mips.MipsModule;
import backend.mips.assembly.MipsAssembly;
import backend.mips.assembly.MipsMdu;

import java.util.ArrayList;

public class HandleComplexity {
    private static int otherCount = 0;
    private static int memCount = 0;
    private static int jumpCount = 0;
    private static int mulCount = 0;
    private static int divCount = 0;
    private static int totalCount = 0;

    public static void Compute() {
        MipsModule mipsModule = BackEnd.GetMipsModule();
        ArrayList<MipsAssembly> textSegment = mipsModule.GetTextSegment();

        for (MipsAssembly mipsAssembly : textSegment) {
            switch (mipsAssembly.GetMipsType()) {
                case ALU, COMPARE, SYSCALL, MARS -> otherCount++;

                case BRANCH, JUMP -> jumpCount++;

                case LSU -> memCount++;

                case MDU -> {
                    MipsMdu mipsMdu = (MipsMdu) mipsAssembly;
                    switch (mipsMdu.GetMduType()) {
                        case MULT -> mulCount++;
                        case DIV -> divCount++;
                        default -> otherCount++;
                    }
                }

                default -> {
                }
            }
        }

        totalCount = divCount * 50 + mulCount * 3 + jumpCount * 3 + memCount * 4 + otherCount;
    }

    public static void PrintReport() {
        Compute();

        StringBuilder builder = new StringBuilder();
        builder.append("total count is: " + totalCount);
        builder.append("\n\t" + "[div]   count is: " + divCount);
        builder.append("\n\t" + "[mul]   count is: " + mulCount);
        builder.append("\n\t" + "[jump]  count is: " + jumpCount);
        builder.append("\n\t" + "[mem]   count is: " + memCount);
        builder.append("\n\t" + "[other] count is: " + otherCount);

        System.out.println(builder);
    }
}
