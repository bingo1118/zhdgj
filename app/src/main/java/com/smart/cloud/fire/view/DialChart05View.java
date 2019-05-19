package com.smart.cloud.fire.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.DialChart;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotAttrInfo;
import org.xclcharts.view.GraphicalView;

import java.util.ArrayList;
import java.util.List;

public class DialChart05View extends GraphicalView {
	
	private String TAG = "DialChart05View";	
	private DialChart chart = new DialChart();

	
	float mP1 = 0.0f;
	float mP2 =  0.0f;



	private int maxOutData=250;
	private float mPercentage = 0f;
	private String TITLE="无数据";
	private String danwei="";
	private String yuzhi="";

	public void setMaxOutData(int data){
		maxOutData=data;
		chart.clearAll();
		initView();
		invalidate();
	}

	public void setPercentage(int data){
		mPercentage=data;
		chart.clearAll();
		initView();
		invalidate();
	}

	public void setTitleData(String title,String danwei,String yuzhi){
		TITLE=title;
		this.danwei=danwei;
		this.yuzhi=yuzhi;
		chart.clearAll();
		initView();
		invalidate();
	}

	public void setAllData(int max,int data,String title,String danwei,String yuzhi){
		maxOutData=max;
		mPercentage=data;
		TITLE=title;
		this.danwei=danwei;
		this.yuzhi=yuzhi;
		chart.clearAll();
		initView();
		invalidate();
	}



	public DialChart05View(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}
	
	public DialChart05View(Context context, AttributeSet attrs){   
        super(context, attrs);   
        initView();
	 }
	 
	 public DialChart05View(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
		chartRender();
	 }
	 
	 @Override  
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
	        super.onSizeChanged(w, h, oldw, oldh);  
	        chart.setChartRange(w ,h ); 
	    }  		
			
	 public void chartRender()
		{
			try {								
							
				//设置标题背景			
//				chart.setApplyBackgroundColor(true);
//				chart.setBackgroundColor( Color.rgb(28, 129, 243) );
				//绘制边框
//				chart.showRoundBorder();

				chart.setPadding(DensityUtil.dip2px(getContext(), 0),DensityUtil.dip2px(getContext(), 0),DensityUtil.dip2px(getContext(), 0),DensityUtil.dip2px(getContext(), 0));
						
				//设置当前百分比
				chart.getPointer().setPercentage(mPercentage/maxOutData);
				
				//设置指针长度
				chart.getPointer().setLength(0.55f);
				
				//增加轴
				addAxis();						
				/////////////////////////////////////////////////////////////
				addPointer();
				//设置附加信息
				addAttrInfo();
				/////////////////////////////////////////////////////////////
												
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.toString());
			}
			
		}
		
		public void addAxis()
		{
		
			List<String> rlabels  = new ArrayList<String>();
//			int j=0;
//			for(int i=0;i<=maxOutData;)
//			{
//				if(0 == i || j == 4)
//				{
//					rlabels.add(Integer.toString(i));
//					j = 0;
//				}else{
//					j++;
//				}
//
//				i+=5;
//			}
			chart.addOuterTicksAxis(0.7f, rlabels);
			
			//环形颜色轴
			List<Float> ringPercentage = new ArrayList<Float>();				
			ringPercentage.add( 0.33f);
			ringPercentage.add( 0.33f);
			ringPercentage.add( 1 - 2 * 0.33f);
			
			List<Integer> rcolor  = new ArrayList<Integer>();	//外圈颜色
			rcolor.add(Color.rgb(133, 206, 130));
			rcolor.add(Color.rgb(252, 210, 9));		
			rcolor.add(Color.rgb(229, 63, 56));	
			chart.addStrokeRingAxis(0.7f,0.65f, ringPercentage, rcolor);
									
			List<String> rlabels2  = new ArrayList<String>();
			for(int i=0;i<=maxOutData;i+=50)
			{
				rlabels2.add(Integer.toString(i));
			}
			chart.addInnerTicksAxis(0.65f, rlabels2);
								
			chart.getPlotAxis().get(1).getFillAxisPaint().setColor(Color.WHITE);//表盘内部颜色
			
			chart.getPlotAxis().get(0).hideAxisLine();
			chart.getPlotAxis().get(2).hideAxisLine();
			chart.getPlotAxis().get(0).getTickMarksPaint().setColor(Color.RED);
			chart.getPlotAxis().get(2).getTickMarksPaint().setColor(Color.GRAY);//刻度线颜色
			chart.getPlotAxis().get(2).getTickLabelPaint().setColor(Color.GRAY);//刻度文字颜色
		
			
		}
		
		
		private void addAttrInfo()
		{
			/////////////////////////////////////////////////////////////
			PlotAttrInfo plotAttrInfo = chart.getPlotAttrInfo();
			//设置附加信息
			Paint paintTB = new Paint();
			paintTB.setColor(Color.GRAY);
			paintTB.setTextAlign(Paint.Align.CENTER);
			paintTB.setTextSize(22);
			paintTB.setAntiAlias(true);
			plotAttrInfo.addAttributeInfo(XEnum.Location.TOP, TITLE, 0.3f, paintTB);

			Paint paintBT = new Paint();
			paintBT.setColor(Color.GRAY);
			paintBT.setTextAlign(Paint.Align.CENTER);
			paintBT.setTextSize(30);
			paintBT.setFakeBoldText(true);
			paintBT.setAntiAlias(true);
			plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM,
					Float.toString(mPercentage), 0.3f, paintBT);

			Paint paintBT2 = new Paint();
			paintBT2.setColor(Color.GRAY);
			paintBT2.setTextAlign(Paint.Align.CENTER);
			paintBT2.setTextSize(18);
			paintBT2.setAntiAlias(true);
			plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM, yuzhi, 0.4f, paintBT2);

			paintBT2.setColor(Color.GRAY);
			paintBT2.setTextAlign(Paint.Align.CENTER);
			paintBT2.setTextSize(12);
			paintBT2.setAntiAlias(true);
			plotAttrInfo.addAttributeInfo(XEnum.Location.BOTTOM, danwei, 0.5f, paintBT2);



		}
		
		public void addPointer()
		{
			chart.setTitle("123");
			chart.setPlotPanMode(XEnum.PanMode.FREE);
		}


		@Override
		public void render(Canvas canvas) {
			// TODO Auto-generated method stub
			 try{
		            chart.render(canvas);
		        } catch (Exception e){
		        	Log.e(TAG, e.toString());
		        }
		}

}
