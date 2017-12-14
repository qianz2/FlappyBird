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
	//��д���췽��
	public BirdGame() throws Exception {
		background = ImageIO.read(getClass().getResource("bg.png"));
		ground = new Ground();
		column1 = new Column(1);
		column2 = new Column(2);
		bird = new Bird();
	}
	
	//��дpaint����,ʵ����Ϸ����Ļ���
	public void paint(Graphics g) {
		g.drawImage(background,0,0,null);
		
		g.drawImage(column1.image, column1.x-column1.width/2, column1.y -column1.height/2, null);
		g.drawImage(column2.image, column2.x-column2.width/2, column2.y -column2.height/2, null);
		g.drawImage(ground.image, ground.x, ground.y, null);
		//��ת��������ϵ
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
	
	
	//������Ϸ�ķ���
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

//����һ������ ��
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
	//���ӵļ�϶
	int gap;
	int distance;
	Random random = new Random();
	//��ʼ�����ӵ����ԣ�����ʹ�ò���n��ʾ�ǵڼ�������
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
	int size;//��Ĵ�С��������ײ���
	
	//������ԣ����ڼ������λ��
	double g;//�������ٶ�
	double t;//���λ��Ƶ�ʱ����
	double v0;//��ʼ�ٶ�
	double speed;//��ǰ�ٶȡ�
	double s;//λ��
	double alpha;//С�����б����
	
	//����һ�����飬������Ķ���֡
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
		s = v0*t+g*t*t/2;//���׵�λ��
		y = y-(int)s;
		double v = v0 - g*t;
		speed = v;
		alpha = Math.atan(s/8);
	}
	
	public void flappy() {
		speed = v0;
	}
	
	public boolean hit(Column col) {
		//���ȼ���Ƿ������ӵķ�Χ��
		if(x>col.x-col.width/2-size/2 && x<col.x+col.width/2+size/2) {
			//�Ƿ��ڷ�϶��
			if(y>col.y-col.gap/2+size/2 && y<col.y+col.gap/2-size/2){
				return false;
			}
			return true;
		}
		return false;
	}
}

