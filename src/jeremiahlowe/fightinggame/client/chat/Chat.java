package jeremiahlowe.fightinggame.client.chat;

import java.util.ArrayList;

import jeremiahlowe.fightinggame.ins.GraphicalInstance;
import jeremiahlowe.fightinggame.ui.IDrawable;

import net.net16.jeremiahlowe.shared.Color;
import net.net16.jeremiahlowe.shared.Timing;
import net.net16.jeremiahlowe.shared.math.GeneralMath;
import net.net16.jeremiahlowe.shared.math.Vector;
import processing.core.PApplet;

public class Chat implements IDrawable{
	private boolean cursorShowing = false;
	private String typedMessage = "hello";
	private Message[] showing;
	private Timing cursorTiming;
	private float border = 1;
	private boolean cursorBlink = false;
	private ArrayList<IChatListener> chatListeners;
	
	public Vector pos;
	public int alignX, alignY;
	public float maximumW, textSize;
	public float blinkSpeed = 0.25f;
	
	public Chat(GraphicalInstance gi) {
		setLines(20);
		cursorTiming = new Timing();
		chatListeners = new ArrayList<IChatListener>();
		textSize = gi.screen.absHeight() * 0.025f;
		maximumW = gi.screen.absWidth() / 3;
		border = gi.screen.absWidth() / 100;
		pos = new Vector(gi.screen.absWidth() - maximumW - border, gi.screen.absHeight() - border);
		alignX = PApplet.LEFT;
		alignY = PApplet.BOTTOM;
		cursorShowing = false;
	}
	
	public void draw(PApplet p, GraphicalInstance gi) {
		p.textAlign(alignX, alignY);
		p.stroke(0); p.fill(0);
		p.textSize(textSize);
		float th = p.textAscent();
		float tbw = th * 1.5f;
		float y = pos.y - tbw;
		float maxW = 100;
		for(int i = 0; i < showing.length; i++) {
			if(showing[i] == null)
				continue;
			float tw = p.textWidth(showing[i].text);
			p.fill(showing[i].color.argb());
			p.text(showing[i].text, pos.x, y);
			if(tw > maxW) maxW = tw;
			y -= th;
		}
		updateCursor();
		p.fill(0, 0, 0, cursorShowing ? 25 : 0);
		p.rect(pos.x, pos.y - tbw, maximumW, tbw);
		if(cursorShowing)
			drawTextEntryArea(p, th, tbw);
	}
	
	private void updateCursor() {
		if(cursorTiming.secs() > blinkSpeed) {
			cursorBlink = !cursorBlink;
			cursorTiming.reset();
		}
	}
	private void drawTextEntryArea(PApplet p, float th, float tbw) {
		float cursorPosX = p.textWidth(typedMessage);
		float sx = pos.x + border;
		if(cursorBlink) {
			float x = sx + cursorPosX, th5 = th / 5;
			p.line(x, pos.y - tbw + th5, x, pos.y - th5);
		}
		p.fill(0);
		p.text(typedMessage, sx, pos.y);
	}
	private void pushMessage(Message text) {
		int len = showing.length;
		Message[] newText = new Message[len];
		newText[0] = text;
		for(int i = 0; i < len - 1; i++) 
			newText[i + 1] = showing[i];
		showing = newText;
	}
	private void setLines(int lines) {
		showing = new Message[lines];
	}
	
	public void pushMessage(String msg, String from) {
		pushMessage(msg, from, Color.BLACK);
	}
	public void pushMessage(String msg, String from, Color color) {
		Message m = new Message();
		m.text = "[" + from + "]: " + msg;
		m.color = color;
		pushMessage(m);
	}
	public boolean typing() {
		return cursorShowing;
	}
	public void startTyping() {
		cursorShowing = true;
		typedMessage = "";
	}
	public void stopTyping(boolean send) {
		cursorShowing = false;
		if(send) {
			pushMessage(typedMessage, "You");
			for(IChatListener i : chatListeners)
				if(i != null)
					i.onSendMessage(typedMessage);
		}
	}
	public void typeChar(char c) {
		int len = typedMessage.length();
		if(c == 8 || c == 127) 
			typedMessage = typedMessage.substring(0, GeneralMath.clamp(0, len, len - 1));
		else if (c >= ' ' && c <= '~') 
			typedMessage += c;
		else
			System.out.println("Invalid character: " + ((int)c));
	}
	public void addChatListener(IChatListener l) {
		chatListeners.add(l);
	}
	public void removeChatListener(IChatListener l) {
		chatListeners.remove(l);
	}
	
	public int getDrawPriority() { return 0; }
	public boolean enabled() { return true; }
}
