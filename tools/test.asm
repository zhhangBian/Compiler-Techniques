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
	
# call void @putint(i32 %v0)
	move $a0 $a1
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @s_0, i64 0, i64 0))
	la $a0, s_0
	li $v0 4
	syscall
	
# call void @putint(i32 %v1)
	move $a0 $a2
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
	
# call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @s_2, i64 0, i64 0))
	la $a0, s_2
	li $v0 4
	syscall
	
# call void @putint(i32 %v0)
	move $a0 $a1
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# call void @putint(i32 %v1)
	move $a0 $a2
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# call void @putint(i32 %v2)
	move $a0 $a3
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_3, i64 0, i64 0))
	la $a0, s_3
	li $v0 4
	syscall
	
# call void @putint(i32 %v3)
	lw $a0 -16($sp)
	li $v0 1
	syscall
	
# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @s_1, i64 0, i64 0))
	la $a0, s_1
	li $v0 4
	syscall
	
# %v22 = icmp eq i32 %v0, 1
	move $k0 $a1
	li $k1 1
	seq $k0 $k0 $k1
	sw $k0 -20($sp)
	
# %v23 = zext i1 %v22 to i32
	lw $k0 -20($sp)
	sw $k0 -24($sp)
	
# %v24 = icmp ne i32 %v23, 0
	lw $k0 -24($sp)
	li $k1 0
	sne $k0 $k0 $k1
	sw $k0 -28($sp)
	
# br i1 %v24, label %b_2, label %b_3
	lw $k0 -28($sp)
	bne $k0 $zero _2
	j _3
	
_2:
	
# call void @f_move(i32 %v1, i32 %v3)
	sw $a3 -32($sp)
	sw $a2 -36($sp)
	sw $a1 -40($sp)
	sw $sp -44($sp)
	sw $ra -48($sp)
	lw $a1 -36($sp)
	lw $a2 -16($sp)
	addi $sp $sp -48
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a3 -28($sp)
	lw $a2 -32($sp)
	lw $a1 -36($sp)
	sw $v0 -32($sp)
	
# br label %b_4
	j _4
	
_3:
	
# %v29 = sub i32 %v0, 1
	move $k0 $a1
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -36($sp)
	
# call void @f_hanoi(i32 %v29, i32 %v1, i32 %v3, i32 %v2)
	sw $a3 -40($sp)
	sw $a2 -44($sp)
	sw $a1 -48($sp)
	sw $sp -52($sp)
	sw $ra -56($sp)
	lw $a1 -36($sp)
	lw $a2 -44($sp)
	lw $a3 -16($sp)
	lw $k0 -40($sp)
	sw $k0 -72($sp)
	addi $sp $sp -56
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a3 -36($sp)
	lw $a2 -40($sp)
	lw $a1 -44($sp)
	sw $v0 -40($sp)
	
# call void @f_move(i32 %v1, i32 %v3)
	sw $a3 -44($sp)
	sw $a2 -48($sp)
	sw $a1 -52($sp)
	sw $sp -56($sp)
	sw $ra -60($sp)
	lw $a1 -48($sp)
	lw $a2 -16($sp)
	addi $sp $sp -60
	jal f_move
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a3 -40($sp)
	lw $a2 -44($sp)
	lw $a1 -48($sp)
	sw $v0 -44($sp)
	
# %v36 = sub i32 %v0, 1
	move $k0 $a1
	li $k1 1
	subu $k0 $k0 $k1
	sw $k0 -48($sp)
	
# call void @f_hanoi(i32 %v36, i32 %v2, i32 %v1, i32 %v3)
	sw $a3 -52($sp)
	sw $a2 -56($sp)
	sw $a1 -60($sp)
	sw $sp -64($sp)
	sw $ra -68($sp)
	lw $a1 -48($sp)
	lw $a2 -52($sp)
	lw $a3 -56($sp)
	lw $k0 -16($sp)
	sw $k0 -84($sp)
	addi $sp $sp -68
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	lw $a3 -48($sp)
	lw $a2 -52($sp)
	lw $a1 -56($sp)
	sw $v0 -52($sp)
	
# br label %b_4
	j _4
	
_4:
	
# ret void
	jr $ra
	
main:
	
_5:
	
# call void @f_hanoi(i32 3, i32 1, i32 2, i32 3)
	sw $sp -4($sp)
	sw $ra -8($sp)
	li $a1 3
	li $a2 1
	li $a3 2
	li $k0 3
	sw $k0 -24($sp)
	addi $sp $sp -8
	jal f_hanoi
	lw $ra 0($sp)
	lw $sp 4($sp)
	sw $v0 -4($sp)
	
# ret i32 0
	li $v0 0
	jr $ra
	
end:
	li $v0 10
	syscall
