package com.example.scanlinetest;

import java.util.ArrayList;
import java.util.Stack;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

public class MainActivity extends Activity {

	/******************************************************/
	/**
	 * 活动边表算法
	 */
	static Point[] polygon;
	static ArrayList<Point[]> inside=null;
	static TextView squareTxt;// 面积
	static TextView horizonTxt;// 面积
	static TextView verticalTxt;// 面积
	static TextView timeTxt;// 时间
	CanvasView canvasVi;
	int screenWidth;
	int screenHeight;
	static Bitmap savedBitmap;
	static Canvas savedCanvas = new Canvas();
	Paint polyPaint;

	static int NONE = -1;
	static int SEEDS = 0;
	static int SCAN = 1;
	static int POLYGON = 2;
	static int IPOLYGON = 3;
	static int CLEAN = 4;
	static int JUDGE = 5;
	static int curAlg = NONE;

	// 计时
	static long enterTime;// 进入算法时间
	static long exitTime;// 退出算法时间
	static long takedTime;// 算法耗时
	int num = 1;// 重复次数

	/******************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		squareTxt = (TextView) findViewById(R.id.squareText);
		horizonTxt = (TextView) findViewById(R.id.horizonText);
		verticalTxt = (TextView) findViewById(R.id.verticalText);
		timeTxt = (TextView) findViewById(R.id.timeText);
		canvasVi = (CanvasView) findViewById(R.id.canvasView1);

		WindowManager wm = this.getWindowManager();
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();

		savedBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
				Config.ARGB_8888);
		savedCanvas.setBitmap(savedBitmap);

		polyPaint = new Paint();
		polyPaint.setColor(Color.RED);
		polyPaint.setStrokeWidth(1);

		inside=new ArrayList<Point[]>();
	}

	// 传统活动边表填充(横扫)
	public void onHorizontalPolygonFillBtn(View v) {
		curAlg = POLYGON;
		calDxDy();
		
		enterTime = System.currentTimeMillis();
		/**************************************************/
		for (int n = 1; n <= num; n++) {
			NETY net = new NETY();
			net.getMinMax();
			net.create();

			AETY aet = new AETY();
			for (int i = 0; i < net.ymax - net.ymin + 1; i++) {
				// NET 转入 AET
				aet.insertNETY(net, i);
				aet.updateAETY(net, i);
				aet.Fill(net, i);
			}
		}
		/**************************************************/
		exitTime = System.currentTimeMillis();
		takedTime = exitTime - enterTime;
		timeTxt.setText(Double.toString(takedTime * 1.0 / num));

		canvasVi.invalidate();// 刷新画面
	}

	// 传统活动边表填充(竖扫)
	public void onVerticalPolygonFillBtn(View v) {
		curAlg = POLYGON;
		calDxDy();
		
		enterTime = System.currentTimeMillis();
		/**************************************************/
		for (int n = 1; n <= num; n++) {
			NETX net = new NETX();
			net.getMinMax();
			net.create();

			AETX aet = new AETX();
			for (int i = 0; i < net.xmax - net.xmin + 1; i++) {
				// NET 转入 AET
				aet.insertNETX(net, i);
				aet.updateAETX(net, i);
				aet.Fill(net, i);
			}
		}
		/**************************************************/
		exitTime = System.currentTimeMillis();
		takedTime = exitTime - enterTime;
		timeTxt.setText(Double.toString(takedTime * 1.0 / num));

		canvasVi.invalidate();// 刷新画面
	}

	// 改进活动边表填充
	public void onImprovedPolygonFillBtn(View v) {
		curAlg = IPOLYGON;
		calDxDy();
		
		enterTime = System.currentTimeMillis();
		/**************************************************/
		for (int n = 1; n <= num; n++) {

			// 寻找纵度和横度总值
			int Horizon = 0, Vertical = 0;
			int circle = polygon.length;
			int rear;
			for (int i = 0; i < circle; i++) {
				rear = (i + 1) % circle;
				Horizon += Math.abs(polygon[rear].x - polygon[i].x);
				Vertical += Math.abs(polygon[rear].y - polygon[i].y);
			}

			if (Vertical <= Horizon) // 矮扁型：横扫
			{
				NETY net = new NETY();
				net.getMinMax();
				net.create();

				AETY aet = new AETY();
				for (int i = 0; i < net.ymax - net.ymin + 1; i++) {
					// NET 转入 AET
					aet.insertNETY(net, i);
					aet.updateAETY(net, i);
					aet.Fill(net, i);
				}
			} else // 长瘦型：竖扫
			{
				NETX net = new NETX();
				net.getMinMax();
				net.create();

				AETX aet = new AETX();
				for (int i = 0; i < net.xmax - net.xmin + 1; i++) {
					// NET 转入 AET
					aet.insertNETX(net, i);
					aet.updateAETX(net, i);
					aet.Fill(net, i);
				}
			}
		}
		/**************************************************/
		exitTime = System.currentTimeMillis();
		takedTime = exitTime - enterTime;
		timeTxt.setText(Double.toString(takedTime * 1.0 / num));

		canvasVi.invalidate();// 刷新画面
	}
