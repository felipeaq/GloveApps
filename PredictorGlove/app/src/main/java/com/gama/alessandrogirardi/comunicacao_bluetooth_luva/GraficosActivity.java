package com.gama.alessandrogirardi.comunicacao_bluetooth_luva;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;

public class GraficosActivity extends AppCompatActivity {
    private GraphView mGraphx;
    private GraphView mGraphy;
    private GraphView mGraphz;
    private GraphView mGraphx_acc;
    private GraphView mGraphy_acc;
    private GraphView mGraphz_acc;
    private CheckBox cbg1;
    private CheckBox cbg2;
    private CheckBox cbg3;
    private CheckBox cbg4;
    private CheckBox cbg5;
    private CheckBox cbg6;
    private CheckBox cba1;
    private CheckBox cba2;
    private CheckBox cba3;
    private CheckBox cba4;
    private CheckBox cba5;
    private CheckBox cba6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficos);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Graphs");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Consts.seriesx1.setColor(Color.GREEN);
        Consts.seriesy1.setColor(Color.GREEN);
        Consts.seriesz1.setColor(Color.GREEN);
        findViewById(R.id.csg1).setBackgroundColor(Color.GREEN);
        Consts.seriesx2.setColor(Color.RED);
        Consts.seriesy2.setColor(Color.RED);
        Consts.seriesz2.setColor(Color.RED);
        findViewById(R.id.csg2).setBackgroundColor(Color.RED);
        Consts.seriesx3.setColor(Color.BLUE);
        Consts.seriesy3.setColor(Color.BLUE);
        Consts.seriesz3.setColor(Color.BLUE);
        findViewById(R.id.csg3).setBackgroundColor(Color.BLUE);
        Consts.seriesx4.setColor(Color.YELLOW);
        Consts.seriesy4.setColor(Color.YELLOW);
        Consts.seriesz4.setColor(Color.YELLOW);
        findViewById(R.id.csg4).setBackgroundColor(Color.YELLOW);
        Consts.seriesx5.setColor(Color.MAGENTA);
        Consts.seriesy5.setColor(Color.MAGENTA);
        Consts.seriesz5.setColor(Color.MAGENTA);
        findViewById(R.id.csg5).setBackgroundColor(Color.MAGENTA);
        Consts.seriesx6.setColor(Color.GRAY);
        Consts.seriesy6.setColor(Color.GRAY);
        Consts.seriesz6.setColor(Color.GRAY);
        findViewById(R.id.csg6).setBackgroundColor(Color.GRAY);

        Consts.seriesx1_acc.setColor(Color.GREEN);
        Consts.seriesy1_acc.setColor(Color.GREEN);
        Consts.seriesz1_acc.setColor(Color.GREEN);
        findViewById(R.id.csa1).setBackgroundColor(Color.GREEN);
        Consts.seriesx2_acc.setColor(Color.RED);
        Consts.seriesy2_acc.setColor(Color.RED);
        Consts.seriesz2_acc.setColor(Color.RED);
        findViewById(R.id.csa2).setBackgroundColor(Color.RED);
        Consts.seriesx3_acc.setColor(Color.BLUE);
        Consts.seriesy3_acc.setColor(Color.BLUE);
        Consts.seriesz3_acc.setColor(Color.BLUE);
        findViewById(R.id.csa3).setBackgroundColor(Color.BLUE);
        Consts.seriesx4_acc.setColor(Color.YELLOW);
        Consts.seriesy4_acc.setColor(Color.YELLOW);
        Consts.seriesz4_acc.setColor(Color.YELLOW);
        findViewById(R.id.csa4).setBackgroundColor(Color.YELLOW);
        Consts.seriesx5_acc.setColor(Color.MAGENTA);
        Consts.seriesy5_acc.setColor(Color.MAGENTA);
        Consts.seriesz5_acc.setColor(Color.MAGENTA);
        findViewById(R.id.csa5).setBackgroundColor(Color.MAGENTA);
        Consts.seriesx6_acc.setColor(Color.GRAY);
        Consts.seriesy6_acc.setColor(Color.GRAY);
        Consts.seriesz6_acc.setColor(Color.GRAY);
        findViewById(R.id.csa6).setBackgroundColor(Color.GRAY);

