package calculator;

import java.util.Scanner;
import java.util.Stack;

/**
 * Version: 1.0
 * Author: A2N1
 * Date: 2024-10-02
 * -----------------------------------------------------
 * The Calculator is a basic arithmetic tool that simulates the behavior of an online calculator.
 * It allows the user to perform operations like addition, subtraction, multiplication,
 * and division, as well as unary operations such as square root, percentage, and inversion.
 * The user inputs mathematical expressions, and the calculator evaluates
 * and displays the result on a screen that can show up to 9 digits.
 * The calculator also includes error handling, such as preventing division by zero,
 * and supports both simple and chained expressions.
 */

public class Calculator {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        Scanner scanner = new Scanner(System.in);

        // Hauptschleife zur Verarbeitung von Benutzereingaben
        while (true) {
            System.out.println("Geben Sie einen mathematischen Ausdruck ein (oder 'q' zum Beenden):");
            String input = scanner.nextLine(); // Erfasst die ganze Zeile vom Benutzer

            // Beenden, wenn der Benutzer 'q' eingibt
            if (input.equalsIgnoreCase("q")) {
                System.out.println("Programm beendet.");
                break;
            }

            try {
                // Berechnung durchführen und Ergebnis anzeigen
                calc.parseAndCalculate(input);
                System.out.println("Ergebnis: " + calc.readScreen());
            } catch (Exception e) {
                // Bei Fehlern eine Fehlermeldung anzeigen
                System.out.println("Ungültiger Ausdruck: " + e.getMessage());
            }

            calc.pressClearKey(); // Zurücksetzen für die nächste Berechnung
        }

