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
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.smart.cloud.fire.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * @ClassName LineChart01View
 * @Description  折线图的例子
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class LineChart01View extends DemoView {
	
	private String TAG = "LineChart01View";
	private String TITLE="Title";
	private LineChart chart = new LineChart();
	
	//标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<LineData> chartData = new LinkedList<LineData>();

	private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	
	public LineChart01View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public LineChart01View(Context context, AttributeSet attrs){   
        super(context, attrs);   
        initView();
	 }
	 
	 public LineChart01View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
//		 	chartLabels();
//			chartDataSet();
//			chartRender();
			
			//綁定手势滑动事件
//			this.bindTouch(this,chart);
	 }

	public void initView(String title,LinkedList<String> l,LinkedList<Double> c)
	{
		TITLE=title;
//		chart=new LineChart();
		labels = new LinkedList<String>();
		chartData = new LinkedList<LineData>();
		chartLabels(l);
		chartDataSet(c);
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
			
			//限制Tickmarks可滑动偏移范围
			chart.setXTickMarksOffsetMargin(ltrb[2] - 0.f);
			chart.setYTickMarksOffsetMargin(ltrb[3] - 0.f);
			
			
			//显示边框
			chart.showRoundBorder();

			chart.disableScale();//禁止缩放
			
			
			//设定数据源
			chart.setCategories(labels);								
			chart.setDataSource(chartData);
			
			//数据轴最大值
//			chart.getDataAxis().setAxisMax(100);

			if(getTheMaxData()>100){
				BigDecimal deSource = new BigDecimal(getTheMaxData());
				double d=deSource.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
				chart.getDataAxis().setAxisMax(deSource.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue()+50);
			}else{
				chart.getDataAxis().setAxisMax(100);
			}

			//数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(20);

			if(getTheMinData()>20){
				chart.getDataAxis().setAxisMin(getTheMinData()-20);
			}else{
				chart.getDataAxis().setAxisMin(0);
			}
			
			//背景网格
//			chart.getPlotGrid().showHorizontalLines();
//			chart.getPlotGrid().showVerticalLines();
			chart.getPlotGrid().showEvenRowBgColor();
			chart.getPlotGrid().showOddRowBgColor();
			
			chart.getPlotGrid().getHorizontalLinePaint().setStrokeWidth(1);
			chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DOT);
			chart.getPlotGrid().setVerticalLineStyle(XEnum.LineStyle.DOT);
			
			chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.RED);
			chart.getPlotGrid().getVerticalLinePaint().setColor(Color.BLUE);
			
			chart.setTitle(TITLE);//标题
//			chart.addSubtitle("(XCL-Charts Demo)");//副标题
			
			chart.getAxisTitle().setLowerTitle("(时间)");

			float margin = DensityUtil.dip2px(getContext(), 10);
			chart.setXTickMarksOffsetMargin(margin);//控制滑动出绘图区域后停止滑动

			chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.EVEN_ODD);//坐标标注模式
			//定义数据轴标签显示格式
//			chart.getCategoryAxis().setLabelFormatter(new IFormatterTextCallBack(){
//
//				@Override
//				public String textFormatter(String value) {
//					// TODO Auto-generated method stub
//					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					ParsePosition pos = new ParsePosition(0);
//					Date strtodate = formatter.parse(value, pos);
//					return (strtodate.getHours()+":"+strtodate.getMinutes());
//				}
//
//			});

			//激活点击监听
			chart.ActiveListenItemClick();
			//为了让触发更灵敏，可以扩大5px的点击监听范围
			chart.extPointClickRange(10);
			chart.showClikedFocus();
												
			//绘制十字交叉线
//			chart.showDyLine();
//			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);

			chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);//设置滑动方向


			//想隐藏轴的可以下面的函数来隐藏
