package com.xishuang.imagesizetest.colorfilter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xishuang.imagesizetest.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author:xishuang
 * Date:2018.04.03
 * Des: ColorMatrixColorFilter的效果调试界面
 * <p>
 * [  a(0), b(1), c(2), d(3), e(4),
 * f(5) ,g(6), h(7), i(8), j(9),
 * k(10),l(11),m(12),n(13),o(14),
 * p(15),q(16),r(17),s(18),t(19)]
 */
public class ColorMatrixActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ImageView imageView;
    private TextView tvModeText;
    private TextView tvHueText;
    private TextView tvSaturationText;
    private TextView tvBrightnessText;
    private SeekBar sBHue;
    private SeekBar sBSaturation;
    private SeekBar sBBrightness;

    ColorMatrix colorMatrix = new ColorMatrix();
    /**
     * 色调，改变颜色
     */
    ColorMatrix hueMatrix = new ColorMatrix();
    /**
     * 饱和度，改变颜色的纯度
     */
    ColorMatrix saturationMatrix = new ColorMatrix();
    /**
     * 亮度，颜色不变
     */
    ColorMatrix brightnessMatrix = new ColorMatrix();

    private List<? extends Map<String, ?>> mDataList;
    float[] matrix = SpecialColorMatrix.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_matrix);

        imageView = (ImageView) findViewById(R.id.color_matrix_img);
        tvModeText = (TextView) findViewById(R.id.color_matrix_mode);
        tvHueText = (TextView) findViewById(R.id.color_matrix_hue_text);
        tvSaturationText = (TextView) findViewById(R.id.color_matrix_saturation_text);
        tvBrightnessText = (TextView) findViewById(R.id.color_matrix_brightness_text);
        //SeekBar
        sBHue = (SeekBar) findViewById(R.id.color_matrix_hue);
        sBSaturation = (SeekBar) findViewById(R.id.color_matrix_saturation);
        sBBrightness = (SeekBar) findViewById(R.id.color_matrix_brightness);
        //RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.color_matrix_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ColorMatrixAdapter());

        sBHue.setOnSeekBarChangeListener(this);
        sBSaturation.setOnSeekBarChangeListener(this);
        sBBrightness.setOnSeekBarChangeListener(this);

        mDataList = getData();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        float hueValue = sBHue.getProgress() * 1.0f;
        float saturationValue = sBSaturation.getProgress() / 128f;
        float brightnessValue = sBBrightness.getProgress() / 128f;

        //展示出这三个值大小
        DecimalFormat format = new DecimalFormat(".0");
        String value = format.format(saturationValue);
        tvHueText.setText("色调值：" + hueValue + "°");
        tvSaturationText.setText("饱和度值：" + value);
        value = format.format(brightnessValue);
        tvBrightnessText.setText("亮度值：" + value);

        //设置色相，为0°和360的时候相当于原图
        hueMatrix.reset();
        hueMatrix.setRotate(0, hueValue);
        hueMatrix.setRotate(1, hueValue);
        hueMatrix.setRotate(2, hueValue);

        //设置饱和度，为1的时候相当于原图
        saturationMatrix.reset();
        saturationMatrix.setSaturation(saturationValue);

        //亮度，为1的时候相当于原图
        brightnessMatrix.reset();
        brightnessMatrix.setScale(brightnessValue, brightnessValue, brightnessValue, 1);

        //将上面三种效果和选中的模式混合在一起
        colorMatrix.reset();
        colorMatrix.postConcat(new ColorMatrix(matrix));
        colorMatrix.postConcat(hueMatrix);
        colorMatrix.postConcat(saturationMatrix);
        colorMatrix.postConcat(brightnessMatrix);

        imageView.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /**
     * 数据初始化
     */
    private List<? extends Map<String, ?>> getData() {
        List<Map<String, Object>> data = new ArrayList<>();
        addItem(data, "效果清空", SpecialColorMatrix.MODE.DEFAULT);
        addItem(data, "怀旧效果", SpecialColorMatrix.MODE.HUAIJIU);
        addItem(data, "底片反转", SpecialColorMatrix.MODE.DIPIAN);
        addItem(data, "灰度效果", SpecialColorMatrix.MODE.GRAY);
        addItem(data, "高亮效果", SpecialColorMatrix.MODE.BRIGHT);

        return data;
    }

    private void addItem(List<Map<String, Object>> data, String title, int value) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("value", value);
        data.add(map);
    }

    class ColorMatrixAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ColorMatrixActivity.this).inflate(R.layout.activity_porter_duff_item, parent, false);
            return new ColorMatrixViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ColorMatrixViewHolder) {
                ColorMatrixViewHolder colorMatrixViewHolder = (ColorMatrixViewHolder) holder;
                colorMatrixViewHolder.itemTextView.setText((position + 1) + "、" + mDataList.get(position).get("title"));
                colorMatrixViewHolder.itemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getLayoutPosition();
                        Toast.makeText(ColorMatrixActivity.this, (String) mDataList.get(position).get("title"), Toast.LENGTH_SHORT).show();
                        matrix = ImageUtil.displayImageColorMatrix(imageView, (Integer) mDataList.get(position).get("value"));
                        tvModeText.setText("模式：" + mDataList.get(position).get("title"));
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mDataList.size();
        }
    }

    class ColorMatrixViewHolder extends RecyclerView.ViewHolder {

        TextView itemTextView;

        ColorMatrixViewHolder(View itemView) {
            super(itemView);
            itemTextView = (TextView) itemView.findViewById(R.id.porter_duff_item_text);
        }
    }
}
