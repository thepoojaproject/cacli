package com.example.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends Activity {
    TextView display;
    String current = "0";
    double first = 0;
    String op = "";
    boolean fresh = true;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        build();
    }

    void build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 22, 18, 18);
        root.setBackgroundColor(Color.rgb(247,247,248));

        display = new TextView(this);
        display.setText("0");
        display.setTextSize(48);
        display.setTextColor(Color.rgb(25,25,25));
        display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        display.setPadding(10, 10, 10, 10);
        root.addView(display, new LinearLayout.LayoutParams(-1, 0, 1.25f));

        String[][] keys = {
            {"AC","⌫","%","÷"},
            {"7","8","9","×"},
            {"4","5","6","−"},
            {"1","2","3","+"},
            {"+/−","0",".","="}
        };

        for (String[] row : keys) {
            LinearLayout line = new LinearLayout(this);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setWeightSum(4);
            for (String k : row) {
                Button btn = new Button(this);
                btn.setText(k);
                btn.setTextSize(22);
                btn.setAllCaps(false);
                btn.setOnClickListener(v -> press(((Button)v).getText().toString()));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, 0, 1);
                p.setMargins(5,5,5,5);
                line.addView(btn,p);
            }
            root.addView(line, new LinearLayout.LayoutParams(-1,0,1));
        }
        setContentView(root);
    }

    void press(String k) {
        if (k.matches("[0-9]") || k.equals(".")) {
            if (fresh || current.equals("0")) current = k.equals(".") ? "0." : k;
            else if (!(k.equals(".") && current.contains("."))) current += k;
            fresh = false;
        } else if (k.equals("AC")) {
            current="0"; first=0; op=""; fresh=true;
        } else if (k.equals("⌫")) {
            if (!fresh && current.length()>1) current=current.substring(0,current.length()-1);
            else current="0";
        } else if (k.equals("+/−")) {
            if (!current.equals("0")) current = current.startsWith("-") ? current.substring(1) : "-"+current;
        } else if (k.equals("%")) {
            current = fmt(Double.parseDouble(current)/100);
        } else if (k.equals("=")) {
            calculate();
        } else {
            first = Double.parseDouble(current);
            op = k;
            fresh = true;
        }
        display.setText(current);
    }

    void calculate() {
        if (op.isEmpty()) return;
        double second;
        try { second=Double.parseDouble(current); } catch(Exception e) { return; }

        double result;
        if (op.equals("+")) result=first+second;
        else if (op.equals("−")) result=first-second;
        else if (op.equals("×")) result=first*second;
        else {
            if (second==0) {
                current="Error"; op=""; fresh=true; display.setText(current); return;
            }
            result=first/second;
        }
        current=fmt(result);
        op="";
        fresh=true;
    }

    String fmt(double x) {
        if (Math.abs(x-Math.rint(x)) < 1e-10)
            return String.format(Locale.US,"%.0f",x);
        return String.format(Locale.US,"%.10f",x)
                .replaceAll("0+$","").replaceAll("\\.$","");
    }
}
