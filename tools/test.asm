.data
	g_0: .word 10
	g_1: .word 0
	g_2: .space 40
	s_1: .asciiz "\n"
	s_0: .asciiz "22373040\n"
	s_2: .asciiz "Input integer: "
	s_7: .asciiz "Test finished!\n"
	s_6: .asciiz "Sum of array elements: "
	s_4: .asciiz "i is 4 or 9!\n"
	s_5: .asciiz "j is 32!\n"
	s_3: .asciiz "Input character: "


.text
	li $t0 51
	sw $t0 g_2+0
	li $t0 92
	sw $t0 g_2+4
	li $t0 0
	sw $t0 g_2+8
	li $t0 0
	sw $t0 g_2+12
	li $t0 0
	sw $t0 g_2+16
	li $t0 0
	sw $t0 g_2+20
	li $t0 0
	sw $t0 g_2+24
	li $t0 0
	sw $t0 g_2+28
	li $t0 0
	sw $t0 g_2+32
	li $t0 0
	sw $t0 g_2+36
	
# jump to main
	jal main
	j end
	
f_add:
	
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
	
# %v5 = load i32, i32* %v3
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -32($sp)
	
# %v6 = add i32 %v4, %v5
	lw $k0 -28($sp)
	lw $k1 -32($sp)
	addu $k0 $k0 $k1
	sw $k0 -36($sp)
	
# ret i32 %v6
	lw $v0 -36($sp)
	jr $ra
	
f_calculate:
	
_1:
	
# %v2 = alloca i32
	addi $k0 $sp -12
	sw $k0 -16($sp)
	
# store i32 %v0, i32* %v2
	move $k0 $a1
	lw $k1 -16($sp)
	sw $k0 0($k1)
	
# %v3 = alloca i32*
	addi $k0 $sp -20
	sw $k0 -24($sp)
	
# store i32* %v1, i32** %v3
	move $k0 $a2
	lw $k1 -24($sp)
	sw $k0 0($k1)
	
# %v4 = alloca i32
	addi $k0 $sp -28
	sw $k0 -32($sp)
	
# %v5 = load i32, i32* %v2
	lw $k0 -16($sp)
	lw $k0 0($k0)
	sw $k0 -36($sp)
	
# %v6 = load i32*, i32** %v3
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -40($sp)
	
