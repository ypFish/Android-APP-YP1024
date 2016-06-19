package com.example.ypgame;

import android.widget.ImageView;


//������Ϣ��
public class BlockInfo {

	//����idֵ
	private int id;
	//X������
	private int pointX ;
	//Y������
	private int pointY;
	//�ڲ�View�ؼ�
	private ImageView block;
	//�ڲ��ؼ��Ƿ�ɼ�
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
