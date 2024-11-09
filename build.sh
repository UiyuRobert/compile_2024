#!/bin/bash

# 导出 libsysy.c 的 .ll 文件
clang -emit-llvm -S libsysy.c -o lib.ll
clang -emit-llvm -S main.c -o main.ll

# 使用 llvm-link 将两个文件链接，生成新的 IR 文件
llvm-link main.ll lib.ll -S -o out.ll

# 用 lli 解释运行
lli out.ll

