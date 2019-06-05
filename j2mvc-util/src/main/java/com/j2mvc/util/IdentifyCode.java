package com.j2mvc.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description 验证码类
 * @Company 贵州沃尔达科技有限公司
 * @Author 杨大江
 * @Version 1.0.1 
 * @Date 2015-1-6
 */
public class IdentifyCode{
	int length = 4;

	int fontSize = 18;
 
	int padding = 2;

	boolean chaos = true;

	Color chaosColor = Color.lightGray;

	Color backgroundColor = new Color(237,237,237);

	String[] fonts = { "Arial", "Georgia", "Times New Roman" };

	String codeSerial = "a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";

	public IdentifyCode() {
	}
	public IdentifyCode(int length) {
		this.length = length;
	}
	public IdentifyCode(int length,Color chaosColor,Color backgroundColor) {
		this.length = length;
		this.chaosColor = chaosColor;
		this.backgroundColor = backgroundColor;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFontSize() {
		return this.fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getPadding() {
		return this.padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public boolean isChaos() {
		return this.chaos;
	}

	public void setChaos(boolean chaos) {
		this.chaos = chaos;
	}

	public Color getChaosColor() {
		return this.chaosColor;
	}

	public void setChaosColor(Color chaosColor) {
		this.chaosColor = chaosColor;
	}

	public Color getBackgroundColor() {
		return this.backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String[] getFonts() {
		return this.fonts;
	}

	public void setFonts(String[] fonts) {
		this.fonts = fonts;
	}

	public String getCodeSerial() {
		return this.codeSerial;
	}

	public void setCodeSerial(String codeSerial) {
		this.codeSerial = codeSerial;
	}

	private Color getRandColor(int fc, int bc) {
//		Random random = new Random();
//		if (fc > 255)
//			fc = 255;
//		if (bc > 255)
//			bc = 255;
//		int r = fc + random.nextInt(bc - fc);
//		int g = fc + random.nextInt(bc - fc);
//		int b = fc + random.nextInt(bc - fc);

		//return new Color(r, g, b);

		return new Color(55, 55, 55);
	}

	private BufferedImage TwistImage(BufferedImage srcBi, boolean bXDir,
			double dMultValue, double dPhase) {
		BufferedImage destBi = new BufferedImage(srcBi.getWidth(),srcBi.getHeight(), 1);

		Graphics graphics = destBi.getGraphics();

		graphics.setColor(this.backgroundColor);
		graphics.fillRect(0, 0, destBi.getWidth(), destBi.getHeight());

		graphics.dispose();

		double dBaseAxisLen = destBi.getWidth();

		for (int i = 0; i < destBi.getWidth(); i++) {
			for (int j = 0; j < destBi.getHeight(); j++) {
				double dx = 0.0D;
				dx = bXDir ? 3.141592653589793D * j / dBaseAxisLen
						: 3.141592653589793D * i / dBaseAxisLen;
				dx += dPhase;
				double dy = Math.sin(dx);

				int nOldX = 0;
				int nOldY = 0;
				nOldX = bXDir ? i + (int) (dy * dMultValue) : i;
				nOldY = bXDir ? j : j + (int) (dy * dMultValue);
				int rgb = srcBi.getRGB(i, j);
				if ((nOldX < 0) || (nOldX >= destBi.getWidth()) || (nOldY < 0)
						|| (nOldY >= destBi.getHeight()))
					continue;
				destBi.setRGB(nOldX, nOldY, rgb);
			}

		}

		return destBi;
	}

	/**
	 * 生成指定验证码缓存图片
	 * @param code
	 * @return
	 */
	private BufferedImage CreateImageCode(String code) {
		int fWidth = this.fontSize + this.padding;

		int imageWidth = code.length() * fWidth + fontSize + this.padding * 0;

		int imageHeight = this.fontSize * 2;

		BufferedImage bi = new BufferedImage(imageWidth, imageHeight, 1);

		Graphics graphics = bi.getGraphics();

		graphics.setColor(this.backgroundColor);
		graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());

		Random random = new Random();

		if (this.chaos) {
			int c = this.length * 10;
			for (int i = 0; i < c; i++) {
				int x = random.nextInt(bi.getWidth());
				int y = random.nextInt(bi.getHeight());
				graphics.setColor(this.chaosColor);
				graphics.drawRect(x, y, 1, 1);
			}

		}

		for (int i = 0; i < code.length(); i++) {
			int findex = random.nextInt(this.fonts.length);
			Font font = new Font(this.fonts[findex], 1, this.fontSize);
			graphics.setFont(font);

			int top = (imageHeight + code.length() * 2) / 2;
			if (i % 2 != 1) {
				top -= code.length();
			}
			int left = i * fWidth + code.length();
			graphics.setColor(getRandColor(1 + i, 250 - i));
			try {
				graphics.drawString(code.substring(i, i + 1), left, top);
			} catch (StringIndexOutOfBoundsException e) {
				System.out.print(e.toString());
			}

		}

		graphics.setColor(new Color(237,237,237));
		graphics.drawRect(0, 0, bi.getWidth() - 1, bi.getHeight() - 1);

		graphics.dispose();

		bi = TwistImage(bi, true, 16.0D, 8.0D);

		return bi;
	}

	/**
	 * 输出验证码图片
	 * @param code
	 * @param response
	 */
	public void CreateImageOnPage(String code, HttpServletResponse response) {
		response.reset();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);

		BufferedImage bi = CreateImageCode(code);
		try {
			ImageIO.write(bi, "PNG", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成验证码
	 * @param codeLen 验证码长度
	 * @return
	 */
	private String CreateIdentifyCode(int codeLen) {
		if (codeLen == 0) {
			codeLen = this.length;
		}
		String[] arr = this.codeSerial.split(",");
		String code = "";
		int randValue = -1;
		Random random = new Random();
		for (int i = 0; i < codeLen; i++) {
			randValue = random.nextInt(arr.length - 1);
			code = code + arr[randValue];
		}
		return code;
	}

	public String CreateIdentifyCode() {
		return CreateIdentifyCode(0);
	}
}