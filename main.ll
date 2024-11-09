; ModuleID = 'main.c'
source_filename = "main.c"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@singleConstDecl = dso_local constant i32 23, align 4
@singleConstDecl_0 = dso_local constant i32 13, align 4
@singleConstDecl_1 = dso_local constant i32 3, align 4
@singleVarDecl = dso_local global i32 -10, align 4
@singleVarDecl_0 = dso_local global i32 23, align 4
@singleVarDecl_1 = dso_local global i32 10, align 4
@.str = private unnamed_addr constant [16 x i8] c"print int : %d\0A\00", align 1
@.str.1 = private unnamed_addr constant [10 x i8] c"19373479\0A\00", align 1
@singleVarDecl_2 = dso_local global i32 0, align 4

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @funcDef_void() #0 {
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @funcDef_0(i32 %0) #0 {
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %4 = load i32, i32* %2, align 4
  %5 = mul nsw i32 %4, 10
  store i32 %5, i32* %3, align 4
  %6 = load i32, i32* %3, align 4
  ret i32 %6
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @funcDef_1(i32 %0, i32 %1) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  store i32 %1, i32* %4, align 4
  %8 = load i32, i32* %3, align 4
  %9 = load i32, i32* %4, align 4
  %10 = mul nsw i32 %8, %9
  store i32 %10, i32* %5, align 4
  %11 = load i32, i32* %4, align 4
  %12 = icmp ne i32 %11, 0
  br i1 %12, label %13, label %22

13:                                               ; preds = %2
  %14 = load i32, i32* %5, align 4
  %15 = load i32, i32* %3, align 4
  %16 = load i32, i32* %4, align 4
  %17 = srem i32 %15, %16
  %18 = add nsw i32 %14, %17
  store i32 %18, i32* %6, align 4
  %19 = load i32, i32* %3, align 4
  %20 = load i32, i32* %4, align 4
  %21 = sdiv i32 %19, %20
  store i32 %21, i32* %7, align 4
  br label %28

22:                                               ; preds = %2
  %23 = load i32, i32* %5, align 4
  %24 = load i32, i32* %3, align 4
  %25 = add nsw i32 %23, %24
  store i32 %25, i32* %6, align 4
  %26 = load i32, i32* %3, align 4
  %27 = sdiv i32 %26, 2
  store i32 %27, i32* %7, align 4
  br label %28

28:                                               ; preds = %22, %13
  %29 = load i32, i32* %6, align 4
  %30 = load i32, i32* %5, align 4
  %31 = sub nsw i32 %29, %30
  store i32 %31, i32* %6, align 4
  %32 = load i32, i32* %5, align 4
  %33 = load i32, i32* %6, align 4
  %34 = add nsw i32 %32, %33
  %35 = load i32, i32* %5, align 4
  %36 = icmp slt i32 %35, 0
  br i1 %36, label %37, label %40

37:                                               ; preds = %28
  %38 = load i32, i32* %5, align 4
  %39 = sub nsw i32 0, %38
  store i32 %39, i32* %5, align 4
  br label %40

40:                                               ; preds = %37, %28
  %41 = load i32, i32* %5, align 4
  %42 = add nsw i32 1, %41
  %43 = load i32, i32* %6, align 4
  %44 = load i32, i32* %7, align 4
  %45 = add nsw i32 %43, %44
  %46 = mul nsw i32 %42, %45
  ret i32 %46
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @printInt(i32 %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @.str, i64 0, i64 0), i32 %3)
  ret void
}

declare dso_local i32 @printf(i8*, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @main() #0 {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca i32, align 4
  %8 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  %9 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.str.1, i64 0, i64 0))
  store i32 10, i32* %2, align 4
  %10 = call i32 (...) @getint()
  store i32 %10, i32* %3, align 4
  %11 = call i32 (...) @getint()
  store i32 %11, i32* %4, align 4
  %12 = call i32 (...) @getint()
  store i32 %12, i32* %5, align 4
  %13 = call i32 (...) @getint()
  store i32 %13, i32* %6, align 4
  %14 = load i32, i32* %3, align 4
  %15 = icmp sgt i32 %14, 5
  br i1 %15, label %16, label %17

16:                                               ; preds = %0
  store i32 5, i32* %3, align 4
  br label %17

17:                                               ; preds = %16, %0
  br label %18

18:                                               ; preds = %69, %17
  %19 = load i32, i32* %2, align 4
  %20 = icmp ne i32 %19, 0
  br i1 %20, label %21, label %70

21:                                               ; preds = %18
  %22 = load i32, i32* %2, align 4
  %23 = sub nsw i32 %22, 1
  store i32 %23, i32* %2, align 4
  %24 = load i32, i32* %4, align 4
  %25 = load i32, i32* %2, align 4
  %26 = icmp sge i32 %24, %25
  br i1 %26, label %27, label %34

