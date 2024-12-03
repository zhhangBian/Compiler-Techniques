import backend.BackEnd;
import frontend.FrontEnd;
import midend.MidEnd;
import optimize.OptimizeManager;
import utils.HandleComplexity;
import utils.IOhandler;
import utils.Setting;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        IOhandler.SetIO();

        FrontEnd.SetInput();
        FrontEnd.GenerateTokenList();
        FrontEnd.GenerateAstTree();

        MidEnd.GenerateSymbolTable();
        MidEnd.GenerateIr();

        if (Setting.FINE_TUNING) {
            OptimizeManager.Init();
            OptimizeManager.Optimize();
        }

        BackEnd.GenerateMips();

        IOhandler.PrintTokenList();
        IOhandler.PrintAstTree();
        IOhandler.PrintSymbolTable();
        IOhandler.PrintErrorMessage();
        IOhandler.PrintLlvm();
        IOhandler.PrintMips();

        HandleComplexity.PrintReport();
    }
}
