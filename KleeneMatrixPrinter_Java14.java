package com.kseg;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Kleen tri valued logic matrix printer
 */
public class KleeneMatrixPrinter_Java14 {

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
            case Falsity: yield Truth;
            case Indefiniteness: yield  Indefiniteness;
            case Truth: yield  Falsity;
        };
    }

    public static K3 implicationOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity:
                yield switch (b) {
                    case Falsity: case Indefiniteness: case Truth: yield Truth;
                };
            case Indefiniteness:
                yield switch (b) {
                    case Falsity: case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
            case Truth:
                yield  switch (b) {
                    case Falsity: yield Falsity;
                    case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
        };
    }

    public static K3 disjunctionOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity:
                yield switch (b) {
                    case Falsity: yield Falsity;
                    case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
            case Indefiniteness:
                yield switch (b) {
                    case Falsity: case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
            case Truth:
                yield switch (b) {
                    case Falsity: case Indefiniteness: case Truth: yield Truth;
                };
        };
    }

    public static K3 conjunctionOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity:
                yield switch (b) {
                    case Falsity: case Indefiniteness: case Truth: yield Falsity;
                };
            case Indefiniteness:
                yield switch (b) {
                    case Falsity: yield Falsity;
                    case Indefiniteness: case Truth: yield Indefiniteness;
                };
            case Truth:
                yield switch (b) {
                    case Falsity: yield Falsity; case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
        };
    }

    public static K3 equivalenceOperator(K3 a, K3 b) {
        return switch (a) {
            case Falsity:
                yield switch (b) {
                    case Falsity: yield Truth;
                    case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Falsity;
                };
            case Indefiniteness:
                yield switch (b) {
                    case Falsity: case Indefiniteness: case Truth: yield Indefiniteness;
                };
            case Truth:
                yield switch (b) {
                    case Falsity: yield Falsity;
                    case Indefiniteness: yield Indefiniteness;
                    case Truth: yield Truth;
                };
        };
    }
}
