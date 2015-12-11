package com.devmoroz.moneyme.widgets;

import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Objects;


public class CalculatorDialog {

    public interface Callback {
        void onCloseClick(String result);
    }

    private final Callback mCallback;

    TextView calculator_result;
    TextView calculator_preview;

    Button button_one, button_two, button_three,
            button_four, button_five, button_six,
            button_seven, button_eight, button_nine,
            button_zero, button_plus, button_minus,
            button_multiply, button_divide,
            button_back, button_dot, button_equals, button_clear, button_close;

    ArrayList<Double> math = new ArrayList<>();
    double first_number;
    double second_number;
    double third_number;
    String intermediate_value = "";

    int currentOperation = 0;
    int nextOperation = 0;
    int tempOperation = 0;
    boolean waitForThirdNumber = false;
    final static int ADD = 1;
    final static int SUBTRACT = 2;
    final static int MULTIPLY = 3;
    final static int DIVISION = 4;
    final static int EQUALS = 9;
    final static int CLEAR = 5;
    final static int DONT_CLEAR = 0;
    int clearDisplay = 0;
    int MAX_LENGTH = 10;

    public CalculatorDialog(View v, Callback callback) {
        mCallback = callback;
        calculator_result = (TextView) v.findViewById(R.id.calculator_result);
        calculator_preview = (TextView) v.findViewById(R.id.calculator_preview);

        button_one = (Button) v.findViewById(R.id.number_one);
        button_two = (Button) v.findViewById(R.id.number_two);
        button_three = (Button) v.findViewById(R.id.number_three);
        button_four = (Button) v.findViewById(R.id.number_four);
        button_five = (Button) v.findViewById(R.id.number_five);
        button_six = (Button) v.findViewById(R.id.number_six);
        button_seven = (Button) v.findViewById(R.id.number_seven);
        button_eight = (Button) v.findViewById(R.id.number_eight);
        button_nine = (Button) v.findViewById(R.id.number_nine);
        button_zero = (Button) v.findViewById(R.id.number_zero);
        button_plus = (Button) v.findViewById(R.id.button_plus);
        button_minus = (Button) v.findViewById(R.id.button_minus);
        button_multiply = (Button) v.findViewById(R.id.button_multiply);
        button_divide = (Button) v.findViewById(R.id.button_divide);
        button_back = (Button) v.findViewById(R.id.button_del);
        button_dot = (Button) v.findViewById(R.id.button_dot);
        button_equals = (Button) v.findViewById(R.id.button_equals);
        button_clear = (Button) v.findViewById(R.id.button_clear);
        button_close = (Button) v.findViewById(R.id.button_close);

        calculator_result.setKeyListener(DigitsKeyListener.getInstance(true, true));
        calculator_result.setText("0");
        registerListeners();
        clearDisplay = CLEAR;
    }

    private void setCalcText(String num) {
        if (clearDisplay == CLEAR) {
            calculator_result.setText("");
        }
        clearDisplay = DONT_CLEAR;
        calculator_result.append(num);
    }

    private void registerListeners() {
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = calculator_result.getText().toString();
                mCallback.onCloseClick(result);
            }
        });
        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("1");
            }
        });
        button_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("2");
            }
        });
        button_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("3");
            }
        });
        button_four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("4");
            }
        });
        button_five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("5");
            }
        });
        button_six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("6");
            }
        });
        button_seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("7");
            }
        });
        button_eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("8");
            }
        });
        button_nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCalcText("9");
            }
        });
        button_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clearDisplay == CLEAR) {
                    calculator_result.setText("");
                }
                if (calculator_result.getText().toString() == "0") {
                    clearDisplay = CLEAR;
                    return;
                }
                clearDisplay = DONT_CLEAR;
                calculator_result.append("0");
            }
        });
        button_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clearDisplay == CLEAR) {
                    calculator_result.setText("0");
                }
                if (calculator_result.getText().toString().contains(".")) {
                    return;
                } else calculator_result.append(".");
            }
        });
        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcLogic(ADD);
            }
        });
        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcLogic(SUBTRACT);
            }
        });
        button_multiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcLogic(MULTIPLY);
            }
        });
        button_divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcLogic(DIVISION);
            }
        });
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clearDisplay == CLEAR) {
                    return;
                }
                String text = calculator_result.getText().toString();
                if (FormatUtils.isEmpty(text) || text == "0") {
                    return;
                } else if (text.length() == 1) {
                    calculator_result.setText("0");
                    clearDisplay = CLEAR;
                } else {
                    String newText = text.substring(0, text.length() - 1);
                    calculator_result.setText(newText);
                }
            }
        });
        button_equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcLogic(EQUALS);
            }
        });
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculator_result.setText("0");
                calculator_preview.setText("");
                clearDisplay = CLEAR;
                first_number = 0;
                second_number = 0;
                third_number = 0;
                math.removeAll(math);
            }
        });
    }

    private void calcLogic(int operator) {

        if (clearDisplay == DONT_CLEAR) {
            String calc_result = calculator_result.getText().toString();
            if (calc_result.endsWith(".") || calc_result.endsWith(".0") || calc_result.endsWith(".00")) {
                String formattedText = calc_result.substring(0, calc_result.indexOf("."));
                calculator_preview.append(formattedText);
                math.add(Double.parseDouble(formattedText));
            } else {
                calculator_preview.append(calc_result);
                math.add(Double.parseDouble(calc_result));
            }

        }
        String calc_preview = calculator_preview.getText().toString();

        if (clearDisplay == CLEAR && calc_preview.length() > 3) {
            String formatted = calc_preview.substring(0, calc_preview.length() - 3);
            calculator_preview.setText(formatted);
        }

        switch (operator) {
            case ADD:
                calculator_preview.append(" + ");
                calculate(ADD);
                break;
            case SUBTRACT:
                calculator_preview.append(" - ");
                calculate(SUBTRACT);
                break;
            case MULTIPLY:
                calculator_preview.append(" * ");
                calculate(MULTIPLY);
                break;
            case DIVISION:
                calculator_preview.append(" / ");
                calculate(DIVISION);
                break;
            case EQUALS:
                calculator_preview.setText("");
                calculate(EQUALS);
                break;
        }

        clearDisplay = CLEAR;
    }

    private void calculate(int operator) {
        if (operator != EQUALS) {
            nextOperation = operator;
        } else {
            nextOperation = 0;
            waitForThirdNumber = false;
        }
        if (math.size() == 3) {
            if ((operator == MULTIPLY || operator == DIVISION) && !waitForThirdNumber) {
                waitForThirdNumber = true;
            } else{
                waitForThirdNumber = false;
            }
            switch (currentOperation) {
                case ADD:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number + second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case SUBTRACT:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number - second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case MULTIPLY:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number * second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case DIVISION:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number / second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
            }
        }
        if (math.size() == 2) {
            if ((operator == MULTIPLY || operator == DIVISION) && !waitForThirdNumber) {
                tempOperation = currentOperation;
                waitForThirdNumber = true;
            }
        }
        if (math.size() == 2 && !waitForThirdNumber) {
            switch (currentOperation) {
                case ADD:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number + second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case SUBTRACT:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number - second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case MULTIPLY:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number * second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
                case DIVISION:
                    first_number = math.get(0);
                    second_number = math.get(1);
                    math.removeAll(math);
                    math.add(first_number / second_number);
                    calculator_result.setText(String.format("%.2f", math.get(0)));
                    break;
            }
        }
        currentOperation = nextOperation;
        if (operator == EQUALS) {
            first_number = 0;
            second_number = 0;
            third_number = 0;
            math.removeAll(math);
        }
    }
}
