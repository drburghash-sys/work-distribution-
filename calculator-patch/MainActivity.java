package com.bms.floatingcalculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean pendingOverlay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try { enterImmersive(); } catch (Throwable ignored) {}

        try {
            CalculatorPanel panel = new CalculatorPanel(this, false);
            setContentView(panel.build(this::requestFloating));
        } catch (Throwable t) {
            Toast.makeText(this, "تعذر فتح الحاسبة: " + t.getClass().getSimpleName(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try { enterImmersive(); } catch (Throwable ignored) {}
        if (pendingOverlay && Settings.canDrawOverlays(this)) {
            pendingOverlay = false;
            startFloatingServiceSafe();
        }
    }

    private void requestFloating() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            pendingOverlay = true;
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } catch (Throwable t) {
                Toast.makeText(this, "فعّل صلاحية الظهور فوق التطبيقات من إعدادات الهاتف", Toast.LENGTH_LONG).show();
            }
            return;
        }
        startFloatingServiceSafe();
    }

    private void startFloatingServiceSafe() {
        try {
            Intent i = new Intent(this, FloatingCalculatorService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i); else startService(i);
            moveTaskToBack(true);
        } catch (Throwable t) {
            Toast.makeText(this, "تعذر تشغيل النافذة العائمة", Toast.LENGTH_LONG).show();
        }
    }

    private void enterImmersive() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
