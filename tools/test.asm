.data
	s_0: .asciiz "->"
	s_1: .asciiz "\n"
	s_2: .asciiz "fun1:\n"


.text
	
# jump to main
	jal main
	j end
	
f_move:
	
_0:
	
# %v2 = alloca i32
	addi $k0 $sp -12
	sw $k0 -16($sp)
	
# store i32 %v0, i32* %v2
	move $k0 $a1
	lw $k1 -16($sp)
	sw $k0 0($k1)
	
# %v3 = alloca i32
	addi $k0 $sp -20
	sw $k0 -24($sp)
	
# store i32 %v1, i32* %v3
	move $k0 $a2
	lw $k1 -24($sp)
	sw $k0 0($k1)
	
# %v4 = load i32, i32* %v2
	lw $k0 -16($sp)
	lw $k0 0($k0)
	sw $k0 -28($sp)
	
# call void @putint(i32 %v4)
	lw $a0 -28($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @s_0, i64 0, i64 0))
	la $a0, s_0
	li $v0 4
	syscall
	
# %v7 = load i32, i32* %v3
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -32($sp)
	
# call void @putint(i32 %v7)
	lw $a0 -32($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# ret void
	jr $ra
	
f_hanoi:
	
_1:
	
# %v4 = alloca i32
	addi $k0 $sp -20
	sw $k0 -24($sp)
	
# store i32 %v0, i32* %v4
	move $k0 $a1
	lw $k1 -24($sp)
	sw $k0 0($k1)
	
# %v5 = alloca i32
	addi $k0 $sp -28
	sw $k0 -32($sp)
	
# store i32 %v1, i32* %v5
	move $k0 $a2
	lw $k1 -32($sp)
	sw $k0 0($k1)
	
# %v6 = alloca i32
	addi $k0 $sp -36
	sw $k0 -40($sp)
	
# store i32 %v2, i32* %v6
	move $k0 $a3
	lw $k1 -40($sp)
	sw $k0 0($k1)
	
# %v7 = alloca i32
	addi $k0 $sp -44
	sw $k0 -48($sp)
	
# store i32 %v3, i32* %v7
	lw $k0 -16($sp)
	lw $k1 -48($sp)
	sw $k0 0($k1)
	
# %v8 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -52($sp)
	
# %v9 = icmp eq i32 %v8, 1
	lw $k0 -52($sp)
	li $k1 1
	seq $k0 $k0 $k1
	sw $k0 -56($sp)
	
# %v10 = zext i1 %v9 to i32
	
# %v11 = icmp ne i32 %v10, 0
	lw $k0 -56($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -60($sp)
	
# br i1 %v11, label %b_2, label %b_3
	lw $k0 -60($sp)
	bne $k0 $zero _2
	j _3
	
# br label %b_2
	j _2
	
_2:
	
# %v13 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -64($sp)
	
# %v14 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -68($sp)
	
# call void @f_move(i32 %v13, i32 %v14)
	sw $a2 -72($sp)
	sw $a3 -76($sp)
	sw $a1 -80($sp)
	sw $sp -84($sp)
	sw $ra -88($sp)
	lw $a1 -64($sp)
	lw $a2 -68($sp)
	addi $sp $sp -88
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -68($sp)
	lw $a3 -72($sp)
	lw $a1 -76($sp)
	sw $v0 -72($sp)
	
# br label %b_4
	j _4
	
_3:
	
# %v15 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -76($sp)
	
# %v16 = sub i32 %v15, 1
	lw $k0 -76($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -80($sp)
	
# %v17 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -84($sp)
	
# %v18 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -88($sp)
	
# %v19 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -92($sp)
	
# call void @f_hanoi(i32 %v16, i32 %v17, i32 %v18, i32 %v19)
	sw $a2 -96($sp)
	sw $a3 -100($sp)
	sw $a1 -104($sp)
	sw $sp -108($sp)
	sw $ra -112($sp)
	lw $a1 -80($sp)
	lw $a2 -84($sp)
	lw $a3 -88($sp)
	lw $k0 -92($sp)
	sw $k0 -128($sp)
	addi $sp $sp -112
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -92($sp)
	lw $a3 -96($sp)
	lw $a1 -100($sp)
	sw $v0 -96($sp)
	
# %v20 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -100($sp)
	
# %v21 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -104($sp)
	
# call void @f_move(i32 %v20, i32 %v21)
	sw $a2 -108($sp)
	sw $a3 -112($sp)
	sw $a1 -116($sp)
	sw $sp -120($sp)
	sw $ra -124($sp)
	lw $a1 -100($sp)
	lw $a2 -104($sp)
	addi $sp $sp -124
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -104($sp)
	lw $a3 -108($sp)
	lw $a1 -112($sp)
	sw $v0 -108($sp)
	
# %v22 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -112($sp)
	
# %v23 = sub i32 %v22, 1
	lw $k0 -112($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -116($sp)
	
# %v24 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -120($sp)
	
# %v25 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -124($sp)
	
# %v26 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -128($sp)
	
# call void @f_hanoi(i32 %v23, i32 %v24, i32 %v25, i32 %v26)
	sw $a2 -132($sp)
	sw $a3 -136($sp)
	sw $a1 -140($sp)
	sw $sp -144($sp)
	sw $ra -148($sp)
	lw $a1 -116($sp)
	lw $a2 -120($sp)
	lw $a3 -124($sp)
	lw $k0 -128($sp)
	sw $k0 -164($sp)
	addi $sp $sp -148
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -128($sp)
	lw $a3 -132($sp)
	lw $a1 -136($sp)
	sw $v0 -132($sp)
	
# br label %b_4
	j _4
	
_4:
	
# ret void
	jr $ra
	
main:
	
_5:
	
# %v0 = alloca i32
	addi $k0 $sp -4
	sw $k0 -8($sp)
	
# store i32 0, i32* %v0
	li $k0 0
	lw $k1 -8($sp)
	sw $k0 0($k1)
	
# store i32 3, i32* %v0
	li $k0 3
	lw $k1 -8($sp)
	sw $k0 0($k1)
	
# %v1 = load i32, i32* %v0
	lw $k0 -8($sp)
	lw $k0 0($k0)
	sw $k0 -12($sp)
	
# call void @f_hanoi(i32 %v1, i32 1, i32 2, i32 3)
	sw $sp -16($sp)
	sw $ra -20($sp)
	lw $a1 -12($sp)
	li $a2 1
	li $a3 2
	li $k0 3
	sw $k0 -36($sp)
	addi $sp $sp -20
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -16($sp)
	
# %v2 = alloca i32
	addi $k0 $sp -20
	sw $k0 -24($sp)
	
# store i32 0, i32* %v2
	li $k0 0
	lw $k1 -24($sp)
	sw $k0 0($k1)
	
# call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @s_2, i64 0, i64 0))
	la $a0, s_2
	li $v0 4
	syscall
	
# %v4 = load i32, i32* %v2
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -28($sp)
	
# call void @putint(i32 %v4)
	lw $a0 -28($sp)
	li $v0 1
	syscall
	
# ret i32 0
	li $v0 0
	jr $ra
	
end:
	li $v0 10
	syscall