27:                                               ; preds = %21
  %28 = load i32, i32* %4, align 4
  %29 = load i32, i32* %2, align 4
  %30 = add nsw i32 %29, 1
  %31 = sdiv i32 %28, %30
  %32 = load i32, i32* %2, align 4
  %33 = add nsw i32 %31, %32
  store i32 %33, i32* %4, align 4
  br label %34

34:                                               ; preds = %27, %21
  %35 = load i32, i32* %5, align 4
  %36 = load i32, i32* %2, align 4
  %37 = icmp sle i32 %35, %36
  br i1 %37, label %38, label %42

38:                                               ; preds = %34
  %39 = load i32, i32* %5, align 4
  %40 = load i32, i32* %2, align 4
  %41 = mul nsw i32 %39, %40
  store i32 %41, i32* %5, align 4
  br label %47

42:                                               ; preds = %34
  %43 = load i32, i32* %5, align 4
  %44 = load i32, i32* %2, align 4
  %45 = add nsw i32 %44, 3
  %46 = srem i32 %43, %45
  store i32 %46, i32* %5, align 4
  br label %47

47:                                               ; preds = %42, %38
  br label %48

48:                                               ; preds = %68, %64, %47
  %49 = load i32, i32* %6, align 4
  %50 = load i32, i32* %5, align 4
  %51 = icmp slt i32 %49, %50
  br i1 %51, label %52, label %69

52:                                               ; preds = %48
  %53 = load i32, i32* %6, align 4
  %54 = load i32, i32* %2, align 4
  %55 = add nsw i32 %53, %54
  store i32 %55, i32* %6, align 4
  %56 = load i32, i32* %6, align 4
  %57 = load i32, i32* %3, align 4
  %58 = icmp eq i32 %56, %57
  br i1 %58, label %59, label %60

59:                                               ; preds = %52
  br label %69

60:                                               ; preds = %52
  %61 = load i32, i32* %6, align 4
  %62 = load i32, i32* %4, align 4
  %63 = icmp ne i32 %61, %62
  br i1 %63, label %64, label %68

64:                                               ; preds = %60
  %65 = load i32, i32* %4, align 4
  %66 = load i32, i32* %6, align 4
  %67 = add nsw i32 %65, %66
  store i32 %67, i32* %6, align 4
  br label %48, !llvm.loop !2

68:                                               ; preds = %60
  br label %48, !llvm.loop !2

69:                                               ; preds = %59, %48
  br label %18, !llvm.loop !4

70:                                               ; preds = %18
  %71 = load i32, i32* %2, align 4
  %72 = icmp ne i32 %71, 0
  br i1 %72, label %75, label %73

73:                                               ; preds = %70
  %74 = load i32, i32* %2, align 4
  call void @printInt(i32 %74)
  br label %75

75:                                               ; preds = %73, %70
  %76 = load i32, i32* %3, align 4
  call void @printInt(i32 %76)
  %77 = load i32, i32* %4, align 4
  call void @printInt(i32 %77)
  %78 = load i32, i32* %5, align 4
  call void @printInt(i32 %78)
  %79 = load i32, i32* %6, align 4
  call void @printInt(i32 %79)
  %80 = load i32, i32* %6, align 4
  %81 = load i32, i32* %5, align 4
  %82 = call i32 @funcDef_1(i32 %80, i32 %81)
  store i32 %82, i32* %7, align 4
  %83 = load i32, i32* %7, align 4
  %84 = load i32, i32* %4, align 4
  %85 = call i32 @funcDef_0(i32 %84)
  %86 = call i32 @funcDef_1(i32 %83, i32 %85)
  store i32 %86, i32* %8, align 4
  call void @funcDef_void()
  %87 = load i32, i32* %7, align 4
  call void @printInt(i32 %87)
  %88 = load i32, i32* %8, align 4
  call void @printInt(i32 %88)
  %89 = load i32, i32* @singleVarDecl, align 4
  %90 = load i32, i32* @singleVarDecl_2, align 4
  %91 = call i32 @funcDef_1(i32 %89, i32 %90)
  %92 = call i32 @funcDef_1(i32 13, i32 3)
  %93 = call i32 @funcDef_1(i32 %91, i32 %92)
  store i32 %93, i32* @singleVarDecl_2, align 4
  %94 = load i32, i32* @singleVarDecl_2, align 4
  call void @printInt(i32 %94)
  ret i32 0
}

declare dso_local i32 @getint(...) #1

attributes #0 = { noinline nounwind optnone uwtable "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "min-legal-vector-width"="0" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "disable-tail-calls"="false" "frame-pointer"="all" "less-precise-fpmad"="false" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" "unsafe-fp-math"="false" "use-soft-float"="false" }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"wchar_size", i32 4}
!1 = !{!"Ubuntu clang version 12.0.1-19ubuntu3"}
!2 = distinct !{!2, !3}
!3 = !{!"llvm.loop.mustprogress"}
!4 = distinct !{!4, !3}
