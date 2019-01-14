package com.litaook.lottery;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//import com.litaook.lottery.util.CopyFile;

/**
 * 抽奖主程序 annual_party_new是备用照片目录，存放所有待抽奖人照片，建议使用人名作为文件名；<br/>
 * images是抽奖的照片目录，抽奖之前，请先复制annual_party_new目录中的照片到该目录中，抽奖过程中，
 * 有人中奖会讲该人照片移动到winners目录，所以images目录中的照片会逐步变少<br/>
 * 抽奖过程中，程序会通过ActionListener刷新列表。<br/>
 * 更新方式：文件列表写到sqlite数据库中，中奖就删除记录(2014-1-21)。
 * 
 * @author litao
 * 
 */
public class Lucky implements ActionListener {
	static Logger log = LogManager.getLogger("Lucky");
	// 真实图片数
	int real_num_images = 0;
	// 图片对象列表
	List<ImageIcon> imageIcons = new ArrayList<ImageIcon>();
	// 图片名称列表
	List<String> imageNames = new ArrayList<String>();
	// 主要几个面板
	JPanel mainPanel, selectPanel, displayPanel, resultPanel; // 抽奖控制按键
	JButton phaseChoice = null;
	// 显示结果标签
	JLabel phaseIconLabel = null, phaseResult = null, phaseBlank1 = null,
			phaseBlank2 = null;

	// 屏幕宽
	int screenWidth = 0;
	// 屏幕高
	int screenHeight = 0;

	// 图片切换间隔时间 ms
	final static int DURATION = 50;
	// 图片显示区域左右边距
	final static int DISPLAY_MARGIN = 100;
	// 通用边距
	final static int MARGIN = 50;
	// 按键宽度
	final static int BUTTON_WIDTH = 200;
	// 结果框宽度
	final static int RESULT_WIDTH = 400;
	// 按键和结果框公用高度
	final static int DOWN_HEIGHT = 100;

	// get the system separator
	String separator = System.getProperty("file.separator");

	// 工作目录
	String rootDir = new StringBuffer(System.getProperty("user.dir")).append(
			File.separator).toString();
	// 图片目录
	final static String IMAGE_DIR = "images/";
	final static String WINNER_DIR = "winners/";
	String WELCOME_IMAGE = new StringBuffer("welcome").append(separator).append("start.jpg").toString();
	String input_file_name = new StringBuffer("images").append(separator).append("inputfile.txt").toString();
	Font bigFont = new Font("Arial", Font.BOLD, 60);
	Font startFont = new Font("Arial", Font.BOLD, 32);

	// Constructor
	public Lucky() throws IOException {
		log.debug("构造函数 - start");
		// 检查屏幕尺寸
		checkScreenSize();

		// 设置各主要面板尺寸
		setPanelsSize();

		// 自定义布局面板
		mainPanel = new JPanel();
		mainPanel.setLayout(null);

		// 添加到主面板中
		mainPanel.add(selectPanel);
		mainPanel.add(displayPanel);
		//displayPanel.add(selectPanel);
		//mainPanel.add(resultPanel);

		// 添加插件，读取所有图片入数组
		addWidgets();

		log.debug("end");
	}

	/**
	 * A simple example program that reads a into a String using StringBuilder.
	 */
	private void ReadTextFileToArray (){
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(input_file_name ))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        System.out.println(line);
 		//	if (sb.length() > 0) {
                 //   		sb.append("\n");
                //	}
                //sb.append(line);
		// add names to the array
		    imageNames.add(line);
		    
