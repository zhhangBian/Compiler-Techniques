.data
	s_3: .asciiz " "
	s_0: .asciiz "->"
	s_1: .asciiz "\n"
	s_2: .asciiz "enter hanoi: "


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
	
# call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @s_2, i64 0, i64 0))
	la $a0, s_2
	li $v0 4
	syscall
	
# %v9 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -52($sp)
	
# call void @putint(i32 %v9)
	lw $a0 -52($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# %v12 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -56($sp)
	
# call void @putint(i32 %v12)
	lw $a0 -56($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# %v15 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -60($sp)
	
# call void @putint(i32 %v15)
	lw $a0 -60($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# %v18 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -64($sp)
	
# call void @putint(i32 %v18)
	lw $a0 -64($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# %v21 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -68($sp)
	
# %v22 = icmp eq i32 %v21, 1
	lw $k0 -68($sp)
	li $k1 1
	seq $k0 $k0 $k1
	sw $k0 -72($sp)
	
# %v23 = zext i1 %v22 to i32
	lw $k0 -72($sp)
	sw $k0 -76($sp)
	
# %v24 = icmp ne i32 %v23, 0
	lw $k0 -76($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -80($sp)
	
# br i1 %v24, label %b_2, label %b_3
	lw $k0 -80($sp)
	bne $k0 $zero _2
	j _3
	
# br label %b_2
	j _2
	
_2:
	
# %v26 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -84($sp)
	
# %v27 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -88($sp)
	
# call void @f_move(i32 %v26, i32 %v27)
	sw $a2 -92($sp)
	sw $a3 -96($sp)
	sw $a1 -100($sp)
	sw $sp -104($sp)
	sw $ra -108($sp)
	lw $a1 -84($sp)
	lw $a2 -88($sp)
	addi $sp $sp -108
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -92($sp)
	lw $a3 -96($sp)
	lw $a1 -100($sp)
	sw $v0 -92($sp)
	
# br label %b_4
	j _4
	
_3:
	
# %v28 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -96($sp)
	
# %v29 = sub i32 %v28, 1
	lw $k0 -96($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -100($sp)
	
# %v30 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -104($sp)
	
# %v31 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -108($sp)
	
# %v32 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -112($sp)
	
# call void @f_hanoi(i32 %v29, i32 %v30, i32 %v31, i32 %v32)
	sw $a2 -116($sp)
	sw $a3 -120($sp)
	sw $a1 -124($sp)
	sw $sp -128($sp)
	sw $ra -132($sp)
	lw $a1 -100($sp)
	lw $a2 -104($sp)
	lw $a3 -108($sp)
	lw $k0 -112($sp)
	sw $k0 -148($sp)
	addi $sp $sp -132
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -116($sp)
	lw $a3 -120($sp)
	lw $a1 -124($sp)
	sw $v0 -116($sp)
	
# %v33 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -120($sp)
	
# %v34 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -124($sp)
	
# call void @f_move(i32 %v33, i32 %v34)
	sw $a2 -128($sp)
	sw $a3 -132($sp)
	sw $a1 -136($sp)
	sw $sp -140($sp)
	sw $ra -144($sp)
	lw $a1 -120($sp)
	lw $a2 -124($sp)
	addi $sp $sp -144
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -128($sp)
	lw $a3 -132($sp)
	lw $a1 -136($sp)
	sw $v0 -128($sp)
	
# %v35 = load i32, i32* %v4
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -132($sp)
	
# %v36 = sub i32 %v35, 1
	lw $k0 -132($sp)
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -136($sp)
	
# %v37 = load i32, i32* %v6
	lw $k0 -40($sp)
	lw $k0 0($k0)
	sw $k0 -140($sp)
	
# %v38 = load i32, i32* %v5
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -144($sp)
	
# %v39 = load i32, i32* %v7
	lw $k0 -48($sp)
	lw $k0 0($k0)
	sw $k0 -148($sp)
	
# call void @f_hanoi(i32 %v36, i32 %v37, i32 %v38, i32 %v39)
	sw $a2 -152($sp)
	sw $a3 -156($sp)
	sw $a1 -160($sp)
	sw $sp -164($sp)
	sw $ra -168($sp)
	lw $a1 -136($sp)
	lw $a2 -140($sp)
	lw $a3 -144($sp)
	lw $k0 -148($sp)
	sw $k0 -184($sp)
	addi $sp $sp -168
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a2 -152($sp)
	lw $a3 -156($sp)
	lw $a1 -160($sp)
	sw $v0 -152($sp)
	
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
