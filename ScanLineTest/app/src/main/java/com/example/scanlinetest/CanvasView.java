package com.example.scanlinetest;

import java.util.ArrayList;
import java.util.Stack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CanvasView extends View {

	/******************************************************/
	/**
	 * 画图
	 */
	// 多边形相关
	protected boolean firstDown = true;// 是否是第一次落下
	protected Path lastPath;// 上次折线路径
	protected final float MAX_CIRCLE = 50;// 圆域临界
	protected PointF beginPoint;
	Bitmap savedBitmap;
	Canvas savedCanvas;
	/******************************************************/
	/**
	 * 活动边表算法
	 */
	static Path path;
	static Paint drawPaint;
	boolean firstFinished=false;
	ArrayList<PointF> polygon;
	ArrayList<PointF> in;
	/******************************************************/
	/**
	 * 扫描线算法
	 */
	int fillColor = Color.RED, oldColor = Color.TRANSPARENT,edgeColor=Color.BLACK,curColor;
	private Stack<Point> pointStack;// 源粒子栈
	protected Point downPoint;
	int width, height;
	private Paint scanPaint;
	private int[] pixels;
	/******************************************************/

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 初始化画笔
		drawPaint = new Paint(Paint.DITHER_FLAG);
		drawPaint.setColor(Color.BLACK);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeWidth(2);
		drawPaint.setAntiAlias(true);
		drawPaint.setDither(true);

		path = new Path();
		lastPath = new Path();
		beginPoint = new PointF();
		polygon = new ArrayList<PointF>();
		pointStack = new Stack<Point>();// 像素堆栈
		downPoint = new Point();

		scanPaint = new Paint();
		scanPaint.setStrokeWidth(1);
		scanPaint.setColor(Color.RED);
	}

	// 触摸事件
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 第一只手指按下
		{
			switch (MainActivity.curAlg) {
			case -1:// 画图
			{
				if (firstDown == true)// 画折线的第一笔
				{
					PointF downPoint = new PointF((int) event.getX(),
							(int) event.getY());
					if(firstFinished == false) polygon.add(downPoint);// 记多边形顶点
					else 
					{
						in=new ArrayList<PointF>();
						in.add(downPoint);
					}

					beginPoint.set(new PointF(event.getX(), event.getY()));
					path.moveTo(beginPoint.x, beginPoint.y);
					lastPath.set(path);

					firstDown = false;
				}
			}
				break;
			case 0:// 种子
			{
				savedBitmap = MainActivity.savedBitmap;
				savedCanvas = MainActivity.savedCanvas;
				seedsFill((int) event.getX(), (int) event.getY());
			}
				break;
			case 1:// 扫描线
			{
				savedBitmap = MainActivity.savedBitmap;
				savedCanvas = MainActivity.savedCanvas;
				downPoint.set((int) event.getX(), (int) event.getY());
				scanFill();
			}
				break;
			}
			invalidate();
		}
			break;
		case MotionEvent.ACTION_MOVE: {
			if (MainActivity.curAlg == MainActivity.NONE) {
				path.set(lastPath);
				path.lineTo(event.getX(), event.getY());
				invalidate();
			}
		}
			break;
		case MotionEvent.ACTION_UP: {
			if (MainActivity.curAlg == MainActivity.NONE) {
				PointF upPoint = new PointF((int) event.getX(),
						(int) event.getY());
				if (distance(beginPoint, upPoint) <= MAX_CIRCLE) {
					path.set(lastPath);
					path.close();

					firstDown = true;

					// 拷贝顶点到Main
					if(firstFinished == false)
					{
						firstFinished=true;
						MainActivity.polygon = new Point[polygon.size()];
						for (int i = 0; i < polygon.size(); i++) {
							MainActivity.polygon[i] = new Point(
									(int) polygon.get(i).x, (int) polygon.get(i).y);
						}
					}
					else
					{
						Point[] p=new Point[in.size()];						
						for (int i = 0; i < in.size(); i++) 
						{
							p[i] = new Point((int) in.get(i).x, (int) in.get(i).y);
						}
						
						(MainActivity.inside).add(p);
					}

					calSquare();

					// 把位图绘好，方便之后填充，然后直接绘bitmap
					(MainActivity.savedCanvas).drawPath(path, drawPaint);
				} else {
					
					if(firstFinished ==false)
					{
						// 记多边形顶点
						polygon.add(upPoint);
					}
					else
					{
						in.add(upPoint);
					}
				}
				lastPath.set(path);
				invalidate();
			}
		}
			break;
		}

		return true;
	}

	protected void onDraw(Canvas canvas) {
		if (MainActivity.curAlg == MainActivity.NONE) // 画图
		{
			canvas.drawPath(path, drawPaint);
		} else {
			canvas.drawBitmap(MainActivity.savedBitmap, 0, 0, new Paint());
		}

	}

	// 计算up与最先down下的距离是否在领域内
	public float distance(PointF begin, PointF end) {
		float x = begin.x - end.x;
		float y = begin.y - end.y;
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算外接矩形面积
	public void calSquare() {
		int n = MainActivity.polygon.length;
		int xmin,xmax,ymin,ymax;
		
		xmin=xmax=MainActivity.polygon[0].x;
		ymin=ymax=MainActivity.polygon[0].y;
		for (int i = 1; i < n; i++) {
			if (MainActivity.polygon[i].x > xmax) {
				xmax = MainActivity.polygon[i].x;
			}
			if (MainActivity.polygon[i].x < xmin) {
				xmin = MainActivity.polygon[i].x;
			}
			if (MainActivity.polygon[i].y > ymax) {
				ymax = MainActivity.polygon[i].y;
			}
			if (MainActivity.polygon[i].y < ymin) {
				ymin = MainActivity.polygon[i].y;
			}
		}
		
		float width=(float)(((xmax-xmin)*1.0/480)*10.16);
		float height=(float)(((ymax-ymin)*1.0/854)*18.08);
		float square=width*height;

		// 显示
		MainActivity.squareTxt.setText(Float.toString(square));
	}
	


	/***************************************************************************/
	/**
	 * 扫描线算法
	 * 
	 * @author chicken
	 * 
	 */
	public void scanFill() {
		/**
		 * 处理耗时操作
		 */
		// 算法初始化
		pointStack.clear();// 清空源粒子栈
		pointStack.push(downPoint);// 入栈

		width = savedBitmap.getWidth();
		height = savedBitmap.getHeight();	
		pixels=new int[width*height];
		savedBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		
		MainActivity.enterTime = System.currentTimeMillis();
		/**************************************************/
		Point tmp;
		int x,y,XLeft,XRight,index;
		while (!pointStack.isEmpty()) {
			tmp = pointStack.pop();
			x = tmp.x;
			y = tmp.y;
			XLeft = XRight = x;
			while (x > 0 && (curColor=pixels[index=width*y+x]) == oldColor
					&& curColor != fillColor) {
				savedBitmap.setPixel(x, y, fillColor);
				pixels[index]=fillColor;
				x--;
			}
			XLeft=x+1;
			
			x = tmp.x + 1;
			while (x < width && (curColor=pixels[index=width*y+x]) == oldColor
					&& curColor != fillColor) {
				savedBitmap.setPixel(x, y, fillColor);
				pixels[index]=fillColor;
				x++;
			}
			XRight=x-1;
			
			if (y > 0) {
				findNewSeedInline(XLeft, XRight, y - 1, scanPaint);
			}
			if (y + 1 < height) {
				findNewSeedInline(XLeft, XRight, y + 1, scanPaint);
			}
		}
		/**************************************************/
		MainActivity.exitTime = System.currentTimeMillis();
		MainActivity.takedTime = MainActivity.exitTime - MainActivity.enterTime;
		(MainActivity.timeTxt).setText(Long.toString(MainActivity.takedTime));
	}

	public void findNewSeedInline(int XLeft, int XRight, int y, Paint paint) {
		Point p;
		Boolean pflag;
		int x = XLeft + 1;
		while (x <= XRight) {
			pflag = false;

			while ((curColor=pixels[width*y+x]) == oldColor && x < XRight
					&& curColor != fillColor) {

				if (pflag == false) {
					pflag = true;
				}
				x++;
			}
			if (pflag == true) {
				if ((x == XRight) && (curColor=pixels[width*y+x]) == oldColor
						&& curColor != fillColor) {
					p = new Point(x, y);
					pointStack.push(p);
				} else {
					p = new Point(x - 1, y);
					pointStack.push(p);
				}
				pflag = false;
			}

			// 处理向右跳过内部的无效点（处理区间右端有障碍点的情况）
			int xenter = x;
			while (pixels[width*y+x] != oldColor) {
				if (x >= XRight || x >= width) {
					break;
				}
				x++;
			}
			if (xenter == x) {
				x++;
			}
		}
	}
	/***************************************************************************/
	/**
	 * 种子递归算法
	 * 
	 * @author chicken
	 * 
	 */
	public void seedsFill(int x,int y) {
		
		Stack<Point> stack=new Stack<Point>();
		stack.push(new Point(x,y));
		Point cur;
		
		while(!stack.isEmpty())
		{
			cur=stack.pop();
			if(savedBitmap.getPixel(cur.x, cur.y) != edgeColor)
			{
				savedBitmap.setPixel(cur.x, cur.y, fillColor);
				
				stack.push(new Point(cur.x,cur.y-1));
				stack.push(new Point(cur.x,cur.y+1));
				stack.push(new Point(cur.x-1,cur.y));
				stack.push(new Point(cur.x+1,cur.y));
			}	
		}
		Log.v("v","lalalalalalal");
		
//		if(savedBitmap.getPixel(x, y) != edgeColor)
//		{
//			savedBitmap.setPixel(x, y, fillColor);
//			
////			seedsFill(x+1,y);
////			seedsFill(x-1,y);
////			seedsFill(x,y+1);
////			seedsFill(x,y-1);
//			
//		}
	}
	
	/***************************************************************************/

}
