package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
	// write your code here
        Main tMain = new Main();
        tMain.readFile("2.txt");
        tMain.interpret();
    }

    class Instruction{
        String instruction;
        Integer level;
        Integer address;
    }

    /**
     * 栈顶指针
     * */
    private Integer top = 0;

    /**
     * 基址指针
     * */
    private Integer base = 0;

    /**
     * 指令指针，指向当前指向的指令的序号
     * */
    private Integer instructionPoint = 0;

    private List<Instruction>instructions = new ArrayList<>();
    private int[] stack = new int[500];

    /**
     * 做输入操作时用的对象
     * */
    private Scanner scanner = new Scanner(System.in);

    /**
     * 进行指令的读取操作
     * */
    private void readFile(String fileName){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String temp;
            while ((temp = bufferedReader.readLine())!=null){
                Instruction instruction = new Instruction();
                instruction.instruction = temp.split(" ")[1];
                instruction.level = Integer.parseInt(temp.split(" ")[2]);
                instruction.address = Integer.parseInt(temp.split(" ")[3]);
                instructions.add(instruction);
            }
        }catch (Exception e){
            System.err.println("读取输入文件的时候发生异常");
            e.printStackTrace();
        }
    }

    /**
     * 进行指令的解释操作
     * */
    private void interpret(){
        while (execute(instructions.get(instructionPoint).instruction, instructions.get(instructionPoint).level, instructions.get(instructionPoint).address)){
            instructionPoint ++;
        }
    }

    /**
     * 对于具体的指令执行
     * */
    private boolean execute(String instruction, Integer level, Integer address){
        switch (Instr.toInstruction(instruction)){
            case LIT:
                //将常数值取到栈顶，a为常数值
                stack[top] = address;
                top ++ ;
                break;
            case LOD:
                //将变量值取到栈顶，a为相对地址，t为层数
                 stack[top] = stack[base + address];
                top ++;
                break;
            case STO:
                //将栈顶内容送入某变量单元中，a为相对地址，t为层数
                top --;
                stack[base + address] = stack[top];
                break;
            case CAL:
                //调用函数，a为函数地址
                stack[top] = base;
                stack[top + 1] = base;
                stack[top + 2] = instructionPoint;
                base = top;
                instructionPoint = address - 1;
                break;
            case INT:
                //在运行栈中为被调用的过程开辟a个单元的数据区
                top = top + address;
                break;
            case JMP:
                //无条件跳转至a地址
                instructionPoint = address - 1;
                break;
            case JPC:
                //条件跳转，当栈顶值为0，则跳转至a地址，否则顺序执行
                top--;
                if (stack[top] == 0){
                    instructionPoint = address - 1;
                }
                break;
            case ADD:
                //次栈顶与栈顶相加，退两个栈元素，结果值进栈
                top --;
                stack[top - 1] = stack[top] + stack[top - 1];
                break;
            case SUB:
                //次栈顶减去栈顶，退两个栈元素，结果值进栈
                top --;
                stack[top - 1] =  stack[top - 1] - stack[top];
                break;
            case MUL:
                //次栈顶乘以栈顶，退两个栈元素，结果值进栈
                top --;
                stack[top - 1] =  stack[top - 1] * stack[top];
                break;
            case DIV:
                //次栈顶除以栈顶，退两个栈元素，结果值进栈
                top --;
                stack[top - 1] =  stack[top - 1] / stack[top];
                break;
            case RED:
                //从命令行读入一个输入置于栈顶
                System.out.println("请输入一个数，输入完成后回车");
                Integer integer = scanner.nextInt();
                stack[top] = integer;
                top ++;
                break;
            case WRT:
                //栈顶值输出至屏幕并换行
                System.out.println("解释器输出：");
                System.out.println(stack[--top]);
                break;
            case RET:
                //函数调用结束后,返回调用点并退栈
                top = base;
                instructionPoint = stack[top + 2];
                base = stack[top + 1];
                if(instructionPoint == 0){
                    return false;
                }
                break;
            default:
                System.out.println(Instr.toInstruction(instruction));
        }
        return true;
    }

    public enum Instr

    {
        NO,
        LIT, LOD, STO, CAL,
        INT, JMP, JPC,
        ADD, SUB, MUL,
        DIV, RED, WRT, RET;

        public static Instr toInstruction(String str)

        {

            try {

                return valueOf(str);

            }

            catch (Exception ex) {

                ex.printStackTrace();
                return NO;
            }

        }

    }
}