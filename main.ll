declare i32 @getint()
declare i32 @getchar()
declare void @putint(i32)
declare void @putchar(i32)
declare void @putstr(i8*)


@.str = private unnamed_addr constant [5 x i8] c"a = \00", align 1
@.str.1 = private unnamed_addr constant [7 x i8] c", b = \00", align 1
@.str.2 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.3 = private unnamed_addr constant [5 x i8] c"b = \00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1

define dso_local i32 @main() {
	%var1 = alloca i32
	store i32 255, i32* %var1
	%var2 = alloca i8
	store i8 2, i8* %var2
	call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str, i64 0, i64 0))
	%var3 = load i32, i32* %var1
	call void @putint(i32 %var3)
	call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.1, i64 0, i64 0))
	%var4 = load i8, i8* %var2
	%var5 = zext i8 %var4 to i32
	call void @putint(i32 %var5)
	call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.2, i64 0, i64 0))
	%var6 = load i32, i32* %var1
	%var7 = load i8, i8* %var2
	%var8 = zext i8 %var7 to i32
	%var9 = add nsw i32 %var6, %var8
	store i32 %var9, i8* %var2
	call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.3, i64 0, i64 0))
	%var11 = load i8, i8* %var2
	%var12 = zext i8 %var11 to i32
	call void @putint(i32 %var12)
	call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4, i64 0, i64 0))
	ret i32 0
}
