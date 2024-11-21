.data
	s_0: .asciiz " "


.text
	# jump to main
	jal main
	j end
	main:
	_0:
	addi $k0 $sp -4
	sw $k0 -4($sp)
	lw $k0 -4($sp)
	lw $k1 -8($sp)
	sw $k0 0($k1)
	addi $k0 $sp -16
	sw $k0 -16($sp)
	addi $k0 $sp -20
	sw $k0 -20($sp)
	addi $k0 $sp -24
	sw $k0 -24($sp)
	lw $k0 -8($sp)
	lw $k0 0($k0)
	sw $k0 -24($sp)
	lw $k0 -24($sp)
	lw $k1 -28($sp)
	addu $k0 $k0 $k1
	sw $k0 -32($sp)
	lw $k0 -32($sp)
	lw $k1 -36($sp)
	sw $k0 0($k1)
	lw $k0 -36($sp)
	lw $k0 0($k0)
	sw $k0 -40($sp)
	lw $k0 -40($sp)
	lw $k1 -44($sp)
	addu $k0 $k0 $k1
	sw $k0 -48($sp)
	lw $k0 -48($sp)
	lw $k1 -52($sp)
	sw $k0 0($k1)
	lw $k0 -52($sp)
	lw $k0 0($k0)
	sw $k0 -56($sp)
	lw $k0 -56($sp)
	lw $k1 -60($sp)
	addu $k0 $k0 $k1
	sw $k0 -64($sp)
	lw $k0 -64($sp)
	lw $k1 -68($sp)
	sw $k0 0($k1)
	lw $k0 -8($sp)
	lw $k0 0($k0)
	sw $k0 -72($sp)
	lw $a0 -72($sp)
	li $v0 1
	syscall
	la $a0, s_0
	li $v0 4
	syscall
	lw $k0 -36($sp)
	lw $k0 0($k0)
	sw $k0 -76($sp)
	lw $a0 -76($sp)
	li $v0 1
	syscall
	la $a0, s_0
	li $v0 4
	syscall
	lw $k0 -52($sp)
	lw $k0 0($k0)
	sw $k0 -80($sp)
	lw $a0 -80($sp)
	li $v0 1
	syscall
	la $a0, s_0
	li $v0 4
	syscall
	lw $k0 -68($sp)
	lw $k0 0($k0)
	sw $k0 -84($sp)
	lw $a0 -84($sp)
	li $v0 1
	syscall
	lw $v0 -88($sp)
	jr $ra
	end:
	li $v0 10
	syscall
