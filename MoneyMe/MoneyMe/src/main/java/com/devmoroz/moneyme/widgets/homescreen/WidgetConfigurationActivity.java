package com.devmoroz.moneyme.widgets.homescreen;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.devmoroz.moneyme.R;

public class WidgetConfigurationActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback, View.OnClickListener {

    private Activity mActivity;
    private Button confirmButton;
    private Button cancelButton;
    private RadioGroup mRadioGroup;
    private LinearLayout backgroundContainer;
    private LinearLayout textColorContainer;
    private SwitchCompat switchWidgetStyle;
    private ImageView colorCircle;
    private TextView isExtendedTextView;
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int selectedTextColor = Color.parseColor("#ffffff");
    private int selectedBackground = Color.parseColor("#1976D2");
    private int transparentBackground = Color.parseColor("#00000000");
    private boolean isTransparent = true;
    private boolean isExtended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configuration);
        mActivity = this;
        setResult(RESULT_CANCELED);

        confirmButton = (Button) findViewById(R.id.saveButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WidgetProvider.updateConfiguration(mActivity, mAppWidgetId, selectedTextColor, isTransparent ? transparentBackground : selectedBackground, isExtended);

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                        mAppWidgetId);
                setResult(RESULT_OK, resultValue);

                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, mActivity, WalletWidget.class);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
                sendBroadcast(intent);

                finish();
            }
        });

        cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mRadioGroup = (RadioGroup) findViewById(R.id.widget_config_radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.widget_config_transparent:
                        backgroundContainer.setVisibility(View.GONE);
                        isTransparent = true;
                        break;

                    case R.id.widget_config_colored:
                        backgroundContainer.setVisibility(View.VISIBLE);
                        backgroundContainer.setOnClickListener(WidgetConfigurationActivity.this);
                        isTransparent = false;
                        break;
                }
            }
        });

        backgroundContainer = (LinearLayout) findViewById(R.id.widget_config_background);
        backgroundContainer.setOnClickListener(this);
        colorCircle = (ImageView) findViewById(R.id.widget_config_background_circle);
        colorCircle.setColorFilter(selectedBackground);
        backgroundContainer.setVisibility(View.GONE);
        textColorContainer = (LinearLayout) findViewById(R.id.widget_config_textcolor);
        textColorContainer.setOnClickListener(this);

        isExtendedTextView = (TextView) findViewById(R.id.widget_config_isExtended_text);
        switchWidgetStyle = (SwitchCompat) findViewById(R.id.widget_config_isExtended);
        switchWidgetStyle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isExtended = isChecked;
                toggleTextExtended(isChecked);
            }
        });


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int selectedColor) {
        switch (dialog.getTitle()) {
            case R.string.widget_text_color:
                selectedTextColor = selectedColor;
                break;
            case R.string.widget_background_color:
                selectedBackground = selectedColor;
                colorCircle.setColorFilter(selectedBackground);
                break;
        }
    }

    public void showColorChooserDialog(int selectedColor, int titleRes) {
        new ColorChooserDialog.Builder(this, titleRes)
                .dynamicButtonColor(false)
                .preselect(selectedColor)
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.widget_config_background:
                showColorChooserDialog(selectedBackground, R.string.widget_background_color);
                break;
            case R.id.widget_config_textcolor:
                showColorChooserDialog(selectedTextColor, R.string.widget_text_color);
                break;
        }
    }

    public void toggleTextExtended(boolean checked) {
        if (checked) {
            isExtendedTextView.setText(R.string.widget_style_max);
        } else {
            isExtendedTextView.setText(R.string.widget_style_min);
        }
    }
}
