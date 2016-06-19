package com.example.ypgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity  {

	//线性布局对象
	LinearLayout ll = null;
	//绝对布局对象
	@SuppressWarnings("deprecation")
	AbsoluteLayout al = null;
	//分数栏
	TextView result = null;
	//方块个数
	final short BLOCKNUM = 16;
	//结果的字符串
	final String resultTile = "SCORE : ";
	//方块位置信息集合
	List<BlockInfo> listBlockInfo = new ArrayList<BlockInfo>();
	//最高块
	int highBlockNumber = 0;
	
	//block图片信息
	//final int[] blockPic = new int[]{R.drawable.p2,R.drawable.p4,R.drawable.p8,R.drawable.p16,R.drawable.p32,R.drawable.p64,
	//						R.drawable.p128,R.drawable.p256,R.drawable.p512,R.drawable.p1024,R.drawable.p2048,R.drawable.p4096,
	//						R.drawable.p8192,R.drawable.p16384,R.drawable.p32768,R.drawable.p65536,R.drawable.win};
	final int[] blockHeroPic = new int[]{R.drawable.p2_0,R.drawable.p4_0,R.drawable.p8_0,R.drawable.p16_0,R.drawable.p32_0,R.drawable.p64_0,
										R.drawable.p128_0,R.drawable.p256_0,R.drawable.p512_0,R.drawable.p1024_0,R.drawable.p2048_0,R.drawable.p4096_0,
										R.drawable.p8192_0,R.drawable.p16384,R.drawable.p32768,R.drawable.p65536,R.drawable.win};
	final int[] highBlockState= new int[]{R.string.d0,R.string.d1,R.string.d2,R.string.d3,R.string.d4,R.string.d5,R.string.d6,R.string.d7,
											R.string.d8,R.string.d9,R.string.d10,R.string.d11,R.string.d12,R.string.d13,R.string.d14,R.string.d15};
	//设置外围边距
	int outterLength = 60;
	//设置内部边距
	int innerLength  = 20;
	//根据屏幕分辨率计算出每个正方块的边长
	int blockLength = 0;
	//速度，毫秒数，越小速度越快
	int speed = 260;
	//是否有模块移动的标志位
	boolean blockFlag = false;
	//移动中锁
	boolean moving  = true;
	//分数
	long score = 0;
	//短促声音播放器
	SoundPool sp  = null;
	//载入声音编号
	int sound1 = 0;
	//播放背景音乐
	MediaPlayer mplayer = null;
	//初次播放音乐
	boolean playSound = false;
	//状态
	TextView tvState= null;
	
	Interpolator interpolator = new DecelerateInterpolator() ;
			 
//			new AccelerateInterpolator(), // 0越来越快		 
//			new DecelerateInterpolator(),// 1越来越慢	 
//			new AccelerateDecelerateInterpolator(),// 2先快后慢	 
//			new AnticipateInterpolator(),// 3先后退一小步然后向前加速	 
//			new OvershootInterpolator(),// 4快速到达终点超出一小步然后回到终点	 
//			new BounceInterpolator(),// 5到达终点产生弹球效果，弹几下再回来
//			new LinearInterpolator() // 6均匀速度

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//全屏显示
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//全屏点亮
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);
		
		ll=(LinearLayout)findViewById(R.id.container);
		al = (AbsoluteLayout)findViewById(R.id.al);
		result =(TextView)this.findViewById(R.id.result);
		tvState=(TextView)this.findViewById(R.id.state);
		//背景音乐
