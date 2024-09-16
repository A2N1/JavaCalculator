package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Retro calculator")
class CalculatorTest {

    @Test
    @DisplayName("should display result after adding two positive multi-digit numbers")
    void testPositiveAddition() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressDigitKey(0);
        calc.pressBinaryOperationKey("+");
        calc.pressDigitKey(2);
        calc.pressDigitKey(0);
        calc.pressEqualsKey();

        String expected = "40";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display result after getting the square root of two")
    void testSquareRoot() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(2);
        calc.pressUnaryOperationKey("√");

        String expected = "1.41421356";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display error when dividing by zero")
    void testDivisionByZero() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(7);
        calc.pressBinaryOperationKey("/");
        calc.pressDigitKey(0);
        calc.pressEqualsKey();

        String expected = "Error";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should display error when drawing the square root of a negative number")
    void testSquareRootOfNegative() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(7);
        calc.pressNegativeKey();
        calc.pressUnaryOperationKey("√");

        String expected = "Error";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should not allow multiple decimal dots")
    void testMultipleDecimalDots() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(1);
        calc.pressDotKey();
        calc.pressDigitKey(7);
        calc.pressDotKey();
        calc.pressDigitKey(8);

        String expected = "1.78";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test //Test: Multiplikation 7x9=63
    @DisplayName("should display result after multiplying two positive numbers")
    void testPositiveMultiplication() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(7);
        calc.pressBinaryOperationKey("x");
        calc.pressDigitKey(9);
        calc.pressEqualsKey();

        String expected = "63";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    //@Test //Punkt vor Strich Bug
    //@DisplayName("should do multiplication and division first, then addition and subtraction")
    //void testMathRule() {
    //Calculator calc = new Calculator();

    //calc.pressDigitKey(5);
    //calc.pressBinaryOperationKey("+");
    //calc.pressDigitKey(3);
    //calc.pressBinaryOperationKey("x");
    //calc.pressDigitKey(4);
    //calc.pressEqualsKey();

    //String expected = "17";
    //String actual = calc.readScreen();

    //assertEquals(expected, actual);
    //}

    @Test //test 9 equals 9
    @DisplayName("should show that a number is equal the same number")
    void testEqualNumber() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(9);
        calc.pressEqualsKey();

        String expected = "9";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should display max. 9 digits")
    void testMaxDigits() {
        Calculator calc = new Calculator();

        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);
        calc.pressDigitKey(5);

        String expected = "555555555";
        String actual = calc.readScreen();

        assertEquals(expected, actual);
    }

}