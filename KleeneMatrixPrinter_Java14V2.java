package com.kseg;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Negation
 * α | ~α
 * --x----
 * f | t
 * u | u
 * t | f
 *
 * Implication
 * → | f u t
 * --x-------
 * f | t t t
 * u | u u t
 * t | f u t
 *
 * Disjunction
 * v | f u t
 * --x-------
 * f | f u t
 * u | u u t
 * t | t t t
 *
 * Conjunction
 * ∧ | f u t
 * --x-------
 * f | f f f
 * u | f u u
 * t | f u t
 *
 * Equivalence
 * ≡ | f u t
 * --x-------
 * f | t u f
 * u | u u u
 * t | f u t
 * Reference: https://filozof.uni.lodz.pl/prac/gm/papers/GM74.pdf
 */
public class KleeneMatrixPrinter_Java14V2 {

    static final String PIPE = "|";
    static final String SEP = " " + PIPE + " ";
    static final String SPACE = " ";
    static final String TITLE = "f u t";

    public static void main(String[] args) {
        printNegationTable();
        printLogicalOperation("→", K3::implicationOperator);
        printLogicalOperation("∨", K3::disjunctionOperator);
        printLogicalOperation("∧", K3::conjunctionOperator);
        printLogicalOperation("≡", K3::equivalenceOperator);
    }

    private static void printNegationTable() {
        printTable("α", "~α", () -> prettyPrintNegationOperationResult(K3::negationOperator));
    }

    private static void printLogicalOperation(String symbol, LogicalOperator operator) {
        printTable(symbol, TITLE, () -> prettyPrintEvaluatedResult(operator));
    }

    private static void printTable(String title1, String title2, TruthTablePrinter printer) {
        printTitle(title1, title2);
        printer.print();
        System.out.println();
    }

    //Pretty print helpers
    private static void printTitle(String title1, String title2) {
        System.out.println(title1 + SEP + title2);
        IntStream.range(0, title1.length() + SEP.length() + title2.length())
                .forEach(value -> {
                    if (value == title1.length() + 1) {
                        System.out.print("x");
                    } else {
                        System.out.print("-");
                    }
                });
        System.out.println();
    }

    private static void prettyPrintNegationOperationResult(LogicalOperator operator) {
        K3.stream().forEach(a -> {
            System.out.println(a.Value + SEP + operator.apply(a, K3.Indefiniteness).Value);
        });
    }

    private static void prettyPrintEvaluatedResult(LogicalOperator operator) {
        K3.stream().forEach(a -> {
            System.out.print(a.Value + SEP);
            K3.stream().forEach(b -> {
                System.out.print(operator.apply(a, b).Value + SPACE);
            });
            System.out.println();
        });
    }
}

interface TruthTablePrinter {
    void print();
}

interface LogicalOperator {
    K3 apply(K3 a, K3 b);
}

enum K3 {
    Truth("t"),
    Indefiniteness("u"),
    Falsity("f");

    public final String Value;

    K3(String value) {
        this.Value = value;
    }

    public static Stream<K3> stream() {
        return Stream.of(Falsity, Indefiniteness, Truth);
    }

    public static K3 negationOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity -> Truth;
            case Indefiniteness -> Indefiniteness;
            case Truth -> Falsity;
        };
    }

    public static K3 implicationOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity -> switch (b) {
                case Falsity, Indefiniteness, Truth -> Truth;
            };
            case Indefiniteness -> switch (b) {
                case Falsity, Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
            case Truth -> switch (b) {
                case Falsity -> Falsity;
                case Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
        };
    }

    public static K3 disjunctionOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity -> switch (b) {
                case Falsity -> Falsity;
                case Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
            case Indefiniteness -> switch (b) {
                case Falsity, Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
            case Truth -> switch (b) {
                case Falsity, Indefiniteness, Truth -> Truth;
            };
        };
    }

    public static K3 conjunctionOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity -> switch (b) {
                case Falsity, Indefiniteness, Truth -> Falsity;
            };
            case Indefiniteness -> switch (b) {
                case Falsity -> Falsity;
                case Indefiniteness, Truth -> Indefiniteness;
            };
            case Truth -> switch (b) {
                case Falsity -> Falsity;
                case Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
        };
    }

    public static K3 equivalenceOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity -> switch (b) {
                case Falsity -> Truth;
                case Indefiniteness -> Indefiniteness;
                case Truth -> Falsity;
            };
            case Indefiniteness -> switch (b) {
                case Falsity, Indefiniteness, Truth -> Indefiniteness;
            };
            case Truth -> switch (b) {
                case Falsity -> Falsity;
                case Indefiniteness -> Indefiniteness;
                case Truth -> Truth;
            };
        };
    }
}
