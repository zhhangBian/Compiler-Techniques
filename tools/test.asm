.data
	s_0: .asciiz "->"
	s_1: .asciiz "\n"


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
	lw $k0 -56($sp)
	sw $k0 -60($sp)
	
# %v11 = icmp ne i32 %v10, 0
	lw $k0 -60($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -64($sp)
	
# br i1 %v11, label %b_2, label %b_3
	lw $k0 -64($sp)
	bne $k0 $zero _2
	j _3
	
# br label %b_2
	j _2
	
_2:
	
# %v13 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -68($sp)
	
# %v14 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -72($sp)
	
# call void @f_move(i32 %v13, i32 %v14)
	sw $a1 -76($sp)
	sw $a2 -80($sp)
	sw $a3 -84($sp)
	sw $sp -88($sp)
	sw $ra -92($sp)
	lw $a1 -68($sp)
	lw $a2 -72($sp)
	addi $sp $sp -92
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a1 -72($sp)
	lw $a2 -76($sp)
	lw $a3 -80($sp)
	sw $v0 -76($sp)
	
# br label %b_4
	j _4
	
_3:
	
# %v15 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -80($sp)
	
# %v16 = sub i32 %v15, 1
	lw $k0 -80($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -84($sp)
	
# %v17 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -88($sp)
	
# %v18 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -92($sp)
	
# %v19 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -96($sp)
	
# call void @f_hanoi(i32 %v16, i32 %v17, i32 %v18, i32 %v19)
	sw $a1 -100($sp)
	sw $a2 -104($sp)
	sw $a3 -108($sp)
	sw $sp -112($sp)
	sw $ra -116($sp)
	lw $a1 -84($sp)
	lw $a2 -88($sp)
	lw $a3 -92($sp)
	lw $k0 -96($sp)
	sw $k0 -132($sp)
	addi $sp $sp -116
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a1 -96($sp)
	lw $a2 -100($sp)
	lw $a3 -104($sp)
	sw $v0 -100($sp)
	
# %v20 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -104($sp)
	
# %v21 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -108($sp)
	
# call void @f_move(i32 %v20, i32 %v21)
	sw $a1 -112($sp)
	sw $a2 -116($sp)
	sw $a3 -120($sp)
	sw $sp -124($sp)
	sw $ra -128($sp)
	lw $a1 -104($sp)
	lw $a2 -108($sp)
	addi $sp $sp -128
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a1 -108($sp)
	lw $a2 -112($sp)
	lw $a3 -116($sp)
	sw $v0 -112($sp)
	
# %v22 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -116($sp)
	
# %v23 = sub i32 %v22, 1
	lw $k0 -116($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -120($sp)
	
# %v24 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -124($sp)
	
# %v25 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -128($sp)
	
# %v26 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -132($sp)
	
# call void @f_hanoi(i32 %v23, i32 %v24, i32 %v25, i32 %v26)
	sw $a1 -136($sp)
	sw $a2 -140($sp)
	sw $a3 -144($sp)
	sw $sp -148($sp)
	sw $ra -152($sp)
	lw $a1 -120($sp)
	lw $a2 -124($sp)
	lw $a3 -128($sp)
	lw $k0 -132($sp)
	sw $k0 -168($sp)
	addi $sp $sp -152
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a1 -132($sp)
	lw $a2 -136($sp)
	lw $a3 -140($sp)
	sw $v0 -136($sp)
	
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
	
# ret i32 0
	li $v0 0
	jr $ra
	
end:
	li $v0 10
	syscall