//		mplayer = MediaPlayer.create(this, R.raw.timemove);
//		mplayer.setLooping(true);
//		try {
//			mplayer.prepare();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		mplayer.start();
		//移动声音
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
		sound1=sp.load(MainActivity.this, R.raw.bs,1);
		
		//获得屏幕的宽高，单位为PX
		DisplayMetrics outMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		float ScreenWidth = outMetrics.widthPixels;
		float ScreenHeight = outMetrics.heightPixels;
		if(ScreenHeight<ScreenWidth){
			ScreenWidth=ScreenHeight;
		}

		al.setMinimumHeight((int)ScreenWidth-outterLength*2);
		al.setMinimumWidth((int)ScreenWidth);
		
		//根据屏幕分辨率计算出每个正方块的边长
		blockLength = (int)((ScreenWidth-outterLength*2-innerLength*5)/4);
		
		//首个方块的坐标值
		final int firstBlock_X = outterLength+innerLength;  
		final int firstBlock_Y = innerLength;  
		
		int pointX,pointY;
		//初始化BlockInfo信息，以及背景图片
		for(int i=0;i<BLOCKNUM;i++){
			BlockInfo bInfo = new BlockInfo();
			bInfo.setId(i);
			bInfo.setAllowMerge(true);
			bInfo.setNumber(0);
			pointX = firstBlock_X+(i%4)*(innerLength+blockLength);
			pointY = firstBlock_Y+(i/4)*(innerLength+blockLength);
			bInfo.setPointX(pointX);
			bInfo.setPointY(pointY);
			listBlockInfo.add(bInfo);
			bInfo.setNumber(-1);
		}
		
		short firstNumber = 0;
		short secondNumber =0;
		//初始两个BlockInfo
		short[] number =new short[BLOCKNUM];
		for(short i=0;i<BLOCKNUM;i++){
			number[i]=i;
		}
		short random = (short) new Random(System.currentTimeMillis()).nextInt(BLOCKNUM);
		firstNumber = number[random];
		short temp = number[BLOCKNUM-1];
		number[BLOCKNUM-1] = number[random]; 
		number[random] = temp;
		random = (short) new Random(System.currentTimeMillis()).nextInt(BLOCKNUM-1);
		secondNumber = number[random];
		createBlock(listBlockInfo.get(firstNumber));
		createBlock(listBlockInfo.get(secondNumber));
		
		//添加触摸屏时间监听
		ll.setLongClickable(true);
		ll.setOnTouchListener(new MyGesture());

	}
	
	
	public void ToRight(){
		int blockMove[] = {14,10,6,2,13,9,5,1,12,8,4,0};
		//遍历各个模块
		for(int i : blockMove)
		{
			BlockInfo currentBlockInfo = listBlockInfo.get(i);
			if(currentBlockInfo.getBlock()==null){
				continue;
			}
			BlockInfo nextBlockInfoToMove = null;
			for(int j=1;j<=3;j++){
				if((i+j)%4==0){
					break;
				}
				BlockInfo nextBlockInfo = listBlockInfo.get(i+j);
				if(nextBlockInfo.getBlock()==null){
					nextBlockInfoToMove=nextBlockInfo;
					
				}else{
					if(nextBlockInfo.isAllowMerge()==false){
						break;
					}
					if(nextBlockInfo.getNumber()!=currentBlockInfo.getNumber()){
						break;
					}else{
						nextBlockInfoToMove=nextBlockInfo;
						break;
					}
				}
			}
			blockToBlock(currentBlockInfo,nextBlockInfoToMove);
		}
	}
	
	public void ToLeft(){
		int blockMove[] = {13,9,5,1,14,10,6,2,15,11,7,3};
		//遍历各个模块
		for(int i : blockMove)
		{
			BlockInfo currentBlockInfo = listBlockInfo.get(i);
			if(currentBlockInfo.getBlock()==null){
				continue;
			}
			BlockInfo nextBlockInfoToMove = null;
			for(int j=1;j<=3;j++){
				if((i+1-j)%4==0){
					break;
				}
				BlockInfo nextBlockInfo = listBlockInfo.get(i-j);
				if(nextBlockInfo.getBlock()==null){
					nextBlockInfoToMove=nextBlockInfo;
					
				}else{
					if(nextBlockInfo.isAllowMerge()==false){
						break;
					}
					if(nextBlockInfo.getNumber()!=currentBlockInfo.getNumber()){
						break;
					}else{
						nextBlockInfoToMove=nextBlockInfo;
						break;
					}
				}
			}
			blockToBlock(currentBlockInfo,nextBlockInfoToMove);
		}
	}
	
	public void ToDown(){
		int blockMove[] = {8,9,10,11,4,5,6,7,0,1,2,3};
		//遍历各个模块
		for(int i : blockMove)
		{
			BlockInfo currentBlockInfo = listBlockInfo.get(i);
			if(currentBlockInfo.getBlock()==null){
				continue;
			}
			BlockInfo nextBlockInfoToMove = null;
			for(int j=1;j<=3;j++){
				if((i+j*4)>=16){
					break;
				}
				BlockInfo nextBlockInfo = listBlockInfo.get(i+j*4);
				if(nextBlockInfo.getBlock()==null){
					nextBlockInfoToMove=nextBlockInfo;
					
				}else{
					if(nextBlockInfo.isAllowMerge()==false){
						break;
					}
					if(nextBlockInfo.getNumber()!=currentBlockInfo.getNumber()){
						break;
					}else{
						nextBlockInfoToMove=nextBlockInfo;
						break;
					}
				}
			}
			blockToBlock(currentBlockInfo,nextBlockInfoToMove);
		}
	}
	
	public void ToUp(){
		int blockMove[] = {4,5,6,7,8,9,10,11,12,13,14,15};
		//遍历各个模块
		for(int i : blockMove)
		{
			BlockInfo currentBlockInfo = listBlockInfo.get(i);
			if(currentBlockInfo.getBlock()==null){
				continue;
			}
			BlockInfo nextBlockInfoToMove = null;
			for(int j=1;j<=3;j++){
				if((i-(j*4)<0)){
					break;
				}
				BlockInfo nextBlockInfo = listBlockInfo.get(i-(j*4));
				if(nextBlockInfo.getBlock()==null){
					nextBlockInfoToMove=nextBlockInfo;
					
				}else{
					if(nextBlockInfo.isAllowMerge()==false){
						break;
					}
					if(nextBlockInfo.getNumber()!=currentBlockInfo.getNumber()){
						break;
					}else{
						nextBlockInfoToMove=nextBlockInfo;
						break;
					}
				}
			}
			blockToBlock(currentBlockInfo,nextBlockInfoToMove);
		}
	}
	
	public void blockToBlock(BlockInfo currentBlockInfo,BlockInfo nextBlockInfoToMove){
		if(nextBlockInfoToMove!=null){
			blockFlag=true;
			if(nextBlockInfoToMove.getNumber()==currentBlockInfo.getNumber()){
				currentBlockInfo.tongBu();
				//移动并合并
				float move_x = nextBlockInfoToMove.getPointX()-currentBlockInfo.getPointX();
				float move_y = nextBlockInfoToMove.getPointY()-currentBlockInfo.getPointY();
				int numberMerge = 0;
				if(currentBlockInfo.getNumber()!=blockHeroPic.length-1){
					numberMerge = currentBlockInfo.getNumber()+1;
				}else{
					numberMerge = currentBlockInfo.getNumber();
				}
				if(numberMerge>highBlockNumber){
					highBlockNumber=numberMerge;
					tvState.setText(highBlockState[highBlockNumber]);
				}
				score=score+(long)Math.pow(2, numberMerge);
				blockMove(currentBlockInfo.getBlock(),nextBlockInfoToMove.getBlock(),move_x,move_y,numberMerge);
				//设置标志位
				nextBlockInfoToMove.setBlock(currentBlockInfo.getBlock());
				nextBlockInfoToMove.setNumber(numberMerge);
				nextBlockInfoToMove.setAllowMerge(false);
				currentBlockInfo.setBlock(null);
				currentBlockInfo.setNumber(-1);
				currentBlockInfo.setAllowMerge(true);
			}else{
				currentBlockInfo.tongBu();
				//移动
				float move_x = nextBlockInfoToMove.getPointX()-currentBlockInfo.getPointX();
				float move_y = nextBlockInfoToMove.getPointY()-currentBlockInfo.getPointY();
				int numberMerge = currentBlockInfo.getNumber();
				blockMove(currentBlockInfo.getBlock(),nextBlockInfoToMove.getBlock(),move_x,move_y,-1);
				//设置标志位
				nextBlockInfoToMove.setBlock(currentBlockInfo.getBlock());
				nextBlockInfoToMove.setNumber(numberMerge);
				nextBlockInfoToMove.setAllowMerge(true);
				currentBlockInfo.setBlock(null);
				currentBlockInfo.setNumber(-1);
				currentBlockInfo.setAllowMerge(true);
			}
		}
	}
	
	public void blockMove(ImageView srcTv,ImageView destTv,float moveX,float moveY,int number){
		
		Animation an = new TranslateAnimation(0, moveX, 0, moveY);
		an.setDuration(speed);
		an.setFillAfter(true);
		an.setInterpolator(interpolator);
		srcTv.bringToFront();
		an.setAnimationListener(new MyAnimation(srcTv,destTv,number));
		srcTv.startAnimation(an);
	}

	public void blockEndMove(){
		
		if(blockFlag==false){
			moving = true;
			return ;
		}
		List<Integer> listNum = new ArrayList<Integer>();
		for(BlockInfo bI:listBlockInfo){
			bI.setAllowMerge(true);
			if(bI.getBlock()==null){
				listNum.add(bI.getId());
			}
		}
		if(listNum.size()<=0){
			return ;
		}
		int random = new Random().nextInt(listNum.size());
		BlockInfo createBlockInfo =  listBlockInfo.get(listNum.get(random));
		MainActivity.this.playSound = true;
		createBlock(createBlockInfo);

	}
	
	
	public void createBlock(BlockInfo createBlockInfo){
		
		int number = 0;
		if(new Random().nextInt(10)<1){
			number=1;
		}
		ImageView im = new ImageView(this);
		im.setMaxWidth(blockLength);
		im.setMaxHeight(blockLength);
		im.setAdjustViewBounds(true);
		im.setScaleType(ScaleType.FIT_XY);
		im.setImageResource(blockHeroPic[number]);
		im.setX(createBlockInfo.getPointX());
		im.setY(createBlockInfo.getPointY());
		al.addView(im);
		createBlockInfo.setNumber(number);
		createBlockInfo.setBlock(im);
		
		Animation an = new ScaleAnimation(0f,1f,0f,1f,Animation.ABSOLUTE,createBlockInfo.getPointX()+blockLength/2,Animation.ABSOLUTE,createBlockInfo.getPointY()+blockLength/2);
		an.setDuration(speed);
		an.setFillAfter(true);
		an.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				result.setText(resultTile+score);
				if(MainActivity.this.playSound){
					sp.play(sound1,0.5f, 0.5f, 1, 0, 1.0f);
				}
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				//本次滑动结束
				moving  = true;
			}
		});
		createBlockInfo.getBlock().startAnimation(an);
	}

	class MyAnimation implements AnimationListener{

		ImageView tv= null;
		ImageView NeedToRmmoveTv= null;
		int number =-1;
		
		public MyAnimation(ImageView tv,ImageView NeedToRmmoveTv,int number){
			this.tv=tv;
			this.number=number;
			this.NeedToRmmoveTv=NeedToRmmoveTv;
		}
		
		@Override
		public void onAnimationStart(Animation animation) {}

		@Override
		public void onAnimationEnd(Animation animation) {
			if(number>=0){
				tv.setImageResource(blockHeroPic[number]);
				
			}
			if(NeedToRmmoveTv!=null){
				//回收block
				al.removeView(NeedToRmmoveTv);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {}
		
	}
	
	class MyGesture implements OnTouchListener{

	    private GestureDetector mGestureDetector;   
	    public MyGesture() {   
	        mGestureDetector = new GestureDetector(MainActivity.this,new OnGestureListener() {
				@Override
				public boolean onSingleTapUp(MotionEvent e) {
					return false;
				}
				
				@Override
				public void onShowPress(MotionEvent e) {
				}
				
				@Override
				public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
						float distanceY) {
					return false;
				}
				
				@Override
				public void onLongPress(MotionEvent e) {			
				}
				
				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
						float velocityY) {
					blockFlag=false;
					if(moving==false){
						return false;
					}
					moving = false;
					if(Math.abs(e1.getX()-e2.getX())>=Math.abs(e1.getY()-e2.getY())){
				        if (e1.getX()-e2.getX() > 50 && Math.abs(velocityX) > 0) {     
				            ToLeft();
				        }else if (e2.getX()-e1.getX() > 50 && Math.abs(velocityX) > 0) {     
				            ToRight();
				        } 
			        }else{
			        	 if (e1.getY()-e2.getY() > 50 && Math.abs(velocityY) > 0) {     
					         ToUp();
			        	 }else if (e2.getY()-e1.getY() > 50 && Math.abs(velocityY) > 0) {        
					         ToDown();
				         } 
			         }
					blockEndMove();
					return false;
				}
				
				@Override
				public boolean onDown(MotionEvent e) {
					return false;
				}
			});
	        mGestureDetector.setIsLongpressEnabled(true);   
	    }
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return mGestureDetector.onTouchEvent(event); 
		}   
		
	}

	@Override
	protected void onStop() {
		
		sp.release();
		
		super.onStop();
	}
	
}




