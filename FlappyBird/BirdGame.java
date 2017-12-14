import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class BirdGame extends JPanel{
	BufferedImage background;
	Ground ground;
	Column column1,column2;
	Bird bird;
	//重写构造方法
	public BirdGame() throws Exception {
		background = ImageIO.read(getClass().getResource("bg.png"));
		ground = new Ground();
		column1 = new Column(1);
		column2 = new Column(2);
		bird = new Bird();
	}
	
	//重写paint方法,实现游戏界面的绘制
	public void paint(Graphics g) {
		g.drawImage(background,0,0,null);
		
		g.drawImage(column1.image, column1.x-column1.width/2, column1.y -column1.height/2, null);
		g.drawImage(column2.image, column2.x-column2.width/2, column2.y -column2.height/2, null);
		g.drawImage(ground.image, ground.x, ground.y, null);
		//旋转绘制坐标系
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(-bird.alpha, bird.x, bird.y);
		g.drawImage(bird.image, bird.x-bird.width/2, bird.y-bird.height,null);
		g2.rotate(bird.alpha, bird.x, bird.y);
	}
	
	public void action() throws Exception {
		MouseListener l = new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				bird.flappy();
			}
		};
		addMouseListener(l);
	}
	
	
	//启动游戏的方法
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
		BirdGame game = new BirdGame();
		frame.setLocation(220, 50);
		frame.add(game);
		frame.setSize(440, 670);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		while(true){
			game.ground.step();
			game.column1.step();
			game.column2.step();
			game.bird.step();
			game.bird.fly();
			if(game.bird.hit(game.column1)||game.bird.hit(game.column2))
				System.out.println("hit");			
			game.action();
			Thread.sleep(10);
			frame.repaint();
		}
	}

}

//创建一个地面 类
class Ground {
	BufferedImage image;
	int x,y;
	int width;
	int height;
	public Ground() throws Exception {
		image = ImageIO.read(getClass().getResource("ground.png"));
		width = image.getWidth();
		height = image.getHeight();
		x = 0;
		y = 500;
	}	
	public void step(){
		x--;
		if(x==-109)
			x = 0;
	}
}

class Column {
	BufferedImage image;
	int x,y;
	int width,height;
	//柱子的间隙
	int gap;
	int distance;
	Random random = new Random();
	//初始化柱子的属性，并且使用参数n表示是第几个柱子
	public Column(int n) throws Exception {
		image = ImageIO.read(getClass().getResource("column.png"));
		width = image.getWidth();
		height = image.getHeight();
		gap = 144;
		distance = 260;
		x = 550+(n-1)*distance;
		y = random.nextInt(220)+132;
	}
	
	public void step() {
		x--;
		if(x==0) {
			x = distance*2 - width/2;
			y = random.nextInt(220)+132;
		}
	}	
}

class Bird {
	BufferedImage image;
	int x,y;
	int width,height;
	int size;//鸟的大小，用于碰撞检测
	
	//添加属性，用于计算鸟的位置
	double g;//重力加速度
	double t;//两次绘制的时间间隔
	double v0;//初始速度
	double speed;//当前速度、
	double s;//位移
	double alpha;//小鸟的倾斜弧度
	
	//定义一个数组，保存鸟的动画帧
	BufferedImage[] images;
	int index;
	
	public Bird() throws Exception {
		image=ImageIO.read(getClass().getResource("0.png"));
		width = image.getWidth();
		this.height = image.getHeight();
		x = 100;
		y = 260;
		size = 40;
		g = 3;
		v0 = 20;
		t = 0.25;
		speed = v0;
		s = 0;
		alpha = 0;
		
		images = new BufferedImage[8];
		for(int i=0;i<8;i++){
			images[i] = ImageIO.read(getClass().getResource(i+".png"));
		}
		index = 0;		
	}
	
	public void fly() {
		index++;
		image = images[(index/12)%8];
	}
	
	public void step(){
		double v0 = speed;
		s = v0*t+g*t*t/2;//上抛的位移
		y = y-(int)s;
		double v = v0 - g*t;
		speed = v;
		alpha = Math.atan(s/8);
	}
	
	public void flappy() {
		speed = v0;
	}
	
	public boolean hit(Column col) {
		//首先检查是否在柱子的范围内
		if(x>col.x-col.width/2-size/2 && x<col.x+col.width/2+size/2) {
			//是否在缝隙中
			if(y>col.y-col.gap/2+size/2 && y<col.y+col.gap/2-size/2){
				return false;
			}
			return true;
		}
		return false;
	}
}

