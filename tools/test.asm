.data
	g_0: .space 40
	g_1: .word 0
	s_1: .asciiz " "
	s_2: .asciiz "\n"
	s_0: .asciiz "x[i]-i: "


.text
	
# jump to main
	jal main
	j end
	
main:
	
_0:
	
# br label %b_1
	j _1
	
_1:
	
# %v0 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -4($sp)
	
# %v1 = icmp sle i32 %v0, 10
	lw $k0 -4($sp)
	li $k1 10
	sle $k0 $k0 $k1
	sw $k0 -8($sp)
	
# %v2 = zext i1 %v1 to i32
	
# %v3 = icmp ne i32 %v2, 0
	lw $k0 -8($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -12($sp)
	
# br i1 %v3, label %b_2, label %b_4
	lw $k0 -12($sp)
	bne $k0 $zero _2
	j _4
	
_2:
	
# %v5 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -16($sp)
	
# %v6 = icmp eq i32 %v5, 2
	lw $k0 -16($sp)
	li $k1 2
	seq $k0 $k0 $k1
	sw $k0 -20($sp)
	
# %v7 = zext i1 %v6 to i32
	
# %v8 = icmp ne i32 %v7, 0
	lw $k0 -20($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -24($sp)
	
# br i1 %v8, label %b_5, label %b_6
	lw $k0 -24($sp)
	bne $k0 $zero _5
	j _6
	
# br label %b_5
	j _5
	
_3:
	
# br label %b_1
	j _1
	
_4:
	
# ret i32 0
	li $v0 0
	jr $ra
	
_5:
	
# store i32 4, i32* @g_1
	li $k0 4
	la $k1, g_1
	sw $k0 0($k1)
	
# br label %b_3
	j _3
	
# br label %b_6
	j _6
	
_6:
	
# %v10 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -28($sp)
	
# %v11 = getelementptr inbounds [10 x i32], [10 x i32]* @g_0, i32 0, i32 %v10
	la $k0, g_0
	lw $k1 -28($sp)
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -32($sp)
	
# %v12 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -36($sp)
	
# store i32 %v12, i32* %v11
	lw $k0 -36($sp)
	lw $k1 -32($sp)
	sw $k0 0($k1)
	
# %v13 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -40($sp)
	
# %v14 = add i32 %v13, 1
	lw $k0 -40($sp)
	li $k1 1
	addu $k0 $k0 $k1
	sw $k0 -44($sp)
	
# store i32 %v14, i32* @g_1
	lw $k0 -44($sp)
	la $k1, g_1
	sw $k0 0($k1)
	
# call void @putstr(i8* getelementptr inbounds ([9 x i8], [9 x i8]* @s_0, i64 0, i64 0))
	la $a0, s_0
	li $v0 4
	syscall
	
# %v16 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -48($sp)
	
# %v17 = getelementptr inbounds [10 x i32], [10 x i32]* @g_0, i32 0, i32 %v16
	la $k0, g_0
	lw $k1 -48($sp)
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -52($sp)
	
# %v18 = load i32, i32* %v17
	lw $k0 -52($sp)
	lw $k0 0($k0)
	sw $k0 -56($sp)
	
# call void @putint(i32 %v18)
	lw $a0 -56($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# %v21 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -60($sp)
	
# call void @putint(i32 %v21)
	lw $a0 -60($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_2, i64 0, i64 0))
	la $a0, s_2
	li $v0 4
	syscall
	
# %v24 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -64($sp)
	
# %v25 = icmp eq i32 %v24, 5
	lw $k0 -64($sp)
	li $k1 5
	seq $k0 $k0 $k1
	sw $k0 -68($sp)
	
# %v26 = zext i1 %v25 to i32
	
# %v27 = icmp ne i32 %v26, 0
	lw $k0 -68($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -72($sp)
	
# br i1 %v27, label %b_7, label %b_8
	lw $k0 -72($sp)
	bne $k0 $zero _7
	j _8
	
# br label %b_7
	j _7
	
_7:
	
# br label %b_4
	j _4
	
# br label %b_8
	j _8
	
_8:
	
# br label %b_3
	j _3
	
end:
	li $v0 10
	syscall