		    log.debug(new StringBuffer("imageNames: ").append(line));
		    }
		real_num_images = imageNames.size();
		    log.debug(new StringBuffer("real_num_images: ").append(real_num_images));
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	/**
	 * 检查屏幕大小
	 */
	private void checkScreenSize() {
		log.debug("检查屏幕尺寸 - start");
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = (int) screensize.getWidth();// 得到宽
		screenHeight = (int) screensize.getHeight();// 得到高

		log.debug(new StringBuffer("屏幕分辨率为 : ").append(screenWidth).append("x")
				.append(screenHeight));
		log.debug("end");
	}

	/**
	 * 设置几个主要JPanel的尺寸
	 */
	private void setPanelsSize() {
		log.debug("设置各主要面板 - start");
		int x, y, w, h = 0;
		// 抽奖按钮区域面板
		selectPanel = new JPanel();
		// 抽奖和暂停按键
		//x = screenWidth / 2 - BUTTON_WIDTH;
		x = (screenWidth-DISPLAY_MARGIN*2-BUTTON_WIDTH)/2+DISPLAY_MARGIN;
		y = screenHeight - MARGIN * 2 - DOWN_HEIGHT;
		w = BUTTON_WIDTH;
		h = DOWN_HEIGHT;
		selectPanel.setBounds(x, y, w, h);

		// 图片显示区域面板
		displayPanel = new JPanel();
		// 图片显示区域大小设置
		x = DISPLAY_MARGIN;
		y = MARGIN;
		w = screenWidth - DISPLAY_MARGIN * 2;
		h = screenHeight - DOWN_HEIGHT - MARGIN * 3;
		displayPanel.setBounds(x, y, w, h);

		// 获奖显示区域面板
		//resultPanel = new JPanel();
		// 获奖显示区域大小
		//x = screenWidth / 2 + MARGIN;
		//y = screenHeight - MARGIN * 2 - DOWN_HEIGHT;
		//w = RESULT_WIDTH;
		//h = DOWN_HEIGHT;
		//resultPanel.setBounds(x, y, w, h);
		log.debug("end");
	}

	/**
	 * 创建插件，并显示图
	 * 
	 * @throws IOException
	 */
	private void addWidgets() throws IOException {
		log.debug("创建插件 - start");

		ReadTextFileToArray();

		// 使用JLabel来显示图片，全部居中显示
		phaseIconLabel = new JLabel();
		phaseIconLabel.setHorizontalAlignment(JLabel.CENTER);
		phaseIconLabel.setVerticalAlignment(JLabel.CENTER);
		phaseIconLabel.setVerticalTextPosition(JLabel.CENTER);
		phaseIconLabel.setHorizontalTextPosition(JLabel.CENTER);
		phaseIconLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		phaseResult = new JLabel();
		phaseBlank1 = new JLabel();
		phaseBlank2 = new JLabel();

		// 创建控制按键
		phaseChoice = new JButton("开始/停止");

		phaseResult.setFont(bigFont);
		//int vert_size =  screenHeight - DOWN_HEIGHT - MARGIN * 3;
		//phaseResult.setLayoutY(vert_size/2-DOWN_HEIGHT);
		phaseBlank1.setFont(bigFont);
		phaseBlank2.setFont(bigFont);
		phaseChoice.setFont(startFont);

		// 显示第一张欢迎图片
		// 读入图
		ImageIcon icon = null;
		// 可直接使用files[i], 无需构造URL
		String fileName = new StringBuffer(rootDir).append(WELCOME_IMAGE)
				.toString();
		try {
			icon = new ImageIcon(ImageIO.read(new File(fileName)));
		} catch (IOException ioe) {
			throw new IOException(new StringBuffer("ImageIO读取图片文件失败 : ")
					.append(fileName).append(", 不是jpg图片格式？").toString());
		}
		log.debug("转换成ImageIcon对象...");
		phaseIconLabel.setIcon(icon);
		phaseIconLabel.setText("Default Text label");

		// 各区域设置边框
		selectPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("操作区"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		//resultPanel.setBorder(BorderFactory.createCompoundBorder(
		//BorderFactory.createTitledBorder("结果区"),
		//BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// 图片区域设置边框
		displayPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("抽奖区"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// 在显示面板上添加各
		//selectPanel.add(phaseChoice);
		//displayPanel.add(phaseIconLabel);
		//resultPanel.add(phaseBlank1);
		displayPanel.add(phaseBlank1);
		displayPanel.add(phaseResult);
		displayPanel.add(phaseBlank2);
		selectPanel.add(phaseChoice);
		//resultPanel.add(phaseResult);
		//resultPanel.add(phaseBlank2);
		log.debug("各显示Panel设置完毕");

		// 给控制按键设置监听器
		phaseChoice.addActionListener(this);
		log.debug("按键添加监听器完毕");

		log.debug("end");
	}

	/**
	 * 产生随机ID
	 * 
	 * @return
	 */
	private int random(int max) {
		log.debug("生成随机数 - start & end");
		return (int) (Math.random() * real_num_images);
	}

	boolean run = false;

	/**
	 * 该方法不会显式调用，在使用了addActionListener监控并在发生事件时触发actionPerformed方法
	 */
	public void actionPerformed(ActionEvent event) {
		log.debug("事件发生，执行处理 - start");
		if (run) {
			run = false;
			phaseBlank1.setText("恭喜");
			phaseBlank2.setText("中奖啦！");
			phaseIconLabel.setText("");
			// 删除获奖照片
			//try {
				//moveLuckyImage(phaseResult.getText());
				// remove the string index from the array
				
				String currentName = phaseResult.getText();
				int k = 0;
				for (k = 0; k < imageNames.size(); k++)
				{
				    if( currentName == imageNames.get(k))
						    {
							    break;
						    }
				}
				imageNames.remove(k);
				real_num_images = imageNames.size();
			/*} catch (IOException ioe) {
				log.error(new StringBuffer("移动获奖图片出错 : ").append(ioe));
			}*/
			log.info(new StringBuffer("获奖人 : ").append(phaseResult.getText()));
			// 重建图片文件列表
/*#if 0
			try {
				readImagesToArrays();
			} catch (IOException e) {
				log.fatal(new StringBuffer("程序已退出 : ").append(e));
				System.err.println(new StringBuffer(
						"程序异常退出，可能是图片目录出现问题，请检查images目录是否存在，其中是否有图片 : ")
						.append(e));
				System.exit(1);
			}
#endif*/
		} else {
			run = true;

			new Thread() {
				public void run() {
					while (run) {

						// 随机显示
						int index = random(real_num_images);
						log.debug(new StringBuffer("随机抽取到的序号 : ")
								.append(index));
						//phaseIconLabel.setIcon(imageIcons.get(index));
						//phaseIconLabel.setText(imageNames.get(index));
						phaseBlank1.setText("");
						// 显示图片名称，不要扩展名
						try {
							String imageName = imageNames.get(index);
							phaseResult.setText(imageName);//.substring(0,
									////imageName.lastIndexOf(".")));
							phaseBlank2.setText("");
						} catch (Exception e) {
							e.printStackTrace();
							log.fatal(new StringBuffer("出现错误，可能是随机抽的图片不存在 : ")
									.append(e));
							System.err.println(new StringBuffer(
									"出现错误，可能是随机抽的图片不存在 : ").append(e));
							System.exit(1);
						}
						try {
							Thread.sleep(DURATION);
						} catch (Exception e) {
							log.fatal(e);
						}
					}
					log.debug("新线程启动 ...");
				}

			}.start();
		}
		log.debug("end");
	}

/*
	public static void setUIFont(FontUIResource f) {
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
		    Object key = keys.nextElement();
		    Object value = UIManager.get(key);
		    if (value instanceof FontUIResource) {
		        FontUIResource orig = (FontUIResource) value;
		        Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
		        UIManager.put(key, new FontUIResource(font));
		    }
		}
	    }
*/
	/**
	 * 主函数
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Lucky luck = null;

		// log4j 2.x
		log.entry();

		// 设置外观
		try {
			log.debug("new Lucky() - 初始化 - start");
			luck = new Lucky();
			log.debug("Lucky() - end");

			// 创建JFrame
			JFrame frame = new JFrame("抽奖啦");
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
			log.debug("setLookAndFeel() - 设置外观 - start & end");

//                        setUIFont(new FontUIResource(new Font("Arial", 0, 20)));
			// 设置frame参数
			log.debug("Frame添加Panel");
			frame.setContentPane(luck.mainPanel);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			frame.setUndecorated(false);

			// 关闭窗口退出
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// 开始显示
			log.debug("Frame.pack() - start");
			frame.pack();
			log.debug("Frame.pack() - end");
			frame.setVisible(true);

		} catch (Exception e) {
			log.fatal(e);
		} finally {
			log.exit();
		}

	}
}
