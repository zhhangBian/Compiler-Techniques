import frontend.FrontEnd;
import midend.MidEnd;
import utils.IOhandler;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        IOhandler.SetIO();

        FrontEnd.SetInput();
        FrontEnd.GenerateTokenList();
        FrontEnd.GenerateAstTree();

        // 拟在遍历过程中，一边生成符号表，一边进行中间代码生成
        MidEnd.Visit();

        IOhandler.PrintTokenList();
        IOhandler.PrintAstTree();
        IOhandler.PrintSymbolTable();
        IOhandler.PrintErrorMessage();
    }
}