//			chart.getDataAxis().hide();
//			chart.getCategoryAxis().hide();
			//想设置刻度线属性的可用下面函数
			chart.getDataAxis().getTickMarksPaint();
			chart.getCategoryAxis().getTickMarksPaint();
			chart.getCategoryAxis().setTickLabelMargin(10);

			
			chart.getPlotArea().extWidth(100.f);
			
			//调整轴显示位置
			chart.setDataAxisLocation(XEnum.AxisLocation.LEFT);
			chart.setCategoryAxisLocation(XEnum.AxisLocation.BOTTOM);
			
			//收缩绘图区右边分割的范围，让绘图区的线不显示出来
			chart.getClipExt().setExtRight(0.f);
			
			
			//test x坐标从刻度线而不是轴开始
			//chart.setXCoordFirstTickmarksBegin(true);
			//chart.getCategoryAxis().showTickMarks();
			//chart.getCategoryAxis().setVerticalTickPosition(XEnum.VerticalAlign.MIDDLE);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
		}
	}
	private void chartDataSet(LinkedList<Double> list)
	{
		LineData lineData1 = new LineData("圆点",list,Color.rgb(234, 83, 71));
		lineData1.setLabelVisible(true);
		lineData1.setDotStyle(XEnum.DotStyle.DOT);
		lineData1.getDotLabelPaint().setColor(Color.BLUE);
		lineData1.getDotLabelPaint().setTextSize(22);
		lineData1.getDotLabelPaint().setTextAlign(Align.LEFT);
//		lineData1.setDotRadius(20);
		lineData1.setLabelVisible(true);
		lineData1.getDotLabelPaint().setTextAlign(Align.CENTER);
		
		lineData1.getLabelOptions().setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
		
		//lineData1.getLabelOptions().
		
		//lineData1.setDataSet(dataSeries);
		this.invalidate();//刷新视图

		chartData.add(lineData1);
	}
	
	private void chartLabels(LinkedList<String> list)
	{
		labels=list;
	}
	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
				
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{			
			triggerClick(event.getX(),event.getY());
		}
		super.onTouchEvent(event);
		return true;
	}
	
	
	//触发监听
	private void triggerClick(float x,float y)
	{		
		
		//交叉线
		if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);		
		if(!chart.getListenItemClickStatus())
		{
			//交叉线
			if(chart.getDyLineVisible())this.invalidate();
		}else{			
			PointPosition record = chart.getPositionRecord(x,y);			
			if( null == record)
			{
				if(chart.getDyLineVisible())this.invalidate();
				return;
			}
	
			LineData lData = chartData.get(record.getDataID());
			Double lValue = lData.getLinePoint().get(record.getDataChildID());
		
			float r = record.getRadius();
			chart.showFocusPointF(record.getPosition(),r + r*0.5f);		
			chart.getFocusPaint().setStyle(Style.STROKE);
			chart.getFocusPaint().setStrokeWidth(3);		
			if(record.getDataID() >= 3)
			{
				chart.getFocusPaint().setColor(Color.BLUE);
			}else{
				chart.getFocusPaint().setColor(Color.RED);
			}		
			
			//在点击处显示tooltip
			mPaintTooltips.setColor(Color.RED);				
			//chart.getToolTip().setCurrentXY(x,y);
			chart.getToolTip().setCurrentXY(record.getPosition().x,record.getPosition().y); 
			
			chart.getToolTip().addToolTip(" Key:"+lData.getLineKey(),mPaintTooltips);
			chart.getToolTip().addToolTip(" Label:"+lData.getLabel(),mPaintTooltips);		
			chart.getToolTip().addToolTip(" Current Value:" +Double.toString(lValue),mPaintTooltips);
				
			
			//当前标签对应的其它点的值
			int cid = record.getDataChildID();
			String xLabels = "";			
			for(LineData data : chartData)
			{
				if(cid < data.getLinePoint().size())
				{
					xLabels = Double.toString(data.getLinePoint().get(cid));					
					chart.getToolTip().addToolTip("Line:"+data.getLabel()+","+ xLabels,mPaintTooltips);					
				}
			}
			
			
			this.invalidate();
		}
		
		
	}
	
	private double getTheMaxData(){
		double d=0;
		for(LineData data : chartData)
		{
			for (int i=0;i < data.getLinePoint().size();i++)
			{
				double temp = data.getLinePoint().get(i);
				if(temp>d){
					d=temp;
				}
			}
		}
		return d;
	}

	private double getTheMinData(){
		double d=1000;
		for(LineData data : chartData)
		{
			for (int i=0;i < data.getLinePoint().size();i++)
			{
				double temp = data.getLinePoint().get(i);
				if(temp<d){
					d=temp;
				}
			}
		}
		return d;
	}
}
