/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.smart.cloud.fire.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName BarChart03View
 * @Description 用于展示定制线与明细刻度线
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * MODIFIED    YYYY-MM-DD   REASON
 */
public class BarChart03View extends DemoView{
	
	private String TAG = "BarChart03View";
	private String chart_Title;
	private BarChart chart = new BarChart();
	//轴数据源
	private List<String> chartLabels = new LinkedList<String>();
	private List<BarData> chartData = new LinkedList<BarData>();
	
	public BarChart03View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
//		initView();
		
	}
	
	public BarChart03View(Context context, AttributeSet attrs){   
        super(context, attrs);   
//        initView();
	 }
	 
	public BarChart03View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
//			initView();
	}
	 
	public void initView(List<String> labels, List<Double> values, String title) {

		this.chartLabels=labels;
		this.chart_Title=title;
		chartDataSet(values);
		chartRender();

		//綁定手势滑动事件
		this.bindTouch(this,chart);

	 }


	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
       //图所占范围大小
        chart.setChartRange(w,h);
    }


	private void chartRender()
	{
		try {

			//设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int [] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

			//显示边框
			chart.showRoundBorder();


			//标题
			chart.setTitle(chart_Title);
			//数据源
			chart.setDataSource(chartData);
			chart.setCategories(chartLabels);

			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);//设置滑动方向

			//图例
			chart.getAxisTitle().setTitleStyle(XEnum.AxisTitleStyle.NORMAL);
			chart.getAxisTitle().setLeftTitle("条数");
			chart.getAxisTitle().setCrossPointTitle("(日期)");

			chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.EVEN_ODD);//坐标标注模式

			//指隔多少个轴刻度(即细刻度)后为主刻度
			chart.getDataAxis().setDetailModeSteps(5);

			//数据轴
			if(getTheMaxData()>100){
				chart.getDataAxis().setAxisMax(getTheMaxData()+20);
			}else{
				chart.getDataAxis().setAxisMax(100);
			}

			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);


			//背景网格
			chart.getPlotGrid().showHorizontalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();

			//定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub
					Double tmp = Double.parseDouble(value);
					DecimalFormat df=new DecimalFormat("#0");
					String label = df.format(tmp).toString();
					return (label);
				}
			});

			//在柱形顶部显示值
			chart.getBar().setItemLabelVisible(true);
			chart.getBar().setBarStyle(XEnum.BarStyle.FILL);


//			chart.disablePanMode();//滑动禁止？

			//设定格式
			chart.setItemLabelFormatter(new IFormatterDoubleCallBack() {
				@Override
				public String doubleFormatter(Double value) {
					// TODO Auto-generated method stub
					DecimalFormat df=new DecimalFormat("#0");
					String label = df.format(value).toString();
					return label;
				}});

			//隐藏Key
			chart.getPlotLegend().hide();

			chart.getClipExt().setExtTop(150.f);

			//柱形和标签居中方式
			chart.setBarCenterStyle(XEnum.BarCenterStyle.TICKMARKS);
			chart.getCategoryAxis().setTickLabelMargin(10);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void chartDataSet(List<Double> values)
	{
		//标签对应的柱形数据集
		List<Double> dataSeriesA= new LinkedList<Double>();
		//依数据值确定对应的柱形颜色.
		List<Integer> dataColorA= new LinkedList<Integer>();
		for(int i=0;i<values.size();i++){
			dataSeriesA.add(values.get(i));
			dataColorA.add(Color.BLUE);
		}
		chartData.clear();
		chartData.add(new BarData("",dataSeriesA,dataColorA,Color.BLUE));
		this.invalidate();//刷新视图
	}

	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
			postInvalidate();
		} catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	
	private void chartAnimation()
	{
		  try {

          	for(int i=0;i< chartData.size() ;i++)
          	{           		       		          		          		          	
          		BarData barData = chartData.get(i);
          		for(int j=0;j<barData.getDataSet().size();j++)
                {     
          			Thread.sleep(100);
          			List<BarData> animationData = new LinkedList<BarData>();
          			List<Double> dataSeries= new LinkedList<Double>();	
          			List<Integer> dataColorA= new LinkedList<Integer>();
          			for(int k=0;k<=j;k++)
          			{          				
          				dataSeries.add(barData.getDataSet().get(k));  
          				dataColorA.add(barData.getDataColor().get(k));  
          			}
          			
          			BarData animationBarData = new BarData("",dataSeries,dataColorA,Color.rgb(53, 169, 239));
          			animationData.add(animationBarData);
          			chart.setDataSource(animationData);
          			postInvalidate(); 
                }          		          		   
          	}
          	 
          }
          catch(Exception e) {
              Thread.currentThread().interrupt();
          }            
	}

	private double getTheMaxData(){
		double d=0;
		List<Double> list=chartData.get(0).getDataSet();
		for (int i=0;i < list.size();i++)
		{
			double temp = list.get(i);
			if(temp>d){
				d=temp;
			}
		}

		return d;
	}
}