// ------------- Gráfico GIRO--------------
        mGraphx = findViewById(R.id.graphx_giro);
        mGraphy = findViewById(R.id.graphy_giro);
        mGraphz = findViewById(R.id.graphz_giro);


        mGraphx.setTitle("Gyroscopes axis X");
        mGraphx.addSeries(Consts.seriesx1);
        mGraphx.addSeries(Consts.seriesx2);
        mGraphx.addSeries(Consts.seriesx3);
        mGraphx.addSeries(Consts.seriesx4);
        mGraphx.addSeries(Consts.seriesx5);
        mGraphx.addSeries(Consts.seriesx6);

        mGraphy.setTitle("Gyroscopes axis Y");
        mGraphy.addSeries(Consts.seriesy1);
        mGraphy.addSeries(Consts.seriesy2);
        mGraphy.addSeries(Consts.seriesy3);
        mGraphy.addSeries(Consts.seriesy4);
        mGraphy.addSeries(Consts.seriesy5);
        mGraphy.addSeries(Consts.seriesy6);

        mGraphz.setTitle("Gyroscopes axis Z");
        mGraphz.addSeries(Consts.seriesz1);
        mGraphz.addSeries(Consts.seriesz2);
        mGraphz.addSeries(Consts.seriesz3);
        mGraphz.addSeries(Consts.seriesz4);
        mGraphz.addSeries(Consts.seriesz5);
        mGraphz.addSeries(Consts.seriesz6);

        mGraphx.getViewport().setXAxisBoundsManual(true);
        mGraphx.getViewport().setYAxisBoundsManual(true);
        mGraphx.getViewport().setMinX(0);
        mGraphx.getViewport().setMaxX(Consts.max_x_points);
        mGraphx.getViewport().setMinY(-3.2);
        mGraphx.getViewport().setMaxY(3.2);

        mGraphy.getViewport().setXAxisBoundsManual(true);
        mGraphy.getViewport().setYAxisBoundsManual(true);
        mGraphy.getViewport().setMinX(0);
        mGraphy.getViewport().setMaxX(Consts.max_x_points);
        mGraphy.getViewport().setMinY(-3.2);
        mGraphy.getViewport().setMaxY(3.2);

        mGraphz.getViewport().setXAxisBoundsManual(true);
        mGraphz.getViewport().setYAxisBoundsManual(true);
        mGraphz.getViewport().setMinX(0);
        mGraphz.getViewport().setMaxX(Consts.max_x_points);
        mGraphz.getViewport().setMinY(-3.2);
        mGraphz.getViewport().setMaxY(3.2);


        // -------------Fim Gráfico Giro--------------

        // ------------- Gráfico ACC--------------


        mGraphx_acc = findViewById(R.id.graphx_acc);
        mGraphy_acc = findViewById(R.id.graphy_acc);
        mGraphz_acc = findViewById(R.id.graphz_acc);

        mGraphx_acc.setTitle("Accelerometers axis X");
        mGraphx_acc.addSeries(Consts.seriesx1_acc);
        mGraphx_acc.addSeries(Consts.seriesx2_acc);
        mGraphx_acc.addSeries(Consts.seriesx3_acc);
        mGraphx_acc.addSeries(Consts.seriesx4_acc);
        mGraphx_acc.addSeries(Consts.seriesx5_acc);
        mGraphx_acc.addSeries(Consts.seriesx6_acc);

        mGraphy_acc.setTitle("Accelerometers axis Y");
        mGraphy_acc.addSeries(Consts.seriesy1_acc);
        mGraphy_acc.addSeries(Consts.seriesy2_acc);
        mGraphy_acc.addSeries(Consts.seriesy3_acc);
        mGraphy_acc.addSeries(Consts.seriesy4_acc);
        mGraphy_acc.addSeries(Consts.seriesy5_acc);
        mGraphy_acc.addSeries(Consts.seriesy6_acc);

        mGraphz_acc.setTitle("Accelerometers axis Z");
        mGraphz_acc.addSeries(Consts.seriesz1_acc);
        mGraphz_acc.addSeries(Consts.seriesz2_acc);
        mGraphz_acc.addSeries(Consts.seriesz3_acc);
        mGraphz_acc.addSeries(Consts.seriesz4_acc);
        mGraphz_acc.addSeries(Consts.seriesz5_acc);
        mGraphz_acc.addSeries(Consts.seriesz6_acc);


        mGraphx_acc.getViewport().setXAxisBoundsManual(true);
        mGraphx_acc.getViewport().setYAxisBoundsManual(true);
        mGraphx_acc.getViewport().setMinX(0);
        mGraphx_acc.getViewport().setMaxX(Consts.max_x_points);
        mGraphx_acc.getViewport().setMinY(-10);
        mGraphx_acc.getViewport().setMaxY(10);

        mGraphy_acc.getViewport().setXAxisBoundsManual(true);
        mGraphy_acc.getViewport().setYAxisBoundsManual(true);
        mGraphy_acc.getViewport().setMinX(0);
        mGraphy_acc.getViewport().setMaxX(Consts.max_x_points);
        mGraphy_acc.getViewport().setMinY(-10);
        mGraphy_acc.getViewport().setMaxY(10);

        mGraphz_acc.getViewport().setXAxisBoundsManual(true);
        mGraphz_acc.getViewport().setYAxisBoundsManual(true);
        mGraphz_acc.getViewport().setMinX(0);
        mGraphz_acc.getViewport().setMaxX(Consts.max_x_points);
        mGraphz_acc.getViewport().setMinY(-10);
        mGraphz_acc.getViewport().setMaxY(10);
        // -------------Fim Gráfico Giro--------------


        // -------------Boxes----------


        cbg1 = findViewById(R.id.checkBox_sg1);
        cbg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg1.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx1)) {
                        mGraphx.addSeries(Consts.seriesx1);
                        mGraphy.addSeries(Consts.seriesy1);
                        mGraphz.addSeries(Consts.seriesz1);
                    }

                } else {
                    mGraphx.removeSeries(Consts.seriesx1);
                    mGraphy.removeSeries(Consts.seriesy1);
                    mGraphz.removeSeries(Consts.seriesz1);
                }
            }
        });

        cbg2 = findViewById(R.id.checkBox_sg2);
        cbg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg2.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx2)) {
                        mGraphx.addSeries(Consts.seriesx2);
                        mGraphy.addSeries(Consts.seriesy2);
                        mGraphz.addSeries(Consts.seriesz2);
                    }

                } else {
                    mGraphx.removeSeries(Consts.seriesx2);
                    mGraphy.removeSeries(Consts.seriesy2);
                    mGraphz.removeSeries(Consts.seriesz2);
                }
            }
        });

        cbg3 = findViewById(R.id.checkBox_sg3);
        cbg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg3.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx3)) {
                        mGraphx.addSeries(Consts.seriesx3);
                        mGraphy.addSeries(Consts.seriesy3);
                        mGraphz.addSeries(Consts.seriesz3);
                    }

                } else {
                    mGraphx.removeSeries(Consts.seriesx3);
                    mGraphy.removeSeries(Consts.seriesy3);
                    mGraphz.removeSeries(Consts.seriesz3);
                }
            }
        });

        cbg4 = findViewById(R.id.checkBox_sg4);
        cbg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg4.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx4)) {
                        mGraphx.addSeries(Consts.seriesx4);
                        mGraphy.addSeries(Consts.seriesy4);
                        mGraphz.addSeries(Consts.seriesz4);
                    }
                } else {
                    mGraphx.removeSeries(Consts.seriesx4);
                    mGraphy.removeSeries(Consts.seriesy4);
                    mGraphz.removeSeries(Consts.seriesz4);
                }
            }
        });

        cbg5 = findViewById(R.id.checkBox_sg5);
        cbg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg5.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx5)) {
                        mGraphx.addSeries(Consts.seriesx5);
                        mGraphy.addSeries(Consts.seriesy5);
                        mGraphz.addSeries(Consts.seriesz5);
                    }
                } else {
                    mGraphx.removeSeries(Consts.seriesx5);
                    mGraphy.removeSeries(Consts.seriesy5);
                    mGraphz.removeSeries(Consts.seriesz5);
                }
            }
        });

        cbg6 = findViewById(R.id.checkBox_sg6);
        cbg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbg6.isChecked()) {
                    if (!mGraphx.getSeries().contains(Consts.seriesx6)) {

                        mGraphx.addSeries(Consts.seriesx6);
                        mGraphy.addSeries(Consts.seriesy6);
                        mGraphz.addSeries(Consts.seriesz6);
                    }
                } else {
                    mGraphx.removeSeries(Consts.seriesx6);
                    mGraphy.removeSeries(Consts.seriesy6);
                    mGraphz.removeSeries(Consts.seriesz6);
                }
            }
        });


        cba1 = findViewById(R.id.checkBox_sa1);
        cba1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba1.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx1_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx1_acc);
                        mGraphy_acc.addSeries(Consts.seriesy1_acc);
                        mGraphz_acc.addSeries(Consts.seriesz1_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx1_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy1_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz1_acc);
                }
            }
        });

        cba2 = findViewById(R.id.checkBox_sa2);
        cba2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba2.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx2_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx2_acc);
                        mGraphy_acc.addSeries(Consts.seriesy2_acc);
                        mGraphz_acc.addSeries(Consts.seriesz2_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx2_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy2_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz2_acc);
                }
            }
        });

        cba3 = findViewById(R.id.checkBox_sa3);
        cba3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba3.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx3_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx3_acc);
                        mGraphy_acc.addSeries(Consts.seriesy3_acc);
                        mGraphz_acc.addSeries(Consts.seriesz3_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx3_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy3_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz3_acc);
                }
            }
        });

        cba4 = findViewById(R.id.checkBox_sa4);

        cba4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba4.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx4_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx4_acc);
                        mGraphy_acc.addSeries(Consts.seriesy4_acc);
                        mGraphz_acc.addSeries(Consts.seriesz4_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx4_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy4_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz4_acc);
                }
            }
        });

        cba5 = findViewById(R.id.checkBox_sa5);
        cba5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba5.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx5_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx5_acc);
                        mGraphy_acc.addSeries(Consts.seriesy5_acc);
                        mGraphz_acc.addSeries(Consts.seriesz5_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx5_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy5_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz5_acc);
                }
            }
        });

        cba6 = findViewById(R.id.checkBox_sa6);
        cba6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cba6.isChecked()) {
                    if (!mGraphx_acc.getSeries().contains(Consts.seriesx6_acc)) {
                        mGraphx_acc.addSeries(Consts.seriesx6_acc);
                        mGraphy_acc.addSeries(Consts.seriesy6_acc);
                        mGraphz_acc.addSeries(Consts.seriesz6_acc);
                    }
                } else {
                    mGraphx_acc.removeSeries(Consts.seriesx6_acc);
                    mGraphy_acc.removeSeries(Consts.seriesy6_acc);
                    mGraphz_acc.removeSeries(Consts.seriesz6_acc);
                }
            }
        });


        final LinearLayout llg = findViewById(R.id.graphs_containerG);
        final LinearLayout lla = findViewById(R.id.graphs_containerA);

        Button btn_acc = findViewById(R.id.btn_acc);
        btn_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llg.setVisibility(View.GONE);
                lla.setVisibility(View.VISIBLE);
            }
        });
        Button btn_gyro = findViewById(R.id.btn_giros);
        btn_gyro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lla.setVisibility(View.GONE);
                llg.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}