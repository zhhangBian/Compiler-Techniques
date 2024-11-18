llvm-link llvm_ir.txt tools/libsysy/lib.ll -S -o out.ll
 cat in.txt| lli out.ll
