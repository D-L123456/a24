package com.example;

import java.util.*;

/**
 * 24点游戏核心算法：求解4个数字通过加减乘除是否能得到24
 */
public class Game24Solver {

    private static final double TARGET = 24.0;
    private static final double EPS = 1e-6;

    /**
     * 判断4个数字是否能算出24
     */
    public static boolean canSolve(int[] nums) {
        List<Double> numbers = new ArrayList<>();
        for (int num : nums) {
            numbers.add((double) num);
        }
        return solve(numbers);
    }

    /**
     * 求解：从列表中任取两个数，用四则运算合并，递归直到只剩一个数
     */
    private static boolean solve(List<Double> numbers) {
        if (numbers.size() == 1) {
            return Math.abs(numbers.get(0) - TARGET) < EPS;
        }

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i == j) continue;

                List<Double> next = new ArrayList<>();
                for (int k = 0; k < numbers.size(); k++) {
                    if (k != i && k != j) {
                        next.add(numbers.get(k));
                    }
                }

                double a = numbers.get(i);
                double b = numbers.get(j);

                // 加法
                next.add(a + b);
                if (solve(next)) return true;
                next.remove(next.size() - 1);

                // 减法
                next.add(a - b);
                if (solve(next)) return true;
                next.remove(next.size() - 1);

                // 乘法
                next.add(a * b);
                if (solve(next)) return true;
                next.remove(next.size() - 1);

                // 除法（除数不为0）
                if (Math.abs(b) > EPS) {
                    next.add(a / b);
                    if (solve(next)) return true;
                    next.remove(next.size() - 1);
                }
            }
        }
        return false;
    }

    /**
     * 找出一个能算出24的表达式（字符串形式）
     */
    public static String findSolution(int[] nums) {
        List<Double> numbers = new ArrayList<>();
        List<String> expressions = new ArrayList<>();
        for (int num : nums) {
            numbers.add((double) num);
            expressions.add(String.valueOf(num));
        }
        String result = findExpr(numbers, expressions);
        return result != null ? result : "无解";
    }

    private static String findExpr(List<Double> numbers, List<String> expressions) {
        if (numbers.size() == 1) {
            if (Math.abs(numbers.get(0) - TARGET) < EPS) {
                return expressions.get(0);
            }
            return null;
        }

        String[] ops = {"+", "-", "*", "/"};

        for (int i = 0; i < numbers.size(); i++) {
            for (int j = 0; j < numbers.size(); j++) {
                if (i == j) continue;

                List<Double> nextNums = new ArrayList<>();
                List<String> nextExprs = new ArrayList<>();
                for (int k = 0; k < numbers.size(); k++) {
                    if (k != i && k != j) {
                        nextNums.add(numbers.get(k));
                        nextExprs.add(expressions.get(k));
                    }
                }

                double a = numbers.get(i);
                double b = numbers.get(j);
                String exprA = expressions.get(i);
                String exprB = expressions.get(j);

                double[] results = {a + b, a - b, a * b};
                String[] exprStrs = {
                    "(" + exprA + " + " + exprB + ")",
                    "(" + exprA + " - " + exprB + ")",
                    "(" + exprA + " * " + exprB + ")"
                };

                for (int op = 0; op < 3; op++) {
                    nextNums.add(results[op]);
                    nextExprs.add(exprStrs[op]);
                    String sol = findExpr(nextNums, nextExprs);
                    if (sol != null) return sol;
                    nextNums.remove(nextNums.size() - 1);
                    nextExprs.remove(nextExprs.size() - 1);
                }

                // 除法
                if (Math.abs(b) > EPS) {
                    nextNums.add(a / b);
                    nextExprs.add("(" + exprA + " / " + exprB + ")");
                    String sol = findExpr(nextNums, nextExprs);
                    if (sol != null) return sol;
                    nextNums.remove(nextNums.size() - 1);
                    nextExprs.remove(nextExprs.size() - 1);
                }
            }
        }
        return null;
    }

    /**
     * 生成4个1-13的随机数字，保证有解
     */
    public static int[] generateSolvablePuzzle() {
        Random random = new Random();
        int[] nums;
        do {
            nums = new int[4];
            for (int i = 0; i < 4; i++) {
                nums[i] = random.nextInt(13) + 1;
            }
        } while (!canSolve(nums));
        return nums;
    }

    /**
     * 验证用户输入的表达式是否合法且结果为24
     * @param expression 用户输入的表达式
     * @param nums 题目给出的4个数字
     * @return 验证结果
     */
    public static ValidationResult validateExpression(String expression, int[] nums) {
        // 去除空格
        String expr = expression.replaceAll("\\s+", "");

        // 检查是否只包含合法字符
        if (!expr.matches("^[0-9+\\-*/().]+$")) {
            return new ValidationResult(false, "表达式包含非法字符，只允许数字和 + - * / ( )");
        }

        // 提取表达式中的所有数字
        List<Integer> usedNums = new ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(expr);
        while (matcher.find()) {
            usedNums.add(Integer.parseInt(matcher.group()));
        }

        // 检查数字是否匹配
        List<Integer> requiredNums = new ArrayList<>();
        for (int num : nums) {
            requiredNums.add(num);
        }
        Collections.sort(usedNums);
        Collections.sort(requiredNums);

        if (!usedNums.equals(requiredNums)) {
            return new ValidationResult(false, "使用的数字与题目不符，请使用给定的4个数字各一次");
        }

        // 计算表达式结果
        try {
            double result = evaluateExpression(expr);
            if (Math.abs(result - TARGET) < EPS) {
                return new ValidationResult(true, "正确！结果等于24");
            } else {
                return new ValidationResult(false, "结果为 " + String.format("%.2f", result) + "，不等于24");
            }
        } catch (Exception e) {
            return new ValidationResult(false, "表达式格式错误：" + e.getMessage());
        }
    }

    /**
     * 简单的表达式求值（支持 + - * / 和括号）
     */
    private static double evaluateExpression(String expr) {
        return new Object() {
            int pos = 0;

            double parse() {
                double result = parseTerm();
                while (pos < expr.length()) {
                    char op = expr.charAt(pos);
                    if (op == '+') {
                        pos++;
                        result += parseTerm();
                    } else if (op == '-') {
                        pos++;
                        result -= parseTerm();
                    } else {
                        break;
                    }
                }
                return result;
            }

            double parseTerm() {
                double result = parseFactor();
                while (pos < expr.length()) {
                    char op = expr.charAt(pos);
                    if (op == '*') {
                        pos++;
                        result *= parseFactor();
                    } else if (op == '/') {
                        pos++;
                        double divisor = parseFactor();
                        if (Math.abs(divisor) < EPS) throw new RuntimeException("除数不能为0");
                        result /= divisor;
                    } else {
                        break;
                    }
                }
                return result;
            }

            double parseFactor() {
                if (pos < expr.length() && expr.charAt(pos) == '(') {
                    pos++; // skip '('
                    double result = parse();
                    pos++; // skip ')'
                    return result;
                }
                // parse number
                int start = pos;
                while (pos < expr.length() && (Character.isDigit(expr.charAt(pos)) || expr.charAt(pos) == '.')) {
                    pos++;
                }
                if (start == pos) {
                    throw new RuntimeException("表达式格式错误");
                }
                return Double.parseDouble(expr.substring(start, pos));
            }
        }.parse();
    }

    /**
     * 验证结果
     */
    public static class ValidationResult {
        public boolean success;
        public String message;

        public ValidationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
