package com.devmoroz.moneyme.widgets;

import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devmoroz.moneyme.R;
import com.devmoroz.moneyme.utils.Expression;
import com.devmoroz.moneyme.utils.FormatUtils;

import java.math.BigDecimal;


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

    String calc = "";
    String calculationRes = "";

    final static int ADD = 1;
    final static int SUBTRACT = 2;
    final static int MULTIPLY = 3;
    final static int DIVISION = 4;
    final static int EQUALS = 9;
    final static int CLEAR = 5;
    final static int DONT_CLEAR = 0;
    final static int TOTAL_CLEAR = 6;
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
        if(clearDisplay == TOTAL_CLEAR){
            calculator_result.setText("");
            calculator_preview.setText("");
        }
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
                mCallback.onCloseClick(calculationRes);
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
                if(clearDisplay == TOTAL_CLEAR){
                    calculator_preview.setText("");
                    calculator_result.setText("");
                }
                if (clearDisplay == CLEAR ) {
                    calculator_result.setText("0");
                }
                if (calculator_result.getText().toString().equals("0")) {
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
                if (clearDisplay == CLEAR || clearDisplay == TOTAL_CLEAR) {
                    calculator_result.setText("0");
                }
                if (calculator_result.getText().toString().contains(".")) {
                    return;
                }
                else{
                    calculator_result.append(".");
                    clearDisplay = DONT_CLEAR;
                }
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
                if (clearDisplay == CLEAR || clearDisplay == TOTAL_CLEAR) {
                    return;
                }
                String text = calculator_result.getText().toString();
                if (FormatUtils.isEmpty(text) || text.equals("0")) {
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
                calculationRes = "";
                clearDisplay = CLEAR;
            }
        });
    }

    private void calculate(){
        calc = calculator_preview.getText().toString();
        if(calc.isEmpty()){
            return;
        }
        try {
            Expression e = new Expression(calc);
            e = e.setPrecision(3);
            BigDecimal result_bd = e.eval();
            calculationRes = String.format("%.2f", result_bd);
            String tmp_result = result_bd.toPlainString();
            calculator_result.setText(tmp_result);
            calc = "";
            clearDisplay = TOTAL_CLEAR;
        }catch (Exception ex){
            calculationRes = "";
        }
    }

    private void calcLogic(int operator) {

        if(operator == EQUALS){
            if(clearDisplay == DONT_CLEAR){
                calculator_preview.append(calculator_result.getText().toString());
                calculate();
                return;
            }
        }
        if(clearDisplay == TOTAL_CLEAR){
            calculator_preview.setText("");
            clearDisplay = DONT_CLEAR;
        }
        if (clearDisplay == DONT_CLEAR) {
            String calc_result = calculator_result.getText().toString();
            if (calc_result.endsWith(".") || calc_result.endsWith(".0") || calc_result.endsWith(".00")) {
                String formattedText = calc_result.substring(0, calc_result.indexOf("."));
                calculator_preview.append(formattedText);
            } else {
                calculator_preview.append(calc_result);
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
                break;
            case SUBTRACT:
                calculator_preview.append(" - ");
                break;
            case MULTIPLY:
                calculator_preview.append(" * ");
                break;
            case DIVISION:
                calculator_preview.append(" / ");
                break;
        }
        clearDisplay = CLEAR;
    }


}
