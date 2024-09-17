package calculator;

import java.util.Scanner;

/**
 * Eine Klasse, die das Verhalten des Online Taschenrechners imitiert, welcher auf
 * https://www.online-calculator.com/ aufgerufen werden kann (ohne die Memory-Funktionen)
 * und dessen Bildschirm bis zu 9 Ziffern darstellen kann.
 * Enthält mit Absicht noch diverse Bugs oder unvollständige Funktionen.
 */
public class Calculator {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Geben Sie einen mathematischen Ausdruck ein (oder 'q' zum Beenden):");
            String input = scanner.nextLine(); // Erfasst die ganze Zeile vom Benutzer

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Programm beendet.");
                break;
            }

            try {
                calc.parseAndCalculate(input); // Berechnung durchführen
                System.out.println("Ergebnis: " + calc.readScreen()); // Ergebnis anzeigen
            } catch (Exception e) {
                System.out.println("Ungültiger Ausdruck: " + e.getMessage());
            }

            calc.pressClearKey(); // Zurücksetzen für die nächste Berechnung
        }

        scanner.close();
    }

    private String screen = "0";
    private double latestValue;
    private String latestOperation = "";

    /**
     * @return den aktuellen Bildschirminhalt als String
     */
    public String readScreen() {
        return screen;
    }

    /**
     * Empfängt den Befehl der C- bzw. CE-Taste (Clear bzw. Clear Entry).
     * Einmaliges Drücken der Taste löscht die zuvor eingegebenen Ziffern auf dem Bildschirm
     * so dass "0" angezeigt wird, jedoch ohne zuvor zwischengespeicherte Werte zu löschen.
     * Wird daraufhin noch einmal die Taste gedrückt, dann werden auch zwischengespeicherte
     * Werte sowie der aktuelle Operationsmodus zurückgesetzt, so dass der Rechner wieder
     * im Ursprungszustand ist.
     */
    public void pressClearKey() {
        screen = "0";
        latestOperation = "";
        latestValue = 0.0;
    }

    public void parseAndCalculate(String input) {
        input = input.replace(" ", ""); // Entferne Leerzeichen
        String[] tokens = input.split("(?<=[-+x/])|(?=[-+x/])"); // Split auf Basis von Operatoren

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

        screen = Double.toString(result);
        if (screen.endsWith(".0")) {
            screen = screen.substring(0, screen.length() - 2); // Entferne unnötige Dezimalstellen
        }
    }

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
     * Empfängt den Wert einer gedrückten Zifferntaste. Da man nur eine Taste auf einmal
     * drücken kann muss der Wert positiv und einstellig sein und zwischen 0 und 9 liegen.
     * Führt in jedem Fall dazu, dass die gerade gedrückte Ziffer auf dem Bildschirm angezeigt
     * oder rechts an die zuvor gedrückte Ziffer angehängt angezeigt wird.
     * Es werden maximal 9 Ziffern dargestellt.
     * @param digit Die Ziffer, deren Taste gedrückt wurde
     */
    public void pressDigitKey(double digit) {
        screen = Double.toString(digit);
        latestValue = digit;
    }

    /**
     * Empfängt den Wert einer gedrückten binären Operationstaste, also eine der vier Operationen
     * Addition, Substraktion, Division, oder Multiplikation, welche zwei Operanden benötigen.
     * Beim ersten Drücken der Taste wird der Bildschirminhalt nicht verändert, sondern nur der
     * Rechner in den passenden Operationsmodus versetzt.
     * Beim zweiten Drücken nach Eingabe einer weiteren Zahl wird direkt des aktuelle Zwischenergebnis
     * auf dem Bildschirm angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * @param operation "+" für Addition, "-" für Substraktion, "x" für Multiplikation, "/" für Division
     */
    public void pressBinaryOperationKey(String operation) {
        latestValue = Double.parseDouble(screen);
        latestOperation = operation;
    }

    /**
     * Empfängt den Wert einer gedrückten unären Operationstaste, also eine der drei Operationen
     * Quadratwurzel, Prozent, Inversion, welche nur einen Operanden benötigen.
     * Beim Drücken der Taste wird direkt die Operation auf den aktuellen Zahlenwert angewendet und
     * der Bildschirminhalt mit dem Ergebnis aktualisiert.
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
     * Empfängt den Befehl der gedrückten Dezimaltrennzeichentaste, im Englischen üblicherweise "."
     * Fügt beim ersten Mal Drücken dem aktuellen Bildschirminhalt das Trennzeichen auf der rechten
     * Seite hinzu und aktualisiert den Bildschirm. Daraufhin eingegebene Zahlen werden rechts vom
     * Trennzeichen angegeben und daher als Dezimalziffern interpretiert.
     * Beim zweimaligem Drücken, oder wenn bereits ein Trennzeichen angezeigt wird, passiert nichts.
     */
    public void pressDotKey() {
        if(!screen.contains(".")) screen = screen + ".";
    }

    /**
     * Empfängt den Befehl der gedrückten Vorzeichenumkehrstaste ("+/-").
     * Zeigt der Bildschirm einen positiven Wert an, so wird ein "-" links angehängt, der Bildschirm
     * aktualisiert und die Inhalt fortan als negativ interpretiert.
     * Zeigt der Bildschirm bereits einen negativen Wert mit führendem Minus an, dann wird dieses
     * entfernt und der Inhalt fortan als positiv interpretiert.
     */
    public void pressNegativeKey() {
        screen = screen.startsWith("-") ? screen.substring(1) : "-" + screen;
    }

    /**
     * Empfängt den Befehl der gedrückten "="-Taste.
     * Wurde zuvor keine Operationstaste gedrückt, wird der zuvor eingegebene Wert ausgegeben.
     * Wurde zuvor eine binäre Operationstaste gedrückt und zwei Operanden eingegeben, wird das
     * Ergebnis der Operation angezeigt. Falls hierbei eine Division durch Null auftritt, wird "Error" angezeigt.
     * Wird die Taste weitere Male gedrückt (ohne andere Tasten dazwischen), so wird die letzte
     * Operation (ggf. inklusive letztem Operand) erneut auf den aktuellen Bildschirminhalt angewandt
     * und das Ergebnis direkt angezeigt.
     */
    public void pressEqualsKey() {
        double secondValue = Double.parseDouble(screen);
        double result = switch (latestOperation) {
            case "+" -> latestValue + secondValue;
            case "-" -> latestValue - secondValue;
            case "x" -> latestValue * secondValue;
            case "/" -> secondValue == 0 ? Double.POSITIVE_INFINITY : latestValue / secondValue;
            default -> secondValue;
        };

        if (Double.isInfinite(result)) {
            screen = "Error";
        } else {
            screen = Double.toString(result);
            if (screen.endsWith(".0")) screen = screen.substring(0, screen.length() - 2);
        }
    }
}