public void onBtn(View v)
{
	curAlg = 456;
	
	NETY net = new NETY();
	net.getMinMax();
	net.create();

	AETY aet = new AETY();
	for (int i = 0; i < net.ymax - net.ymin + 1; i++) {
		// NET 转入 AET
		aet.insertNETY(net, i);
		aet.updateAETY(net, i);
		aet.Fill(net, i);
	}

	canvasVi.invalidate();// 刷新画面
}
	// 种子递归填充
	public void onSeedsFillBtn(View v) {
		curAlg = SEEDS;
	}

	// 扫描线填充
	public void onScanlineFillBtn(View v) {
		curAlg = SCAN;
	}

	// 逐点填充
	public void onJudgeFillBtn(View v) {
		curAlg = JUDGE;
		judgeFill();
	}

	// 清空
	public void onCleanBtn(View v) {
		curAlg = CLEAN;
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		savedCanvas.drawPaint(paint);

		savedCanvas.drawPath(CanvasView.path, CanvasView.drawPaint);
		canvasVi.invalidate();
	}

	/***************************************************************************/
	/**
	 * 传统活动边表算法
	 * 
	 * @author chicken
	 * 
	 */
	// 边结点(AET\NET)
	class EdgeY {
		double xi;
		double dx;
		int ym;
		EdgeY next = null;

		EdgeY(double xi, double dx, int ym) {
			this.xi = xi;
			this.dx = dx;
			this.ym = ym;
		}
	}

	/**
	 * NET表
	 */
	class NETY {
		EdgeY[] bucket;// 桶头结点(扫描线条数)
		int ymin, ymax;

		void create() {
			createNETY();// 构建NET

		}

		void getMinMax() {
			ymin = polygon[0].y;
			ymax = polygon[0].y;
			for (int i = 1; i < polygon.length; i++) {
				if (polygon[i].y > ymax) {
					ymax = polygon[i].y;
				}
				if (polygon[i].y < ymin) {
					ymin = polygon[i].y;
				}
			}
		}

		void createNETY() {
			// 构造扫描线数量个桶结点
			bucket = new EdgeY[ymax - ymin + 1];

			int n = polygon.length;
			for (int y = ymin; y <= ymax; y++) {
				// 构造桶头结点并初始化
				bucket[y - ymin] = new EdgeY(0, 0, y);
				EdgeY tmp = bucket[y - ymin];// 连接用游标

				// 是否与顶点相交
				for (int cur = 0; cur < n; cur++) {
					// 扫描线穿越顶点
					if (y == polygon[cur].y) {

						int front = (cur - 1 + n) % n;// 前驱
						int rear = (cur + 1) % n;// 后继
						EdgeY firstEdge = null, secondEdge = null;
						double firstDx = -9999;
						double secondDx = -9999;

						// 前驱顶点在高处时加入
						if (polygon[front].y > y) {
							// 构造边结点并初始化
							firstDx = (polygon[cur].x - polygon[front].x) * 1.0
									/ (polygon[cur].y - polygon[front].y);
							firstEdge = new EdgeY(polygon[cur].x, firstDx,
									polygon[front].y);
							tmp.next = firstEdge;
							tmp = firstEdge;
						}

						// 后继顶点在高处时加入
						if (polygon[rear].y > y) {
							// 构造边结点并初始化
							secondDx = (polygon[cur].x - polygon[rear].x) * 1.0
									/ (polygon[cur].y - polygon[rear].y);
							secondEdge = new EdgeY(polygon[cur].x, secondDx,
									polygon[rear].y);
							tmp.next = secondEdge;
							tmp = secondEdge;
						}
					}
				}
				/**************************************/
				if(inside.size() != 0) //没有孔洞的情况
				{
					//内点入NET
					for(int i=0;i<inside.size();i++)
					{	
						Point[] in=inside.get(i);
			
						// 是否与顶点相交
						int m = in.length;
						for (int cur = 0; cur < m; cur++) {
							// 扫描线穿越顶点
							if (y == in[cur].y) {
	
								int front = (cur - 1 + m) % m;// 前驱
								int rear = (cur + 1) % m;// 后继
								EdgeY firstEdge = null, secondEdge = null;
								double firstDx = -9999;
								double secondDx = -9999;
	
								// 前驱顶点在高处时加入
								if (in[front].y > y) {
									// 构造边结点并初始化
									firstDx = (in[cur].x - in[front].x) * 1.0
											/ (in[cur].y - in[front].y);
									firstEdge = new EdgeY(in[cur].x, firstDx,
											in[front].y);
									tmp.next = firstEdge;
									tmp = firstEdge;
								}
	
								// 后继顶点在高处时加入
								if (in[rear].y > y) {
									// 构造边结点并初始化
									secondDx = (in[cur].x - in[rear].x) * 1.0
											/ (in[cur].y - in[rear].y);
									secondEdge = new EdgeY(in[cur].x, secondDx,
											in[rear].y);
									tmp.next = secondEdge;
									tmp = secondEdge;
								}
							}
						}
					}
				}
				/**************************************/
			}		
		}

		// 遍历NET
		void Traverse() {
			for (int i = 0; i < bucket.length; i++) {
				EdgeY tmp = bucket[i];
				System.out.println("扫描线：" + tmp.ym);
				while (tmp.next != null) {
					System.out.println(tmp.next.dx + "");
					tmp = tmp.next;
				}
			}
		}
	}

	/**
	 * AET表
	 */
	class AETY {
		EdgeY head;

		AETY() {
			head = new EdgeY(0, 0, 0);
			head.next = null;
		}

		// 将第line条扫描线的所有新边升序插入进活动边表
		void insertNETY(NETY net, int line) {
			EdgeY curNET = net.bucket[line].next;// 找到桶结点
			EdgeY rearNET;
			EdgeY curAET = head;// 找到头结点
			EdgeY rearAET;
			while (curNET != null)// NET表未完
			{
				rearNET = curNET.next; // 提前准备好后继结点，以备当前结点被改以后可以继续搜索NET
				while (curAET != null) { // AET表未完
					// 夹逼的话就将其插入
					if (curNET.xi >= curAET.xi
							&& (curAET.next == null || curNET.xi <= curAET.next.xi)) {
						rearAET = curAET.next;
						curAET.next = curNET;
						curNET.next = rearAET;

						curAET = head; // 指针还原
						break;
					} else {
						curAET = curAET.next;
					}
				}
				curNET = rearNET;
			}
		}

		// 删除到达ym的边并更新xi步进值
		void updateAETY(NETY net, int line) {

			// 删除旧边
			EdgeY prior = head;// 找到头结点
			EdgeY cur = head.next;
			while (cur != null) {
				if (net.bucket[line].ym == cur.ym) // 越过某边就删除该边
				{
					prior.next = cur.next;
					cur = prior.next;
				} else {
					if (prior.next == null)
						break;
					else {
						prior = prior.next;
						cur = prior.next;
					}
				}
			}

			// 由连贯性更新AET中边
			EdgeY tmp = head.next;// 找到头结点
			while (tmp != null) {
				tmp.xi += tmp.dx;
				tmp = tmp.next;
			}

			if (curAlg == POLYGON) // 传统活动边表法
			{
				// 冒泡重排序
				EdgeY begin = head; // 当前趟开始结点的前驱结点
				EdgeY walk; // 游走结点
				while (begin.next != null) {
					prior = begin;
					walk = begin.next;
					while (walk.next != null) {
						EdgeY rear = walk.next;
						if (walk.xi > rear.xi) {
							prior.next = rear;
							walk.next = rear.next;
							rear.next = walk;
						}

						prior = prior.next;
						walk = prior.next;
					}
					begin = begin.next;
				}
			} else if(curAlg == IPOLYGON)// 改进活动边表法（判断有没有连续两者的xi相同）
			{
				EdgeY front = head;
				EdgeY walk = head.next;// 找到头结点
				EdgeY behind;

				if (head.next != null) {
					while (walk.next != null) {
						behind = walk.next;
						if (walk.xi > behind.xi) {
							front.next = behind;
							walk.next = behind.next;
							behind.next = walk;
						}
						front = front.next;
						walk = front.next;
					}
				}
			}
		}

		// 两两交点填充
		void Fill(NETY net, int line) {
			// 由连贯性更新AET中边
			EdgeY first = head.next;// 找到头结点

			if (first != null) // 已经没有交点了就退出
			{
				EdgeY second = first.next;
				while (second != null) {
					savedCanvas.drawLine((float) first.xi,
							(float) net.bucket[line].ym, (float) second.xi,
							(float) net.bucket[line].ym, polyPaint);

					if (second.next == null)
						break;
					else {
						first = first.next.next;
						second = second.next.next;
					}
				}
			}
		}

		// 遍历AET
		void Traverse() {
			EdgeY tmp = head.next;// 找到头结点

			while (tmp != null) {
				System.out.println(tmp.xi);
				tmp = tmp.next;
			}
			System.out.println("完");
		}
	}

	/***************************************************************************/
	/***************************************************************************/
	// 扫描线为X

	// 边结点(AET\NET)
	class EdgeX {
		double yi;
		double dy;
		int xm;
		EdgeX next = null;

		EdgeX(double yi, double dy, int xm) {
			this.yi = yi;
			this.dy = dy;
			this.xm = xm;
		}
	}

	/**
	 * NET表
	 */
	class NETX {
		EdgeX[] bucket;// 桶头结点(扫描线条数)
		int xmin, xmax;

		void create() {
			createNETX();// 构建NET

		}

		void getMinMax() {
			xmin = polygon[0].x;
			xmax = polygon[0].x;
			for (int i = 1; i < polygon.length; i++) {
				if (polygon[i].x > xmax) {
					xmax = polygon[i].x;
				}
				if (polygon[i].x < xmin) {
					xmin = polygon[i].x;
				}
			}
		}

		void createNETX() {
			// 构造扫描线数量个桶结点
			bucket = new EdgeX[xmax - xmin + 1];

			int n = polygon.length;
			for (int x = xmin; x <= xmax; x++) {
				// 构造桶头结点并初始化
				bucket[x - xmin] = new EdgeX(0, 0, x);
				EdgeX tmp = bucket[x - xmin];// 连接用游标

				// 是否与顶点相交
				for (int cur = 0; cur < n; cur++) {
					// 扫描线穿越顶点
					if (x == polygon[cur].x) {

						int front = (cur - 1 + n) % n;// 前驱
						int rear = (cur + 1) % n;// 后继
						EdgeX firstEdge = null, secondEdge = null;
						double firstDy = -9999;
						double secondDy = -9999;

						// 前驱顶点在高处时加入
						if (polygon[front].x > x) {
							// 构造边结点并初始化
							firstDy = (polygon[cur].y - polygon[front].y) * 1.0
									/ (polygon[cur].x - polygon[front].x);
							firstEdge = new EdgeX(polygon[cur].y, firstDy,
									polygon[front].x);
							tmp.next = firstEdge;
							tmp = firstEdge;
						}

						// 后继顶点在高处时加入
						if (polygon[rear].x > x) {
							// 构造边结点并初始化
							secondDy = (polygon[cur].y - polygon[rear].y) * 1.0
									/ (polygon[cur].x - polygon[rear].x);
							secondEdge = new EdgeX(polygon[cur].y, secondDy,
									polygon[rear].x);
							tmp.next = secondEdge;
							tmp = secondEdge;
						}
					}
				}
				/**************************************/
				if(inside.size() != 0) //没有孔洞的情况
				{
					//内点入NET
					for(int i=0;i<inside.size();i++)
					{	
						Point[] in=inside.get(i);
			
						// 是否与顶点相交
						int m = in.length;
						for (int cur = 0; cur < m; cur++) {
							// 扫描线穿越顶点
							if (x == in[cur].x) {
	
								int front = (cur - 1 + m) % m;// 前驱
								int rear = (cur + 1) % m;// 后继
								EdgeX firstEdge = null, secondEdge = null;
								double firstDy = -9999;
								double secondDy = -9999;
	
								// 前驱顶点在高处时加入
								if (in[front].x > x) {
									// 构造边结点并初始化
									firstDy = (in[cur].y - in[front].y) * 1.0
											/ (in[cur].x - in[front].x);
									firstEdge = new EdgeX(in[cur].y, firstDy,
											in[front].x);
									tmp.next = firstEdge;
									tmp = firstEdge;
								}
	
								// 后继顶点在高处时加入
								if (in[rear].x > x) {
									// 构造边结点并初始化
									secondDy = (in[cur].y - in[rear].y) * 1.0
											/ (in[cur].x - in[rear].x);
									secondEdge = new EdgeX(in[cur].y, secondDy,
											in[rear].x);
									tmp.next = secondEdge;
									tmp = secondEdge;
								}
							}
						}
					}
				}
				/**************************************/
			}	
		}

		// 遍历NET
		void Traverse() {
			for (int i = 0; i < bucket.length; i++) {
				EdgeX tmp = bucket[i];
				System.out.println("扫描线：" + tmp.xm);
				while (tmp.next != null) {
					System.out.println(tmp.next.dy + "");
					tmp = tmp.next;
				}
			}
		}
	}

	/**
	 * AET表
	 */
	class AETX {
		EdgeX head;

		AETX() {
			head = new EdgeX(0, 0, 0);
			head.next = null;
		}

		// 将第line条扫描线的所有新边升序插入进活动边表
		void insertNETX(NETX net, int line) {
			EdgeX curNET = net.bucket[line].next;// 找到桶结点
			EdgeX rearNET;
			EdgeX curAET = head;// 找到头结点
			EdgeX rearAET;
			while (curNET != null)// NET表未完
			{
				rearNET = curNET.next; // 提前准备好后继结点，以备当前结点被改以后可以继续搜索NET
				while (curAET != null) { // AET表未完
					// 夹逼的话就将其插入
					if (curNET.yi >= curAET.yi
							&& (curAET.next == null || curNET.yi <= curAET.next.yi)) {
						rearAET = curAET.next;
						curAET.next = curNET;
						curNET.next = rearAET;

						curAET = head; // 指针还原
						break;
					} else {
						curAET = curAET.next;
					}
				}
				curNET = rearNET;
			}
		}

		// 删除到达ym的边并更新xi步进值
		void updateAETX(NETX net, int line) {

			// 删除旧边
			EdgeX prior = head;// 找到头结点
			EdgeX cur = head.next;
			while (cur != null) {
				if (net.bucket[line].xm == cur.xm) // 越过某边就删除该边
				{
					prior.next = cur.next;
					cur = prior.next;
				} else {
					if (prior.next == null)
						break;
					else {
						prior = prior.next;
						cur = prior.next;
					}
				}
			}

			// 由连贯性更新AET中边
			EdgeX tmp = head.next;// 找到头结点
			while (tmp != null) {
				tmp.yi += tmp.dy;
				tmp = tmp.next;
			}

			if (curAlg == POLYGON) // 传统活动边表法
			{
				// 冒泡重排序
				EdgeX begin = head; // 当前趟开始结点的前驱结点
				EdgeX walk; // 游走结点
				while (begin.next != null) {
					prior = begin;
					walk = begin.next;
					while (walk.next != null) {
						EdgeX rear = walk.next;
						if (walk.yi > rear.yi) {
							prior.next = rear;
							walk.next = rear.next;
							rear.next = walk;
						}

						prior = prior.next;
						walk = prior.next;
					}
					begin = begin.next;
				}
			} else if(curAlg == IPOLYGON)// 改进活动边表法（判断有没有连续两者的xi相同）
			{
				EdgeX front = head;
				EdgeX walk = head.next;// 找到头结点
				EdgeX behind;

				if (head.next != null) {
					while (walk.next != null) {
						behind = walk.next;
						if (walk.yi > behind.yi) {
							front.next = behind;
							walk.next = behind.next;
							behind.next = walk;
						}
						front = front.next;
						walk = front.next;
					}
				}
			}
		}

		// 两两交点填充
		void Fill(NETX net, int line) {
			// 由连贯性更新AET中边
			EdgeX first = head.next;// 找到头结点

			if (first != null) // 已经没有交点了就退出
			{
				EdgeX second = first.next;
				while (second != null) {
					savedCanvas.drawLine((float) net.bucket[line].xm,
							(float) first.yi, (float) net.bucket[line].xm,
							(float) second.yi, polyPaint);

					if (second.next == null)
						break;
					else {
						first = first.next.next;
						second = second.next.next;
					}
				}
			}
		}

		// 遍历AET
		void Traverse() {
			EdgeX tmp = head.next;// 找到头结点

			while (tmp != null) {
				System.out.println(tmp.yi);
				tmp = tmp.next;
			}
			System.out.println("完");
		}
	}

	/***************************************************************************/
	/**
	 * 逐点判别算法
	 * 
	 * @author chicken
	 * 
	 */
	public void judgeFill() // 逐点判别并填充内点
	{
		int width = savedBitmap.getWidth();
		int height = savedBitmap.getHeight();
		int fillColor = Color.RED;

		enterTime = System.currentTimeMillis();
		/**************************************************/
		for (int x = 0; x <= width; x++) {
			for (int y = 0; y <= height; y++) {
				if (isInside(new Point(x, y))) {
					savedBitmap.setPixel(x, y, fillColor);
				}
			}
		}
		/**************************************************/
		exitTime = System.currentTimeMillis();
		takedTime = exitTime - enterTime;
		timeTxt.setText(Long.toString(takedTime));

		canvasVi.invalidate();
	}

	public boolean isInside(Point pt) {
		int i, j;
		boolean inside = false, redo = false;
		int N = polygon.length;
		redo = true;
		for (i = 0; i < N; ++i) {
			if (polygon[i].x == pt.x && // 是否在顶点上
					polygon[i].y == pt.y) {
				redo = false;
				inside = true;
				break;
			}
		}

		while (redo) {
			redo = false;
			inside = false;
			for (i = 0, j = N - 1; i < N; j = i++) {
				if ((polygon[i].y < pt.y && pt.y < polygon[j].y)
						|| (polygon[j].y < pt.y && pt.y < polygon[i].y)) {
					if (pt.x <= polygon[i].x || pt.x <= polygon[j].x) {
						double _x = (pt.y - polygon[i].y)
								* (polygon[j].x - polygon[i].x)
								/ (polygon[j].y - polygon[i].y) + polygon[i].x;
						if (pt.x < _x) // 在线的左侧
							inside = !inside;
						else if (pt.x == _x) // 在线上
						{
							inside = true;
							break;
						}
					}
				} else if (pt.y == polygon[i].y) {
					if (pt.x < polygon[i].x) // 交点在顶点上
					{
						if (polygon[i].y > polygon[j].y)
							--pt.y;
						else
							++pt.y;
						redo = true;
						break;
					}
				} else if (polygon[i].y == polygon[j].y
						&& // 在水平的边界线上
						pt.y == polygon[i].y
						&& ((polygon[i].x < pt.x && pt.x < polygon[j].x) || (polygon[j].x < pt.x && pt.x < polygon[i].x))) {
					inside = true;
					break;
				}
			}
		}

		return inside;
	}

	/***************************************************************************/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			android.os.Process.killProcess(android.os.Process.myPid());// 杀死进程
			MainActivity.this.onDestroy();// 摧毁活动
			System.exit(0);// 返回系统
		}
		return super.onKeyDown(keyCode, event);
	}

	public void calDxDy()
	{
		//寻找纵度和横度总值
		int dx=0,dy=0;
		int circle=MainActivity.polygon.length;
		int rear;
		
		//outside
		for(int i=0;i<circle;i++)
		{
			rear=(i+1)%circle;
			dx+=Math.abs(MainActivity.polygon[rear].x-MainActivity.polygon[i].x);
			dy+=Math.abs(MainActivity.polygon[rear].y-MainActivity.polygon[i].y);
		}
		
		//inside
		for(int i=0;i<inside.size();i++)
		{	
			Point[] in=inside.get(i);
			
			circle=in.length;
			for(int j=0;j<circle;j++)
			{
				rear=(j+1)%circle;
				dx+=Math.abs(in[rear].x-in[j].x);
				dy+=Math.abs(in[rear].y-in[j].y);
			}
		}
		
		
		//像素转化成厘米
		float Dx,Dy;
		Dx=(float)((dx*1.0/480)*10.16);
		Dy=(float)((dy*1.0/854)*18.08);
		//显示
		horizonTxt.setText(Float.toString(Dx));
		verticalTxt.setText(Float.toString(Dy));
	}
}
