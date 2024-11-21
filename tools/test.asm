.data
	s_12: .asciiz " "
	s_5: .asciiz "Sum in func_with_param: "
	s_7: .asciiz "Negative intArray[0]: "
	s_16: .asciiz ", as char: "
	s_10: .asciiz ", Remainder: "
	s_0: .asciiz "Function with parameters: a = "
	s_6: .asciiz "22373141\n"
	s_11: .asciiz "Sum of ASCII codes1: "
	s_13: .asciiz "Sum of ASCII codes2: "
	s_1: .asciiz ", b = "
	s_4: .asciiz "\n"
	s_8: .asciiz "Positive intArray[0]: "
	s_3: .asciiz ", str[0] = "
	s_9: .asciiz "Quotient: "
	s_14: .asciiz "x1 = "
	s_2: .asciiz " arr[0] = "
	s_15: .asciiz "a1 = "
	constIntArray: .space 12
	constCharArray: .space 20
	constCharArray2: .space 20
	intArray: .space 20
	charArray: .space 20


.text
	li $t0 10
	sw $t0 constIntArray+0
	li $t0 20
	sw $t0 constIntArray+4
	li $t0 30
	sw $t0 constIntArray+8
	li $t0 65
	sw $t0 constCharArray+0
	li $t0 66
	sw $t0 constCharArray+4
	li $t0 67
	sw $t0 constCharArray+8
	li $t0 68
	sw $t0 constCharArray+12
	li $t0 69
	sw $t0 constCharArray+16
	li $t0 97
	sw $t0 constCharArray2+0
	li $t0 98
	sw $t0 constCharArray2+4
	li $t0 99
	sw $t0 constCharArray2+8
	# jump to main
	jal main
	main:
	
	j end
	
	end:
	li $v0 10
	syscall