# %v7 = getelementptr inbounds i32, i32* %v6, i32 0
	lw $k0 -40($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -44($sp)
	
# %v8 = load i32, i32* %v7
	lw $k0 -44($sp)
	lw $k0 0($k0)
	sw $k0 -48($sp)
	
# %v9 = call i32 @f_add(i32 %v5, i32 %v8)
	sw $a1 -52($sp)
	sw $a2 -56($sp)
	sw $sp -60($sp)
	sw $ra -64($sp)
	lw $a1 -36($sp)
	lw $a2 -48($sp)
	addi $sp $sp -64
	jal f_add
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a1 -48($sp)
	lw $a2 -52($sp)
	sw $v0 -52($sp)
	
# %v10 = load i32, i32* %v2
	lw $k0 -16($sp)
	lw $k0 0($k0)
	sw $k0 -56($sp)
	
# %v11 = load i32*, i32** %v3
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -60($sp)
	
# %v12 = getelementptr inbounds i32, i32* %v11, i32 1
	lw $k0 -60($sp)
	li $k1 1
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -64($sp)
	
# %v13 = load i32, i32* %v12
	lw $k0 -64($sp)
	lw $k0 0($k0)
	sw $k0 -68($sp)
	
# %v14 = sub i32 %v10, %v13
	lw $k0 -56($sp)
	lw $k1 -68($sp)
	subu $k0 $k0 $k1
	sw $k0 -72($sp)
	
# %v15 = mul i32 %v9, %v14
	lw $k0 -52($sp)
	lw $k1 -72($sp)
	mult $k0 $k1
	mflo $k0
	sw $k0 -76($sp)
	
# %v16 = load i32*, i32** %v3
	lw $k0 -24($sp)
	lw $k0 0($k0)
	sw $k0 -80($sp)
	
# %v17 = getelementptr inbounds i32, i32* %v16, i32 2
	lw $k0 -80($sp)
	li $k1 2
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -84($sp)
	
# %v18 = load i32, i32* %v17
	lw $k0 -84($sp)
	lw $k0 0($k0)
	sw $k0 -88($sp)
	
# %v19 = sdiv i32 %v15, %v18
	lw $k0 -76($sp)
	lw $k1 -88($sp)
	div $k0 $k1
	mflo $k0
	sw $k0 -92($sp)
	
# %v20 = load i32, i32* %v2
	lw $k0 -16($sp)
	lw $k0 0($k0)
	sw $k0 -96($sp)
	
# %v21 = srem i32 %v19, %v20
	lw $k0 -92($sp)
	lw $k1 -96($sp)
	div $k0 $k1
	mfhi $k0
	sw $k0 -100($sp)
	
# %v22 = sub i32 0, 3
	li $k0 0
	li $k1 3
	subu $k0 $k0 $k1
	sw $k0 -104($sp)
	
# %v23 = sub i32 0, %v22
	li $k0 0
	lw $k1 -104($sp)
	subu $k0 $k0 $k1
	sw $k0 -108($sp)
	
# %v24 = sub i32 0, %v23
	li $k0 0
	lw $k1 -108($sp)
	subu $k0 $k0 $k1
	sw $k0 -112($sp)
	
# %v25 = sub i32 %v21, %v24
	lw $k0 -100($sp)
	lw $k1 -112($sp)
	subu $k0 $k0 $k1
	sw $k0 -116($sp)
	
# %v26 = sub i32 0, 6
	li $k0 0
	li $k1 6
	subu $k0 $k0 $k1
	sw $k0 -120($sp)
	
# %v27 = sub i32 0, %v26
	li $k0 0
	lw $k1 -120($sp)
	subu $k0 $k0 $k1
	sw $k0 -124($sp)
	
# %v28 = add i32 %v25, %v27
	lw $k0 -116($sp)
	lw $k1 -124($sp)
	addu $k0 $k0 $k1
	sw $k0 -128($sp)
	
# store i32 %v28, i32* %v4
	lw $k0 -128($sp)
	lw $k1 -32($sp)
	sw $k0 0($k1)
	
# %v29 = load i32, i32* %v4
	lw $k0 -32($sp)
	lw $k0 0($k0)
	sw $k0 -132($sp)
	
# %v30 = icmp sle i32 %v29, 5
	lw $k0 -132($sp)
	li $k1 5
	sle $k0 $k0 $k1
	sw $k0 -136($sp)
	
# %v31 = zext i1 %v30 to i32
	lw $k0 -136($sp)
	sw $k0 -140($sp)
	
# %v32 = icmp ne i32 %v31, 0
	lw $k0 -140($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -144($sp)
	
# br i1 %v32, label %b_2, label %b_3
	lw $k0 -144($sp)
	bne $k0 $zero _2
	j _3
	
# br label %b_2
	j _2
	
_2:
	
# ret i32 1
	li $v0 1
	jr $ra
	
# br label %b_4
	j _4
	
_3:
	
# ret i32 0
	li $v0 0
	jr $ra
	
# br label %b_4
	j _4
	
_4:
	
# %v34 = sub i32 0, 1
	li $k0 0
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -148($sp)
	
# ret i32 %v34
	lw $v0 -148($sp)
	jr $ra
	
f_printName:
	
_5:
	
# %v0 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -4($sp)
	
# %v1 = add i32 %v0, 1
	lw $k0 -4($sp)
	li $k1 1
	addu $k0 $k0 $k1
	sw $k0 -8($sp)
	
# store i32 %v1, i32* @g_1
	lw $k0 -8($sp)
	la $k1, g_1
	sw $k0 0($k1)
	
# %v2 = load i32, i32* @g_1
	la $k0, g_1
	lw $k0 0($k0)
	sw $k0 -12($sp)
	
# %v3 = icmp ne i32 %v2, 0
	lw $k0 -12($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -16($sp)
	
# %v4 = zext i1 %v3 to i32
	lw $k0 -16($sp)
	sw $k0 -20($sp)
	
# %v5 = icmp ne i32 %v4, 0
	lw $k0 -20($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -24($sp)
	
# br i1 %v5, label %b_6, label %b_7
	lw $k0 -24($sp)
	bne $k0 $zero _6
	j _7
	
# br label %b_6
	j _6
	
_6:
	
# call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @s_0, i64 0, i64 0))
	la $a0, s_0
	li $v0 4
	syscall
	
# br label %b_7
	j _7
	
_7:
	
# ret void
	jr $ra
	
f_print:
	
_8:
	
# %v1 = alloca i8
	addi $k0 $sp -8
	sw $k0 -12($sp)
	
# store i8 %v0, i8* %v1
	move $k0 $a1
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# %v2 = load i8, i8* %v1
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -16($sp)
	
# call void @putch(i8 %v2)
	lw $a0 -16($sp)
	li $v0 11
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# ret void
	jr $ra
	
f_get_first:
	
_9:
	
# %v1 = alloca i8*
	addi $k0 $sp -8
	sw $k0 -12($sp)
	
# store i8* %v0, i8** %v1
	move $k0 $a1
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# %v2 = load i8*, i8** %v1
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -16($sp)
	
# %v3 = getelementptr inbounds i8, i8* %v2, i32 0
	lw $k0 -16($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -20($sp)
	
# %v4 = load i8, i8* %v3
	lw $k0 -20($sp)
	lw $k0 0($k0)
	sw $k0 -24($sp)
	
# ret i8 %v4
	lw $v0 -24($sp)
	jr $ra
	
main:
	
_10:
	
# call void @f_printName()
	sw $sp -4($sp)
	sw $ra -8($sp)
	addi $sp $sp -8
	jal f_printName
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -4($sp)
	
# %v0 = alloca i32
	addi $k0 $sp -8
	sw $k0 -12($sp)
	
# store i32 0, i32* %v0
	li $k0 0
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# %v1 = alloca i32
	addi $k0 $sp -16
	sw $k0 -20($sp)
	
# store i32 8, i32* %v1
	li $k0 8
	lw $k1 -20($sp)
	sw $k0 0($k1)
	
# %v2 = alloca i8
	addi $k0 $sp -24
	sw $k0 -28($sp)
	
# %v3 = call i32 @getint()
	li $v0 5
	syscall
	sw $v0 -32($sp)
	
# store i32 %v3, i32* %v0
	lw $k0 -32($sp)
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# %v4 = call i32 @getchar()
	li $v0 12
	syscall
	sw $v0 -36($sp)
	
# %v5 = trunc i32 %v4 to i8
	lw $k0 -36($sp)
	andi $k0 $k0 255
	sw $k0 -40($sp)
	
# store i8 %v5, i8* %v2
	lw $k0 -40($sp)
	lw $k1 -28($sp)
	sw $k0 0($k1)
	
# call void @putstr(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @s_2, i64 0, i64 0))
	la $a0, s_2
	li $v0 4
	syscall
	
# %v7 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -44($sp)
	
# call void @putint(i32 %v7)
	lw $a0 -44($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([18 x i8], [18 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# %v11 = load i8, i8* %v2
	lw $k0 -28($sp)
	lw $k0 0($k0)
	sw $k0 -48($sp)
	
# call void @putch(i8 %v11)
	lw $a0 -48($sp)
	li $v0 11
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# %v14 = alloca i32
	addi $k0 $sp -52
	sw $k0 -56($sp)
	
# store i32 5, i32* %v14
	li $k0 5
	lw $k1 -56($sp)
	sw $k0 0($k1)
	
# %v15 = alloca [10 x i32]
	addi $k0 $sp -96
	sw $k0 -100($sp)
	
# %v16 = alloca [12 x i8]
	addi $k0 $sp -148
	sw $k0 -152($sp)
	
# %v17 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 0
	lw $k0 -152($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -156($sp)
	
# store i8 113, i8* %v17
	li $k0 113
	lw $k1 -156($sp)
	sw $k0 0($k1)
	
# %v18 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 1
	lw $k0 -152($sp)
	li $k1 1
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -160($sp)
	
# store i8 119, i8* %v18
	li $k0 119
	lw $k1 -160($sp)
	sw $k0 0($k1)
	
# %v19 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 2
	lw $k0 -152($sp)
	li $k1 2
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -164($sp)
	
# store i8 101, i8* %v19
	li $k0 101
	lw $k1 -164($sp)
	sw $k0 0($k1)
	
# %v20 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 3
	lw $k0 -152($sp)
	li $k1 3
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -168($sp)
	
# store i8 114, i8* %v20
	li $k0 114
	lw $k1 -168($sp)
	sw $k0 0($k1)
	
# %v21 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 4
	lw $k0 -152($sp)
	li $k1 4
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -172($sp)
	
# store i8 116, i8* %v21
	li $k0 116
	lw $k1 -172($sp)
	sw $k0 0($k1)
	
# %v22 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 5
	lw $k0 -152($sp)
	li $k1 5
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -176($sp)
	
# store i8 121, i8* %v22
	li $k0 121
	lw $k1 -176($sp)
	sw $k0 0($k1)
	
# %v23 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 6
	lw $k0 -152($sp)
	li $k1 6
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -180($sp)
	
# store i8 117, i8* %v23
	li $k0 117
	lw $k1 -180($sp)
	sw $k0 0($k1)
	
# %v24 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 7
	lw $k0 -152($sp)
	li $k1 7
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -184($sp)
	
# store i8 105, i8* %v24
	li $k0 105
	lw $k1 -184($sp)
	sw $k0 0($k1)
	
# %v25 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 8
	lw $k0 -152($sp)
	li $k1 8
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -188($sp)
	
# store i8 111, i8* %v25
	li $k0 111
	lw $k1 -188($sp)
	sw $k0 0($k1)
	
# %v26 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 9
	lw $k0 -152($sp)
	li $k1 9
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -192($sp)
	
# store i8 112, i8* %v26
	li $k0 112
	lw $k1 -192($sp)
	sw $k0 0($k1)
	
# %v27 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i8 11
	lw $k0 -152($sp)
	li $k1 11
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -196($sp)
	
# store i8 10, i8* %v27
	li $k0 10
	lw $k1 -196($sp)
	sw $k0 0($k1)
	
# %v28 = alloca [10 x i8]
	addi $k0 $sp -236
	sw $k0 -240($sp)
	
# %v29 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 0
	lw $k0 -240($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -244($sp)
	
# store i8 115, i8* %v29
	li $k0 115
	lw $k1 -244($sp)
	sw $k0 0($k1)
	
# %v30 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 1
	lw $k0 -240($sp)
	li $k1 1
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -248($sp)
	
# store i8 116, i8* %v30
	li $k0 116
	lw $k1 -248($sp)
	sw $k0 0($k1)
	
# %v31 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 2
	lw $k0 -240($sp)
	li $k1 2
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -252($sp)
	
# store i8 114, i8* %v31
	li $k0 114
	lw $k1 -252($sp)
	sw $k0 0($k1)
	
# %v32 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 3
	lw $k0 -240($sp)
	li $k1 3
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -256($sp)
	
# store i8 0, i8* %v32
	li $k0 0
	lw $k1 -256($sp)
	sw $k0 0($k1)
	
# %v33 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 4
	lw $k0 -240($sp)
	li $k1 4
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -260($sp)
	
# store i8 0, i8* %v33
	li $k0 0
	lw $k1 -260($sp)
	sw $k0 0($k1)
	
# %v34 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 5
	lw $k0 -240($sp)
	li $k1 5
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -264($sp)
	
# store i8 0, i8* %v34
	li $k0 0
	lw $k1 -264($sp)
	sw $k0 0($k1)
	
# %v35 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 6
	lw $k0 -240($sp)
	li $k1 6
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -268($sp)
	
# store i8 0, i8* %v35
	li $k0 0
	lw $k1 -268($sp)
	sw $k0 0($k1)
	
# %v36 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 7
	lw $k0 -240($sp)
	li $k1 7
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -272($sp)
	
# store i8 0, i8* %v36
	li $k0 0
	lw $k1 -272($sp)
	sw $k0 0($k1)
	
# %v37 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 8
	lw $k0 -240($sp)
	li $k1 8
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -276($sp)
	
# store i8 0, i8* %v37
	li $k0 0
	lw $k1 -276($sp)
	sw $k0 0($k1)
	
# %v38 = getelementptr inbounds [10 x i8], [10 x i8]* %v28, i32 0, i8 9
	lw $k0 -240($sp)
	li $k1 9
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -280($sp)
	
# store i8 0, i8* %v38
	li $k0 0
	lw $k1 -280($sp)
	sw $k0 0($k1)
	
# store i32 0, i32* %v0
	li $k0 0
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# br label %b_11
	j _11
	
_11:
	
# %v39 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -284($sp)
	
# %v40 = load i32, i32* @g_0
	la $k0, g_0
	lw $k0 0($k0)
	sw $k0 -288($sp)
	
# %v41 = icmp slt i32 %v39, %v40
	lw $k0 -284($sp)
	lw $k1 -288($sp)
	slt $k0 $k0 $k1
	sw $k0 -292($sp)
	
# %v42 = zext i1 %v41 to i32
	lw $k0 -292($sp)
	sw $k0 -296($sp)
	
# %v43 = icmp ne i32 %v42, 0
	lw $k0 -296($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -300($sp)
	
# br i1 %v43, label %b_12, label %b_14
	lw $k0 -300($sp)
	bne $k0 $zero _12
	j _14
	
_12:
	
# %v45 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -304($sp)
	
# %v46 = getelementptr inbounds [10 x i32], [10 x i32]* %v15, i32 0, i32 %v45
	lw $k0 -100($sp)
	lw $k1 -304($sp)
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -308($sp)
	
# %v47 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -312($sp)
	
# store i32 %v47, i32* %v46
	lw $k0 -312($sp)
	lw $k1 -308($sp)
	sw $k0 0($k1)
	
# %v48 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -316($sp)
	
# %v49 = icmp eq i32 %v48, 4
	lw $k0 -316($sp)
	li $k1 4
	seq $k0 $k0 $k1
	sw $k0 -320($sp)
	
# %v50 = zext i1 %v49 to i32
	lw $k0 -320($sp)
	sw $k0 -324($sp)
	
# %v51 = icmp ne i32 %v50, 0
	lw $k0 -324($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -328($sp)
	
# br i1 %v51, label %b_18, label %b_17
	lw $k0 -328($sp)
	bne $k0 $zero _18
	j _17
	
_13:
	
# %v88 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -332($sp)
	
# %v89 = add i32 %v88, 1
	lw $k0 -332($sp)
	li $k1 1
	addu $k0 $k0 $k1
	sw $k0 -336($sp)
	
# store i32 %v89, i32* %v0
	lw $k0 -336($sp)
	lw $k1 -12($sp)
	sw $k0 0($k1)
	
# br label %b_11
	j _11
	
_14:
	
# %v90 = alloca [20 x i32]
	addi $k0 $sp -416
	sw $k0 -420($sp)
	
# %v91 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 0
	lw $k0 -420($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -424($sp)
	
# store i32 3, i32* %v91
	li $k0 3
	lw $k1 -424($sp)
	sw $k0 0($k1)
	
# %v92 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 1
	lw $k0 -420($sp)
	li $k1 1
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -428($sp)
	
# store i32 2, i32* %v92
	li $k0 2
	lw $k1 -428($sp)
	sw $k0 0($k1)
	
# %v93 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 2
	lw $k0 -420($sp)
	li $k1 2
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -432($sp)
	
# store i32 1, i32* %v93
	li $k0 1
	lw $k1 -432($sp)
	sw $k0 0($k1)
	
# %v94 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 3
	lw $k0 -420($sp)
	li $k1 3
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -436($sp)
	
# store i32 0, i32* %v94
	li $k0 0
	lw $k1 -436($sp)
	sw $k0 0($k1)
	
# %v95 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 4
	lw $k0 -420($sp)
	li $k1 4
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -440($sp)
	
# store i32 0, i32* %v95
	li $k0 0
	lw $k1 -440($sp)
	sw $k0 0($k1)
	
# %v96 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 5
	lw $k0 -420($sp)
	li $k1 5
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -444($sp)
	
# store i32 0, i32* %v96
	li $k0 0
	lw $k1 -444($sp)
	sw $k0 0($k1)
	
# %v97 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 6
	lw $k0 -420($sp)
	li $k1 6
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -448($sp)
	
# store i32 0, i32* %v97
	li $k0 0
	lw $k1 -448($sp)
	sw $k0 0($k1)
	
# %v98 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 7
	lw $k0 -420($sp)
	li $k1 7
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -452($sp)
	
# store i32 0, i32* %v98
	li $k0 0
	lw $k1 -452($sp)
	sw $k0 0($k1)
	
# %v99 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 8
	lw $k0 -420($sp)
	li $k1 8
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -456($sp)
	
# store i32 0, i32* %v99
	li $k0 0
	lw $k1 -456($sp)
	sw $k0 0($k1)
	
# %v100 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 9
	lw $k0 -420($sp)
	li $k1 9
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -460($sp)
	
# store i32 0, i32* %v100
	li $k0 0
	lw $k1 -460($sp)
	sw $k0 0($k1)
	
# %v101 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 10
	lw $k0 -420($sp)
	li $k1 10
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -464($sp)
	
# store i32 0, i32* %v101
	li $k0 0
	lw $k1 -464($sp)
	sw $k0 0($k1)
	
# %v102 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 11
	lw $k0 -420($sp)
	li $k1 11
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -468($sp)
	
# store i32 0, i32* %v102
	li $k0 0
	lw $k1 -468($sp)
	sw $k0 0($k1)
	
# %v103 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 12
	lw $k0 -420($sp)
	li $k1 12
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -472($sp)
	
# store i32 0, i32* %v103
	li $k0 0
	lw $k1 -472($sp)
	sw $k0 0($k1)
	
# %v104 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 13
	lw $k0 -420($sp)
	li $k1 13
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -476($sp)
	
# store i32 0, i32* %v104
	li $k0 0
	lw $k1 -476($sp)
	sw $k0 0($k1)
	
# %v105 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 14
	lw $k0 -420($sp)
	li $k1 14
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -480($sp)
	
# store i32 0, i32* %v105
	li $k0 0
	lw $k1 -480($sp)
	sw $k0 0($k1)
	
# %v106 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 15
	lw $k0 -420($sp)
	li $k1 15
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -484($sp)
	
# store i32 0, i32* %v106
	li $k0 0
	lw $k1 -484($sp)
	sw $k0 0($k1)
	
# %v107 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 16
	lw $k0 -420($sp)
	li $k1 16
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -488($sp)
	
# store i32 0, i32* %v107
	li $k0 0
	lw $k1 -488($sp)
	sw $k0 0($k1)
	
# %v108 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 17
	lw $k0 -420($sp)
	li $k1 17
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -492($sp)
	
# store i32 0, i32* %v108
	li $k0 0
	lw $k1 -492($sp)
	sw $k0 0($k1)
	
# %v109 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 18
	lw $k0 -420($sp)
	li $k1 18
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -496($sp)
	
# store i32 0, i32* %v109
	li $k0 0
	lw $k1 -496($sp)
	sw $k0 0($k1)
	
# %v110 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 19
	lw $k0 -420($sp)
	li $k1 19
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -500($sp)
	
# store i32 0, i32* %v110
	li $k0 0
	lw $k1 -500($sp)
	sw $k0 0($k1)
	
# %v111 = alloca i32
	addi $k0 $sp -504
	sw $k0 -508($sp)
	
# store i32 0, i32* %v111
	li $k0 0
	lw $k1 -508($sp)
	sw $k0 0($k1)
	
# %v112 = alloca i32
	addi $k0 $sp -512
	sw $k0 -516($sp)
	
# store i32 0, i32* %v112
	li $k0 0
	lw $k1 -516($sp)
	sw $k0 0($k1)
	
# br label %b_35
	j _35
	
_15:
	
# call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @s_4, i64 0, i64 0))
	la $a0, s_4
	li $v0 4
	syscall
	
# %v67 = alloca i32
	addi $k0 $sp -520
	sw $k0 -524($sp)
	
# store i32 1, i32* %v67
	li $k0 1
	lw $k1 -524($sp)
	sw $k0 0($k1)
	
# br label %b_19
	j _19
	
_16:
	
# %v82 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -528($sp)
	
# %v83 = srem i32 %v82, 2
	lw $k0 -528($sp)
	li $k1 2
	div $k0 $k1
	mfhi $k0
	sw $k0 -532($sp)
	
# %v84 = icmp eq i32 %v83, 0
	lw $k0 -532($sp)
	li $k1 0
	seq $k0 $k0 $k1
	sw $k0 -536($sp)
	
# %v85 = zext i1 %v84 to i32
	lw $k0 -536($sp)
	sw $k0 -540($sp)
	
# %v86 = icmp ne i32 %v85, 0
	lw $k0 -540($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -544($sp)
	
# br i1 %v86, label %b_28, label %b_29
	lw $k0 -544($sp)
	bne $k0 $zero _28
	j _29
	
# br label %b_28
	j _28
	
_17:
	
# %v61 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -548($sp)
	
# %v62 = icmp sge i32 %v61, 9
	lw $k0 -548($sp)
	li $k1 9
	sge $k0 $k0 $k1
	sw $k0 -552($sp)
	
# %v63 = zext i1 %v62 to i32
	lw $k0 -552($sp)
	sw $k0 -556($sp)
	
# %v64 = icmp ne i32 %v63, 0
	lw $k0 -556($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -560($sp)
	
# br i1 %v64, label %b_15, label %b_16
	lw $k0 -560($sp)
	bne $k0 $zero _15
	j _16
	
# br label %b_15
	j _15
	
_18:
	
# %v53 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -564($sp)
	
# %v54 = load i8, i8* %v2
	lw $k0 -28($sp)
	lw $k0 0($k0)
	sw $k0 -568($sp)
	
# %v55 = zext i8 %v54 to i32
	lw $k0 -568($sp)
	sw $k0 -572($sp)
	
# %v56 = icmp slt i32 %v53, %v55
	lw $k0 -564($sp)
	lw $k1 -572($sp)
	slt $k0 $k0 $k1
	sw $k0 -576($sp)
	
# %v57 = zext i1 %v56 to i32
	lw $k0 -576($sp)
	sw $k0 -580($sp)
	
# %v58 = icmp ne i32 %v57, 0
	lw $k0 -580($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -584($sp)
	
# br i1 %v58, label %b_15, label %b_17
	lw $k0 -584($sp)
	bne $k0 $zero _15
	j _17
	
# br i1 %v58, label %b_15, label %b_17
	lw $k0 -584($sp)
	bne $k0 $zero _15
	j _17
	
_19:
	
# br label %b_20
	j _20
	
_20:
	
# %v68 = load i32, i32* %v67
	lw $k0 -524($sp)
	lw $k0 0($k0)
	sw $k0 -588($sp)
	
# %v69 = icmp sgt i32 %v68, 100
	lw $k0 -588($sp)
	li $k1 100
	sgt $k0 $k0 $k1
	sw $k0 -592($sp)
	
# %v70 = zext i1 %v69 to i32
	lw $k0 -592($sp)
	sw $k0 -596($sp)
	
# %v71 = icmp ne i32 %v70, 0
	lw $k0 -596($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -600($sp)
	
# br i1 %v71, label %b_23, label %b_24
	lw $k0 -600($sp)
	bne $k0 $zero _23
	j _24
	
# br label %b_23
	j _23
	
_21:
	
# %v79 = load i32, i32* %v67
	lw $k0 -524($sp)
	lw $k0 0($k0)
	sw $k0 -604($sp)
	
# %v80 = load i32, i32* %v67
	lw $k0 -524($sp)
	lw $k0 0($k0)
	sw $k0 -608($sp)
	
# %v81 = add i32 %v79, %v80
	lw $k0 -604($sp)
	lw $k1 -608($sp)
	addu $k0 $k0 $k1
	sw $k0 -612($sp)
	
# store i32 %v81, i32* %v67
	lw $k0 -612($sp)
	lw $k1 -524($sp)
	sw $k0 0($k1)
	
# br label %b_19
	j _19
	
_22:
	
# br label %b_16
	j _16
	
_23:
	
# br label %b_22
	j _22
	
# br label %b_25
	j _25
	
_24:
	
# %v73 = load i32, i32* %v67
	lw $k0 -524($sp)
	lw $k0 0($k0)
	sw $k0 -616($sp)
	
# %v74 = icmp ne i32 %v73, 32
	lw $k0 -616($sp)
	li $k1 32
	sne $k0 $k0 $k1
	sw $k0 -620($sp)
	
# %v75 = zext i1 %v74 to i32
	lw $k0 -620($sp)
	sw $k0 -624($sp)
	
# %v76 = icmp ne i32 %v75, 0
	lw $k0 -624($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -628($sp)
	
# br i1 %v76, label %b_26, label %b_27
	lw $k0 -628($sp)
	bne $k0 $zero _26
	j _27
	
# br label %b_26
	j _26
	
_25:
	
# call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @s_5, i64 0, i64 0))
	la $a0, s_5
	li $v0 4
	syscall
	
# br label %b_21
	j _21
	
_26:
	
# br label %b_21
	j _21
	
# br label %b_27
	j _27
	
_27:
	
# br label %b_25
	j _25
	
_28:
	
# br label %b_30
	j _30
	
_29:
	
# br label %b_34
	j _34
	
_30:
	
# br label %b_31
	j _31
	
_31:
	
# br label %b_33
	j _33
	
# br label %b_32
	j _32
	
_32:
	
# br label %b_30
	j _30
	
_33:
	
# br label %b_34
	j _34
	
_34:
	
# br label %b_13
	j _13
	
_35:
	
# %v113 = load i32, i32* %v112
	lw $k0 -516($sp)
	lw $k0 0($k0)
	sw $k0 -632($sp)
	
# %v114 = load i32, i32* @g_0
	la $k0, g_0
	lw $k0 0($k0)
	sw $k0 -636($sp)
	
# %v115 = icmp slt i32 %v113, %v114
	lw $k0 -632($sp)
	lw $k1 -636($sp)
	slt $k0 $k0 $k1
	sw $k0 -640($sp)
	
# %v116 = zext i1 %v115 to i32
	lw $k0 -640($sp)
	sw $k0 -644($sp)
	
# %v117 = icmp ne i32 %v116, 0
	lw $k0 -644($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -648($sp)
	
# br i1 %v117, label %b_36, label %b_38
	lw $k0 -648($sp)
	bne $k0 $zero _36
	j _38
	
_36:
	
# %v119 = load i32, i32* %v112
	lw $k0 -516($sp)
	lw $k0 0($k0)
	sw $k0 -652($sp)
	
# %v120 = icmp slt i32 %v119, 3
	lw $k0 -652($sp)
	li $k1 3
	slt $k0 $k0 $k1
	sw $k0 -656($sp)
	
# %v121 = zext i1 %v120 to i32
	lw $k0 -656($sp)
	sw $k0 -660($sp)
	
# %v122 = icmp ne i32 %v121, 0
	lw $k0 -660($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -664($sp)
	
# br i1 %v122, label %b_39, label %b_40
	lw $k0 -664($sp)
	bne $k0 $zero _39
	j _40
	
# br label %b_39
	j _39
	
_37:
	
# %v129 = load i32, i32* %v112
	lw $k0 -516($sp)
	lw $k0 0($k0)
	sw $k0 -668($sp)
	
# %v130 = add i32 %v129, 1
	lw $k0 -668($sp)
	li $k1 1
	addu $k0 $k0 $k1
	sw $k0 -672($sp)
	
# store i32 %v130, i32* %v112
	lw $k0 -672($sp)
	lw $k1 -516($sp)
	sw $k0 0($k1)
	
# br label %b_35
	j _35
	
_38:
	
# call void @putstr(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @s_6, i64 0, i64 0))
	la $a0, s_6
	li $v0 4
	syscall
	
# %v132 = load i32, i32* %v111
	lw $k0 -508($sp)
	lw $k0 0($k0)
	sw $k0 -676($sp)
	
# call void @putint(i32 %v132)
	lw $a0 -676($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# %v135 = load i32, i32* %v0
	lw $k0 -12($sp)
	lw $k0 0($k0)
	sw $k0 -680($sp)
	
# %v136 = getelementptr inbounds [10 x i32], [10 x i32]* %v15, i32 0, i32 0
	lw $k0 -100($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -684($sp)
	
# %v137 = call i32 @f_calculate(i32 %v135, i32* %v136)
	sw $sp -688($sp)
	sw $ra -692($sp)
	lw $a1 -680($sp)
	lw $a2 -684($sp)
	addi $sp $sp -692
	jal f_calculate
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -688($sp)
	
# %v138 = icmp eq i32 0, %v137
	li $k0 0
	lw $k1 -688($sp)
	seq $k0 $k0 $k1
	sw $k0 -692($sp)
	
# %v139 = zext i1 %v138 to i32
	lw $k0 -692($sp)
	sw $k0 -696($sp)
	
# %v140 = icmp ne i32 %v139, 0
	lw $k0 -696($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -700($sp)
	
# br i1 %v140, label %b_41, label %b_42
	lw $k0 -700($sp)
	bne $k0 $zero _41
	j _42
	
# br label %b_41
	j _41
	
_39:
	
# %v124 = load i32, i32* %v111
	lw $k0 -508($sp)
	lw $k0 0($k0)
	sw $k0 -704($sp)
	
# %v125 = load i32, i32* %v112
	lw $k0 -516($sp)
	lw $k0 0($k0)
	sw $k0 -708($sp)
	
# %v126 = getelementptr inbounds [20 x i32], [20 x i32]* %v90, i32 0, i32 %v125
	lw $k0 -420($sp)
	lw $k1 -708($sp)
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -712($sp)
	
# %v127 = load i32, i32* %v126
	lw $k0 -712($sp)
	lw $k0 0($k0)
	sw $k0 -716($sp)
	
# %v128 = add i32 %v124, %v127
	lw $k0 -704($sp)
	lw $k1 -716($sp)
	addu $k0 $k0 $k1
	sw $k0 -720($sp)
	
# store i32 %v128, i32* %v111
	lw $k0 -720($sp)
	lw $k1 -508($sp)
	sw $k0 0($k1)
	
# br label %b_40
	j _40
	
_40:
	
# br label %b_37
	j _37
	
_41:
	
# %v142 = getelementptr inbounds [12 x i8], [12 x i8]* %v16, i32 0, i32 0
	lw $k0 -152($sp)
	li $k1 0
	sll $k1 $k1 2
	addu $k0 $k1 $k0
	sw $k0 -724($sp)
	
# %v143 = call i8 @f_get_first(i8* %v142)
	sw $sp -728($sp)
	sw $ra -732($sp)
	lw $a1 -724($sp)
	addi $sp $sp -732
	jal f_get_first
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -728($sp)
	
# call void @f_print(i8 %v143)
	sw $sp -732($sp)
	sw $ra -736($sp)
	lw $a1 -728($sp)
	addi $sp $sp -736
	jal f_print
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -732($sp)
	
# br label %b_42
	j _42
	
_42:
	
# call void @putstr(i8* getelementptr inbounds ([16 x i8], [16 x i8]* @s_7, i64 0, i64 0))
	la $a0, s_7
	li $v0 4
	syscall
	
# ret i32 0
	li $v0 0
	jr $ra
	
end:
	li $v0 10
	syscall
