; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str = private unnamed_addr constant [5 x i8] c"a = \00", align 1
@.str.1 = private unnamed_addr constant [7 x i8] c", b = \00", align 1
@.str.2 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.3 = private unnamed_addr constant [5 x i8] c"b = \00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5 = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.1.6 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.2.9 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3.10 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4.11 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5.14 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local i32 @main() {
  %var1 = alloca i32, align 4
  store i32 255, ptr %var1, align 4
  %var2 = alloca i8, align 1
  store i8 2, ptr %var2, align 1
  call void @putstr(ptr @.str)
  %var3 = load i32, ptr %var1, align 4
  call void @putint(i32 %var3)
  call void @putstr(ptr @.str.1)
  %var4 = load i8, ptr %var2, align 1
  %var5 = zext i8 %var4 to i32
  call void @putint(i32 %var5)
  call void @putstr(ptr @.str.2)
  %var6 = load i32, ptr %var1, align 4
  %var7 = load i8, ptr %var2, align 1
  %var8 = zext i8 %var7 to i32
  %var9 = add nsw i32 %var6, %var8
  store i32 %var9, ptr %var2, align 4
  call void @putstr(ptr @.str.3)
  %var11 = load i8, ptr %var2, align 1
  %var12 = zext i8 %var11 to i32
  call void @putint(i32 %var12)
  call void @putstr(ptr @.str.4)
  ret i32 0
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getchar() #0 {
  %1 = alloca i8, align 1
  %2 = call i32 (ptr, ...) @__isoc99_scanf(ptr @.str.5, ptr %1)
  %3 = load i8, ptr %1, align 1
  %4 = sext i8 %3 to i32
  ret i32 %4
}

declare dso_local i32 @__isoc99_scanf(ptr, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getint() #0 {
  %1 = alloca i32, align 4
  %2 = call i32 (ptr, ...) @__isoc99_scanf(ptr @.str.1.6, ptr %1)
  br label %3

3:                                                ; preds = %6, %0
  %4 = call i32 @getchar()
  %5 = icmp ne i32 %4, 10
  br i1 %5, label %6, label %7

6:                                                ; preds = %3
  br label %3, !llvm.loop !2

7:                                                ; preds = %3
  %8 = load i32, ptr %1, align 4
  ret i32 %8
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getarray(ptr %0) #0 {
  %2 = alloca ptr, align 8
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  store ptr %0, ptr %2, align 8
  %5 = call i32 (ptr, ...) @__isoc99_scanf(ptr @.str.1.6, ptr %3)
  store i32 0, ptr %4, align 4
  br label %6

6:                                                ; preds = %16, %1
  %7 = load i32, ptr %4, align 4
  %8 = load i32, ptr %3, align 4
  %9 = icmp slt i32 %7, %8
  br i1 %9, label %10, label %19

10:                                               ; preds = %6
  %11 = load ptr, ptr %2, align 8
  %12 = load i32, ptr %4, align 4
  %13 = sext i32 %12 to i64
  %14 = getelementptr inbounds i32, ptr %11, i64 %13
  %15 = call i32 (ptr, ...) @__isoc99_scanf(ptr @.str.1.6, ptr %14)
  br label %16

16:                                               ; preds = %10
  %17 = load i32, ptr %4, align 4
  %18 = add nsw i32 %17, 1
  store i32 %18, ptr %4, align 4
  br label %6, !llvm.loop !4

19:                                               ; preds = %6
  %20 = load i32, ptr %3, align 4
  ret i32 %20
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putint(i32 %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, ptr %2, align 4
  %3 = load i32, ptr %2, align 4
  %4 = call i32 (ptr, ...) @printf(ptr @.str.1.6, i32 %3)
  ret void
}

declare dso_local i32 @printf(ptr, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putch(i32 %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, ptr %2, align 4
  %3 = load i32, ptr %2, align 4
  %4 = call i32 (ptr, ...) @printf(ptr @.str.5, i32 %3)
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putarray(i32 %0, ptr %1) #0 {
  %3 = alloca i32, align 4
  %4 = alloca ptr, align 8
  %5 = alloca i32, align 4
  store i32 %0, ptr %3, align 4
  store ptr %1, ptr %4, align 8
  %6 = load i32, ptr %3, align 4
  %7 = call i32 (ptr, ...) @printf(ptr @.str.2.9, i32 %6)
  store i32 0, ptr %5, align 4
  br label %8

8:                                                ; preds = %19, %2
  %9 = load i32, ptr %5, align 4
  %10 = load i32, ptr %3, align 4
  %11 = icmp slt i32 %9, %10
  br i1 %11, label %12, label %22

12:                                               ; preds = %8
  %13 = load ptr, ptr %4, align 8
  %14 = load i32, ptr %5, align 4
  %15 = sext i32 %14 to i64
  %16 = getelementptr inbounds i32, ptr %13, i64 %15
  %17 = load i32, ptr %16, align 4
  %18 = call i32 (ptr, ...) @printf(ptr @.str.3.10, i32 %17)
  br label %19

19:                                               ; preds = %12
  %20 = load i32, ptr %5, align 4
  %21 = add nsw i32 %20, 1
  store i32 %21, ptr %5, align 4
  br label %8, !llvm.loop !5

22:                                               ; preds = %8
  %23 = call i32 (ptr, ...) @printf(ptr @.str.4.11)
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putstr(ptr %0) #0 {
  %2 = alloca ptr, align 8
  store ptr %0, ptr %2, align 8
  %3 = load ptr, ptr %2, align 8
  %4 = call i32 (ptr, ...) @printf(ptr @.str.5.14, ptr %3)
  ret void
}

attributes #0 = { noinline nounwind optnone uwtable "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.ident = !{!0}
!llvm.module.flags = !{!1}

!0 = !{!"Ubuntu clang version 12.0.1-19ubuntu3"}
!1 = !{i32 1, !"wchar_size", i32 4}
!2 = distinct !{!2, !3}
!3 = !{!"llvm.loop.mustprogress"}
!4 = distinct !{!4, !3}
!5 = distinct !{!5, !3}
