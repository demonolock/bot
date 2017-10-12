package edu.technopolis;

import java.math.BigInteger;

public class DummyFibonacciAlgorithm implements FibonacciAlgorithm {

    @Override
    public String evaluate(int index) {
        return print(fib(index));
    }


        static int MAX = 4000;

        public static void main(String[] args) {
            System.out.println(fib(1));

        }

        static long[] fib(int i){
            long[] curr = new long[MAX];
            long[] digit1 = new long[MAX];
            long[] digit2 = new long[MAX];
            digit1[0] = 1;
            digit2[0] = 0;

            if (i == 0)
                return digit2;
            if (i == 1)
                return digit1;
            int j = 1;
            while (j < i) {
                curr = sum(digit1,digit2).clone();
                digit2 = digit1.clone();
                digit1 = curr.clone();
                j++;
            }
            return curr;
        }


        static String print(long[] digit){
            StringBuilder s = new StringBuilder("");
            boolean isNule = true;
            for (int i = digit.length-1; i >= 0; i--) {
                if (isNule) {
                    if (digit[i] != 0) {
                        isNule = false;
                        s.append(digit[i]);
                    }
                }
                else {
                    int col = getCountsOfDigits(digit[i]);
                    if (col < 18) {
                        for (; col < 18; col++)
                            s.append(0);
                    }
                    s.append(digit[i]);
                }
            }
            return String.valueOf(s);
        }

        //Инверсия листа, чтобы при сложении не париться с индексами
        public static long[] inverse(long[] digit){
            long[] d = new long[MAX];
            for (int i = digit.length-1; i > -1; i--) {
                d[digit.length - i - 1] = digit[i];
            }
            return d;
        }

        //количество знаков в числе
        public static int getCountsOfDigits(long number) {
            return(number == 0) ? 1 : (int) Math.ceil(Math.log10(Math.abs(number) + 0.5));
        }

        //Считает сумму двух ооочень больших чисел
        static long[] sum(long[] digit1, long[] digit2){
            long[] digit = new long[MAX];
            for (int i = 0; i < MAX ; i++) {
                digit[i] += digit1[i] + digit2[i];
                if (getCountsOfDigits(digit[i]) == 19) {
                    digit[i] -= (long)Math.pow(10, 18);
                    digit[i + 1] = 1;
                }
            }
            return digit;
        }
    }

