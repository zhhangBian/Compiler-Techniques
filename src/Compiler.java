import backend.BackEnd;
import error.ErrorRecorder;
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
        IOhandler.PrintTokenList();

        FrontEnd.GenerateAstTree();
        IOhandler.PrintAstTree();

        MidEnd.GenerateSymbolTable();

        IOhandler.PrintSymbolTable();
        IOhandler.PrintErrorMessage();

        if (ErrorRecorder.HaveNoError()) {
            MidEnd.GenerateIr();

            if (Setting.FINE_TUNING) {
                IOhandler.PrintLlvmInit();
                OptimizeManager.Init();
                OptimizeManager.Optimize();
            }

            BackEnd.GenerateMips();
        }

        if (ErrorRecorder.HaveNoError()) {
            IOhandler.PrintLlvm();
            IOhandler.PrintMips();

            HandleComplexity.PrintReport();
        }
    }
}