        scanner.close();
    }

    // Attribute zur Speicherung des Bildschirminhalts, des letzten Wertes und der letzten Operation
    private String screen = "0";
    private double latestValue;
    private String latestOperation = "";
    private Stack<String> history = new Stack<>();

    /**
     * Gibt den aktuellen Bildschirminhalt zurück
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Setzt den Bildschirminhalt auf "0" zurück und löscht die letzte Operation und den letzten Wert.
     * Simuliert das Drücken der C- oder CE-Taste (Clear).
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    /**
     * Parst den eingegebenen mathematischen Ausdruck und berechnet das Ergebnis.
     * Unterstützt einfache mathematische Operationen (+, -, x, /) und Klammerausdrücke.
     * Speichert das Ergebnis auf dem Bildschirm.
     *
     * @param input Der mathematische Ausdruck als String
     */
    public void parseAndCalculate(String input) {
        input = input.replace(" ", ""); // Entferne Leerzeichen

        // Überprüfe auf Klammerausdrücke und wende deren Berechnung an
        if (input.contains("(") || input.contains(")")) {
            screen = Double.toString(evaluateExpression(input));
        } else {
            // Zerlege den Ausdruck in Zahlen und Operatoren
            String[] tokens = input.split("(?<=[-+x/])|(?=[-+x/])");

            // Überprüfe, ob der Ausdruck gültig ist (mindestens zwei Operanden und ein Operator)
            if (tokens.length < 3 || tokens.length % 2 == 0) {
                throw new IllegalArgumentException("Ungültiger Ausdruck. Es werden zwei Operanden und ein Operator benötigt.");
            }

            // Starte mit dem ersten Wert
            double result = Double.parseDouble(tokens[0]);

            // Iteriere über die Operatoren und Operanden
            for (int i = 1; i < tokens.length; i += 2) {
                String operation = tokens[i];
                double nextOperand = Double.parseDouble(tokens[i + 1]);

                // Führe die Operation auf den resultierenden Wert aus
                result = applyOperation(result, nextOperand, operation);
            }

            // Aktualisiere den Bildschirm mit dem Ergebnis
            screen = Double.toString(result);
            if (screen.endsWith(".0")) {
                screen = screen.substring(0, screen.length() - 2); // Entferne unnötige Dezimalstellen
            }
        }

        // Füge das Ergebnis zur Historie hinzu
        history.push(screen);
    }

    /**
     * Wendet eine mathematische Operation (Addition, Subtraktion, Multiplikation oder Division)
     * auf zwei Operanden an und gibt das Ergebnis zurück.
     *
     * @param leftOperand Der linke Operand (zuerst eingegebene Zahl)
     * @param rightOperand Der rechte Operand (zweite Zahl)
     * @param operation Der Operator als String ("+", "-", "x", "/")
     * @return Das Ergebnis der Operation
     */
    private double applyOperation(double leftOperand, double rightOperand, String operation) {
        return switch (operation) {
            case "+" -> leftOperand + rightOperand;
            case "-" -> leftOperand - rightOperand;
            case "x" -> leftOperand * rightOperand;
            case "/" -> {
                if (rightOperand == 0) throw new ArithmeticException("Division durch 0");
                yield leftOperand / rightOperand;
            }
            default -> throw new IllegalArgumentException("Unbekannte Operation: " + operation);
        };
    }

    /**
     * Bewertet einen mathematischen Ausdruck, der Klammern und mehrere Operationen enthält.
     * Verwendet einen Stack zur korrekten Reihenfolge der Operationen.
     *
     * @param expression Der mathematische Ausdruck als String
     * @return Das Ergebnis der Berechnung
     */
    private double evaluateExpression(String expression) {
        Stack<Double> values = new Stack<>(); // Speichert Zahlen
        Stack<Character> operators = new Stack<>(); // Speichert Operatoren

        for (int i = 0; i < expression.length(); i++) {
            char token = expression.charAt(i);

            // Aktuelle Zahl extrahieren
            if (Character.isDigit(token)) {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                values.push(Double.parseDouble(sb.toString()));
                i--; // Zurücksetzen, weil der Index um eins zu viel erhöht wurde
            } else if (token == '(') {
                // Öffnende Klammer in den Operatoren-Stack einfügen
                operators.push(token);
            } else if (token == ')') {
                // Bei schließender Klammer alle Operationen innerhalb der Klammer ausführen
                while (operators.peek() != '(') {
                    values.push(applyOperation(values.pop(), values.pop(), String.valueOf(operators.pop())));
                }
                operators.pop(); // Öffnende Klammer entfernen
            } else if ("+-x/".indexOf(token) != -1) { // Operatoren nach Priorität verarbeiten
                while (!operators.isEmpty() && precedence(token) <= precedence(operators.peek())) {
                    values.push(applyOperation(values.pop(), values.pop(), String.valueOf(operators.pop())));
                }
                operators.push(token);
            }
        }

        // Verbleibende Operationen ausführen
        while (!operators.isEmpty()) {
            values.push(applyOperation(values.pop(), values.pop(), String.valueOf(operators.pop())));
        }

        return values.pop(); // Endergebnis
    }

    /**
     * Bestimmt die Priorität eines Operators. Multiplikation und Division haben höhere Priorität als Addition und Subtraktion.
     *
     * @param operator Der Operator als char
     * @return Die Priorität des Operators (höhere Zahl = höhere Priorität)
     */
    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case 'x', '/' -> 2;
            default -> 0;
        };
    }

    /**
     * Simuliert das Drücken einer Zifferntaste. Aktualisiert den Bildschirm mit der neuen Ziffer.
     *
     * @param digit Die Ziffer, die gedrückt wurde
     */
    public void pressDigitKey(double digit) {
        screen = Double.toString(digit);
        latestValue = digit;
    }
    /**
     * Simuliert das Drücken einer binären Operationstaste (Addition, Subtraktion, Multiplikation oder Division).
     * Setzt den Rechner in den entsprechenden Operationsmodus.
     *
     * @param operation "+" für Addition, "-" für Subtraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
    }

    /**
     * Simuliert das Drücken einer unären Operationstaste (Quadratwurzel, Prozent, Inversion).
     * Führt die Operation auf den aktuellen Bildschirmwert aus und zeigt das Ergebnis an.
     *
     * @param operation "√" für Quadratwurzel, "%" für Prozent, "1/x" für Inversion
     */
    public void pressUnaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
        var result = switch(operation) {
            case "√" -> Math.sqrt(Double.parseDouble(screen));
            case "%" -> Double.parseDouble(screen) / 100;
            case "1/x" -> 1 / Double.parseDouble(screen);
            default -> throw new IllegalArgumentException();
        };
        screen = Double.toString(result);
        if(screen.equals("NaN")) screen = "Error";
        if(screen.contains(".") && screen.length() > 11) screen = screen.substring(0, 10);
    }

    /**
     * Simuliert das Drücken der Dezimaltrennzeichentaste.
     * Wenn noch kein Dezimalpunkt vorhanden ist, wird er am Ende des Bildschirminhalts hinzugefügt.
     * Daraufhin eingegebene Zahlen werden als Dezimalzahlen interpretiert.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }


    /**
     * Simuliert das Drücken der Vorzeichenumkehrtaste ("+/-").
     * Wenn der Bildschirmwert positiv ist, wird ein Minuszeichen hinzugefügt.
     * Ist der Wert bereits negativ, wird das Minuszeichen entfernt.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Simuliert das Drücken der "="-Taste.
     * Wendet die zuletzt gewählte binäre Operation (Addition, Subtraktion, Multiplikation oder Division)
     * auf den aktuellen Bildschirmwert (secondValue) und den gespeicherten letzten Wert (latestValue) an.
     * Falls keine Operation vorhanden ist, wird der aktuelle Wert einfach zurückgegeben.
     * Wird eine Division durch Null erkannt, wird "Error" angezeigt.
     */
    public void pressEqualsKey() {
        double secondValue = Double.parseDouble(screen);
        double result = switch (latestOperation) {
            case "+" -> latestValue + secondValue;
            case "-" -> latestValue - secondValue;
            case "x" -> latestValue * secondValue;
            case "/" -> secondValue == 0 ? Double.POSITIVE_INFINITY : latestValue / secondValue;
            default -> secondValue; // Falls keine Operation vorhanden ist, wird der aktuelle Wert zurückgegeben
        };

        // Überprüfe auf Division durch 0 oder unendliche Ergebnisse
        if (Double.isInfinite(result)) {
            screen = "Error";
        } else {
            screen = Double.toString(result);
            if (screen.endsWith(".0")) screen = screen.substring(0, screen.length() - 2);
        }
    }
}
