package com.example.ypgame;

import android.widget.ImageView;


//方块信息类
public class BlockInfo {

	//方块id值
	private int id;
	//X轴坐标
	private int pointX ;
	//Y轴坐标
	private int pointY;
	//内部View控件
	private ImageView block;
	//内部控件是否可加
	private boolean allowMerge ;
	
	private int number ;
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPointX() {
		return pointX;
	}
	public void setPointX(int pointX) {
		this.pointX = pointX;
	}
	public int getPointY() {
		return pointY;
	}
	public void setPointY(int pointY) {
		this.pointY = pointY;
	}

	public ImageView getBlock() {
		return block;
	}
	public void setBlock(ImageView block) {
		this.block = block;
	}
	public boolean isAllowMerge() {
		return allowMerge;
	}
	public void setAllowMerge(boolean allowMerge) {
		this.allowMerge = allowMerge;
	}

	public void tongBu(){
		if(block==null){
			return ;
		}
		this.block.setX(pointX);
		this.block.setY(pointY);
	}

	
}
