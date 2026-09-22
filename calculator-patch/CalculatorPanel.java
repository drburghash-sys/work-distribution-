package com.bms.floatingcalculator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class CalculatorPanel {
    public interface ExtraAction { void onFloatRequested(); }

    private final Context context;
    private final CalculatorEngine engine = CalculatorState.ENGINE;
    private final boolean compact;
    private TextView expression;
    private TextView result;
    private HorizontalScrollView scroll;
    private TextView memoryIndicator;

    private static final int BG = Color.rgb(12, 15, 22);
    private static final int PANEL = Color.rgb(24, 29, 39);
    private static final int KEY = Color.rgb(38, 45, 58);
    private static final int OP = Color.rgb(61, 87, 138);
    private static final int SPECIAL = Color.rgb(90, 63, 120);
    private static final int EQUAL = Color.rgb(28, 120, 92);

    public CalculatorPanel(Context context, boolean compact) {
        this.context = context;
        this.compact = compact;
    }

    public View build(ExtraAction extraAction) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(compact ? 8 : 14), dp(compact ? 6 : 12), dp(compact ? 8 : 14), dp(compact ? 8 : 14));
        root.setBackgroundColor(BG);

        LinearLayout top = new LinearLayout(context);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(context);
        title.setText("BMS Calculator");
        title.setTextColor(Color.WHITE);
        title.setTextSize(compact ? 13 : 17);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        top.addView(title, new LinearLayout.LayoutParams(0, dp(compact ? 28 : 36), 1f));

        memoryIndicator = topLabel(engine.hasMemory() ? "M●" : "M○");
        top.addView(memoryIndicator, new LinearLayout.LayoutParams(dp(compact ? 42 : 52), dp(compact ? 28 : 36)));

        if (extraAction != null) {
            Button floating = topButton("◩");
            floating.setContentDescription("Floating calculator");
            floating.setOnClickListener(v -> extraAction.onFloatRequested());
            LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(dp(compact ? 40 : 48), dp(compact ? 28 : 36));
            flp.setMarginStart(dp(6));
            top.addView(floating, flp);
        }
        root.addView(top, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setFillViewport(false);
        expression = new TextView(context);
        expression.setSingleLine(true);
        expression.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        expression.setTextColor(Color.rgb(190, 199, 214));
        expression.setTextSize(compact ? 18 : 26);
        expression.setTypeface(Typeface.MONOSPACE);
        expression.setPadding(dp(8), dp(2), dp(8), dp(2));
        scroll.addView(expression, new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(compact ? 42 : 56));
        slp.topMargin = dp(4);
        root.addView(scroll, slp);

        result = new TextView(context);
        result.setSingleLine(true);
        result.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        result.setTextColor(Color.WHITE);
        result.setTextSize(compact ? 24 : 38);
        result.setTypeface(Typeface.DEFAULT_BOLD);
        result.setPadding(dp(8), 0, dp(8), dp(4));
        root.addView(result, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(compact ? 44 : 62)));

        LinearLayout keys = new LinearLayout(context);
        keys.setOrientation(LinearLayout.VERTICAL);

        addRow(keys,
                key("MC", SPECIAL, v -> { engine.memoryClear(); refresh(); }),
                key("MR", SPECIAL, v -> { engine.memoryRecall(); refresh(); }),
                key("MS", SPECIAL, v -> { engine.memoryStore(); refresh(); }),
                key("⌫", SPECIAL, v -> { engine.backspace(); refresh(); }));
        addRow(keys,
                key("x²", SPECIAL, v -> { engine.square(); refresh(); }),
                key("√x", SPECIAL, v -> { engine.sqrt(); refresh(); }),
                key("1/x", SPECIAL, v -> { engine.reciprocal(); refresh(); }),
                key("÷", OP, v -> { engine.operator('/'); refresh(); }));
        addRow(keys,
                key("7", KEY, v -> digit('7')),
                key("8", KEY, v -> digit('8')),
                key("9", KEY, v -> digit('9')),
                key("×", OP, v -> { engine.operator('*'); refresh(); }));
        addRow(keys,
                key("4", KEY, v -> digit('4')),
                key("5", KEY, v -> digit('5')),
                key("6", KEY, v -> digit('6')),
                key("−", OP, v -> { engine.operator('-'); refresh(); }));
        addRow(keys,
                key("1", KEY, v -> digit('1')),
                key("2", KEY, v -> digit('2')),
                key("3", KEY, v -> digit('3')),
                key("+", OP, v -> { engine.operator('+'); refresh(); }));
        addRow(keys,
                key("%", SPECIAL, v -> { engine.percent(); refresh(); }),
                key("0", KEY, v -> digit('0')),
                key(".", KEY, v -> { engine.decimal(); refresh(); }),
                key("±", SPECIAL, v -> { engine.toggleSign(); refresh(); }));

        LinearLayout last = new LinearLayout(context);
        last.setOrientation(LinearLayout.HORIZONTAL);
        addToRow(last, key("AC", SPECIAL, v -> { engine.clearAll(); refresh(); }), 1f);
        addToRow(last, key("=", EQUAL, v -> { engine.equalsResult(); refresh(); }), 2f);
        addToRow(last, key("C", SPECIAL, v -> { engine.clearAll(); refresh(); }), 1f);
        keys.addView(last, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout.LayoutParams klp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        klp.topMargin = dp(4);
        root.addView(keys, klp);

        refresh();
        return root;
    }

    private void addRow(LinearLayout parent, Button... buttons) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (Button b : buttons) addToRow(row, b, 1f);
        parent.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
    }

    private void addToRow(LinearLayout row, Button b, float weight) {
        int m = dp(compact ? 2 : 3);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.setMargins(m, m, m, m);
        row.addView(b, lp);
    }

    private Button key(String text, int bg, View.OnClickListener click) {
        Button b = new Button(context);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(compact ? 15 : 20);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        b.setGravity(Gravity.CENTER);
        b.setPadding(0,0,0,0);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(bg);
        gd.setCornerRadius(dp(compact ? 10 : 14));
        b.setBackground(gd);
        b.setOnClickListener(click);
        return b;
    }

    private Button topButton(String text) {
        Button b = new Button(context);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(compact ? 11 : 13);
        b.setPadding(0,0,0,0);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(PANEL);
        gd.setCornerRadius(dp(9));
        b.setBackground(gd);
        return b;
    }

    private TextView topLabel(String text) {
        TextView t = new TextView(context);
        t.setText(text);
        t.setTextColor(Color.WHITE);
        t.setTextSize(compact ? 11 : 13);
        t.setGravity(Gravity.CENTER);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(PANEL);
        gd.setCornerRadius(dp(9));
        t.setBackground(gd);
        return t;
    }

    private void digit(char c) { engine.digit(c); refresh(); }

    private void refresh() {
        if (expression == null) return;
        expression.setText(engine.getExpression());
        result.setText(engine.getResultPreview());
        memoryIndicator.setText(engine.hasMemory() ? "M●" : "M○");
        scroll.post(() -> scroll.fullScroll(View.FOCUS_RIGHT));
    }

    private int dp(int v) { return (int) (v * context.getResources().getDisplayMetrics().density + 0.5f); }
}
