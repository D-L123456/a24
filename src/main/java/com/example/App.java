package com.example;

/**
 * 24点游戏主程序
 */
public class App {

    public static void main(String[] args) {
        System.out.println("=== 24点游戏 ===");
        System.out.println();

        // 演示：生成一道题并求解
        int[] puzzle = Game24Solver.generateSolvablePuzzle();
        System.out.print("题目：");
        for (int i = 0; i < puzzle.length; i++) {
            System.out.print(puzzle[i]);
            if (i < puzzle.length - 1) System.out.print(", ");
        }
        System.out.println();

        boolean canSolve = Game24Solver.canSolve(puzzle);
        System.out.println("是否有解：" + (canSolve ? "是" : "否"));

        if (canSolve) {
            String solution = Game24Solver.findSolution(puzzle);
            System.out.println("一个解：" + solution);
        }

        // 验证用户表达式
        System.out.println();
        String testExpr = puzzle[0] + "+" + puzzle[1] + "+" + puzzle[2] + "+" + puzzle[3];
        System.out.println("验证表达式 \"" + testExpr + "\"：");
        Game24Solver.ValidationResult result = Game24Solver.validateExpression(testExpr, puzzle);
        System.out.println(result.message);
    }
}
